package hu.bearmaster.springtutorial.boot.security.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordsMatchingValidator.class)
@Documented
public @interface PasswordsMatching {

  String message() default "{passwords.not.matching}";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

}