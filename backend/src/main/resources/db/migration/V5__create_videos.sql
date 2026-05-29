-- V5__create_videos.sql
-- Cria a tabela de vídeos para suporte a streaming HLS

CREATE TABLE IF NOT EXISTS videos (
    id             BIGSERIAL PRIMARY KEY,
    movie_id       BIGINT NOT NULL,
    original_path  VARCHAR(500),
    hls_path       VARCHAR(500),
    duration_seconds INTEGER,
    file_size_bytes  BIGINT,
    status         VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    error_message  VARCHAR(1000),
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_videos_movie FOREIGN KEY (movie_id)
        REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT chk_video_status CHECK (status IN ('PENDING','PROCESSING','READY','ERROR'))
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_videos_movie_id ON videos(movie_id);
CREATE INDEX IF NOT EXISTS idx_videos_status ON videos(status);
