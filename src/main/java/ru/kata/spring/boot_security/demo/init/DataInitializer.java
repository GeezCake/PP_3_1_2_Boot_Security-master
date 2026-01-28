package ru.kata.spring.boot_security.demo.init;

import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Component
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;

    public DataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void init() {
        if (!userService.getAllUsers().isEmpty()) {
            return;
        }

        Role roleAdmin = getOrCreateRole("ROLE_ADMIN");
        Role roleUser = getOrCreateRole("ROLE_USER");

        User admin = new User();
        admin.setUsername("admin@mail.ru");
        admin.setPassword("admin");
        admin.setFirstName("admin");
        admin.setLastName("admin");
        admin.setAge((byte) 35);
        admin.setEmail("admin@mail.ru");
        admin.setRoles(new HashSet<>());
        admin.getRoles().add(roleAdmin);
        admin.getRoles().add(roleUser);
        userService.createUser(admin);

        User user = new User();
        user.setUsername("user@mail.ru");
        user.setPassword("user");
        user.setFirstName("user");
        user.setLastName("user");
        user.setAge((byte) 30);
        user.setEmail("user@mail.ru");
        user.setRoles(new HashSet<>());
        user.getRoles().add(roleUser);
        userService.createUser(user);
    }

    private Role getOrCreateRole(String name) {
        Role role = roleService.getRoleByName(name);
        if (role != null) {
            return role;
        }

        roleService.createRole(new Role(name));
        return roleService.getRoleByName(name);
    }
}
