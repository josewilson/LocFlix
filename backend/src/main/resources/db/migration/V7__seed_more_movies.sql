-- V7__seed_more_movies.sql
-- Amplia o catálogo de demonstração com mais 20 filmes famosos, distribuídos nas
-- 8 categorias. Mesmo padrão idempotente da V6 (insere por título se não existir;
-- vincula categorias resolvendo IDs por nome se o vínculo ainda não existir).

INSERT INTO movies (title, description, genre, director, movie_type, release_date, duration_minutes, price, available)
SELECT seed.title, seed.description, seed.genre, seed.director, seed.movie_type,
       seed.release_date, seed.duration_minutes, seed.price, seed.available
FROM (
    VALUES
        ('Vingadores: Ultimato', 'Os heróis remanescentes se unem para reverter o estrago causado por Thanos.', 'Ação', 'Anthony e Joe Russo', 'movie', '2019-04-25'::date, 181, 18.90, TRUE),
        ('O Cavaleiro das Trevas', 'Batman enfrenta o Coringa, que mergulha Gotham no caos.', 'Ação', 'Christopher Nolan', 'movie', '2008-07-18'::date, 152, 16.90, TRUE),
        ('O Poderoso Chefão', 'A saga da família Corleone e a sucessão no comando da máfia.', 'Drama', 'Francis Ford Coppola', 'movie', '1972-03-24'::date, 175, 14.90, TRUE),
        ('Pulp Fiction: Tempo de Violência', 'Histórias de crime se entrelaçam em Los Angeles.', 'Drama', 'Quentin Tarantino', 'movie', '1994-10-14'::date, 154, 13.90, TRUE),
        ('De Volta para o Futuro', 'Um adolescente viaja ao passado num DeLorean e altera a história dos pais.', 'Ficção Científica', 'Robert Zemeckis', 'movie', '1985-07-03'::date, 116, 12.90, TRUE),
        ('Jurassic Park: O Parque dos Dinossauros', 'Um parque com dinossauros clonados sai do controle.', 'Ação', 'Steven Spielberg', 'movie', '1993-06-11'::date, 127, 14.90, TRUE),
        ('O Resgate do Soldado Ryan', 'Um pelotão arrisca tudo para resgatar um soldado na Segunda Guerra.', 'Drama', 'Steven Spielberg', 'movie', '1998-07-24'::date, 169, 14.90, TRUE),
        ('Coringa', 'A origem sombria de um dos vilões mais famosos dos quadrinhos.', 'Drama', 'Todd Phillips', 'movie', '2019-10-04'::date, 122, 15.90, TRUE),
        ('Parasita', 'Uma família pobre se infiltra na rotina de uma família rica.', 'Drama', 'Bong Joon-ho', 'movie', '2019-05-30'::date, 132, 15.90, TRUE),
        ('Bastardos Inglórios', 'Um grupo planeja eliminar líderes nazistas durante a guerra.', 'Ação', 'Quentin Tarantino', 'movie', '2009-08-21'::date, 153, 14.90, TRUE),
        ('Whiplash: Em Busca da Perfeição', 'Um jovem baterista é levado ao limite por um maestro implacável.', 'Drama', 'Damien Chazelle', 'movie', '2014-10-10'::date, 106, 13.90, TRUE),
        ('O Grande Hotel Budapeste', 'As aventuras de um lendário concierge e seu protegido.', 'Comédia', 'Wes Anderson', 'movie', '2014-03-28'::date, 99, 12.90, TRUE),
        ('Corra!', 'Um rapaz descobre um segredo aterrorizante ao conhecer a família da namorada.', 'Terror', 'Jordan Peele', 'movie', '2017-02-24'::date, 104, 12.90, TRUE),
        ('It: A Coisa', 'Crianças enfrentam um palhaço maligno que aterroriza sua cidade.', 'Terror', 'Andy Muschietti', 'movie', '2017-09-08'::date, 135, 13.90, TRUE),
        ('Shrek', 'Um ogro mal-humorado parte numa missão para recuperar seu pântano.', 'Animação', 'Andrew Adamson', 'movie', '2001-05-18'::date, 90, 10.90, TRUE),
        ('WALL·E', 'Um robô solitário na Terra do futuro encontra um propósito e um amor.', 'Animação', 'Andrew Stanton', 'movie', '2008-06-27'::date, 98, 10.90, TRUE),
        ('Toy Story 3', 'Os brinquedos enfrentam um futuro incerto quando Andy cresce.', 'Animação', 'Lee Unkrich', 'movie', '2010-06-18'::date, 103, 10.90, TRUE),
        ('Antes do Amanhecer', 'Dois estranhos passam uma noite inesquecível caminhando por Viena.', 'Romance', 'Richard Linklater', 'movie', '1995-01-27'::date, 101, 11.90, TRUE),
        ('A Marcha dos Pinguins', 'A jornada dos pinguins-imperadores pela sobrevivência na Antártica.', 'Documentário', 'Luc Jacquet', 'movie', '2005-01-26'::date, 80, 8.90, TRUE),
        ('O Senhor dos Anéis: O Retorno do Rei', 'A batalha final pela Terra-média e o destino do Um Anel.', 'Ação', 'Peter Jackson', 'movie', '2003-12-17'::date, 201, 17.90, TRUE)
) AS seed(title, description, genre, director, movie_type, release_date, duration_minutes, price, available)
WHERE NOT EXISTS (
    SELECT 1 FROM movies m WHERE m.title = seed.title
);

INSERT INTO movie_categories (movie_id, category_id)
SELECT m.id, c.id
FROM (
    VALUES
        ('Vingadores: Ultimato', 'Ação'),
        ('Vingadores: Ultimato', 'Ficção Científica'),
        ('O Cavaleiro das Trevas', 'Ação'),
        ('O Cavaleiro das Trevas', 'Drama'),
        ('O Poderoso Chefão', 'Drama'),
        ('Pulp Fiction: Tempo de Violência', 'Drama'),
        ('De Volta para o Futuro', 'Ficção Científica'),
        ('De Volta para o Futuro', 'Comédia'),
        ('Jurassic Park: O Parque dos Dinossauros', 'Ação'),
        ('Jurassic Park: O Parque dos Dinossauros', 'Ficção Científica'),
        ('O Resgate do Soldado Ryan', 'Drama'),
        ('O Resgate do Soldado Ryan', 'Ação'),
        ('Coringa', 'Drama'),
        ('Parasita', 'Drama'),
        ('Bastardos Inglórios', 'Ação'),
        ('Bastardos Inglórios', 'Drama'),
        ('Whiplash: Em Busca da Perfeição', 'Drama'),
        ('O Grande Hotel Budapeste', 'Comédia'),
        ('Corra!', 'Terror'),
        ('It: A Coisa', 'Terror'),
        ('Shrek', 'Animação'),
        ('Shrek', 'Comédia'),
        ('WALL·E', 'Animação'),
        ('WALL·E', 'Ficção Científica'),
        ('Toy Story 3', 'Animação'),
        ('Antes do Amanhecer', 'Romance'),
        ('A Marcha dos Pinguins', 'Documentário'),
        ('O Senhor dos Anéis: O Retorno do Rei', 'Ação'),
        ('O Senhor dos Anéis: O Retorno do Rei', 'Drama')
) AS link(movie_title, category_name)
JOIN movies m ON m.title = link.movie_title
JOIN categories c ON c.name = link.category_name
WHERE NOT EXISTS (
    SELECT 1 FROM movie_categories mc WHERE mc.movie_id = m.id AND mc.category_id = c.id
);
