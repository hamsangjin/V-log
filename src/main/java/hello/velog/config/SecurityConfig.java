package hello.velog.config;

import hello.velog.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/vlog",
                                "/vlog/trending",
                                "/vlog/latest",
                                "/vlog/feed",
                                "/vlog/loginform",
                                "/vlog/userregform",
                                "/vlog/userreg",
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/fragments/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/vlog/loginform")
                        .loginProcessingUrl("/vlog/login")
                        .defaultSuccessUrl("/vlog")
                        .failureUrl("/vlog/loginform?error=true")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/vlog/logout")
                        .logoutSuccessUrl("/vlog")
                        .permitAll()
                )
                .csrf().disable(); // CSRF 보호 비활성화

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
