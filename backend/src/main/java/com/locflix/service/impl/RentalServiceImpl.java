package com.locflix.service.impl;

import com.locflix.constant.RentalStatus;
import com.locflix.dto.request.CreateRentalRequest;
import com.locflix.dto.response.RentalResponse;
import com.locflix.exception.BusinessLogicException;
import com.locflix.exception.ResourceNotFoundException;
import com.locflix.mapper.RentalMapper;
import com.locflix.repository.MovieRepository;
import com.locflix.repository.RentalRepository;
import com.locflix.repository.UserRepository;
import com.locflix.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de Locações.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RentalMapper rentalMapper;

    @Override
    public RentalResponse create(Long userId, CreateRentalRequest request) {
        log.info("Creating new rental for user: {} with movie ID: {}", userId, request.getMovieId());

        // Valida usuário
        var user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "ID", userId));

        // Valida filme
        var movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", request.getMovieId()));

        // Verifica se filme está disponível
        if (!movie.getAvailable()) {
            log.warn("Movie not available for rental: {}", request.getMovieId());
            throw new BusinessLogicException("Filme indisponível para locação");
        }

        // Verifica se usuário já tem uma locação ativa deste filme
        if (rentalRepository.hasActiveRental(userId, request.getMovieId())) {
            log.warn("User already has active rental for this movie: user {} movie {}", userId, request.getMovieId());
            throw new BusinessLogicException("Você já possui uma locação ativa deste filme");
        }

        // Calcula data de devolução
        LocalDateTime rentalDate = LocalDateTime.now();
        LocalDateTime dueDate = rentalDate.plusDays(request.getDaysToRent());

        // Calcula preço total
        BigDecimal totalPrice = movie.getPrice().multiply(BigDecimal.valueOf(request.getDaysToRent()));

        // Cria a locação
        var rental = com.locflix.entity.Rental.builder()
                .user(user)
                .movie(movie)
                .rentalDate(rentalDate)
                .dueDate(dueDate)
                .totalPrice(totalPrice)
                .status(RentalStatus.ACTIVE)
                .build();

        var savedRental = rentalRepository.save(rental);

        log.info("Rental created successfully with ID: {}", savedRental.getId());
        return rentalMapper.toResponse(savedRental);
    }

    @Override
    public RentalResponse returnRental(Long userId, Long rentalId) {
        log.info("Returning rental: {} for user: {}", rentalId, userId);

        var rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> ResourceNotFoundException.of("Locação", "ID", rentalId));

        // Verifica se a locação pertence ao usuário
        if (!rental.getUser().getId().equals(userId)) {
            log.warn("Rental does not belong to user: rental {} user {}", rentalId, userId);
            throw new BusinessLogicException("Esta locação não pertence ao usuário");
        }

        // Verifica se a locação já foi devolvida
        if (rental.getStatus() == RentalStatus.COMPLETED) {
            log.warn("Rental already returned: {}", rentalId);
            throw new BusinessLogicException("Esta locação já foi devolvida");
        }

        // Marca como devolvida
        rental.setReturnDate(LocalDateTime.now());
        rental.setStatus(RentalStatus.COMPLETED);

        var updatedRental = rentalRepository.save(rental);

        log.info("Rental returned successfully: {}", rentalId);
        return rentalMapper.toResponse(updatedRental);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalResponse getById(Long id) {
        log.debug("Fetching rental by ID: {}", id);

        var rental = rentalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Locação", "ID", id));

        return rentalMapper.toResponse(rental);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalResponse> getUserRentals(Long userId, Pageable pageable) {
        log.debug("Fetching rentals for user: {}", userId);

        // Valida usuário
        userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "ID", userId));

        var rentals = rentalRepository.findByUserId(userId, pageable);
        return rentals.map(rentalMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalResponse> getUserActiveRentals(Long userId) {
        log.debug("Fetching active rentals for user: {}", userId);

        // Valida usuário
        userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "ID", userId));

        var rentals = rentalRepository.findActiveRentalsByUser(userId);
        return rentals.stream()
                .map(rentalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalResponse> getUserCompletedRentals(Long userId, Pageable pageable) {
        log.debug("Fetching completed rentals for user: {}", userId);

        // Valida usuário
        userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Usuário", "ID", userId));

        var rentals = rentalRepository.findCompletedRentalsByUser(userId, pageable);
        return rentals.map(rentalMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalResponse> getOverdueRentals(Pageable pageable) {
        log.debug("Fetching overdue rentals");

        var rentals = rentalRepository.findOverdueRentals(pageable);
        return rentals.map(rentalMapper::toResponse);
    }

    @Override
    public RentalResponse cancel(Long userId, Long rentalId) {
        log.info("Cancelling rental: {} for user: {}", rentalId, userId);

        var rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> ResourceNotFoundException.of("Locação", "ID", rentalId));

        // Verifica se pertence ao usuário
        if (!rental.getUser().getId().equals(userId)) {
            log.warn("Rental does not belong to user: rental {} user {}", rentalId, userId);
            throw new BusinessLogicException("Esta locação não pertence ao usuário");
        }

        // Verifica se pode ser cancelada
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            log.warn("Can only cancel active rentals: {}", rentalId);
            throw new BusinessLogicException("Apenas locações ativas podem ser canceladas");
        }

        rental.setStatus(RentalStatus.CANCELLED);
        var cancelledRental = rentalRepository.save(rental);

        log.info("Rental cancelled successfully: {}", rentalId);
        return rentalMapper.toResponse(cancelledRental);
    }
}

