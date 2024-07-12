package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.service.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class BlogController {
    private final UserService userService;
    private final PostService postService;
    private final BlogService blogService;
    private final MarkdownService markdownService;

    @GetMapping
    public String home() {
        return "home";
    }

    @GetMapping("/myblog/@{username}")
    public String myBlog(@PathVariable String username, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");
        List<Post> posts;

        User findUser = userService.findByUsername(username);

        if (sessionUser != null && sessionUser.getId().equals(findUser.getId())) {
            // 로그인한 유저가 자신의 블로그를 보는 경우
            posts = postService.getUserPosts(findUser.getId(), null, false);
        } else {
            // 로그아웃 상태이거나 다른 유저의 블로그를 보는 경우
            posts = postService.getUserPosts(findUser.getId(), false, false);
        }

        Blog blog = blogService.findBlogByUserId(findUser.getId());
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        model.addAttribute("blogOwner", postService.getUserById(findUser.getId()));
        return "myblog";
    }

    @GetMapping("/myblog/@{username}/{id}")
    public String getPost(@PathVariable String username, @PathVariable Long id, HttpServletRequest request, Model model) {
        User findUser = userService.findByUsername(username);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Post post = postService.getPostById(id);
        Blog blog = blogService.findBlogByUserId(findUser.getId());

        // 게시물 권한 확인
        if (post.getPrivacySetting() || post.getTemporarySetting()) {
            if (user == null || !user.getId().equals(findUser.getId())) {
                return "redirect:/vlog/myblog/@" + findUser.getUsername(); // 접근 제한
            }
        }

        // 마크다운 내용을 HTML로 변환
        String htmlContent = markdownService.convertMarkdownToHtml(post.getContent());

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", postService.getUserById(findUser.getId()));
        model.addAttribute("htmlContent", htmlContent);

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