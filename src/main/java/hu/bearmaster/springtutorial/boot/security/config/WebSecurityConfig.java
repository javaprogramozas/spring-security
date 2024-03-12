package hu.bearmaster.springtutorial.boot.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bearmaster.springtutorial.boot.security.authentication.CsrfCookieFilter;
import hu.bearmaster.springtutorial.boot.security.authentication.MyFormLoginHandler;
import hu.bearmaster.springtutorial.boot.security.authentication.SpaCsrfTokenRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
		MyFormLoginHandler formHandler = new MyFormLoginHandler(objectMapper);
		CookieCsrfTokenRepository csrfRepository = new CookieCsrfTokenRepository();
		csrfRepository.setCookieCustomizer(cookie -> cookie
				.domain("bearmaster.hu")
				.secure(true)
				.httpOnly(false));
		http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/favicon.ico", "/error", "/posts/latest", "/me").permitAll()
						.requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher("/login\\?.*")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(formHandler)
                        .failureHandler(formHandler)
                        .permitAll())
                .oauth2Login(oauth -> oauth.loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(myAuthMapper())))
                .logout(logout -> logout
                        .logoutUrl("/logout")
						.logoutSuccessHandler(formHandler)
                        .permitAll())
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.csrfTokenRepository(csrfRepository)
						.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
				.addFilterAfter(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(exceptionHandling ->
						exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(UNAUTHORIZED)));

		return http.build();
	}

	private GrantedAuthoritiesMapper myAuthMapper() {
		return authorities -> {
			for (GrantedAuthority authority : authorities) {
				if (authority instanceof OAuth2UserAuthority) {
					return Set.of(new SimpleGrantedAuthority("ROLE_USER"));
				}
			}
			return authorities;
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("https://api.bearmaster.hu", "https://bearmaster.hu"));
		configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
		configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}