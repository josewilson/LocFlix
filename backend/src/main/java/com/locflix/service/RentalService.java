package com.locflix.service;

import com.locflix.dto.request.CreateRentalRequest;
import com.locflix.dto.response.RentalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface de serviço para gerenciamento de Locações.
 */
public interface RentalService {

    /**
     * Cria uma nova locação de filme para o usuário autenticado.
     *
     * @param userId ID do usuário
     * @param request Dados da locação (filme e duração)
     * @return Locação criada
     */
    RentalResponse create(Long userId, CreateRentalRequest request);

    /**
     * Devolve um filme (marca a locação como concluída).
     *
     * @param userId ID do usuário
     * @param rentalId ID da locação
     * @return Locação atualizada
     */
    RentalResponse returnRental(Long userId, Long rentalId);

    /**
     * Obtém uma locação por ID.
     *
     * @param id ID da locação
     * @return Locação encontrada
     */
    RentalResponse getById(Long id);

    /**
     * Lista locações de um usuário (com paginação).
     *
     * @param userId ID do usuário
     * @param pageable Informações de paginação
     * @return Página de locações do usuário
     */
    Page<RentalResponse> getUserRentals(Long userId, Pageable pageable);

    /**
     * Lista locações ativas do usuário.
     *
     * @param userId ID do usuário
     * @return Lista de locações ativas
     */
    List<RentalResponse> getUserActiveRentals(Long userId);

    /**
     * Lista locações completadas do usuário.
     *
     * @param userId ID do usuário
     * @param pageable Informações de paginação
     * @return Página de locações completadas
     */
    Page<RentalResponse> getUserCompletedRentals(Long userId, Pageable pageable);

    /**
     * Lista locações atrasadas (admin).
     *
     * @param pageable Informações de paginação
     * @return Página de locações atrasadas
     */
    Page<RentalResponse> getOverdueRentals(Pageable pageable);

    /**
     * Cancela uma locação.
     *
     * @param userId ID do usuário
     * @param rentalId ID da locação
     * @return Locação cancelada
     */
    RentalResponse cancel(Long userId, Long rentalId);
}

