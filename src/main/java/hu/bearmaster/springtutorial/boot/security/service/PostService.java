package hu.bearmaster.springtutorial.boot.security.service;

import hu.bearmaster.springtutorial.boot.security.model.vo.Post;
import hu.bearmaster.springtutorial.boot.security.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll().stream()
                .map(Post::from)
                .toList();
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
}
