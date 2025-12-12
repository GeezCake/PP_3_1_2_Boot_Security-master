package ru.kata.spring.boot_security.demo.init;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

@Component
public class DataInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {
        Long usersCount = entityManager
                .createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();

        if (usersCount != 0L) {
            return;
        }

        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleUser = new Role("ROLE_USER");
        entityManager.persist(roleAdmin);
        entityManager.persist(roleUser);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setFirstName("Admin");
        admin.setLastName("Adminov");
        admin.setEmail("admin@example.com");
        admin.getRoles().add(roleAdmin);
        admin.getRoles().add(roleUser);
        entityManager.persist(admin);

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user"));
        user.setFirstName("User");
        user.setLastName("Userov");
        user.setEmail("user@example.com");
        user.getRoles().add(roleUser);
        entityManager.persist(user);
    }
}

