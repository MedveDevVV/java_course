package autoservice.repository.impl;

import autoservice.model.Role;
import autoservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaRoleRepository implements RoleRepository {
    private final SessionFactory sessionFactory;
    @Override
    public Optional<Role> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }
}
