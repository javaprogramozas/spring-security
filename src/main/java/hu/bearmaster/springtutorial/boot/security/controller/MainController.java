package hu.bearmaster.springtutorial.boot.security.controller;

import hu.bearmaster.springtutorial.boot.security.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final PostService postService;

    public MainController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("latestPosts", postService.getLatestPosts());
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
