-- V3__seed_idempotent_data.sql
-- Seed complementar idempotente para ambientes já provisionados

INSERT INTO roles (name, description) VALUES
	('ADMIN', 'Administrador do sistema'),
	('USER', 'Usuário regular'),
	('PREMIUM', 'Usuário premium com benefícios adicionais')
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, description) VALUES
	('Ação', 'Filmes de ação e aventura'),
	('Romance', 'Filmes românticos'),
	('Comédia', 'Filmes de comédia'),
	('Drama', 'Filmes dramáticos'),
	('Terror', 'Filmes de terror e suspense'),
	('Ficção Científica', 'Filmes de ficção científica'),
	('Animação', 'Filmes e séries animados'),
	('Documentário', 'Documentários')
ON CONFLICT (name) DO NOTHING;

INSERT INTO movies (title, description, genre, release_date, duration_minutes, price, available)
SELECT seed.title,
	   seed.description,
	   seed.genre,
	   seed.release_date,
	   seed.duration_minutes,
	   seed.price,
	   seed.available
FROM (
	VALUES
		('Matrix', 'Descrição do filme Matrix', 'Ficção Científica', '1999-03-31'::date, 136, 15.90, TRUE),
		('Titanic', 'Descrição do filme Titanic', 'Romance', '1997-12-19'::date, 194, 12.90, TRUE),
		('O Iluminado', 'Descrição do filme O Iluminado', 'Terror', '1980-10-23'::date, 146, 10.90, TRUE),
		('Roterdã', 'Descrição do filme Roterdã', 'Ação', '2008-07-18'::date, 148, 14.90, TRUE),
		('Toy Story', 'Descrição do filme Toy Story', 'Animação', '1995-11-22'::date, 81, 9.90, TRUE)
) AS seed(title, description, genre, release_date, duration_minutes, price, available)
WHERE NOT EXISTS (
	SELECT 1
	FROM movies m
	WHERE m.title = seed.title
);

INSERT INTO movie_categories (movie_id, category_id)
SELECT m.id, c.id
FROM movies m
JOIN categories c ON (
	(m.title = 'Matrix' AND c.name = 'Ficção Científica') OR
	(m.title = 'Titanic' AND c.name = 'Romance') OR
	(m.title = 'O Iluminado' AND c.name = 'Terror') OR
	(m.title = 'Roterdã' AND c.name = 'Ação') OR
	(m.title = 'Toy Story' AND c.name = 'Animação')
)
ON CONFLICT DO NOTHING;

