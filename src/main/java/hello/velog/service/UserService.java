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

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final FollowService followService;
    private final SeriesService seriesService;
    private final BlogService blogService;


    // 회원가입 관련
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

    // user 저장 및 수정
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 회원가입 시 역할 할당
    private void assignRoleToUser(User user, String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        role.ifPresent(r -> {
            UserRole userRole = new UserRole(user, r);
            userRoleRepository.save(userRole);
        });
    }

    // username 중복 확인
    @Transactional(readOnly = true)
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // email 중복 확인
    @Transactional(readOnly = true)
    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    // 로그인 관련 로직
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 시큐리티 유저 인증 정보
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return findByUsername(userDetails.getUsername());
        }
        return null;
    }

    // 유저 프로필 이미지 변경
    @Transactional
    public String handleProfileImageUpload(MultipartFile profileImage){
        String profileImagePath = "/images/user/default-image.png";
        if (!profileImage.isEmpty()) {
            String uploadDir = "/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/user/";
            String uuid = UUID.randomUUID().toString();
            String originalFilename = profileImage.getOriginalFilename();
            String storedFilename = uuid + "_" + originalFilename;

            File destFile = new File(uploadDir + storedFilename);
            try {
                profileImage.transferTo(destFile);
            } catch (IOException e) {
                throw new ImageUploadException("이미지 업로드 중 오류가 발생했습니다.");
            }

            profileImagePath = "/images/user/" + storedFilename;
        }
        return profileImagePath;
    }

    // 회원 탈퇴 로직
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Invalid user Id:" + userId));

        // 게시물 삭제 시 게시물과 연결된 댓글, 좋아요, 태그 삭제됨
        postService.deletePostsByUser(user);
        // 본인이 작성하지 않은 게시물의 댓글 및 답글 삭제
        commentService.deleteCommentsByUser(userId);
        // 본인이 작성하지 않은 게시물의 좋아요 삭제
        likeService.deleteLikesByUser(userId);
        // 본인을 팔로우하거나 본인이 팔로우한 정보 삭제
        followService.deleteFollowsByUser(userId);
        // 유저와 연결된 블로그 및 팔로우 삭제
        blogService.deleteBlogsAndSeriesByUser(userId);
        // 유저 삭제
        userRepository.delete(user);
    }











    // 게시물 작성자 정보 리턴
    @Transactional(readOnly = true)
    public User getUserByPostId(Long postId) {
        Long userId = postRepository.findUserIdByPostId(postId);
        return userRepository.findById(userId).orElse(null);
    }
}
