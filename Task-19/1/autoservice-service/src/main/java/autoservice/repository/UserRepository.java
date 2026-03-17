package autoservice.repository;

import autoservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    void save(User user);
    void delete(User user);
}
