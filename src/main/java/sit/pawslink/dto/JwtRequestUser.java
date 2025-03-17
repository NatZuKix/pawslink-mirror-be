package sit.pawslink.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class JwtRequestUser {

    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
