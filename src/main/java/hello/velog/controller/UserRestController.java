package hello.velog.controller;

import hello.velog.domain.User;
import hello.velog.exception.EmailAlreadyExistsException;
import hello.velog.exception.UsernameAlreadyExistsException;
import hello.velog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PostMapping("/userreg")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/users/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean isTaken = userService.isUsernameTaken(username);
        return ResponseEntity.ok(isTaken);
    }

    @GetMapping("/api/users/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean isTaken = userService.isEmailTaken(email);
        return ResponseEntity.ok(isTaken);
    }
}