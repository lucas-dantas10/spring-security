package tech.buildrun.springsecurity.controller.user;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import tech.buildrun.springsecurity.dto.user.CreateUserDto;
import tech.buildrun.springsecurity.dto.user.UserResponse;
import tech.buildrun.springsecurity.entities.Role;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.RoleRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

@RestController
public class UserController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(
        UserRepository userRepository, 
        BCryptPasswordEncoder passwordEncoder,
        RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @PostMapping("/user")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserDto request) {
        var role = roleRepository.findByName(Role.Values.BASIC.name());
        var user = userRepository.findByUsername(request.username());

        user.ifPresentOrElse(
            userExistent -> {
                System.out.println("usuario ja existe");
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
            },
            () -> {
                var userCreate = new User();
                userCreate.setUsername(request.username());
                userCreate.setPassword(passwordEncoder.encode(request.password()));
                userCreate.setRoles(Set.of(role));
                userRepository.save(userCreate);
            }
        );

        return ResponseEntity.ok(new UserResponse(request.username()));
    }   

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
