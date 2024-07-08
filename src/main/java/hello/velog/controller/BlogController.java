package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vlog")
@RequiredArgsConstructor

public class BlogController {
    private final UserService userService;
    private final PostService postService;
    private final BlogService blogService;

    @GetMapping("/myblog/{userId}")
    public String myBlog(@PathVariable Long userId, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");
        List<Post> posts;

        if (sessionUser != null && sessionUser.getId().equals(userId)) {
            // 로그인한 유저가 자신의 블로그를 보는 경우
            posts = postService.getUserPosts(userId, null, false);
        } else {
            // 로그아웃 상태이거나 다른 유저의 블로그를 보는 경우
            posts = postService.getUserPosts(userId, false, false);
        }

        Blog blog = blogService.findBlogByUserId(userId);
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        model.addAttribute("blogOwner", postService.getUserById(userId));
        return "myblog";
    }

    @GetMapping("/myblog/{userId}/{id}")
    public String getPost(@PathVariable Long userId, @PathVariable Long id,
                          HttpServletRequest request, Model model){

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        Post post = postService.getPostById(id);

        // 일반적으론 게시물 클릭을 통해 접속해 아래 if문이 필요없지만, url을 통한 접속을 제한하는 것이다.
        // 게시물 권한 확인
        if (post.getPrivacySetting() || post.getTemporarySetting()) {
            // 로그아웃 상태이거나 다른 유저의 블로그를 보는 경우
            if (user == null || !user.getId().equals(userId)) {
                return "redirect:/vlog/myblog/" + userId;      // 접근할 수 없으면 다시 게시글 목록으로
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("blogOwner", postService.getUserById(userId));

        return "postDetail";
    }

    @GetMapping("/saves")
    public String save(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
            return "redirect:/vlog/loginform";
        }
        List<Post> posts = postService.getUserPosts(user.getId(), null, true);
        model.addAttribute("posts", posts);
        return "saves";
    }
}