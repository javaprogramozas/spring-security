package hu.bearmaster.springtutorial.boot.security.model.vo;

import hu.bearmaster.springtutorial.boot.security.model.UserStatus;
import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class User {
    private Long id;

    private String displayName;

    private String email;

    private UserStatus status;

    private ZonedDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("displayName='" + displayName + "'")
                .add("email='" + email + "'")
                .add("status=" + status)
                .add("createdAt=" + createdAt)
                .toString();
    }

    public static User from(UserDto userDto) {
        User user = new User();
        user.id = userDto.getId();
        user.displayName = userDto.getDisplayName();
        user.email = userDto.getEmail();
        user.status = userDto.getStatus();
        user.createdAt = userDto.getCreatedAt();
        return user;
    }
}
