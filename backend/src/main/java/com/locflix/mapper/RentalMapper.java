package com.locflix.mapper;

import com.locflix.dto.response.RentalResponse;
import com.locflix.entity.Rental;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Rental entity e seus DTOs.
 */
@Component
public class RentalMapper {

    /**
     * Converte uma entidade Rental para RentalResponse.
     *
     * @param rental Entidade Rental
     * @return DTO de resposta
     */
    public RentalResponse toResponse(Rental rental) {
        return RentalResponse.builder()
                .id(rental.getId())
                .userId(rental.getUser().getId())
                .userName(rental.getUser().getFullName())
                .movieId(rental.getMovie().getId())
                .movieTitle(rental.getMovie().getTitle())
                .rentalDate(rental.getRentalDate())
                .dueDate(rental.getDueDate())
                .returnDate(rental.getReturnDate())
                .totalPrice(rental.getTotalPrice())
                .status(rental.getStatus().name())
                .isOverdue(rental.isOverdue())
                .daysRented(rental.getDaysRented())
                .createdAt(rental.getCreatedAt())
                .updatedAt(rental.getUpdatedAt())
                .build();
    }
}

