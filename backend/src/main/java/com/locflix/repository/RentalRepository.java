package com.locflix.repository;

import com.locflix.constant.RentalStatus;
import com.locflix.entity.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para acesso aos dados de Locações no banco de dados.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    /**
     * Busca locações de um usuário (com paginação).
     *
     * @param userId ID do usuário
     * @param pageable Informações de paginação
     * @return Página de locações do usuário
     */
    Page<Rental> findByUserId(Long userId, Pageable pageable);

    /**
     * Busca locações ativas de um usuário.
     *
     * @param userId ID do usuário
     * @return List de locações ativas
     */
    @Query("""
            SELECT r FROM Rental r 
            WHERE r.user.id = :userId 
              AND r.status = 'ACTIVE'
            ORDER BY r.dueDate
            """)
    List<Rental> findActiveRentalsByUser(@Param("userId") Long userId);

    /**
     * Busca locações atrasadas (vencidas e não devolvidas).
     *
     * @param pageable Informações de paginação
     * @return Página de locações atrasadas
     */
    @Query("""
            SELECT r FROM Rental r 
            WHERE r.status = 'ACTIVE' 
              AND r.dueDate < CURRENT_TIMESTAMP
            ORDER BY r.dueDate
            """)
    Page<Rental> findOverdueRentals(Pageable pageable);

    /**
     * Busca locações completadas (devolvidas).
     *
     * @param userId ID do usuário
     * @param pageable Informações de paginação
     * @return Página de locações completadas
     */
    @Query("""
            SELECT r FROM Rental r 
            WHERE r.user.id = :userId 
              AND r.status = 'COMPLETED'
            ORDER BY r.returnDate DESC
            """)
    Page<Rental> findCompletedRentalsByUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Busca locações por status (com paginação).
     *
     * @param status Status da locação
     * @param pageable Informações de paginação
     * @return Página de locações com o status
     */
    Page<Rental> findByStatus(RentalStatus status, Pageable pageable);

    /**
     * Verifica se um usuário tem uma locação ativa do filme.
     *
     * @param userId ID do usuário
     * @param movieId ID do filme
     * @return true se tem locação ativa
     */
    @Query("""
            SELECT COUNT(r) > 0 FROM Rental r 
            WHERE r.user.id = :userId 
              AND r.movie.id = :movieId 
              AND r.status = 'ACTIVE'
            """)
    boolean hasActiveRental(@Param("userId") Long userId, @Param("movieId") Long movieId);

    /**
     * Busca locações para renovação (próximas ao vencimento).
     *
     * @param startDate Data/hora inicial da janela
     * @param endDate Data/hora final da janela
     * @param pageable Informações de paginação
     * @return Página de locações próximas ao vencimento
     */
    Page<Rental> findByStatusAndDueDateBetween(
            RentalStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );
}

