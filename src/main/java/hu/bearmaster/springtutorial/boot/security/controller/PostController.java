package hu.bearmaster.springtutorial.boot.security.controller;

import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import hu.bearmaster.springtutorial.boot.security.model.exception.NotFoundException;
import hu.bearmaster.springtutorial.boot.security.model.request.UpdatePostRequest;
import hu.bearmaster.springtutorial.boot.security.model.vo.Post;
import hu.bearmaster.springtutorial.boot.security.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public String getAllPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "posts";
    }

    @PreAuthorize("not #edit or hasRole('EDITOR')")
    @GetMapping("/post/{id}")
    public String getPostById(Model model, @PathVariable long id, @RequestParam(name = "edit", required = false, defaultValue = "false") boolean edit) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
        model.addAttribute("post", post);

        return edit ? "post-edit" : "post";
    }

    @PostMapping("/post/{id}")
    public String updatePost(@PathVariable long id, UpdatePostRequest post, @AuthenticationPrincipal UserDto user) {
        postService.updatePost(id, post, user);
        return "redirect:/post/" + id;
    }
}
