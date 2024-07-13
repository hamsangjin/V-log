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
        // 세션을 통해 로그인 정보 얻기
        User user = userService.getSessionUser(request);

        // 블로그 주인의 유저 정보 획득
        User blogOwner = userService.findByUsername(username);

        // 현재 접속하는 정보에 따라 게시물 정보를 다르게 표시
        List<Post> posts;
        if (user != null && user.getId().equals(blogOwner.getId())) {
            // 로그인한 유저가 자신의 블로그를 보는 경우
            posts = postService.getUserPosts(blogOwner.getId(), null, false);
        } else {
            // 로그아웃 상태이거나 다른 유저의 블로그를 보는 경우
            posts = postService.getUserPosts(blogOwner.getId(), false, false);
        }

        // 블로그 주인의 blog, user 정보 모델에 전달
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        model.addAttribute("blogOwner", blogOwner);
        return "myblog";
    }

    @GetMapping("/myblog/@{username}/{id}")
    public String getPost(@PathVariable String username, @PathVariable Long id, HttpServletRequest request, Model model) {
        // 세션을 통해 로그인 정보 얻기
        User user = userService.getSessionUser(request);

        User blogOwner = userService.findByUsername(username);          // 블로그 주인의 유저 정보
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());    // 블로그 주인의 블로그 정보
        Post post = postService.getPostById(id);                        // 요청한 게시물의 정보

        // 게시물이 비공개거나 임시글인지 확인
        if (post.getPrivacySetting() || post.getTemporarySetting()) {
            // 둘 중에 하나라도 해당되면 본인의 게시물이 아닌 이상 안보이므로, 다시 게시물 리스트로 이동
            if (user == null || !user.getId().equals(blogOwner.getId())) {
                return "redirect:/vlog/myblog/@" + blogOwner.getUsername(); // 접근 제한
            }
        }

        // 마크다운 내용을 HTML로 변환
        String htmlContent = markdownService.convertMarkdownToHtml(post.getContent());

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("htmlContent", htmlContent);

        return "postDetail";
    }

    @GetMapping("/saves")
    public String save(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        // 세션을 통해 로그인 정보 얻기
        User user = userService.getSessionUser(request);

        // 로그인이 되어 있지 않으면 접속할 수 없음 -> 시큐리티로 바꾸면 없애도 될듯
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
            return "redirect:/vlog/loginform";
        }

        // 비공개 여부와 상관없이 임시글이면 다 불러오기
        List<Post> posts = postService.getUserPosts(user.getId(), null, true);
        model.addAttribute("posts", posts);
        return "saves";
    }

    @GetMapping("/liked")
    public String liked(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        // 세션을 통해 로그인 정보 얻기
        User user = userService.getSessionUser(request);

        // 로그인이 되어 있지 않으면 접속할 수 없음 -> 시큐리티로 바꾸면 없애도 될듯
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
            return "redirect:/vlog/loginform";
        }

        // 본인이 좋아요한 게시물들의 목록을 불러옴
        List<Post> likedPosts = postService.getLikedPosts(user.getId());
        model.addAttribute("posts", likedPosts);
        return "liked";
    }
}