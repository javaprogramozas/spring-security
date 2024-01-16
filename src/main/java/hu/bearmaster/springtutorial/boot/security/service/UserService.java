package hu.bearmaster.springtutorial.boot.security.service;

import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import hu.bearmaster.springtutorial.boot.security.model.vo.User;
import hu.bearmaster.springtutorial.boot.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

        return org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
                .username(userDto.getEmail())
                .password("password")
                .roles("USER")
                .build();
    }
}
