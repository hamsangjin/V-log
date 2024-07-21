package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.dto.TagCount;
import hello.velog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final ThumbnailService thumbnailService;
    private final LikeService likeService;

    // home에 관한 코드 시작
    private void addHomeModelAttributes(Model model, User user, List<Post> posts, String activeTab) {
        Map<Long, String> postUsernames = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> postService.getUsernameByUserId(post.getUserId())));

        Map<Long, String> postUserThumbnail = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> postService.getProfileImageByUserId(post.getUserId())));

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("postUsernames", postUsernames);
        model.addAttribute("postUserThumbnail", postUserThumbnail);
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
        addHomeModelAttributes(model, user, posts, "trending");
        return "home";
    }

    @GetMapping("/latest")
    public String latest(Model model) {
        List<Post> posts = postService.getLatestPosts();
        User user = userService.getCurrentUser();
        addHomeModelAttributes(model, user, posts, "latest");
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
            addHomeModelAttributes(model, user, posts, "feed");
        }
        return "home";
    }

    //  ----------------------------------------------------------------------------------------------------------------

    // myblog에 관한 코드 시작
    private void addMyblogModelAttributes(Model model, User user, User blogOwner, Blog blog, String activeTab) {
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

    private void addMyblogPostsModelAttributes(Model model, User user, User blogOwner, String page, List<Post> posts, int postSize, List<TagCount> tagsWithCount) {
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());

        addMyblogModelAttributes(model, user, blogOwner, blog, page);

        model.addAttribute("posts", posts);
        model.addAttribute("postSize", postSize);
        model.addAttribute("tags", postService.getTagsByUser(blogOwner.getId()));
        model.addAttribute("tagsWithCount", tagsWithCount);
    }

    // myblog로 이동 시 posts로 리다이렉트
    @GetMapping("/myblog/@{username}")
    public String myBlog(@PathVariable String username) {
        return "redirect:/vlog/myblog/@" + username + "/posts";
    }

    // posts - 전체보기 선택(기본값)
    @GetMapping("/myblog/@{username}/posts")
    public String myBlogPosts(@PathVariable String username, Model model) {
        // 로그인 유저 및 게시물 작성자 정보 불러오기
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());

        // 게시물 주인이 본인이냐 아니냐에 따라 다르게 return
        List<Post> posts = postService.getUserPosts(blogOwner.getId(), isBlogOwner ? null : false, false);
        int postSize = posts.size();        // 전체 게시물 개수

        // 각각의 태그에 달려있는 게시물 개수
        List<TagCount> tagsWithCount = postService.getTagsWithCountByUser(blogOwner.getId(), isBlogOwner);

        addMyblogPostsModelAttributes(model, user, blogOwner, "posts", posts, postSize, tagsWithCount);
        return "myblog";
    }

    // posts - 특정 태그 선택
    @GetMapping("/myblog/@{username}/posts/tag/{tagName}")
    public String getPostsByTag(@PathVariable String username, @PathVariable String tagName, Model model) {
        // 로그인 유저 및 게시물 작성자 정보 불러오기
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());

        List<Post> posts = postService.getUserPostsByTag(blogOwner.getId(), tagName, isBlogOwner ? null : false, false);
        int postSize = posts.size();

        List<TagCount> tagsWithCount = postService.getTagsWithCountByUser(blogOwner.getId(), isBlogOwner);

        addMyblogPostsModelAttributes(model, user, blogOwner, "posts", posts, postSize, tagsWithCount);
        model.addAttribute("selectedTag", tagName);
        return "myblog";
    }

    // series 목록 출력
    @GetMapping("/myblog/@{username}/series")
    public String myBlogSeries(@PathVariable String username, Model model) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());

        Blog blog = blogService.findBlogByUserId(blogOwner.getId());

        List<Series> seriesList = seriesService.findAllSeriesByBlogId(blog.getId());    // 해당 블로그가 가지고 있는 모든 시리즈 조회
        List<Map<String, Object>> seriesWithThumbnails = new ArrayList<>();             // 시리즈마다 썸네일 사진이 저장되어 있는 Map을 저장할 리스트
        Map<Long, Integer> seriesPostCountMap = new HashMap<>();                        // 시리즈마다 게시물 개수

        // 모든 시리즈 반복
        for (Series series : seriesList) {
            // 시리즈 중 가장 첫 번째 게시물 불러오기
            Post firstPost = seriesService.findFirstPostBySeriesId(series.getId());
            // 첫 번째 게시물의 썸네일 사진 불러오고 없으면 기본 이미지
            String thumbnailImage = firstPost != null ? firstPost.getThumbnailImage() : "/images/no-seriesThumbnail.png";

            // 시리즈: 특정 시리즈, 썸네일 이미지: 특정 썸네일 이미지 저장해 리스트에 추가
            Map<String, Object> seriesMap = new HashMap<>();
            seriesMap.put("series", series);
            seriesMap.put("thumbnailImage", thumbnailImage);
            seriesWithThumbnails.add(seriesMap);

            // 블로그 주인이냐 아니냐에 따라 비공개글까지 출력할지 말지 선택
            seriesPostCountMap.put(series.getId(), seriesService.findPostBySeriesId(series.getId(), isBlogOwner).size());
        }

        addMyblogModelAttributes(model, user, blogOwner, blog, "series");
        model.addAttribute("seriesList", seriesWithThumbnails);
        model.addAttribute("seriesPostCountMap", seriesPostCountMap);
        return "myblog";
    }

    // 특정 시리즈의 게시물들 출력
    @GetMapping("/myblog/@{username}/series/{title}")
    public String myBlogSeriesDetail(@PathVariable String username, @PathVariable String title, Model model) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());

        // 게시물 주인인 경우 비공개글까지 출력
        List<Post> posts = postService.getPostsByBlogIdAndSeriesTitle(blog.getId(), title, isBlogOwner);

        model.addAttribute("user", user);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        return "seriesDetail";
    }

    // 소개
    @GetMapping("/myblog/@{username}/about")
    public String getBlogAbout(@PathVariable String username, Model model) {
        User user = userService.getCurrentUser();
        Blog blog = userService.findBlogByUsername(username);
        User blogOwner = userService.findByUsername(username);

        addMyblogModelAttributes(model, user, blogOwner, blog, "about");

        return "myblog";
    }

    // 소개글 수정 및 작성
    @PostMapping("/myblog/@{username}/about/update")
    public String updateBlogIntro(@PathVariable String username, @RequestParam String intro) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        boolean isBlogOwner = user != null && user.getId().equals(blogOwner.getId());

        if (!isBlogOwner)   return "redirect:/vlog/myblog/@" + username + "/about";

        Blog blog = userService.findBlogByUsername(username);
        blog.setIntro(intro);
        blogService.saveBlog(blog);
        return "redirect:/vlog/myblog/@" + username + "/about";
    }

    //  ----------------------------------------------------------------------------------------------------------------
    // 게시글 상세보기
    @GetMapping("/myblog/@{username}/{id}")
    public String getPost(@PathVariable String username, @PathVariable Long id, Model model) {
        User user = userService.getCurrentUser();
        User blogOwner = userService.findByUsername(username);
        Post post = postService.getPostById(id, user, blogOwner);       // 임시글이거나 비공개 글에 접근하려고 하면 권한 확인
        Blog blog = blogService.findBlogByUserId(blogOwner.getId());
        String htmlContent = markdownService.convertMarkdownToHtml(post.getContent());

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("blog", blog);
        model.addAttribute("blogOwner", blogOwner);
        model.addAttribute("htmlContent", htmlContent);
        model.addAttribute("user", user);

        return "postDetail";
    }

    // 임시글 목록 보기
    @GetMapping("/saves")
    public String save(Model model) {
        User user = userService.getCurrentUser();

        // saves에 접근하면 로그인 된 상태이므로 비공개 글과 상관없이 임시글이 true인 게시물 다 불러오기
        List<Post> posts = postService.getUserPosts(user.getId(), null, true);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "saves";
    }

    // 좋아요한 목록 보기
    @GetMapping("/liked")
    public String liked(Model model) {
        User user = userService.getCurrentUser();

        List<Post> likedPosts = likeService.getLikedPosts(user.getId());
        Map<Long, String> postUsernames = likedPosts.stream()
                .collect(Collectors.toMap(Post::getId, post -> postService.getUsernameByUserId(post.getUserId())));

        model.addAttribute("user", user);
        model.addAttribute("posts", likedPosts);
        model.addAttribute("postUsernames", postUsernames);
        return "liked";
    }


    //  ----------------------------------------------------------------------------------------------------------------

    // 설정에 관한 코드
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
        String imagePath = thumbnailService.uploadThumbnail(image, "USER");
        user.setProfileImage(imagePath);
        userService.saveUser(user);

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
        userService.updateName(user, name);
        blogService.updateInfo(user.getBlog(), info);

        return "redirect:/vlog/settings";
    }

    // 블로그 제목 수정
    @PostMapping("/updateTitle")
    public String updateTitle(@RequestParam("title") String title) {
        User user = userService.getCurrentUser();
        blogService.updateTitle(user.getBlog(), title);

        return "redirect:/vlog/settings";
    }

    // 이메일 변경
    @PostMapping("/updateEmail")
    public String updateEmail(@RequestParam("email") String email) {
        User user = userService.getCurrentUser();
        userService.updateEmail(user, email);

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
