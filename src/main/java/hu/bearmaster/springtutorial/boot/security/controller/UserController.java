package hu.bearmaster.springtutorial.boot.security.controller;

import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import hu.bearmaster.springtutorial.boot.security.model.exception.NotFoundException;
import hu.bearmaster.springtutorial.boot.security.model.request.CreateUserRequest;
import hu.bearmaster.springtutorial.boot.security.model.vo.User;
import hu.bearmaster.springtutorial.boot.security.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @PostAuthorize("returnObject.email == authentication.name or hasRole('ADMIN')")
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @PostMapping("/user")
    public User createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return userService.createUser(createUserRequest);
    }

    @GetMapping("/me")
    public User currentUser(@AuthenticationPrincipal UserDto userDto) {
        if (userDto != null) {
            return User.from(userDto);
        } else {
            throw new NotFoundException("No authenticated user found!");
        }
    }
}
