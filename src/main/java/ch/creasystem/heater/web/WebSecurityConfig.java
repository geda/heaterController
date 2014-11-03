package ch.creasystem.heater.web;

import java.security.SecureRandom;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
	private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EntityManagerFactory emf;

	@Value("${admin.username}")
	private String username;

	@Value("${admin.password}")
	private String password;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
		csrfTokenRepository.setHeaderName(CSRF_HEADER_NAME);
		http.csrf().csrfTokenRepository(csrfTokenRepository);

		http.authorizeRequests().antMatchers("/assets/**", "/webjars/**", "/login/**", "/api-docs/**").permitAll()
				.anyRequest().fullyAuthenticated();

		http.formLogin().loginProcessingUrl("/login").loginPage("/login").failureUrl("/login?error");

		http.logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout");

		http.headers()
				.contentTypeOptions()
				.xssProtection()
				.httpStrictTransportSecurity()
				.addHeaderWriter(
						new StaticHeadersWriter("Access-Control-Allow-Origin", "http://petstore.swagger.wordnik.com"))
				.addHeaderWriter(new CsrfTokenCookieWriter(csrfTokenRepository, CSRF_COOKIE_NAME));
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());

		auth.inMemoryAuthentication().passwordEncoder(passwordEncoder).withUser(username)
				.password(passwordEncoder.encode(password)).authorities("ADMIN");

	}
}
