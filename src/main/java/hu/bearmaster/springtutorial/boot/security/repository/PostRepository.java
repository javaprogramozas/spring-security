package hu.bearmaster.springtutorial.boot.security.repository;

import hu.bearmaster.springtutorial.boot.security.model.dto.PostDto;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostDto, Long> {
}
