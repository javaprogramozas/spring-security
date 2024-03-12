package hu.bearmaster.springtutorial.boot.security.service;

import hu.bearmaster.springtutorial.boot.security.model.dto.PostDto;
import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import hu.bearmaster.springtutorial.boot.security.model.exception.NotFoundException;
import hu.bearmaster.springtutorial.boot.security.model.request.UpdatePostRequest;
import hu.bearmaster.springtutorial.boot.security.model.vo.Post;
import hu.bearmaster.springtutorial.boot.security.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final Example<PostDto> PUBLISHED_POST_EXAMPLE = Example.of(new PostDto(true));

    private final PermissionService permissionService;

    private final PostRepository postRepository;

    public PostService(PermissionService permissionService, PostRepository postRepository) {
        this.permissionService = permissionService;
        this.postRepository = postRepository;
    }

    @PostFilter("filterObject.author.email == authentication.name")
    //@PostFilter("hasPermission(filterObject, 'READ'")
    public List<Post> getAllPosts() {
        return postRepository.findAll().stream()
                .map(Post::from)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Post> getLatestPosts() {
        return postRepository.findAll(PUBLISHED_POST_EXAMPLE, PageRequest.of(0, 5, Sort.by("createdAt").descending())).stream()
                .map(Post::from)
                .toList();
    }

    public Optional<Post> getPostById(long id) {
        return postRepository.findById(id)
                .map(Post::from);
    }

    @Transactional
    public Post createPost(UpdatePostRequest post, UserDto user) {
        PostDto postDto = new PostDto();
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setTopic(post.getTopic());
        postDto.setSlug(post.getSlug());
        postDto.setPublished(post.isPublished());
        postDto.setCreatedAt(ZonedDateTime.now());
        postDto.setAuthor(user);

        PostDto savedPost = postRepository.save(postDto);
        updatePermissions(savedPost);
        return Post.from(savedPost);
    }

    @Transactional
    public Post updatePost(long id, UpdatePostRequest post) {
        PostDto postDto = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));

        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setTopic(post.getTopic());
        postDto.setSlug(post.getSlug());
        postDto.setPublished(post.isPublished());

        updatePermissions(postDto);
        return Post.from(postDto);
    }

    private void updatePermissions(PostDto post) {
        permissionService.addPermission(post.getAuthor(), post.getId(), Post.class, "READ");
        permissionService.addPermission(post.getAuthor(), post.getId(), Post.class, "WRITE");
        if (post.isPublished()) {
            permissionService.addPermission("ROLE_USER", post.getId(), Post.class, "READ");
        } else {
            permissionService.removePermission("ROLE_USER", post.getId(), Post.class, "READ");
        }
    }
}
