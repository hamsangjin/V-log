package hello.velog.controller;

import hello.velog.domain.User;
import hello.velog.service.UserService;
import jakarta.servlet.http.*;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/velog")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String home() {
        return "home";
    }

    @GetMapping("/loginform")
    public String loginForm(Model model) {
        model.addAttribute("user", new User());
        return "loginform";
    }

    @PostMapping("/loginform")
    public String login(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // 로그인할 때 아이디 및 비밀번호 확인 로직
        try {
            userService.login(user.getUsername(), user.getPassword(), request);     // 유저 정보 넘겨줌
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMSG", e.getMessage());
            return "redirect:/velog/loginform";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMSG", e.getMessage());
            return "redirect:/velog/loginform";
        }
        return "redirect:/velog";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();       // 세션 무효화
        return "redirect:/velog";
    }

    @GetMapping("/userregform")
    public String userRegForm(Model model) {
        model.addAttribute("user", new User());
        return "userregform";
    }
}
