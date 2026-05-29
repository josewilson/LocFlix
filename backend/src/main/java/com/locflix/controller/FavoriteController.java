package com.locflix.controller;

import com.locflix.constant.ApiConstant;
import com.locflix.dto.response.MovieResponse;
import com.locflix.exception.ResourceNotFoundException;
import com.locflix.repository.UserRepository;
import com.locflix.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstant.FAVORITES_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Endpoints para gerenciar filmes favoritos do usuário")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Retorna os filmes favoritos do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de favoritos retornada com sucesso")
    public ResponseEntity<List<MovieResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(favoriteService.listFavorites(getUserId(authentication)));
    }

    @PostMapping("/{movieId}")
    @Operation(summary = "Adicionar favorito", description = "Marca um filme como favorito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filme favoritado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    })
    public ResponseEntity<MovieResponse> add(@PathVariable Long movieId, Authentication authentication) {
        return ResponseEntity.ok(favoriteService.addFavorite(getUserId(authentication), movieId));
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Remover favorito", description = "Remove um filme da lista de favoritos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filme removido dos favoritos"),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    })
    public ResponseEntity<MovieResponse> remove(@PathVariable Long movieId, Authentication authentication) {
        return ResponseEntity.ok(favoriteService.removeFavorite(getUserId(authentication), movieId));
    }

    private Long getUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .map(com.locflix.entity.User::getId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "email", userDetails.getUsername()));
        }
        throw new ResourceNotFoundException("Usuário autenticado não encontrado no contexto de segurança");
    }
}

