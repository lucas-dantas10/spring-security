package tech.buildrun.springsecurity.controller.token;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tech.buildrun.springsecurity.dto.login.LoginRequest;
import tech.buildrun.springsecurity.dto.login.LoginResponse;
import tech.buildrun.springsecurity.entities.Role;
import tech.buildrun.springsecurity.repository.UserRepository;

@RestController
public class TokenController {
    
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenController(
        JwtEncoder jwtEncoder, 
        UserRepository userRepository,
        BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var user = userRepository.findByUsername(request.username());

        if (user.isEmpty() || !user.get().isLoginCorrect(request, passwordEncoder)) {
            throw new BadCredentialsException("user or password is invalid!");
        }
        
        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.get().getRoles()
            .stream()
            .map(Role::getName)
            .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet
            .builder()
            .issuer("mybackend")
            .subject(user.get().getUserId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .claim("scope", scopes)
            .build();
        
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }
}
