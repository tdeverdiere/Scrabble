package fr.tdeverdiere.scrabble.securingweb;

import fr.tdeverdiere.scrabble.domain.Authorities;
import fr.tdeverdiere.scrabble.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.domain.Example;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private Environment environment;

	@Resource
	private UserRepository userRepository;


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

			updateDevUsers();

		}

		return userDetailsManager;
	}

	private void updateDevUsers() {
		fr.tdeverdiere.scrabble.domain.User catherine = userRepository.getUserByUsername("catherine");
		catherine.setFirstName("Catherine");
		catherine.setLastName("Colin de Verdière");
		Set<Authorities> authorities1 = new HashSet<>();
		Authorities authority1 = new Authorities();
		authority1.setAuthority("ROLE_USER");
		authorities1.add(authority1);
		catherine.setAuthorities(authorities1);

		userRepository.save(catherine);
		fr.tdeverdiere.scrabble.domain.User thomas = userRepository.getUserByUsername("thomas");
		thomas.setFirstName("Thomas");
		thomas.setLastName("Colin de Verdière");
		Set<Authorities> authorities2 = new HashSet<>();
		Authorities authority2 = new Authorities();
		authority2.setAuthority("ROLE_USER");
		authorities2.add(authority2);
		thomas.setAuthorities(authorities2);
		userRepository.save(thomas);
	}


}
