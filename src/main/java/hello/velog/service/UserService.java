package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    public void login(String username, String password, HttpServletRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이디입니다."));
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(NoSuchElementException::new);
    }

    // 사용자 등록 메서드
    public User register(User user) {
        // 아이디 중복 확인
        if (isUsernameTaken(user.getUsername())) {
            throw new IllegalArgumentException("아이디가 이미 존재합니다.");
        }

        // 이메일 중복 확인
        if (isEmailTaken(user.getEmail())) {
            throw new IllegalArgumentException("이메일이 이미 존재합니다.");
        }

        // 임시로 이미지는 기본 이미지로 설정
        user.setProfileImage("/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/default-profile.png");

        // 사용자 정보를 데이터베이스에 저장
        User savedUser = userRepository.save(user);

        // 역할을 ROLE_USER로 설정
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        if (role.isPresent()){
            UserRole userRole = new UserRole(savedUser, role.get());
            userRoleRepository.save(userRole);
        }
        return savedUser;
    }

    // 아이디 중복 확인 메서드
    public boolean isUsernameTaken(String username) {
        // 주어진 아이디로 사용자 검색
        Optional<User> user = userRepository.findByUsername(username);
        // 사용자가 존재하면 true 반환
        return user.isPresent();
    }

    // 이메일 중복 확인 메서드
    public boolean isEmailTaken(String email) {
        // 주어진 이메일로 사용자 검색
        Optional<User> user = userRepository.findByEmail(email);
        // 사용자가 존재하면 true 반환
        return user.isPresent();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}