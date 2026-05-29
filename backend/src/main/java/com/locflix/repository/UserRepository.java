package com.locflix.repository;

import com.locflix.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para acesso aos dados de Usuários no banco de dados.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Encontra um usuário pelo email.
     *
     * @param email Email do usuário
     * @return Optional com o usuário se encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se um email já existe no banco.
     *
     * @param email Email a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Encontra um usuário ativo pelo email.
     *
     * @param email Email do usuário
     * @return Optional com o usuário ativo se encontrado
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findActiveByEmail(@Param("email") String email);

    /**
     * Busca usuários pelo nome (first name ou last name).
     *
     * @param name Nome a buscar
     * @return List de usuários encontrados
     */
    @Query("""
            SELECT u FROM User u 
            WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
               OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY u.firstName
            """)
    java.util.List<User> findByNameContaining(@Param("name") String name);
}

