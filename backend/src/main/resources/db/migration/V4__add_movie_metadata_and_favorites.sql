-- V4__add_movie_metadata_and_favorites.sql
-- Adiciona metadados extras para filmes importados e suporte a favoritos

ALTER TABLE movies
    ADD COLUMN IF NOT EXISTS director VARCHAR(255),
    ADD COLUMN IF NOT EXISTS movie_type VARCHAR(50);

CREATE TABLE IF NOT EXISTS user_favorites (
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_favorites_movie_id
    ON user_favorites(movie_id);

