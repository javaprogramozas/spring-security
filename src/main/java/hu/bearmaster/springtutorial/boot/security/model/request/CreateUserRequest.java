package hu.bearmaster.springtutorial.boot.security.model.request;

import hu.bearmaster.springtutorial.boot.security.validation.PasswordsMatching;
import hu.bearmaster.springtutorial.boot.security.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.StringJoiner;

@PasswordsMatching
public class CreateUserRequest {

    @NotBlank
    private String displayName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    private String passwordAgain;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateUserRequest.class.getSimpleName() + "[", "]")
                .add("displayName='" + displayName + "'")
                .add("email='" + email + "'")
                .toString();
    }
}
