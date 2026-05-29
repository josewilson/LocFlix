package com.locflix.repository;

import com.locflix.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso aos dados de Categorias no banco de dados.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    /**
     * Busca categorias por nome (partial match).
     *
     * @param name Parte do nome a buscar
     * @return List de categorias encontradas
     */
    @Query("""
            SELECT c FROM Category c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY c.name
            """)
    List<Category> findByNameContaining(@Param("name") String name);

    /**
     * Verifica se uma categoria já existe pelo nome.
     *
     * @param name Nome da categoria
     * @return true se existe
     */
    boolean existsByName(String name);

    /**
     * Busca categorias mais populares (com mais filmes).
     *
     * @return List de categorias ordenadas por número de filmes
     */
    @Query("SELECT c FROM Category c LEFT JOIN c.movies m GROUP BY c.id, c.name ORDER BY COUNT(m) DESC, c.name")
    List<Category> findMostPopular();
}

