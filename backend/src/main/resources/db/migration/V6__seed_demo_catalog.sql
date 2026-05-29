-- V6__seed_demo_catalog.sql
-- Catálogo de demonstração: insere filmes variados distribuídos nas 8 categorias
-- existentes. Idempotente: cada filme só é inserido se ainda não existir (por título),
-- e cada vínculo filme-categoria só é criado se ainda não existir.

-- 1) Filmes (insere apenas os que ainda não existem, comparando pelo título)
INSERT INTO movies (title, description, genre, director, movie_type, release_date, duration_minutes, price, available)
SELECT seed.title, seed.description, seed.genre, seed.director, seed.movie_type,
       seed.release_date, seed.duration_minutes, seed.price, seed.available
FROM (
    VALUES
        ('Mad Max: Estrada da Fúria', 'Em um deserto pós-apocalíptico, Max ajuda uma rebelde a fugir de um tirano.', 'Ação', 'George Miller', 'movie', '2015-05-15'::date, 120, 16.90, TRUE),
        ('John Wick', 'Um ex-assassino de aluguel volta à ativa para vingar a morte de seu cão.', 'Ação', 'Chad Stahelski', 'movie', '2014-10-24'::date, 101, 14.90, TRUE),
        ('Gladiador', 'Um general romano traído busca vingança como gladiador na arena.', 'Ação', 'Ridley Scott', 'movie', '2000-05-05'::date, 155, 13.90, TRUE),
        ('O Senhor dos Anéis: A Sociedade do Anel', 'Um hobbit parte numa jornada para destruir um anel poderoso.', 'Ação', 'Peter Jackson', 'movie', '2001-12-19'::date, 178, 17.90, TRUE),

        ('Diário de uma Paixão', 'Um casal vive um amor intenso apesar das diferenças sociais.', 'Romance', 'Nick Cassavetes', 'movie', '2004-06-25'::date, 123, 11.90, TRUE),
        ('La La Land', 'Um músico e uma atriz se apaixonam enquanto perseguem seus sonhos.', 'Romance', 'Damien Chazelle', 'movie', '2016-12-09'::date, 128, 15.90, TRUE),
        ('Orgulho e Preconceito', 'Elizabeth Bennet enfrenta orgulho e mal-entendidos no amor.', 'Romance', 'Joe Wright', 'movie', '2005-09-16'::date, 129, 12.90, TRUE),

        ('Que Bom Te Ver Viva', 'Comédia leve sobre reencontros e segundas chances.', 'Comédia', 'Lúcia Murat', 'movie', '2019-03-21'::date, 98, 9.90, TRUE),
        ('Se Beber, Não Case!', 'Quatro amigos perdem o noivo após uma despedida caótica em Las Vegas.', 'Comédia', 'Todd Phillips', 'movie', '2009-06-05'::date, 100, 10.90, TRUE),
        ('Todo Mundo em Pânico', 'Paródia escrachada dos filmes de terror adolescente.', 'Comédia', 'Keenen Ivory Wayans', 'movie', '2000-07-07'::date, 88, 8.90, TRUE),

        ('Clube da Luta', 'Um homem insone e um vendedor de sabão criam um clube secreto.', 'Drama', 'David Fincher', 'movie', '1999-10-15'::date, 139, 14.90, TRUE),
        ('À Espera de um Milagre', 'Um guarda de prisão descobre o dom sobrenatural de um detento.', 'Drama', 'Frank Darabont', 'movie', '1999-12-10'::date, 189, 13.90, TRUE),
        ('Cidade de Deus', 'A ascensão do crime organizado numa favela do Rio de Janeiro.', 'Drama', 'Fernando Meirelles', 'movie', '2002-08-30'::date, 130, 12.90, TRUE),
        ('Forrest Gump', 'Um homem simples testemunha momentos marcantes da história dos EUA.', 'Drama', 'Robert Zemeckis', 'movie', '1994-07-06'::date, 142, 13.90, TRUE),

        ('A Bruxa', 'Uma família puritana enfrenta forças sombrias numa floresta.', 'Terror', 'Robert Eggers', 'movie', '2015-01-27'::date, 92, 11.90, TRUE),
        ('Hereditário', 'Após a morte da avó, uma família é assombrada por um destino sinistro.', 'Terror', 'Ari Aster', 'movie', '2018-06-08'::date, 127, 13.90, TRUE),
        ('Invocação do Mal', 'Investigadores paranormais ajudam uma família aterrorizada.', 'Terror', 'James Wan', 'movie', '2013-07-19'::date, 112, 12.90, TRUE),

        ('Interestelar', 'Exploradores cruzam um buraco de minhoca em busca de um novo lar.', 'Ficção Científica', 'Christopher Nolan', 'movie', '2014-11-06'::date, 169, 17.90, TRUE),
        ('Blade Runner 2049', 'Um caçador de replicantes descobre um segredo que pode mudar tudo.', 'Ficção Científica', 'Denis Villeneuve', 'movie', '2017-10-06'::date, 164, 16.90, TRUE),
        ('A Origem', 'Um ladrão invade sonhos para implantar uma ideia na mente de um alvo.', 'Ficção Científica', 'Christopher Nolan', 'movie', '2010-07-16'::date, 148, 16.90, TRUE),

        ('Procurando Nemo', 'Um peixe-palhaço atravessa o oceano para encontrar o filho.', 'Animação', 'Andrew Stanton', 'movie', '2003-05-30'::date, 100, 9.90, TRUE),
        ('Divertida Mente', 'As emoções de uma garota ganham vida em sua mente.', 'Animação', 'Pete Docter', 'movie', '2015-06-19'::date, 95, 10.90, TRUE),
        ('O Rei Leão', 'Um jovem leão precisa assumir seu lugar como rei.', 'Animação', 'Roger Allers', 'movie', '1994-06-24'::date, 88, 9.90, TRUE),

        ('Nosso Planeta', 'Documentário sobre a beleza e a fragilidade dos ecossistemas da Terra.', 'Documentário', 'Alastair Fothergill', 'movie', '2019-04-05'::date, 50, 7.90, TRUE),
        ('O Dilema das Redes', 'Especialistas revelam os efeitos das redes sociais na sociedade.', 'Documentário', 'Jeff Orlowski', 'movie', '2020-01-26'::date, 94, 8.90, TRUE)
) AS seed(title, description, genre, director, movie_type, release_date, duration_minutes, price, available)
WHERE NOT EXISTS (
    SELECT 1 FROM movies m WHERE m.title = seed.title
);

