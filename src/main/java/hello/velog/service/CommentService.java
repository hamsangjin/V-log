package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.exception.*;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
    }

    @Transactional
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 댓글 작성자인지 확인
    @Transactional(readOnly = true)
    public boolean isCommentOwner(Long userId, Long OwnerId) {
        return userId.equals(OwnerId);
    }

    // 게시글 작성자인지 확인
    @Transactional(readOnly = true)
    public boolean isPostOwner(Long userId, Long OwnerId) {
        return userId.equals(OwnerId);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        deleteCommentRecursively(comment);
    }

    // 삭제할 댓글(답글)에 달려있는 답글들까지 연쇄 삭제
    @Transactional
    public void deleteCommentRecursively(Comment comment) {
        for (Comment reply : comment.getReplies()) {
            deleteCommentRecursively(reply);
        }
        commentRepository.delete(comment);
    }

    // 게시물에 달려있는 댓글 삭제
    @Transactional
    public void deleteByPostId(Long postId) {
        commentRepository.deleteByPostId(postId);
    }

    // 탈퇴할 유저가 작성한 댓글삭제
    @Transactional
    public void deleteCommentsByUser(Long userId) {
        List<Comment> byUserIdComment = commentRepository.findByUserId(userId);
        for (Comment comment : byUserIdComment) {
            deleteComment(comment.getId());
        }
    }
}
