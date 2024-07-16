package hello.velog.controller;

import hello.velog.domain.User;
import hello.velog.service.UserService;
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

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/vlog";
    }

    @GetMapping("/userregform")
    public String userRegForm(Model model) {
        model.addAttribute("user", new User());
        return "userregform";
    }

    @PostMapping("/register")
    public String register(User user, RedirectAttributes redirectAttributes) {
        try {
            userService.register(user);
            return "redirect:/vlog/loginform";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMSG", e.getMessage());
            return "redirect:/vlog/userregform";
        }
    }
}
