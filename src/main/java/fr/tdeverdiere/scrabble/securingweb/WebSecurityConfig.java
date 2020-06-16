package fr.tdeverdiere.scrabble.securingweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private Environment environment;

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/img/**", "/favicon.ico", "/css/**").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);

		if (environment.acceptsProfiles(Profiles.of("dev"))) {
			UserDetails user1 =
					User.withDefaultPasswordEncoder()
							.username("catherine")
							.password("0404")
							.roles("USER")
							.build();
			UserDetails user2 = User.withDefaultPasswordEncoder()
					.username("thomas")
					.password("0705")
					.roles("USER")
					.build();

			userDetailsManager.createUser(user1);
			userDetailsManager.createUser(user2);

		}

		return userDetailsManager;
	}
}
