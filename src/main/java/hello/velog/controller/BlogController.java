package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.dto.TagCount;
import hello.velog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    // home에 관한 코드 시작

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
        User user = userService.getCurrentUser();
        addCommonAttributes(model, user, posts, "trending");
        return "home";
    }

    @GetMapping("/latest")
    public String latest(Model model) {
        List<Post> posts = postService.getLatestPosts();
        User user = userService.getCurrentUser();
        addCommonAttributes(model, user, posts, "latest");
        return "home";
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        User user = userService.getCurrentUser();
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

    // myblog에 관한 코드 시작

    private void addCommonAttributes(Model model, User user, User blogOwner, Blog blog, String activeTab) {
        long followerCount = followService.getFollowerCount(blogOwner.getId());
        long followingCount = followService.getFollowingCount(blogOwner.getId());
        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());

        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("user", user);
    }

    @GetMapping("/myblog/@{username}")
    public String myBlog(@PathVariable String username) {
        return "redirect:/vlog/myblog/@" + username + "/posts";
    }

    @GetMapping("/myblog/@{username}/posts")
    public String myBlogPosts(@PathVariable String username, Model model) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());

        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());
        List<Post> posts = postService.getUserPosts(blogOwner.getId(), isBlogOwner ? null : false, false);
        List<TagCount> tagsWithCount = postService.getTagsWithCountByUser(blogOwner.getId(), isBlogOwner);
        int postSize = postService.getUserPosts(blogOwner.getId(), isBlogOwner ? null : false, false).size();

        addCommonAttributes(model, user, blogOwner, blog, "posts");
        model.addAttribute("tags", postService.getTagsByUser(blogOwner.getId()));  // 태그 목록 추가
        model.addAttribute("tagsWithCount", tagsWithCount);  // 태그와 게시물 개수 추가
        model.addAttribute("posts", posts);
        model.addAttribute("postSize", postSize);

        return "myblog";
    }

    @GetMapping("/myblog/@{username}/posts/tag/{tagName}")
    public String getPostsByTag(@PathVariable String username, @PathVariable String tagName, Model model) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());

        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());
        List<Post> posts = postService.getUserPostsByTag(blogOwner.getId(), tagName, isBlogOwner ? null : false, false);
        int postSize = postService.getUserPosts(blogOwner.getId(), isBlogOwner ? null : false, false).size();

        List<TagCount> tagsWithCount = postService.getTagsWithCountByUser(blogOwner.getId(), isBlogOwner);

        addCommonAttributes(model, user, blogOwner, blog, "posts");
        model.addAttribute("posts", posts);
        model.addAttribute("postSize", postSize);
        model.addAttribute("tags", postService.getTagsByUser(blogOwner.getId()));  // 태그 목록 추가
        model.addAttribute("tagsWithCount", tagsWithCount);  // 태그와 게시물 개수 추가
        model.addAttribute("selectedTag", tagName);

        return "myblog";
    }

    @GetMapping("/myblog/@{username}/series")
    public String myBlogSeries(@PathVariable String username, Model model) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());

        List<Series> seriesList = seriesService.findAllSeriesByBlogId(blog.getId());
        List<Map<String, Object>> seriesWithThumbnails = new ArrayList<>();
        for (Series series : seriesList) {
            Post firstPost = seriesService.findFirstPostBySeriesId(series.getId());
            String thumbnailImage = firstPost != null ? firstPost.getThumbnailImage() : "/images/post/default-image.png";
            Map<String, Object> seriesMap = new HashMap<>();
            seriesMap.put("series", series);
            seriesMap.put("thumbnailImage", thumbnailImage);
            seriesWithThumbnails.add(seriesMap);
        }

        addCommonAttributes(model, user, blogOwner, blog, "series");
        model.addAttribute("seriesList", seriesWithThumbnails);

        return "myblog";
    }

    @GetMapping("/myblog/@{username}/about")
    public String getBlogAbout(@PathVariable String username, Model model) {
        User user = userService.getCurrentUser();
        Blog blog = blogService.findBlogByUsername(username);

        addCommonAttributes(model, user, blog.getUser(), blog, "about");

        return "myblog";
    }

    @PostMapping("/myblog/@{username}/about/update")
    public String updateBlogIntro(@PathVariable String username, @RequestParam String intro) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());

        if (!isBlogOwner)   return "redirect:/vlog/myblog/@" + username + "/about";

        Blog blog = blogService.findBlogByUsername(username);
        blog.setIntro(intro);
        blogService.saveBlog(blog);
        return "redirect:/vlog/myblog/@" + username + "/about";
    }

    @GetMapping("/myblog/@{username}/{id}")
    public String getPost(@PathVariable String username, @PathVariable Long id, Model model) {
        User user = userService.getCurrentUser();

        Post post = postService.getPostById(id);
        User blogOwner = userService.findById(post.getUserId());
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());

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
        model.addAttribute("user", user);

        return "postDetail";
    }

    @GetMapping("/saves")
    public String save(Model model) {
        User user = userService.getCurrentUser();

        List<Post> posts = postService.getUserPosts(user.getId(), null, true);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "saves";
    }

    @GetMapping("/liked")
    public String liked(Model model) {
        User user = userService.getCurrentUser();

        List<Post> likedPosts = postService.getLikedPosts(user.getId());
        Map<Long, String> postUsernames = likedPosts.stream()
                .collect(Collectors.toMap(Post::getId, post -> postService.getUsernameByUserId(post.getUserId())));

        model.addAttribute("user", user);
        model.addAttribute("posts", likedPosts);
        model.addAttribute("postUsernames", postUsernames);
        return "liked";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        User user = userService.getCurrentUser();
        Blog blog = blogService.findBlogByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        return "settings";
    }

    // 이미지 업로드
    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("image") MultipartFile image) {
        User user = userService.getCurrentUser();
        if (!image.isEmpty()) {
            String imagePath = null;
            try {
                imagePath = postService.handleThumbnailImageUpload(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            user.setProfileImage(imagePath);
            userService.saveUser(user);
        }
        return "redirect:/vlog/settings";
    }

    // 이미지 제거
    @PostMapping("/removeImage")
    public String removeImage() {
        User user = userService.getCurrentUser();
        user.setProfileImage("/images/user/default-image.png");
        userService.saveUser(user);
        return "redirect:/vlog/settings";
    }

    // 이름 및 info 수정
    @PostMapping("/updateNameInfo")
    public String updateNameInfo(@RequestParam("name") String name, @RequestParam("info") String info) {
        User user = userService.getCurrentUser();
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        user.getBlog().setInfo(info);
        userService.saveUser(user);
        blogService.saveBlog(user.getBlog());
        return "redirect:/vlog/settings";
    }

    // 블로그 제목 수정
    @PostMapping("/updateTitle")
    public String updateTitle(@RequestParam("title") String title) {
        User user = userService.getCurrentUser();
        if (title != null && !title.trim().isEmpty()) {
            user.getBlog().setTitle(title);
            blogService.saveBlog(user.getBlog());
        }
        return "redirect:/vlog/settings";
    }

    // 이메일 변경
    @PostMapping("/updateEmail")
    public String updateEmail(@RequestParam("email") String email) {
        User user = userService.getCurrentUser();
        if (email != null && !email.trim().isEmpty() && !userService.isEmailTaken(email)) {
            user.setEmail(email);
            userService.saveUser(user);
        }
        return "redirect:/vlog/settings";
    }

    // 회원 탈퇴
    @PostMapping("/deleteUser")
    public String deleteUser() {
        User user = userService.getCurrentUser();
        userService.deleteUser(user.getId());
        return "redirect:/vlog/logout";
    }
}
