package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final SeriesService seriesService;
    private final UserService userService;

    @GetMapping("/newpost")
    public String newPostForm(Model model) {
        User user = userService.getCurrentUser();

        model.addAttribute("user", user);
        model.addAttribute("post", new Post());
        model.addAttribute("seriesList", seriesService.findAllSeriesByBlogId(user.getBlog().getId()));
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
            RedirectAttributes redirectAttributes) {

        User user = userService.getCurrentUser();

        try {
            postService.createNewPost(user, title, content, thumbnailImageFile, thumbnailText, seriesId, newSeries, tagsString, privacySetting, temporarySetting);
            redirectAttributes.addFlashAttribute("message", "글이 성공적으로 작성되었습니다.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMSG", "썸네일 이미지 업로드 중 오류가 발생했습니다.");
            return "redirect:/vlog/newpost";
        }

        return "redirect:/vlog/myblog/@" + user.getUsername();
    }

    @PostMapping("/myblog/{username}/delete/{postId}")
    public String deletePost(@PathVariable String username, @PathVariable Long postId, RedirectAttributes redirectAttributes) {
        postService.deletePost(postId);
        redirectAttributes.addFlashAttribute("message", "글이 성공적으로 삭제되었습니다.");
        return "redirect:/vlog/myblog/@" + username;
    }

    @GetMapping("/myblog/{username}/edit/{postId}")
    public String editPostForm(@PathVariable String username, @PathVariable Long postId, Model model) {
        User user = userService.getCurrentUser();
        Post post = postService.getPostById(postId);

        if (!user.getUsername().equals(username) || !post.getUserId().equals(user.getId())) {
            return "redirect:/vlog/myblog/@" + username;
        }

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("seriesList", seriesService.findAllSeriesByBlogId(user.getBlog().getId()));
        return "editpost";
    }

    @PostMapping("/myblog/{username}/edit/{postId}")
    public String updatePost(
            @PathVariable String username,
            @PathVariable Long postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("thumbnailImageFile") MultipartFile thumbnailImageFile,
            @RequestParam("thumbnailText") String thumbnailText,
            @RequestParam(value = "seriesId", required = false) Long seriesId,
            @RequestParam(value = "newSeries", required = false) String newSeries,
            @RequestParam("tags") String tagsString,
            @RequestParam(value = "privacySetting", required = false, defaultValue = "false") boolean privacySetting,
            @RequestParam(value = "temporarySetting", required = false, defaultValue = "false") boolean temporarySetting,
            RedirectAttributes redirectAttributes) {

        User user = userService.getCurrentUser();
        try {
            postService.updatePost(user, postId, title, content, thumbnailImageFile, thumbnailText, seriesId, newSeries, tagsString, privacySetting, temporarySetting);
            redirectAttributes.addFlashAttribute("message", "글이 성공적으로 수정되었습니다.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMSG", "썸네일 이미지 업로드 중 오류가 발생했습니다.");
            return "redirect:/vlog/myblog/" + username + "/edit/" + postId;
        }

        return "redirect:/vlog/myblog/@" + username;
    }
}
