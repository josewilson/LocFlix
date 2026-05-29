package com.locflix.config;

import com.locflix.dto.response.ExternalMovieResponse;
import com.locflix.entity.Movie;
import com.locflix.repository.MovieRepository;
import com.locflix.service.external.ExternalMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Preenche o campo {@code image_url} dos filmes do catálogo buscando o pôster no OMDB
 * pelo IMDb ID (mais confiável que busca textual, já que os títulos estão em português).
 *
 * <p>Executa uma única vez por filme: só processa registros sem imagem e apenas quando
 * a chave do OMDB ({@code OMDB_API_KEY}) está configurada. É idempotente — em reinícios,
 * filmes já preenchidos são ignorados.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovieImageBootstrap {

    private final MovieRepository movieRepository;
    private final ExternalMovieService externalMovieService;

    @Value("${movie.external.omdb.api-key:}")
    private String omdbApiKey;

    /** Mapa título (como cadastrado no seed) -> IMDb ID, para busca precisa no OMDB. */
    private static final Map<String, String> TITLE_TO_IMDB_ID = Map.ofEntries(
            // Seed inicial (V2/V3)
            Map.entry("Matrix", "tt0133093"),
            Map.entry("Titanic", "tt0120338"),
            Map.entry("O Iluminado", "tt0081505"),
            Map.entry("Toy Story", "tt0114709"),
            Map.entry("Duro de Matar", "tt0095016"),
            // Catálogo de demonstração (V6)
            Map.entry("Mad Max: Estrada da Fúria", "tt1392190"),
            Map.entry("John Wick", "tt2911666"),
            Map.entry("Gladiador", "tt0172495"),
            Map.entry("O Senhor dos Anéis: A Sociedade do Anel", "tt0120737"),
            Map.entry("Diário de uma Paixão", "tt0332280"),
            Map.entry("La La Land", "tt3783958"),
            Map.entry("Orgulho e Preconceito", "tt0414387"),
            Map.entry("Se Beber, Não Case!", "tt1119646"),
            Map.entry("Todo Mundo em Pânico", "tt0175142"),
            Map.entry("Clube da Luta", "tt0137523"),
            Map.entry("À Espera de um Milagre", "tt0120689"),
            Map.entry("Cidade de Deus", "tt0317248"),
            Map.entry("Forrest Gump", "tt0109830"),
            Map.entry("A Bruxa", "tt4263482"),
            Map.entry("Hereditário", "tt7784604"),
            Map.entry("Invocação do Mal", "tt1457767"),
            Map.entry("Interestelar", "tt0816692"),
            Map.entry("Blade Runner 2049", "tt1856101"),
            Map.entry("A Origem", "tt1375666"),
            Map.entry("Procurando Nemo", "tt0266543"),
            Map.entry("Divertida Mente", "tt2096673"),
            Map.entry("O Rei Leão", "tt0110357"),
            Map.entry("Nosso Planeta", "tt9253866"),
            Map.entry("O Dilema das Redes", "tt11464826"),
            // Catálogo ampliado (V7)
            Map.entry("Vingadores: Ultimato", "tt4154796"),
            Map.entry("O Cavaleiro das Trevas", "tt0468569"),
            Map.entry("O Poderoso Chefão", "tt0068646"),
            Map.entry("Pulp Fiction: Tempo de Violência", "tt0110912"),
            Map.entry("De Volta para o Futuro", "tt0088763"),
            Map.entry("Jurassic Park: O Parque dos Dinossauros", "tt0107290"),
            Map.entry("O Resgate do Soldado Ryan", "tt0120815"),
            Map.entry("Coringa", "tt7286456"),
            Map.entry("Parasita", "tt6751668"),
            Map.entry("Bastardos Inglórios", "tt0361748"),
            Map.entry("Whiplash: Em Busca da Perfeição", "tt2582802"),
            Map.entry("O Grande Hotel Budapeste", "tt2278388"),
            Map.entry("Corra!", "tt5052448"),
            Map.entry("It: A Coisa", "tt1396484"),
            Map.entry("Shrek", "tt0126029"),
            Map.entry("WALL·E", "tt0910970"),
            Map.entry("Toy Story 3", "tt0435761"),
            Map.entry("Antes do Amanhecer", "tt0112471"),
            Map.entry("A Marcha dos Pinguins", "tt0428803"),
            Map.entry("O Senhor dos Anéis: O Retorno do Rei", "tt0167260")
    );

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void backfillImages() {
        if (!StringUtils.hasText(omdbApiKey)) {
            log.info("Backfill de imagens ignorado: OMDB_API_KEY nao configurada.");
            return;
        }

        int updated = 0;
        for (Movie movie : movieRepository.findAll()) {
            if (StringUtils.hasText(movie.getImageUrl())) {
                continue;
            }

            String imageUrl = resolvePoster(movie.getTitle());
            if (imageUrl != null) {
                movie.setImageUrl(imageUrl);
                movieRepository.save(movie);
                updated++;
            }
        }

        log.info("Backfill de imagens concluido: {} filme(s) atualizado(s) via OMDB.", updated);
    }

    /**
     * Busca o pôster no OMDB: primeiro por IMDb ID (mapa curado); se o título não estiver
     * mapeado, tenta uma busca textual. Retorna apenas URLs http(s) válidas.
     */
    private String resolvePoster(String title) {
        try {
            String imdbId = TITLE_TO_IMDB_ID.get(title);
            if (imdbId != null) {
                ExternalMovieResponse details = externalMovieService.getByExternalId(imdbId);
                return httpPoster(details != null ? details.getImageUrl() : null);
            }

            return externalMovieService.search(title).stream()
                    .filter(m -> title.equalsIgnoreCase(m.getTitle()))
                    .map(ExternalMovieResponse::getImageUrl)
                    .map(this::httpPoster)
                    .filter(StringUtils::hasText)
                    .findFirst()
                    .orElse(null);
        } catch (Exception ex) {
            log.warn("Nao foi possivel obter poster para '{}': {}", title, ex.getMessage());
            return null;
        }
    }

    /** Aceita somente URLs http(s) (descarta os fallbacks em data:image/svg). */
    private String httpPoster(String url) {
        if (StringUtils.hasText(url) && url.startsWith("http")) {
            return url;
        }
        return null;
    }
}
