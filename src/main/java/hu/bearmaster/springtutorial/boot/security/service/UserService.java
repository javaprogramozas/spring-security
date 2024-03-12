package hu.bearmaster.springtutorial.boot.security.service;

import hu.bearmaster.springtutorial.boot.security.model.UserStatus;
import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import hu.bearmaster.springtutorial.boot.security.model.request.CreateUserRequest;
import hu.bearmaster.springtutorial.boot.security.model.vo.User;
import hu.bearmaster.springtutorial.boot.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //@PostAuthorize("returnObject?.email == authentication.name")
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id)
                .map(User::from);
    }

    public List<User> getUsers() {
        return userRepository.findAll().stream()
                .map(User::from)
                .toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("Looking up user with email: {}", username);
        UserDto userDto = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No registered user with " + username));
        LOGGER.debug("User found: {}", userDto);

        return userDto;
    }

    public User createUser(CreateUserRequest createUserRequest) {
        // TODO check email uniqueness
        UserDto userDto = new UserDto();
        userDto.setDisplayName(createUserRequest.getDisplayName());
        userDto.setEmail(createUserRequest.getEmail());
        userDto.setStatus(UserStatus.ACTIVE);
        userDto.setCreatedAt(ZonedDateTime.now());
        userDto.setEncodedPassword(passwordEncoder.encode(createUserRequest.getPassword()));

        return User.from(userRepository.save(userDto));
    }
}
