package com.locflix.controller;

import com.locflix.constant.ApiConstant;
import com.locflix.dto.request.CreateUserRequest;
import com.locflix.dto.request.LoginRequest;
import com.locflix.dto.response.LoginResponse;
import com.locflix.dto.response.UserResponse;
import com.locflix.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para autenticação e registro de usuários.
 */
@RestController
@RequestMapping(ApiConstant.AUTH_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para login e registro de usuários")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint de login.
     *
     * @param loginRequest Credenciais do usuário (email e senha)
     * @return Token JWT e dados do usuário
     */
    @PostMapping("/login")
    @Operation(
            summary = "Fazer login",
            description = "Autentica um usuário e retorna um token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de registro de novo usuário.
     *
     * @param registerRequest Dados de registro (email, senha, nome, etc)
     * @return Dados do novo usuário criado
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Registra um novo usuário no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário registrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já registrado")
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest registerRequest) {
        UserResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

