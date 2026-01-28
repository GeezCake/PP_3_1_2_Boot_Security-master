package ru.kata.spring.boot_security.demo.controller.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.mapper.RoleMapper;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public AdminRestController(UserService userService,
                               RoleService roleService,
                               UserMapper userMapper,
                               RoleMapper roleMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        return userMapper.toDtoList(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
        }
        return userMapper.toDto(user);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto dto) {
        User user = userMapper.toEntity(dto, roleService);
        user.setId(null);
        userService.createUser(user);
        // после persist id уже установлен
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDto(userService.getUserByUsername(user.getUsername())));
    }

    @PutMapping("/users")
    public UserResponseDto updateUser(@RequestBody UserRequestDto dto) {
        if (dto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id must be provided for update");
        }

        User existing = userService.getUserById(dto.getId());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + dto.getId());
        }

        User user = userMapper.toEntity(dto, roleService);
        userService.updateUser(user);
        User updated = userService.getUserById(dto.getId());
        return userMapper.toDto(updated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User existing = userService.getUserById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public List<RoleDto> getAllRoles() {
        return roleMapper.toDtoList(roleService.getAllRoles());
    }
}
