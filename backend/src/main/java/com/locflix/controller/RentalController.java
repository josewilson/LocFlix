package com.locflix.controller;

import com.locflix.constant.ApiConstant;
import com.locflix.dto.request.CreateRentalRequest;
import com.locflix.dto.response.RentalResponse;
import com.locflix.exception.ResourceNotFoundException;
import com.locflix.repository.UserRepository;
import com.locflix.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Locações.
 */
@RestController
@RequestMapping(ApiConstant.RENTALS_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Locações", description = "Endpoints para gerenciar locações de filmes")
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Criar nova locação", description = "Aluga um filme para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Locação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado"),
            @ApiResponse(responseCode = "409", description = "Filme indisponível ou já alugado")
    })
    public ResponseEntity<RentalResponse> create(
            @Valid @RequestBody CreateRentalRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        RentalResponse response = rentalService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Devolver filme", description = "Marca uma locação como devolvida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filme devolvido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Locação não encontrada"),
            @ApiResponse(responseCode = "409", description = "Locação não é do usuário ou já foi devolvida")
    })
    public ResponseEntity<RentalResponse> returnRental(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        RentalResponse response = rentalService.returnRental(userId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter locação por ID", description = "Retorna os dados de uma locação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locação encontrada"),
            @ApiResponse(responseCode = "404", description = "Locação não encontrada")
    })
    public ResponseEntity<RentalResponse> getById(@PathVariable Long id) {
        RentalResponse response = rentalService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar minhas locações", description = "Lista todas as locações do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de locações retornada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<RentalResponse>> getUserRentals(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Page<RentalResponse> response = rentalService.getUserRentals(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Minhas locações ativas", description = "Lista apenas as locações ativas do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de locações ativas retornada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<RentalResponse>> getUserActiveRentals(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<RentalResponse> response = rentalService.getUserActiveRentals(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Histórico de locações", description = "Lista as locações já devolvidas pelo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de locações retornado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<RentalResponse>> getUserCompletedRentals(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Page<RentalResponse> response = rentalService.getUserCompletedRentals(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Locações atrasadas", description = "Lista todas as locações atrasadas (apenas admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de locações atrasadas retornada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Page<RentalResponse>> getOverdueRentals(@PageableDefault(size = 20) Pageable pageable) {
        Page<RentalResponse> response = rentalService.getOverdueRentals(pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar locação", description = "Cancela uma locação ativa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locação cancelada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Locação não encontrada"),
            @ApiResponse(responseCode = "409", description = "Locação não é do usuário ou não pode ser cancelada")
    })
    public ResponseEntity<RentalResponse> cancel(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        RentalResponse response = rentalService.cancel(userId, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Extrai o ID do usuário autenticado a partir do e-mail (username) do principal.
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.findByEmail(userDetails.getUsername())
                    .map(user -> user.getId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "email", userDetails.getUsername()));
        }
        throw new ResourceNotFoundException("Usuário autenticado não encontrado no contexto de segurança");
    }
}

