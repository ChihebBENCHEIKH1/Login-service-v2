package com.spring.social.confige;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.spring.social.confige.jwt.JwtAuthorizationFilter;
import com.spring.social.dao.UserRepository;
import com.spring.social.service.UserService;

@Configuration
@EnableJpaRepositories(basePackages = "com.spring.social.dao",
entityManagerFactoryRef = "entityManagerFactory")
//@EntityScan(basePackages = "com.spring.social.model")
//@ComponentScan("com.spring.social")
public class SpringConfigue extends WebSecurityConfigurerAdapter {
    private UserRepository userRepository;
    private UserService userService;
    public SpringConfigue(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }
    
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtAuthorizationFilter(authenticationManager(),userRepository))
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS,"/**")
            .permitAll()
            .antMatchers("/auth/login")
            .permitAll()
            .antMatchers("/social/**")
            .permitAll()
            .antMatchers("/api/**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
    }
    @Bean
    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }
    
   
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}