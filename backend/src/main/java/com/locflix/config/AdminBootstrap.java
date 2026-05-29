package com.locflix.config;

import com.locflix.entity.Role;
import com.locflix.entity.User;
import com.locflix.exception.BusinessLogicException;
import com.locflix.repository.RoleRepository;
import com.locflix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

/**
 * Inicializa um usuário administrador opcionalmente configurado por ambiente.
 * Mantém o processo idempotente para evitar duplicação em reinicializações.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_ENABLED:true}")
    private boolean enabled;

    @Value("${ADMIN_EMAIL:}")
    private String email;

    @Value("${ADMIN_PASSWORD:}")
    private String password;

    @Value("${ADMIN_FIRST_NAME:Admin}")
    private String firstName;

    @Value("${ADMIN_LAST_NAME:Locflix}")
    private String lastName;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedAdminUser() {
        if (!enabled) {
            log.info("Admin bootstrap desabilitado por configuracao.");
            return;
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.warn("Admin bootstrap ignorado: ADMIN_EMAIL/ADMIN_PASSWORD nao configurados.");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new BusinessLogicException("Role ADMIN nao encontrada para bootstrap."));

        userRepository.findByEmail(email).ifPresentOrElse(existing -> {
            boolean updated = existing.getRoles().stream()
                    .noneMatch(role -> "ADMIN".equalsIgnoreCase(role.getName()));

            if (updated) {
                existing.addRole(adminRole);
                userRepository.save(existing);
                log.info("Role ADMIN associada ao usuario existente: {}", email);
            } else {
                log.info("Usuario admin ja existente: {}", email);
            }
        }, () -> {
            User admin = User.builder()
                    .email(email.trim())
                    .password(passwordEncoder.encode(password))
                    .firstName(firstName == null || firstName.isBlank() ? "Admin" : firstName.trim())
                    .lastName(lastName == null || lastName.isBlank() ? "Locflix" : lastName.trim())
                    .active(true)
                    .roles(new HashSet<>())
                    .build();

            admin.addRole(adminRole);
            userRepository.save(admin);
            log.info("Usuario administrador inicial criado com sucesso: {}", email);
        });
    }
}
