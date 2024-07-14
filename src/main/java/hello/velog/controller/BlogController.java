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
    private final SeriesService seriesService;
    private final MarkdownService markdownService;

    @GetMapping
    public String home() {
        return "home";
    }

    @GetMapping("/myblog/@{username}")
    public String myBlog(@PathVariable String username, HttpServletRequest request, Model model) {
        return "redirect:/vlog/myblog/@" + username + "/posts";
    }

    @GetMapping("/myblog/@{username}/posts")
    public String myBlogPosts(@PathVariable String username, HttpServletRequest request, Model model) {
        User user = userService.getSessionUser(request);
        User blogOwner = userService.findByUsername(username);

        List<Post> posts;
        if (user != null && user.getId().equals(blogOwner.getId())) {
            posts = postService.getUserPosts(blogOwner.getId(), null, false);
        } else {
            posts = postService.getUserPosts(blogOwner.getId(), false, false);
        }

        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("activeTab", "posts");
        return "myblog";
    }

    @GetMapping("/myblog/@{username}/series")
    public String myBlogSeries(@PathVariable String username, HttpServletRequest request, Model model) {
        User blogOwner = userService.findByUsername(username);
        List<Series> seriesList = seriesService.findAllSeriesByBlogId(blogOwner.getBlog().getId());
        List<Map<String, Object>> seriesWithThumbnails = new ArrayList<>();

        for (Series series : seriesList) {
            Post firstPost = seriesService.findFirstPostBySeriesId(series.getId());
            String thumbnailImage = firstPost != null ? firstPost.getThumbnailImage() : "/images/post/default-image.png";
            Map<String, Object> seriesMap = new HashMap<>();
            seriesMap.put("series", series);
            seriesMap.put("thumbnailImage", thumbnailImage);
            seriesWithThumbnails.add(seriesMap);
        }

        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        model.addAttribute("blog", blog);
        model.addAttribute("seriesList", seriesWithThumbnails);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("activeTab", "series");
        return "myblog";
    }

    @GetMapping("/myblog/@{username}/about")
    public String getBlogAbout(@PathVariable String username, Model model, HttpServletRequest request) {
        Blog blog = blogService.findBlogByUsername(username);
        User user = userService.getSessionUser(request);
        boolean isBlogOwner = user != null && user.getUsername().equals(username);

        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blog.getUser());
        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("username", username); // username 추가
        model.addAttribute("activeTab", "about");
        return "myblog";
    }

    @PostMapping("/myblog/@{username}/about/update")
    public String updateBlogIntro(@PathVariable String username, HttpServletRequest request, @RequestParam String intro) {
        System.out.println(username);
        User user = userService.getSessionUser(request);
        User blogOwner = userService.findByUsername(username);

        if (user == null || !user.getId().equals(blogOwner.getId())) {
            return "redirect:/vlog/myblog/@" + username + "/about";
        }

        Blog blog = blogService.findBlogByUsername(username);
        blog.setIntro(intro);
        blogService.saveBlog(blog);
        return "redirect:/vlog/myblog/@" + username + "/about";
    }

    @GetMapping("/myblog/@{username}/{id}")
    public String getPost(@PathVariable String username, @PathVariable Long id, HttpServletRequest request, Model model) {
        User user = userService.getSessionUser(request);

        User blogOwner = userService.findByUsername(username);
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        Post post = postService.getPostById(id);

        if (post.getPrivacySetting() || post.getTemporarySetting()) {
            if (user == null || !user.getId().equals(blogOwner.getId())) {
                return "redirect:/vlog/myblog/@" + blogOwner.getUsername();
            }
        }

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
        User user = userService.getSessionUser(request);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
            return "redirect:/vlog/loginform";
        }

        List<Post> posts = postService.getUserPosts(user.getId(), null, true);
        model.addAttribute("posts", posts);
        return "saves";
    }

    @GetMapping("/liked")
    public String liked(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getSessionUser(request);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
            return "redirect:/vlog/loginform";
        }

        List<Post> likedPosts = postService.getLikedPosts(user.getId());
        model.addAttribute("posts", likedPosts);
        return "liked";
    }
}
