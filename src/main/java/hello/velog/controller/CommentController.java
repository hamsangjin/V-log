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

        User blogOwner = userService.getUserByPostId(comment.getPost().getId());

        return "redirect:/vlog/myblog/@" + blogOwner.getUsername() + "/" + postId;
    }

    @PostMapping("/comments/delete")
    public String deleteComment(@RequestParam Long commentId, @RequestParam Long postId) {
        User currentUser = userService.getCurrentUser();
        Comment comment = commentService.getCommentById(commentId);
        User blogOwner = userService.getUserByPostId(comment.getPost().getId());

        if (currentUser.getId().equals(comment.getUser().getId()) || currentUser.getId().equals(comment.getPost().getUserId())) {
            commentService.deleteComment(commentId);
        } else {
            throw new RuntimeException("Unauthorized action");
        }
        return "redirect:/vlog/myblog/@" + blogOwner.getUsername() + "/" + postId;
    }

    @PostMapping("/comments/update")
    public String updateComment(@RequestParam Long commentId, @RequestParam Long postId, @RequestParam String content) {
        Comment comment = commentService.getCommentById(commentId);

        User blogOwner = userService.getUserByPostId(comment.getPost().getId());
        comment.setContent(content);
        commentService.saveComment(comment);

        return "redirect:/vlog/myblog/@" + blogOwner.getUsername() + "/" + postId;
    }
}
