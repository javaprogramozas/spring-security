package hu.bearmaster.springtutorial.boot.security.controller;

import hu.bearmaster.springtutorial.boot.security.model.exception.NotFoundException;
import hu.bearmaster.springtutorial.boot.security.model.request.CreateUserRequest;
import hu.bearmaster.springtutorial.boot.security.model.vo.User;
import hu.bearmaster.springtutorial.boot.security.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String getAllUsers(Model model, @SessionAttribute(required = false) Long visitedUserId) {
        List<User> users = userService.getUsers();
        model.addAttribute("users", users);
        model.addAttribute("highlighted", visitedUserId);
        return "users";
    }

    @GetMapping("/user/{id}")
    public String getUserById(Model model, @PathVariable long id, HttpSession session) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        model.addAttribute("user", user);

        if (user != null) {
            session.setAttribute("visitedUserId", user.getId());
        }

        return "user";
    }

    @GetMapping("/user/registration")
    public String registrationForm(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest());
        return "register";
    }

    @PostMapping("/user/registration")
    public String createUser(@Valid CreateUserRequest createUserRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.createUser(createUserRequest);
        return "redirect:/login?register";
    }
}
