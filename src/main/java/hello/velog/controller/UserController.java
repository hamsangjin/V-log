package hello.velog.controller;

import hello.velog.domain.User;
import hello.velog.service.UserService;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/loginform")
    public String loginForm(Model model) {
        model.addAttribute("user", new User());
        return "loginform";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            userService.login(username, password, session);
            return "redirect:/vlog";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMSG", e.getMessage());
            return "redirect:/vlog/loginform";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/vlog";
    }


    @GetMapping("/userregform")
    public String userRegForm(Model model) {
        model.addAttribute("user", new User());
        return "userregform";
    }
}
