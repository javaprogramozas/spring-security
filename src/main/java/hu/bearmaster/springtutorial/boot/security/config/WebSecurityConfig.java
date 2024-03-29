package hu.bearmaster.springtutorial.boot.security.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import java.util.Set;

@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.requiresChannel(channel -> channel.anyRequest().requiresSecure())
				.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/user/registration", "/favicon.ico", "/error", "/").permitAll()
				.requestMatchers(RegexRequestMatcher.regexMatcher("/login\\?.*")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults())
			.formLogin(login -> login
					.loginPage("/login")
					.permitAll())
			.oauth2Login(oauth -> oauth.loginPage("/login")
					.userInfoEndpoint(userInfo -> userInfo
							.userAuthoritiesMapper(myAuthMapper())))
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/login?logout")
					.permitAll());

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
	public ServletWebServerFactory servletContainer(@Value("${server.port.http}") int httpPort) {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createStandardConnector(httpPort));
		return tomcat;
	}

	private Connector createStandardConnector(int httpPort) {
		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setPort(httpPort);
		return connector;
	}

}