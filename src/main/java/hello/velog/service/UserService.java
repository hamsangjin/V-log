package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.exception.*;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final BlogRepository blogRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final PostService postService;
    private final CommentService commentService;

    @Transactional
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Blog blog = new Blog(user, user.getName() + "의 블로그");
        user.setBlog(blog);
        User savedUser = userRepository.save(user);

        blogRepository.save(blog);

        assignRoleToUser(savedUser, "USER");
        return savedUser;
    }

    private void assignRoleToUser(User user, String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        role.ifPresent(r -> {
            UserRole userRole = new UserRole(user, r);
            userRoleRepository.save(userRole);
        });
    }

    @Transactional(readOnly = true)
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(Long userId) {
        return userRepository.countByFollowers_Id(userId);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(Long userId) {
        return userRepository.countByFollowing_Id(userId);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return findByUsername(userDetails.getUsername());
        }
        return null;
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }


    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Invalid user Id:" + userId));

        deletePostsByUser(user);
        deleteCommentsByUser(userId);
        deleteLikesByUser(userId);
        deleteFollowsByUser(userId);
        deleteBlogsByUser(userId);

        // 유저 삭제는 마지막에 수행
        userRepository.delete(user);
    }

    @Transactional
    public void deletePostsByUser(User user) {
        List<Post> byUserIdPost = postRepository.findByUserId(user.getId());
        for (Post post : byUserIdPost) {
            postService.deletePost(post.getId());
        }
    }

    @Transactional
    public void deleteCommentsByUser(Long userId) {
        List<Comment> byUserIdComment = commentRepository.findByUserId(userId);
        for (Comment comment : byUserIdComment) {
            commentService.deleteComment(comment.getId());
        }
    }

    @Transactional
    public void deleteLikesByUser(Long userId) {
        likeRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteFollowsByUser(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        for (Follow follow : follows) {
            followRepository.delete(follow);
        }
        follows = followRepository.findByFolloweeId(userId); // Followee로 설정된 경우도 삭제
        for (Follow follow : follows) {
            followRepository.delete(follow);
        }
    }

    @Transactional
    public void deleteBlogsByUser(Long userId) {
        blogRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public User getUserByPostId(Long postId) {
        Long userId = postRepository.findUserIdByPostId(postId);
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public String handleProfileImageUpload(MultipartFile profileImage) throws IOException {
        String profileImagePath = "/images/user/default-image.png";
        if (!profileImage.isEmpty()) {
            String uploadDir = "/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/user/";
            String uuid = UUID.randomUUID().toString();
            String originalFilename = profileImage.getOriginalFilename();
            String storedFilename = uuid + "_" + originalFilename;

            File destFile = new File(uploadDir + storedFilename);
            profileImage.transferTo(destFile);

            profileImagePath = "/images/user/" + storedFilename;
        }
        return profileImagePath;
    }
}
