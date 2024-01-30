package hu.bearmaster.springtutorial.boot.security.service;

import hu.bearmaster.springtutorial.boot.security.model.dto.PostDto;
import hu.bearmaster.springtutorial.boot.security.model.exception.NotFoundException;
import hu.bearmaster.springtutorial.boot.security.model.request.UpdatePostRequest;
import hu.bearmaster.springtutorial.boot.security.model.vo.Post;
import hu.bearmaster.springtutorial.boot.security.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @PostFilter("filterObject.author.email == authentication.name")
    public List<Post> getAllPosts() {
        return postRepository.findAll().stream()
                .map(Post::from)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Post> getLatestPosts() {
        return postRepository.findAll(PageRequest.of(0, 5, Sort.by("createdAt").descending())).stream()
                .map(Post::from)
                .toList();
    }

    public Optional<Post> getPostById(long id) {
        return postRepository.findById(id)
                .map(Post::from);
    }

    //TODO security check
    @Transactional
    public void updatePost(long id, UpdatePostRequest post) {
        PostDto postDto = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));

        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setTopic(post.getTopic());
        postDto.setSlug(post.getSlug());
    }
}
