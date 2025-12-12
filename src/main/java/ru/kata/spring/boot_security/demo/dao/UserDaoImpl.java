package ru.kata.spring.boot_security.demo.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import ru.kata.spring.boot_security.demo.model.User;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Override
    public void removeUser(Long id) {
        entityManager.createQuery("DELETE FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public List<User> getAllUsers() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
                User.class
        );
        return query.getResultList();
    }

    @Override
    public User getUserByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username",
                User.class
        );
        query.setParameter("username", username);

        List<User> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}



