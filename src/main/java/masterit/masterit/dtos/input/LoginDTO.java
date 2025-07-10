package masterit.masterit.dtos.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import masterit.masterit.entities.User;
import masterit.masterit.validations.annotations.UniqueValue;

@Data
public class LoginDTO {
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    private String password;
}
