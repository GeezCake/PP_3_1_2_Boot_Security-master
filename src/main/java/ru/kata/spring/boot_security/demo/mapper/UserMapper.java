package ru.kata.spring.boot_security.demo.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    UserResponseDto toDto(User user);

    List<UserResponseDto> toDtoList(List<User> users);

    /**
     * Maps incoming request DTO to entity.
     * Roles are resolved by id via RoleService.
     */
    @Mapping(target = "roles", source = "roleIds", qualifiedByName = "roleIdsToRoles")
    User toEntity(UserRequestDto dto, @Context RoleService roleService);

    @Named("roleIdsToRoles")
    default Set<Role> roleIdsToRoles(Set<Long> roleIds, @Context RoleService roleService) {
        Set<Role> roles = new HashSet<>();
        if (roleIds == null) {
            return roles;
        }
        for (Long roleId : roleIds) {
            if (roleId == null) {
                continue;
            }
            Role role = roleService.getRoleById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        return roles;
    }
}
