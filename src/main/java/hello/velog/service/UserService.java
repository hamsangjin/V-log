package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final BlogRepository blogRepository;

    public void login(String username, String password, HttpServletRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이디입니다."));
        if (!user.getPassword().equals(password))       throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(NoSuchElementException::new);
    }

    // 사용자 등록 메서드
    @Transactional
    public User register(User user) {
        // 아이디 중복 확인
        if (isUsernameTaken(user.getUsername()))    throw new IllegalArgumentException("아이디가 이미 존재합니다.");
        // 이메일 중복 확인
        if (isEmailTaken(user.getEmail()))          throw new IllegalArgumentException("이메일이 이미 존재합니다.");

        Blog blog = new Blog(user, user.getName()); // 블로그 엔티티 생성 및 설정
        user.setBlog(blog);                         // User 엔티티 설정
        User savedUser = userRepository.save(user); // User 엔티티를 먼저 저장

        blogRepository.save(blog);                      // Blog 엔티티를 그 다음에 저장

        // 역할을 ROLE_USER로 설정
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        if (role.isPresent()) {
            UserRole userRole = new UserRole(savedUser, role.get());
            userRoleRepository.save(userRole);
        }

        return savedUser;
    }

    // 아이디 중복 확인 메서드
    public boolean isUsernameTaken(String username) {
        Optional<User> user = userRepository.findByUsername(username);  // 주어진 아이디로 사용자 검색
        return user.isPresent();                                        // 사용자가 존재하면 true 반환
    }

    // 이메일 중복 확인 메서드
    public boolean isEmailTaken(String email) {
        Optional<User> user = userRepository.findByEmail(email);        // 주어진 이메일로 사용자 검색
        return user.isPresent();                                        // 사용자가 존재하면 true 반환
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}