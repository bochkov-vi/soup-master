package ru.itain.soup.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static final String LOGIN_PROCESSING_URL = "/login";
	private static final String LOGIN_FAILURE_URL = "/login?error";
	private static final String LOGIN_URL = "/login";
	private static final String LOGOUT_SUCCESS_URL = "/login";
	/**
	 * Require login to access internal pages and configure login form.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin();
		// Not using Spring CSRF here to be able to use plain HTML for the login page
		http.csrf().disable() // Vaadin has built-in Cross-Site Request Forgery already.
				// Register our CustomRequestCache that saves unauthorized access attempts, so
				// the user is redirected after login.
				.requestCache().requestCache(new CustomRequestCache()) // We add a customized request cache to filter out framework internal request. Check CustomRequestCache implementation for details.

				// Restrict access to our application.
				.and().authorizeRequests()

				// Allow all flow internal requests.
				.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll() // Permits a set of Vaadin related request types (check SecurityUtils for details).

				// Allow all requests by logged in users.
				.anyRequest().authenticated() // Force authentication for all views.

				// Configure the login page.
				.and().formLogin().loginPage(LOGIN_URL).permitAll() // Configure the URL to the login page for redirects and permit access to everyone.
				.loginProcessingUrl(LOGIN_PROCESSING_URL) // Configure the login URL Spring Security is expecting POST requests to (form submit).
				.failureUrl(LOGIN_FAILURE_URL)

				// Configure logout
				.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(
				"/img/**",
				"/icons/**",
				"/presentation-viewer/**",
				"/js/**",
				"/api/pdf/**",
				"/api/videos/**",
				"/git.json",

				// Vaadin Flow static resources // Mandatory
				"/VAADIN/**",

				// the standard favicon URI
				"/favicon.ico",

				// the robots exclusion standard
				"/robots.txt",

				// web application manifest // Needed only when developing a Progressive Web Application.
				"/manifest.webmanifest",
				"/sw.js",
				"/offline-page.html",

				// (development mode) static resources // Allows access to frontend resources in development mode.
				"/frontend/**",

				// (development mode) webjars // Allows access to frontend resources in development mode.
				"/webjars/**",

				// (production mode) static resources // Grants access to all bundled resources. This is important for your login view (if a Polymer template needs to be accessed) or for every other public page.
				"/frontend-es5/**", "/frontend-es6/**");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, DataSource dataSource) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username, password, enabled"
									  + " from users.\"user\" where username=?")
				.authoritiesByUsernameQuery("select username, authority "
											+ "from users.\"user\" where username=?");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
