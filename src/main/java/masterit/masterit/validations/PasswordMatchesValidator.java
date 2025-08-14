package masterit.masterit.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import masterit.masterit.dtos.input.ResetPasswordDTO;
import masterit.masterit.validations.annotations.PasswordMatches;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, ResetPasswordDTO> {

    @Override
    public boolean isValid(ResetPasswordDTO dto, ConstraintValidatorContext context) {
        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (dto.getPassword() == null || dto.getPasswordConfirmation() == null) {
            context.buildConstraintViolationWithTemplate("Password and confirmation cannot be null")
                    .addPropertyNode("password")
                    .addConstraintViolation();
            valid = false;
        }

        if (dto.getPassword() != null && dto.getPasswordConfirmation() != null
                && !dto.getPassword().equals(dto.getPasswordConfirmation())) {
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("passwordConfirmation")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
