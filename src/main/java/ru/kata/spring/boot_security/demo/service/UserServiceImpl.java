package ru.kata.spring.boot_security.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public void create(User user) {
        userDao.addUser(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        userDao.updateUser(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userDao.removeUser(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userDao.getUserById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userDao.getAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userDao.getUserByUsername(username);
    }
}



