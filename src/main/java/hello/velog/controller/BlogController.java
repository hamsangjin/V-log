package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class BlogController {
    private final UserService userService;
    private final PostService postService;
    private final BlogService blogService;
    private final SeriesService seriesService;
    private final MarkdownService markdownService;
    private final FollowService followService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername());
        }
        return null;
    }

    private void addCommonAttributes(Model model, User user, List<Post> posts, String activeTab) {
        Map<Long, String> postUsernames = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> postService.getUsernameByUserId(post.getUserId())));

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("postUsernames", postUsernames);
        model.addAttribute("activeTab", activeTab);
    }

    @GetMapping
    public String home() {
        return "redirect:/vlog/trending";
    }

    @GetMapping("/trending")
    public String trending(Model model) {
        List<Post> posts = postService.getTrendingPosts();
        User user = getCurrentUser();
        addCommonAttributes(model, user, posts, "trending");
        return "home";
    }

    @GetMapping("/latest")
    public String latest(Model model) {
        List<Post> posts = postService.getLatestPosts();
        User user = getCurrentUser();
        addCommonAttributes(model, user, posts, "latest");
        return "home";
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        User user = getCurrentUser();
        if (user == null) {
            model.addAttribute("user", null);
            model.addAttribute("posts", Collections.emptyList());
            model.addAttribute("activeTab", "feed");
        } else {
            List<Follow> follows = followService.findByFollower(user);
            List<Long> followedUserIds = follows.stream()
                    .map(follow -> follow.getFollowee().getId())
                    .collect(Collectors.toList());
            List<Post> posts = postService.getPostsFromFollowedUsers(followedUserIds);
            addCommonAttributes(model, user, posts, "feed");
        }
        return "home";
    }

    @GetMapping("/myblog/@{username}")
    public String myBlog(@PathVariable String username, Model model) {
        return "redirect:/vlog/myblog/@" + username + "/posts";
    }

    @GetMapping("/myblog/@{username}/posts")
    public String myBlogPosts(@PathVariable String username, Model model) {
        User user = getCurrentUser();
        User blogOwner = userService.findByUsername(username);

        List<Post> posts;
        if (user != null && user.getId().equals(blogOwner.getId())) {
            posts = postService.getUserPosts(blogOwner.getId(), null, false);
        } else {
            posts = postService.getUserPosts(blogOwner.getId(), false, false);
        }

        long followerCount = followService.getFollowerCount(blogOwner.getId());
        long followingCount = followService.getFollowingCount(blogOwner.getId());

        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("activeTab", "posts");
        model.addAttribute("isBlogOwner", user != null && user.getId().equals(blogOwner.getId()));
        model.addAttribute("sessionUser", user);
        return "myblog";
    }

    @GetMapping("/myblog/@{username}/series")
    public String myBlogSeries(@PathVariable String username, Model model) {
        User user = getCurrentUser();
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

        long followerCount = followService.getFollowerCount(blogOwner.getId());
        long followingCount = followService.getFollowingCount(blogOwner.getId());

        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        model.addAttribute("blog", blog);
        model.addAttribute("seriesList", seriesWithThumbnails);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("activeTab", "series");
        model.addAttribute("isBlogOwner", user != null && user.getId().equals(blogOwner.getId()));
        model.addAttribute("sessionUser", user);
        return "myblog";
    }

    @GetMapping("/myblog/@{username}/about")
    public String getBlogAbout(@PathVariable String username, Model model) {
        Blog blog = blogService.findBlogByUsername(username);
        User user = getCurrentUser();
        boolean isBlogOwner = user != null && user.getUsername().equals(username);

        long followerCount = followService.getFollowerCount(blog.getUser().getId());
        long followingCount = followService.getFollowingCount(blog.getUser().getId());

        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blog.getUser());
        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("username", username);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("activeTab", "about");
        model.addAttribute("sessionUser", user);
        return "myblog";
    }

    @PostMapping("/myblog/@{username}/about/update")
    public String updateBlogIntro(@PathVariable String username, @RequestParam String intro) {
        User user = getCurrentUser();
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
    public String getPost(@PathVariable String username, @PathVariable Long id, Model model) {
        User user = getCurrentUser();

        Post post = postService.getPostById(id);
        User blogOwner = userService.findById(post.getUserId());
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());

        if (!blogOwner.getUsername().equals(username)) {
            return "redirect:/vlog/myblog/@" + username;
        }

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
        model.addAttribute("sessionUser", user);

        return "postDetail";
    }

    @GetMapping("/saves")
    public String save(Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
            return "redirect:/vlog/loginform";
        }

        List<Post> posts = postService.getUserPosts(user.getId(), null, true);
        model.addAttribute("posts", posts);
        return "saves";
    }

    @GetMapping("/liked")
    public String liked(Model model, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
            return "redirect:/vlog/loginform";
        }

        List<Post> likedPosts = postService.getLikedPosts(user.getId());
        Map<Long, String> postUsernames = likedPosts.stream()
                .collect(Collectors.toMap(Post::getId, post -> postService.getUsernameByUserId(post.getUserId())));

        model.addAttribute("posts", likedPosts);
        model.addAttribute("postUsernames", postUsernames);
        return "liked";
    }
}
