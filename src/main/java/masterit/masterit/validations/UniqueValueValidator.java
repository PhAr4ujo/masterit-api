package masterit.masterit.validations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import masterit.masterit.validations.annotations.UniqueValue;
import org.springframework.util.Assert;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    private String fieldName;
    private Class<?> domainClass;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.domainClass = constraintAnnotation.domainClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        var query = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + domainClass.getName() + " e WHERE e." + fieldName + " = :value"
        );
        query.setParameter("value", value);

        Long count = (Long) query.getSingleResult();
        return count == 0;
    }
}
