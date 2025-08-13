package masterit.masterit.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import masterit.masterit.dtos.input.ResetPasswordDTO;
import masterit.masterit.validations.annotations.PasswordMatches;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ResetPasswordDTO> {

    @Override
    public boolean isValid(ResetPasswordDTO dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getPasswordConfirmation() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getPasswordConfirmation());
    }
}
