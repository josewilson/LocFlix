package com.locflix.constant;

/**
 * Constantes gerais da aplicação LOCFLIX.
 */
public final class ApiConstant {

    private ApiConstant() {
        // Previne instanciação
    }

    // API Versioning
    public static final String API_VERSION = "/api/v1";

    // Base Paths
    public static final String AUTH_BASE_PATH = API_VERSION + "/auth";
    public static final String USERS_BASE_PATH = API_VERSION + "/users";
    public static final String MOVIES_BASE_PATH = API_VERSION + "/movies";
    public static final String CATEGORIES_BASE_PATH = API_VERSION + "/categories";
    public static final String RENTALS_BASE_PATH = API_VERSION + "/rentals";
    public static final String FAVORITES_BASE_PATH = API_VERSION + "/favorites";
    public static final String VIDEOS_BASE_PATH = API_VERSION + "/videos";

    // Response Messages
    public static final String CREATED_SUCCESSFULLY = "Recurso criado com sucesso";
    public static final String UPDATED_SUCCESSFULLY = "Recurso atualizado com sucesso";
    public static final String DELETED_SUCCESSFULLY = "Recurso removido com sucesso";
    public static final String FOUND_SUCCESSFULLY = "Recurso encontrado com sucesso";

    // JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_BEARER_PREFIX = "Bearer ";

    // Pagination
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Validation Messages
    public static final String INVALID_EMAIL_FORMAT = "Formato de email inválido";
    public static final String PASSWORD_REQUIRED = "Senha é obrigatória";
    public static final String USERNAME_REQUIRED = "Nome de usuário é obrigatório";
    public static final String EMAIL_ALREADY_EXISTS = "Email já está registrado no sistema";
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String MOVIE_NOT_FOUND = "Filme não encontrado";
    public static final String CATEGORY_NOT_FOUND = "Categoria não encontrada";
    public static final String RENTAL_NOT_FOUND = "Locação não encontrada";

    // Business Rules
    public static final String INVALID_CREDENTIALS = "Credenciais inválidas";
    public static final String INVALID_TOKEN = "Token JWT inválido ou expirado";
    public static final String UNAUTHORIZED_ACCESS = "Acesso não autorizado";
    public static final String MOVIE_NOT_AVAILABLE = "Filme indisponível para locação";
}

