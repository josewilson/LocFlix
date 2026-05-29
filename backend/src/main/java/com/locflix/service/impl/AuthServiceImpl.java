package com.locflix.service.impl;

import com.locflix.constant.ApiConstant;
import com.locflix.dto.request.CreateUserRequest;
import com.locflix.dto.request.LoginRequest;
import com.locflix.dto.response.LoginResponse;
import com.locflix.dto.response.UserResponse;
import com.locflix.exception.BusinessLogicException;
import com.locflix.mapper.UserMapper;
import com.locflix.repository.RoleRepository;
import com.locflix.repository.UserRepository;
import com.locflix.security.JwtTokenProvider;
import com.locflix.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do serviço de autenticação e registro de usuários.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Autentica um usuário com email e senha.
     *
     * @param loginRequest Dados de login (email e senha)
     * @return LoginResponse com token JWT e dados do usuário
     * @throws BusinessLogicException se as credenciais forem inválidas
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Attempting to authenticate user: {}", loginRequest.getEmail());

        try {
            // Autentica as credenciais
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Gera o token JWT
            String token = jwtTokenProvider.generateToken(authentication);

            // Obtém os dados do usuário
            UserResponse userResponse = userMapper.toResponse(
                    (com.locflix.entity.User) authentication.getPrincipal()
            );

            log.info("User {} successfully authenticated", loginRequest.getEmail());

            return LoginResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .expiresIn(jwtExpiration)
                    .user(userResponse)
                    .build();

        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for user: {}", loginRequest.getEmail());
            throw ex;
        } catch (Exception ex) {
            log.warn("Authentication failed for user: {}", loginRequest.getEmail());
            throw new BusinessLogicException(ApiConstant.INVALID_CREDENTIALS);
        }
    }

    /**
     * Registra um novo usuário no sistema.
     *
     * @param registerRequest Dados de registro (email, senha, nome, etc)
     * @return UserResponse com os dados do usuário criado
     * @throws BusinessLogicException se o email já estiver registrado
     */
    @Override
    public UserResponse register(CreateUserRequest registerRequest) {
        log.info("Attempting to register new user: {}", registerRequest.getEmail());

        // Verifica se o email já existe
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: email already exists: {}", registerRequest.getEmail());
            throw new BusinessLogicException(ApiConstant.EMAIL_ALREADY_EXISTS);
        }

        try {
            // Cria a nova entidade User
            com.locflix.entity.User newUser = userMapper.toEntity(registerRequest);

            // Encripta a senha
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            // Adiciona o role padrão (USER)
            var userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new BusinessLogicException("Role USER não encontrada no sistema"));
            newUser.addRole(userRole);

            // Salva o novo usuário
            com.locflix.entity.User savedUser = userRepository.save(newUser);

            log.info("User successfully registered: {}", registerRequest.getEmail());

            return userMapper.toResponse(savedUser);

        } catch (BusinessLogicException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error during user registration", ex);
            throw new BusinessLogicException("Erro ao registrar novo usuário. Por favor, tente novamente.");
        }
    }
}

