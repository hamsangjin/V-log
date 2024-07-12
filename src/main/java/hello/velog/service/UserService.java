package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.exception.*;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final BlogRepository blogRepository;

    // 로그인 로직 개선: 세션 관리 제거
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));
        if (!user.getPassword().equals(password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    // 사용자 등록 메서드
    @Transactional
    public User register(User user) {
        validateUsernameAndEmail(user.getUsername(), user.getEmail());

        Blog blog = new Blog(user, user.getName());
        user.setBlog(blog);
        User savedUser = userRepository.save(user);

        blogRepository.save(blog);

        assignRoleToUser(savedUser, "USER");

        return savedUser;
    }

    // 중복 체크 및 예외 처리
    private void validateUsernameAndEmail(String username, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("아이디가 이미 존재합니다.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이메일이 이미 존재합니다.");
        }
    }

    // 사용자에게 역할 할당
    private void assignRoleToUser(User user, String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        role.ifPresent(r -> {
            UserRole userRole = new UserRole(user, r);
            userRoleRepository.save(userRole);
        });
    }

    // 기존의 아이디와 이메일 중복 확인 메소드는 그대로 유지합니다.
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }
}