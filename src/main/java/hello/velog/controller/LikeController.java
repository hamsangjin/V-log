package hello.velog.controller;

import hello.velog.domain.User;
import hello.velog.service.LikeService;
import hello.velog.service.UserService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    @PostMapping("/like")
    public ResponseEntity<?> handleLike(@RequestParam Long postId, HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        if (user == null)   return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"User must be logged in\"}");

        boolean isLiked = likeService.toggleLike(postId, user);
        return ResponseEntity.ok("{\"status\": \"" + (isLiked ? "Liked" : "Unliked") + "\"}");
    }

    @GetMapping("/like/status")
    public ResponseEntity<?> checkLikeStatus(@RequestParam Long postId, HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        if (user == null)   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"status\": \"Not Logged In\"}");

        boolean isLiked = likeService.isLikedByUser(postId, user);
        return ResponseEntity.ok("{\"liked\": " + isLiked + "}");
    }

}
