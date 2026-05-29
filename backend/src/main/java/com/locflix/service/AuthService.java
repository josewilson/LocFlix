package com.locflix.service;

import com.locflix.dto.request.CreateUserRequest;
import com.locflix.dto.request.LoginRequest;
import com.locflix.dto.response.LoginResponse;
import com.locflix.dto.response.UserResponse;

/**
 * Interface de serviço para autenticação e registro de usuários.
 */
public interface AuthService {

    /**
     * Autentica um usuário com email e senha.
     *
     * @param loginRequest Dados de login
     * @return Resposta com token JWT e dados do usuário
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * Registra um novo usuário no sistema.
     *
     * @param registerRequest Dados de registro
     * @return Dados do usuário criado
     */
    UserResponse register(CreateUserRequest registerRequest);
}

