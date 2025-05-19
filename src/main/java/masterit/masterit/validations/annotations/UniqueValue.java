package masterit.masterit.validations.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import masterit.masterit.validations.UniqueValueValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueValueValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValue {
    String message() default "This value already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldName();

    Class<?> domainClass();
}
