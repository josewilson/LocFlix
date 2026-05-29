-- V2__insert_initial_data.sql
-- Inserção de dados iniciais

-- Inserir Roles padrões
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrador do sistema'),
('USER', 'Usuário regular'),
('PREMIUM', 'Usuário premium com benefícios adicionais');

-- Inserir Categorias
INSERT INTO categories (name, description) VALUES
('Ação', 'Filmes de ação e aventura'),
('Romance', 'Filmes românticos'),
('Comédia', 'Filmes de comédia'),
('Drama', 'Filmes dramáticos'),
('Terror', 'Filmes de terror e suspense'),
('Ficção Científica', 'Filmes de ficção científica'),
('Animação', 'Filmes e séries animados'),
('Documentário', 'Documentários');

-- Inserir alguns Filmes de exemplo
INSERT INTO movies (title, description, genre, release_date, duration_minutes, price, available) VALUES
('Matrix', 'Descrição do filme Matrix', 'Ficção Científica', '1999-03-31', 136, 15.90, TRUE),
('Titanic', 'Descrição do filme Titanic', 'Romance', '1997-12-19', 194, 12.90, TRUE),
('O Iluminado', 'Descrição do filme O Iluminado', 'Terror', '1980-10-23', 146, 10.90, TRUE),
('Roterdã', 'Descrição do filme Roterdã', 'Ação', '2008-07-18', 148, 14.90, TRUE),
('Toy Story', 'Descrição do filme Toy Story', 'Animação', '1995-11-22', 81, 9.90, TRUE);

-- Relacionar filmes com categorias
INSERT INTO movie_categories (movie_id, category_id) VALUES
(1, 6), -- Matrix - Ficção Científica
(2, 2), -- Titanic - Romance
(3, 5), -- O Iluminado - Terror
(4, 1), -- Roterdã - Ação
(5, 7); -- Toy Story - Animação

