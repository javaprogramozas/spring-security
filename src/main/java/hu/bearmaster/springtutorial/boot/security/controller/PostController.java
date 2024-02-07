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

    @PreAuthorize("hasPermission(#id, 'hu.bearmaster.springtutorial.boot.security.model.vo.Post', 'READ')")
    @GetMapping("/post/{id}")
    public String getPostById(Model model, @PathVariable long id) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
        model.addAttribute("post", post);

        return "post";
    }

    @PreAuthorize("hasRole('EDITOR')")
    @GetMapping(value = "/post/new")
    public String createNewPost(Model model) {
        model.addAttribute("post", new Post());
        return "post-edit";
    }

    @PreAuthorize("hasRole('EDITOR')")
    @PostMapping("/post")
    public String createNewPost(UpdatePostRequest post, @AuthenticationPrincipal UserDto user) {
        long postId = postService.createPost(post, user);
        return "redirect:/post/" + postId;
    }

    @PreAuthorize("hasPermission(#id, 'hu.bearmaster.springtutorial.boot.security.model.vo.Post', 'WRITE')")
    @GetMapping(value = "/post/{id}", params = {"edit=true"})
    public String getPostByIdForEdit(Model model, @PathVariable long id) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
        model.addAttribute("post", post);

        return "post-edit";
    }

    @PreAuthorize("hasPermission(#id, 'hu.bearmaster.springtutorial.boot.security.model.vo.Post', 'WRITE')")
    @PostMapping("/post/{id}")
    public String updatePost(@PathVariable long id, UpdatePostRequest post) {
        postService.updatePost(id, post);
        return "redirect:/post/" + id;
    }
}
