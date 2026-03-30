package autoservice.initializer;

import autoservice.model.Role;
import autoservice.model.User;
import autoservice.repository.RoleRepository;
import autoservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Component
@DependsOn("liquibaseInitializer")
public class UserDataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;

    public UserDataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_PASSWORD:}") String adminPassword
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
    }

    @PostConstruct
    @Transactional
    public void init() {
        boolean hasEnvFile = adminPassword != null && !adminPassword.isEmpty();

        if (!hasEnvFile) {
            createUserIfNotExists("test_admin", "1", "ROLE_ADMIN");
            createUserIfNotExists("test_manager", "2", "ROLE_MANAGER");
            createUserIfNotExists("test_master", "3", "ROLE_MASTER");
            log.warn("Используются пароли по умолчанию для тестовых пользователей! " +
                    "Задайте реальные пароли через переменные окружения " +
                    "ADMIN_PASSWORD, MANAGER_PASSWORD и MASTER_PASSWORD.");
        } else {
            createUserIfNotExists("admin", adminPassword, "ROLE_ADMIN");
        }
    }

    private void createUserIfNotExists(String username, String rawPassword, String roleName) {
        if (userRepository.existsByUsername(username)) {
            log.debug("Пользователь {} уже существует", username);
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена: " + roleName));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(Set.of(role));

        userRepository.save(user);
        log.info("Создан пользователь: {} с ролью {}", username, roleName);
    }

    @PreDestroy
    @Transactional
    public void cleanup() {
        userRepository.findByUsername("test_admin").ifPresent(userRepository::delete);
        userRepository.findByUsername("test_manager").ifPresent(userRepository::delete);
        userRepository.findByUsername("test_master").ifPresent(userRepository::delete);
        log.info("Тестовые пользователи удалены");
    }
}