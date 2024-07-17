package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @PostMapping("/comments")
    public String addComment(@RequestParam Long postId, @RequestParam String content, @RequestParam(required = false) Long parentId) {
        User user = userService.getCurrentUser();
        Post post = postService.getPostById(postId);
        Comment parentComment = parentId != null ? commentService.getCommentById(parentId) : null;

        Comment comment = new Comment(post, user, content, parentComment);
        commentService.saveComment(comment);

        return "redirect:/vlog/myblog/@" + user.getUsername() + "/" + postId;
    }

    @PostMapping("/comments/delete")
    public String deleteComment(@RequestParam Long commentId, @RequestParam Long postId) {
        commentService.deleteComment(commentId);
        User user = userService.getCurrentUser();
        return "redirect:/vlog/myblog/@" + user.getUsername() + "/" + postId;
    }
}
