-- V1__initial_schema.sql
-- Criação do schema inicial do LOCFLIX

-- Tabela de Roles (Papéis de usuário)
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_name UNIQUE (name)
);

-- Tabela de Usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT chk_user_email_format CHECK (email LIKE '%@%.%')
);

-- Tabela de Categorias
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_category_name UNIQUE (name)
);

-- Tabela de Filmes
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100),
    release_date DATE,
    duration_minutes INTEGER CHECK (duration_minutes > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    image_url VARCHAR(500),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Relacionamento entre Filmes e Categorias
CREATE TABLE movie_categories (
    movie_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, category_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Tabela de Locações
CREATE TABLE rentals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    rental_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    total_price DECIMAL(10, 2) NOT NULL CHECK (total_price >= 0),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rental_dates CHECK (due_date > rental_date)
);

CREATE UNIQUE INDEX idx_unique_active_rental
    ON rentals(user_id, movie_id)
    WHERE status = 'ACTIVE';

-- Tabela de Relacionamento entre Usuários e Roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índices para melhorar performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_rentals_user_id ON rentals(user_id);
CREATE INDEX idx_rentals_movie_id ON rentals(movie_id);
CREATE INDEX idx_rentals_status ON rentals(status);
CREATE INDEX idx_rentals_return_date ON rentals(return_date);
CREATE INDEX idx_movies_title ON movies(title);
CREATE INDEX idx_movies_available ON movies(available);
CREATE INDEX idx_movie_categories_category_id ON movie_categories(category_id);

-- Comentários das tabelas
COMMENT ON TABLE roles IS 'Papéis de usuário do sistema';
COMMENT ON TABLE users IS 'Usuários da plataforma';
COMMENT ON TABLE categories IS 'Categorias de filmes';
COMMENT ON TABLE movies IS 'Catálogo de filmes';
COMMENT ON TABLE movie_categories IS 'Relacionamento entre filmes e categorias';
COMMENT ON TABLE rentals IS 'Histórico de locações';
COMMENT ON TABLE user_roles IS 'Relacionamento entre usuários e seus papéis';

