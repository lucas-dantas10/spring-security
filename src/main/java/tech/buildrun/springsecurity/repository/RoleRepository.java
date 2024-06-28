package tech.buildrun.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tech.buildrun.springsecurity.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
