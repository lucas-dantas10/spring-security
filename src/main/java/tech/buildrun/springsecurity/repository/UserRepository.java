package tech.buildrun.springsecurity.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tech.buildrun.springsecurity.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
}
