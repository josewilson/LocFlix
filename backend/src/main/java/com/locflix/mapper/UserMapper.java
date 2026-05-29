package com.locflix.mapper;

import com.locflix.dto.request.CreateUserRequest;
import com.locflix.dto.response.UserResponse;
import com.locflix.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para conversão entre User entity e seus DTOs.
 */
@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Converte um CreateUserRequest para uma entidade User.
     *
     * @param request Requisição de criação de usuário
     * @return Entidade User
     */
    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .email(request.getEmail().toLowerCase())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    /**
     * Converte uma entidade User para UserResponse.
     *
     * @param user Entidade User
     * @return DTO de resposta
     */
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .active(user.getActive())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().toUpperCase())
                        .collect(Collectors.toSet())
                )
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

