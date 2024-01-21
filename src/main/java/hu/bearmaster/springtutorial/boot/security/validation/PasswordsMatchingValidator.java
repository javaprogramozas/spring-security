package hu.bearmaster.springtutorial.boot.security.validation;

import hu.bearmaster.springtutorial.boot.security.model.request.CreateUserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordsMatchingValidator implements ConstraintValidator<PasswordsMatching, CreateUserRequest> {

    @Override
    public boolean isValid(CreateUserRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (!Objects.equals(value.getPassword(), value.getPasswordAgain())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("ugyanaz kell legyen")
                    .addPropertyNode("passwordAgain")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
