package hello.velog.config;

import hello.velog.repository.UserRepository;
import hello.velog.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                                "/vlog/login",
                                "/vlog/userregform",
                                "/vlog/userreg",
                                "/vlog/api/users/check-username",
                                "/vlog/api/users/check-email",
                                "/css/**",
                                "/images/**",
                                "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/vlog/loginform")
                        .loginProcessingUrl("/vlog/login")
                        .defaultSuccessUrl("/vlog")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/vlog/logout")
                        .logoutSuccessUrl("/vlog")
                        .permitAll()
                )
                .csrf().disable(); // 필요시 주석 처리하여 활성화 해볼 수 있습니다

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
