package ru.kata.spring.boot_security.demo.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;

import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);

    List<RoleDto> toDtoList(List<Role> roles);

    Set<RoleDto> toDtoSet(Set<Role> roles);
}
