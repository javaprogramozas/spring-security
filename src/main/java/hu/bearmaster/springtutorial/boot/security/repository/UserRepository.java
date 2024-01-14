package hu.bearmaster.springtutorial.boot.security.repository;

import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDto, Long> {
}
