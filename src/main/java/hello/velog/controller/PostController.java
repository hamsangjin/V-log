package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final SeriesService seriesService;
    private final TagService tagService;
    private final UserService userService;

    @GetMapping("/newpost")
    public String newPostForm(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = getSessionUser(request, redirectAttributes);
        if (user == null) return "redirect:/vlog/loginform";

        model.addAttribute("user", user);
        model.addAttribute("post", new Post());
        model.addAttribute("seriesList", seriesService.findAllSeries());
        return "newpost";
    }

    @PostMapping("/newpost")
    public String createNewPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("thumbnailImageFile") MultipartFile thumbnailImageFile,
            @RequestParam("thumbnailText") String thumbnailText,
            @RequestParam(value = "seriesId", required = false) Long seriesId,
            @RequestParam(value = "newSeries", required = false) String newSeries,
            @RequestParam("tags") String tagsString,
            @RequestParam(value = "privacySetting", required = false, defaultValue = "false") boolean privacySetting,
            @RequestParam(value = "temporarySetting", required = false, defaultValue = "false") boolean temporarySetting,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User user = getSessionUser(request, redirectAttributes);
        if (user == null) return "redirect:/vlog/loginform";

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setThumbnailText(thumbnailText);
        post.setPrivacySetting(privacySetting);
        post.setTemporarySetting(temporarySetting);

        Series series = seriesService.handleSeriesCreationOrUpdate(newSeries, seriesId, user);
        post.setSeries(series);

        List<Tag> tags = tagService.processTags(tagsString, user.getBlog().getId());
        post.setTags(tags);

        try {
            String thumbnailImagePath = postService.handleThumbnailImageUpload(thumbnailImageFile);
            post.setThumbnailImage(thumbnailImagePath);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMSG", "썸네일 이미지 업로드 중 오류가 발생했습니다.");
            return "redirect:/vlog/newpost";
        }

        post.setUserId(user.getId());

        postService.savePost(post);
        redirectAttributes.addFlashAttribute("message", "글이 성공적으로 작성되었습니다.");
        return "redirect:/vlog/myblog/@" + user.getUsername();
    }

    @PostMapping("/myblog/{username}/delete/{postId}")
    public String deletePost(@PathVariable String username, @PathVariable Long postId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = userService.getSessionUser(request);
        if (user == null || !user.getUsername().equals(username)) {
            redirectAttributes.addFlashAttribute("errorMSG", "삭제 권한이 없습니다.");
            return "redirect:/vlog/loginform";
        }

        postService.deletePost(postId);
        redirectAttributes.addFlashAttribute("message", "글이 성공적으로 삭제되었습니다.");
        return "redirect:/vlog/myblog/@" + username;
    }

    private User getSessionUser(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = userService.getSessionUser(request);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMSG", "로그인이 필요한 기능입니다.");
        }
        return user;
    }
}
