package tech.buildrun.springsecurity.controller.user;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
    
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

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
        var role = roleRepository.findByName(Role.Values.BASIC.getName());
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
}
