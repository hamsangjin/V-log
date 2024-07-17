package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndParentIsNull(postId);
    }

    @Transactional
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        deleteCommentRecursively(comment);
    }

    private void deleteCommentRecursively(Comment comment) {
        for (Comment reply : comment.getReplies()) {
            deleteCommentRecursively(reply);
        }
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}
