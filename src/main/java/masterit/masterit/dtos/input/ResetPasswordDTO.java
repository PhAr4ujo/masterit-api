package masterit.masterit.dtos.input;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(required = true)
    private String token;

    @NotBlank
    @Size(max = 255)
    @JsonProperty(required = true)
    private String password;

    @NotBlank
    @Size(max = 255)
    @JsonProperty(required = true)
    private String passwordConfirmation;
}
