package hu.bearmaster.springtutorial.boot.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorize) -> authorize
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults())
			.formLogin(login -> login
					.loginPage("/login")
					.permitAll())
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/login?logout")
					.permitAll());

		return http.build();
	}

}