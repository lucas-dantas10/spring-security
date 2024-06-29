package tech.buildrun.springsecurity.dto.login;

public record LoginResponse(String token,Long expiresIn) {
}
