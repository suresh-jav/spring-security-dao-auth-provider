/**
 * This is just to learn the different flow of security. Don't implement this directly on PROD.
 * And for simplicity purpose, I have written everything in one file...
 */

package dev.sk.springsecuritydaoauthprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
public class SpringSecurityDaoAuthProviderApplication {

	@RestController
	static class MyController{
		@RequestMapping("/")
		public String test(){
			return "Hello Spring Security with DAO Auth Provider...";
		}
	}

	@Configuration
	static class ProjectConfig{

		@Bean
		public UserDetailsService userDetailsService(){
			UserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
			userDetailsManager.createUser(
					User.withUsername("admin")
						.password("{bcrypt}$2a$10$w/lxmu4ol5/2SbLDKu7Ff.2gY9usdWleD0ioPxDIw7gSTH3mqluWy")
						.authorities(List.of()).build()
			);
			return userDetailsManager;
		}
		@Bean
		public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
			DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
			daoAuthenticationProvider.setUserDetailsService(userDetailsService);
			return daoAuthenticationProvider;
		}
	}
	@Configuration
//    @EnableWebSecurity(debug = true)
	@EnableWebSecurity
	static class SecurityConfig{

		@Autowired
		AuthenticationProvider authenticationProvider;
		@Bean
		SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{
			httpSecurity.authorizeRequests(x->x.anyRequest().authenticated());
			httpSecurity.formLogin(Customizer.withDefaults());
			return httpSecurity.build();
		}
		@Bean
		public AuthenticationManager authenticationManager(HttpSecurity httpSecurity)throws Exception{
			AuthenticationManagerBuilder authenticationManagerBuilder =
					httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
			authenticationManagerBuilder.authenticationProvider(authenticationProvider);
			return authenticationManagerBuilder.build();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityDaoAuthProviderApplication.class, args);
	}

}
