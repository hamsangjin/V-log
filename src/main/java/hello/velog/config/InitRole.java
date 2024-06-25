package hello.velog.config;

import hello.velog.domain.Role;
import hello.velog.repository.RoleRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;

@Configuration
public class InitRole{
    @Bean
    ApplicationRunner init(RoleRepository roleRepository) {
        return args -> {
            roleRepository.save(new Role(1L, "ROLE_USER"));
            roleRepository.save(new Role(2L, "ROLE_ADMIN"));
        };
    }
}