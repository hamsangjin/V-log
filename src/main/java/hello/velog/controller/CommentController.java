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

    // 댓글 추가
    @PostMapping("/comments")
    public String addComment(@RequestParam Long postId, @RequestParam String content, @RequestParam(required = false) Long parentId) {
        User user = userService.getCurrentUser();       // 현재 인증된 유저
        Post post = postService.getPostById(postId);    // postId에 해당하는 게시물

        // 부모 댓글
        Comment parentComment = parentId != null ? commentService.getCommentById(parentId) : null;

        Comment comment = new Comment(post, user, content, parentComment);
        commentService.saveComment(comment);

        User blogOwner = userService.getUserByPostId(comment.getPost().getId());

        return "redirect:/vlog/myblog/@" + blogOwner.getUsername() + "/" + postId;
    }

    // 댓글 삭제
    @PostMapping("/comments/delete")
    public String deleteComment(@RequestParam Long commentId, @RequestParam Long postId) {
        User user = userService.getCurrentUser();
        Comment comment = commentService.getCommentById(commentId);
        User blogOwner = userService.getUserByPostId(comment.getPost().getId());

        // 댓글 삭제 권한 확인
        commentService.isCommentOwner(user.getId(), comment.getUser().getId());
        commentService.isPostOwner(user.getId(), comment.getPost().getId());

        commentService.deleteComment(commentId);

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
