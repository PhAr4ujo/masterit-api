package masterit.masterit.dtos.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import masterit.masterit.entities.User;
import masterit.masterit.validations.annotations.PasswordMatches;
import masterit.masterit.validations.annotations.UniqueValue;

@PasswordMatches
@Data
public class ResetPasswordDTO {
    @NotBlank
    @Size(max = 255)
    private String token;

    @NotBlank
    @Size(max = 255)
    private String password;

    @NotBlank
    @Size(max = 255)
    private String passwordConfirmation;
}
