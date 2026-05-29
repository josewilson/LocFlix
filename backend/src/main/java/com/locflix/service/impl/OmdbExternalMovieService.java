package com.locflix.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locflix.dto.response.ExternalMovieResponse;
import com.locflix.service.external.ExternalMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OmdbExternalMovieService implements ExternalMovieService {

    private static final DateTimeFormatter OMDB_RELEASE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    @Value("${movie.external.omdb.api-key:}")
    private String apiKey;

    @Value("${movie.external.omdb.base-url:https://www.omdbapi.com/}")
    private String baseUrl;

    @Override
    public List<ExternalMovieResponse> search(String title) {
        if (!StringUtils.hasText(title)) {
            return List.of();
        }

        if (!StringUtils.hasText(apiKey)) {
            return fallbackSearch(title);
        }

        try {
            return searchAcrossPages(title);
        } catch (Exception ex) {
            log.warn("Fallback para catalogo externo local ao buscar '{}': {}", title, ex.getMessage());
            return fallbackSearch(title);
        }
    }

    @Override
    public ExternalMovieResponse getByExternalId(String externalId) {
        if (!StringUtils.hasText(externalId)) {
            throw new IllegalArgumentException("externalId é obrigatório");
        }

        if (!StringUtils.hasText(apiKey)) {
            return fallbackDetails(externalId);
        }

        try {
            URI uri = URI.create(baseUrl + "?apikey=" + apiKey + "&i=" + URLEncoder.encode(externalId.trim(), StandardCharsets.UTF_8) + "&plot=full");
            JsonNode root = readJson(uri);
            if (!isSuccess(root)) {
                throw new IllegalArgumentException(root.path("Error").asText("Filme externo não encontrado"));
            }
            return toExternalMovieResponse(root);
        } catch (Exception ex) {
            log.warn("Fallback para detalhe externo local ao buscar '{}': {}", externalId, ex.getMessage());
            return fallbackDetails(externalId);
        }
    }

    private JsonNode readJson(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }

    private List<ExternalMovieResponse> searchAcrossPages(String title) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(title.trim(), StandardCharsets.UTF_8);
        LinkedHashMap<String, ExternalMovieResponse> moviesById = new LinkedHashMap<>();
        int pagesToFetch = 1;

        for (int page = 1; page <= pagesToFetch; page++) {
            URI uri = URI.create(baseUrl + "?apikey=" + apiKey + "&s=" + encoded + "&type=movie&page=" + page);
            JsonNode root = readJson(uri);

            if (!isSuccess(root)) {
                break;
            }

            if (page == 1) {
                pagesToFetch = Math.max(1, Math.min(3, (int) Math.ceil(parseTotalResults(root) / 10.0)));
            }

            JsonNode search = root.path("Search");
            if (search.isArray()) {
                for (JsonNode item : search) {
                    ExternalMovieResponse movie = ExternalMovieResponse.builder()
                            .externalId(text(item, "imdbID"))
                            .title(text(item, "Title"))
                            .releaseYear(text(item, "Year"))
                            .movieType(text(item, "Type"))
                            .imageUrl(normalizePoster(text(item, "Poster")))
                            .build();

                    if (StringUtils.hasText(movie.getExternalId())) {
                        moviesById.putIfAbsent(movie.getExternalId(), movie);
                    }
                }
            }
        }

        return new ArrayList<>(moviesById.values());
    }

    private boolean isSuccess(JsonNode root) {
        return root != null && "True".equalsIgnoreCase(root.path("Response").asText());
    }

    private int parseTotalResults(JsonNode root) {
        try {
            return Integer.parseInt(root.path("totalResults").asText("0"));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private ExternalMovieResponse toExternalMovieResponse(JsonNode root) {
        return ExternalMovieResponse.builder()
                .externalId(text(root, "imdbID"))
                .title(text(root, "Title"))
                .description(text(root, "Plot"))
                .genre(text(root, "Genre"))
                .director(text(root, "Director"))
                .movieType(text(root, "Type"))
                .releaseYear(text(root, "Year"))
                .releaseDate(parseReleaseDate(text(root, "Released"), text(root, "Year")))
                .durationMinutes(parseRuntime(text(root, "Runtime")))
                .imageUrl(normalizePoster(text(root, "Poster")))
                .build();
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        String text = value.isMissingNode() ? null : value.asText(null);
        if (text == null || text.isBlank() || "N/A".equalsIgnoreCase(text)) {
            return null;
        }
        return text.trim();
    }

    private String normalizePoster(String poster) {
        if (!StringUtils.hasText(poster) || "N/A".equalsIgnoreCase(poster.trim())) {
            return null;
        }
        return poster.trim();
    }

    private Integer parseRuntime(String runtime) {
        if (!StringUtils.hasText(runtime) || "N/A".equalsIgnoreCase(runtime.trim())) {
            return null;
        }
        String digits = runtime.replaceAll("[^0-9]", "");
        if (!StringUtils.hasText(digits)) {
            return null;
        }
        return Integer.parseInt(digits);
    }

    private LocalDate parseReleaseDate(String released, String year) {
        if (StringUtils.hasText(released) && !"N/A".equalsIgnoreCase(released.trim())) {
            try {
                return LocalDate.parse(released.trim(), OMDB_RELEASE_FORMAT);
            } catch (DateTimeParseException ignored) {
                // fallback abaixo
            }
        }

        if (StringUtils.hasText(year)) {
            try {
                return LocalDate.of(Integer.parseInt(year.replaceAll("[^0-9]", "")), 1, 1);
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    private List<ExternalMovieResponse> fallbackSearch(String title) {
        String normalized = title.toLowerCase(Locale.ROOT).trim();
        return fallbackCatalog().stream()
                .filter(movie -> movie.getTitle().toLowerCase(Locale.ROOT).contains(normalized)
                        || Optional.ofNullable(movie.getGenre()).orElse("").toLowerCase(Locale.ROOT).contains(normalized)
                        || Optional.ofNullable(movie.getDirector()).orElse("").toLowerCase(Locale.ROOT).contains(normalized))
                .collect(Collectors.toList());
    }

    private ExternalMovieResponse fallbackDetails(String externalId) {
        return fallbackCatalog().stream()
                .filter(movie -> movie.getExternalId().equalsIgnoreCase(externalId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Filme externo não encontrado: " + externalId));
    }

    private List<ExternalMovieResponse> fallbackCatalog() {
        return List.of(
                ExternalMovieResponse.builder().externalId("tt0133093").title("The Matrix").description("Um hacker descobre a verdadeira natureza da realidade.").genre("Action, Sci-Fi").director("Lana Wachowski, Lilly Wachowski").movieType("movie").releaseDate(LocalDate.of(1999, 3, 31)).releaseYear("1999").durationMinutes(136).imageUrl(fallbackPoster("The Matrix")).build(),
                ExternalMovieResponse.builder().externalId("tt1375666").title("Inception").description("Um ladrão entra nos sonhos para roubar segredos.").genre("Action, Sci-Fi").director("Christopher Nolan").movieType("movie").releaseDate(LocalDate.of(2010, 7, 16)).releaseYear("2010").durationMinutes(148).imageUrl(fallbackPoster("Inception")).build(),
                ExternalMovieResponse.builder().externalId("tt0111161").title("The Shawshank Redemption").description("A amizade e a esperança em uma prisão de segurança máxima.").genre("Drama").director("Frank Darabont").movieType("movie").releaseDate(LocalDate.of(1994, 9, 23)).releaseYear("1994").durationMinutes(142).imageUrl(fallbackPoster("The Shawshank Redemption")).build(),
                ExternalMovieResponse.builder().externalId("tt0468569").title("The Dark Knight").description("Batman enfrenta o Coringa em Gotham City.").genre("Action, Crime, Drama").director("Christopher Nolan").movieType("movie").releaseDate(LocalDate.of(2008, 7, 18)).releaseYear("2008").durationMinutes(152).imageUrl(fallbackPoster("The Dark Knight")).build(),
                ExternalMovieResponse.builder().externalId("tt0109830").title("Forrest Gump").description("A vida extraordinária de um homem comum.").genre("Drama, Romance").director("Robert Zemeckis").movieType("movie").releaseDate(LocalDate.of(1994, 7, 6)).releaseYear("1994").durationMinutes(142).imageUrl(fallbackPoster("Forrest Gump")).build(),
                ExternalMovieResponse.builder().externalId("tt0120338").title("Titanic").description("Um romance se desenrola a bordo do navio Titanic.").genre("Drama, Romance").director("James Cameron").movieType("movie").releaseDate(LocalDate.of(1997, 12, 19)).releaseYear("1997").durationMinutes(194).imageUrl(fallbackPoster("Titanic")).build(),
                ExternalMovieResponse.builder().externalId("tt0816692").title("Interstellar").description("Uma missão espacial busca salvar a humanidade.").genre("Adventure, Drama, Sci-Fi").director("Christopher Nolan").movieType("movie").releaseDate(LocalDate.of(2014, 11, 7)).releaseYear("2014").durationMinutes(169).imageUrl(fallbackPoster("Interstellar")).build(),
                ExternalMovieResponse.builder().externalId("tt0110912").title("Pulp Fiction").description("Histórias criminais interligadas em Los Angeles.").genre("Crime, Drama").director("Quentin Tarantino").movieType("movie").releaseDate(LocalDate.of(1994, 10, 14)).releaseYear("1994").durationMinutes(154).imageUrl(fallbackPoster("Pulp Fiction")).build(),
                ExternalMovieResponse.builder().externalId("tt0172495").title("Gladiator").description("Um general romano busca vingança e honra.").genre("Action, Adventure, Drama").director("Ridley Scott").movieType("movie").releaseDate(LocalDate.of(2000, 5, 5)).releaseYear("2000").durationMinutes(155).imageUrl(fallbackPoster("Gladiator")).build(),
                ExternalMovieResponse.builder().externalId("tt0137523").title("Fight Club").description("Um clube secreto desafia a vida moderna.").genre("Drama").director("David Fincher").movieType("movie").releaseDate(LocalDate.of(1999, 10, 15)).releaseYear("1999").durationMinutes(139).imageUrl(fallbackPoster("Fight Club")).build(),
                ExternalMovieResponse.builder().externalId("tt0108052").title("Schindler's List").description("A história real de um empresário que salvou centenas de vidas.").genre("Biography, Drama, History").director("Steven Spielberg").movieType("movie").releaseDate(LocalDate.of(1993, 12, 15)).releaseYear("1993").durationMinutes(195).imageUrl(fallbackPoster("Schindler's List")).build(),
                ExternalMovieResponse.builder().externalId("tt0114709").title("Toy Story").description("Brinquedos ganham vida quando ninguém está olhando.").genre("Animation, Adventure, Comedy").director("John Lasseter").movieType("movie").releaseDate(LocalDate.of(1995, 11, 22)).releaseYear("1995").durationMinutes(81).imageUrl(fallbackPoster("Toy Story")).build()
        );
    }

    private String fallbackPoster(String title) {
        String safeTitle = title.replace("&", "and").replace("'", "");
        String svg = """
                <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 600 900'>
                  <defs>
                    <linearGradient id='g' x1='0' x2='1' y1='0' y2='1'>
                      <stop offset='0%' stop-color='#111827'/>
                      <stop offset='100%' stop-color='#e50914'/>
                    </linearGradient>
                  </defs>
                  <rect width='600' height='900' rx='36' fill='url(#g)'/>
                  <rect x='48' y='48' width='504' height='804' rx='28' fill='rgba(255,255,255,0.06)'/>
                  <text x='300' y='405' text-anchor='middle' fill='white' font-family='Arial, Helvetica, sans-serif' font-size='42' font-weight='700'>LOCFLIX</text>
                  <text x='300' y='480' text-anchor='middle' fill='#f8fafc' font-family='Arial, Helvetica, sans-serif' font-size='30'>%s</text>
                  <text x='300' y='555' text-anchor='middle' fill='#fecaca' font-family='Arial, Helvetica, sans-serif' font-size='20'>Poster indisponível</text>
                </svg>
                """.formatted(safeTitle);
        return "data:image/svg+xml;charset=UTF-8," + URLEncoder.encode(svg, StandardCharsets.UTF_8);
    }
}

