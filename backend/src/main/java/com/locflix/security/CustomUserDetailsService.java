package com.locflix.security;

import com.locflix.exception.ResourceNotFoundException;
import com.locflix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de UserDetails para integração com Spring Security.
 * Responsável por carregar os dados do usuário para autenticação.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carrega os dados do usuário pelo username (email neste caso).
     *
     * @param username Email do usuário
     * @return UserDetails do usuário
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);

        return userRepository.findByEmail(username)
                .map(user -> {
                    log.debug("User found: {}", username);
                    return user;
                })
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado com email: " + username);
                });
    }
}

