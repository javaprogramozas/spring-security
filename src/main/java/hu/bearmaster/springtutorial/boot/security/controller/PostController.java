package hu.bearmaster.springtutorial.boot.security.controller;

import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import hu.bearmaster.springtutorial.boot.security.model.exception.NotFoundException;
import hu.bearmaster.springtutorial.boot.security.model.request.UpdatePostRequest;
import hu.bearmaster.springtutorial.boot.security.model.vo.Post;
import hu.bearmaster.springtutorial.boot.security.service.PostService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/posts/latest")
    public List<Post> getLatestPosts() {
        return postService.getLatestPosts();
    }

    @PreAuthorize("hasPermission(#id, 'hu.bearmaster.springtutorial.boot.security.model.vo.Post', 'READ')")
    @GetMapping("/post/{id}")
    public Post getPostById(@PathVariable long id) {
        return postService.getPostById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @PostMapping("/post")
    public Post createNewPost(@RequestBody UpdatePostRequest post, @AuthenticationPrincipal UserDto user) {
        return postService.createPost(post, user);
    }

    @PreAuthorize("hasPermission(#id, 'hu.bearmaster.springtutorial.boot.security.model.vo.Post', 'WRITE')")
    @PostMapping("/post/{id}")
    public Post updatePost(@PathVariable long id, UpdatePostRequest post) {
        return postService.updatePost(id, post);
    }
}
