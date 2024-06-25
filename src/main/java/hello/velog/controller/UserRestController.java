package hello.velog.controller;

import hello.velog.domain.User;
import hello.velog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/velog")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    // 사용자 등록 엔드포인트
    @PostMapping("/userregform")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        // 아이디 및 이메일이 중복된 경우 예외 처리
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 아이디 중복 확인 엔드포인트
    @GetMapping("/api/users/check-username")
    public boolean checkUsername(@RequestParam String username) {
        // 아이디 중복 확인 로직 호출
        return userService.isUsernameTaken(username);
    }

    // 이메일 중복 확인 엔드포인트
    @GetMapping("/api/users/check-email")
    public boolean checkEmail(@RequestParam String email) {
        // 이메일 중복 확인 로직 호출
        return userService.isEmailTaken(email);
    }
}