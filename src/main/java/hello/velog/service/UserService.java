package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.exception.*;
import hello.velog.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Blog blog = new Blog(user, user.getName());
        user.setBlog(blog);
        User savedUser = userRepository.save(user);

        blogRepository.save(blog);

        assignRoleToUser(savedUser, "USER");
        return savedUser;
    }

    private void assignRoleToUser(User user, String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        role.ifPresent(r -> {
            UserRole userRole = new UserRole(user, r);
            userRoleRepository.save(userRole);
        });
    }

    @Transactional(readOnly = true)
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public User getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(Long userId) {
        return userRepository.countByFollowers_Id(userId);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(Long userId) {
        return userRepository.countByFollowing_Id(userId);
    }
}
