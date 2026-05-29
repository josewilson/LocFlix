package com.locflix.repository;

import com.locflix.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para acesso aos dados de Papéis (Roles) no banco de dados.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Encontra um papel pelo nome.
     *
     * @param name Nome do papel
     * @return Optional com o papel se encontrado
     */
    Optional<Role> findByName(String name);
}

