package masterit.masterit.dtos.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import masterit.masterit.entities.User;
import masterit.masterit.validations.annotations.UniqueValue;

@Data
public class RegisterDTO {
    @NotBlank
    @Size(max = 255)
    @UniqueValue(domainClass = User.class, fieldName = "name", message = "Name already in use")
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    @UniqueValue(domainClass = User.class, fieldName = "email", message = "Email already in use")
    private String email;

    @NotBlank
    private String password;

    private final String provider = "local";
}
