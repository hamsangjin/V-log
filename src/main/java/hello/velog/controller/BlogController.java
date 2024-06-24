package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/velog")
public class BlogController {
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;

    @GetMapping("/myblog/{userId}")
    public String viewMyBlog(@PathVariable Long userId, Model model) {
        User blogOwner = userService.findById(userId);
        if (blogOwner == null) {
            return "redirect:/velog"; // 유저가 없는 경우 홈으로 리디렉션
        }

        List<Post> userPosts = postService.findPostsByUserId(userId);
        model.addAttribute("blogOwner", blogOwner); // 블로그 주인 정보를 추가
        model.addAttribute("posts", userPosts);
        return "myblog";
    }
}