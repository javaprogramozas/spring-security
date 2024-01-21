package hu.bearmaster.springtutorial.boot.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) {
            return true;
        }

        boolean valid = true;
        if (value.length() < 6) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("legalább 6 karakter hosszú")
                    .addConstraintViolation();
            valid = false;
        }
        if (value.chars().noneMatch(Character::isUpperCase)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("legalább 1 nagybetű")
                    .addConstraintViolation();
            valid = false;
        }
        if (value.chars().noneMatch(Character::isLowerCase)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("legalább 1 kisbetű")
                    .addConstraintViolation();
            valid = false;
        }
        if (value.chars().noneMatch(Character::isDigit)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("legalább 1 számjegy")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