-- 2) Vínculos filme-categoria (idempotente, resolvendo IDs por nome)
INSERT INTO movie_categories (movie_id, category_id)
SELECT m.id, c.id
FROM (
    VALUES
        ('Mad Max: Estrada da Fúria', 'Ação'),
        ('Mad Max: Estrada da Fúria', 'Ficção Científica'),
        ('John Wick', 'Ação'),
        ('Gladiador', 'Ação'),
        ('Gladiador', 'Drama'),
        ('O Senhor dos Anéis: A Sociedade do Anel', 'Ação'),
        ('Diário de uma Paixão', 'Romance'),
        ('Diário de uma Paixão', 'Drama'),
        ('La La Land', 'Romance'),
        ('La La Land', 'Comédia'),
        ('Orgulho e Preconceito', 'Romance'),
        ('Que Bom Te Ver Viva', 'Comédia'),
        ('Se Beber, Não Case!', 'Comédia'),
        ('Todo Mundo em Pânico', 'Comédia'),
        ('Todo Mundo em Pânico', 'Terror'),
        ('Clube da Luta', 'Drama'),
        ('À Espera de um Milagre', 'Drama'),
        ('Cidade de Deus', 'Drama'),
        ('Forrest Gump', 'Drama'),
        ('Forrest Gump', 'Romance'),
        ('A Bruxa', 'Terror'),
        ('Hereditário', 'Terror'),
        ('Invocação do Mal', 'Terror'),
        ('Interestelar', 'Ficção Científica'),
        ('Interestelar', 'Drama'),
        ('Blade Runner 2049', 'Ficção Científica'),
        ('A Origem', 'Ficção Científica'),
        ('A Origem', 'Ação'),
        ('Procurando Nemo', 'Animação'),
        ('Divertida Mente', 'Animação'),
        ('O Rei Leão', 'Animação'),
        ('Nosso Planeta', 'Documentário'),
        ('O Dilema das Redes', 'Documentário')
) AS link(movie_title, category_name)
JOIN movies m ON m.title = link.movie_title
JOIN categories c ON c.name = link.category_name
WHERE NOT EXISTS (
    SELECT 1 FROM movie_categories mc WHERE mc.movie_id = m.id AND mc.category_id = c.id
);
