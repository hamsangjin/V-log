package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.dto.TagCount;
import hello.velog.exception.*;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    private final SeriesService seriesService;
    private final TagService tagService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final PostTagService postTagService;
    private final ThumbnailService thumbnailService;

    private void preparePost(Post post, User user, String title, String content, String thumbnailText, boolean privacySetting, boolean temporarySetting, MultipartFile thumbnailImageFile, Long seriesId, String newSeries, String tagsString) throws IOException {
        post.setTitle(title);
        post.setContent(content);
        post.setThumbnailText(thumbnailText);
        post.setPrivacySetting(privacySetting);
        post.setTemporarySetting(temporarySetting);
        post.setUserId(user.getId());

        Series series = seriesService.handleSeriesCreationOrUpdate(newSeries, seriesId, user);
        post.setSeries(series);

        List<Tag> tags = tagService.processTags(tagsString, user.getBlog().getId());
        post.setTags(tags);

        if (!thumbnailImageFile.isEmpty()) {
            String thumbnailImagePath = thumbnailService.uploadThumbnail(thumbnailImageFile);
            post.setThumbnailImage(thumbnailImagePath);
        }
    }

    // 게시물 작성
    @Transactional
    public void createNewPost(User user, String title, String content, MultipartFile thumbnailImageFile, String thumbnailText, Long seriesId, String newSeries, String tagsString, boolean privacySetting, boolean temporarySetting) throws IOException {
        Post post = new Post();
        preparePost(post, user, title, content, thumbnailText, privacySetting, temporarySetting, thumbnailImageFile, seriesId, newSeries, tagsString);
        postRepository.save(post);
    }

    // 게시물 수정
    @Transactional
    public void updatePost(User user, Long postId, String title, String content, MultipartFile thumbnailImageFile, String thumbnailText, Long seriesId, String newSeries, String tagsString, boolean privacySetting, boolean temporarySetting) throws IOException {
        Post post = getPostById(postId);
        preparePost(post, user, title, content, thumbnailText, privacySetting, temporarySetting, thumbnailImageFile, seriesId, newSeries, tagsString);
        postRepository.save(post);
    }

    // 게시글 좋아요 순으로 조회
    @Transactional(readOnly = true)
    public List<Post> getTrendingPosts() {
        return postRepository.findAllByOrderByLikesDesc();
    }

    // 게시글 작성 일자 순으로 조회
    @Transactional(readOnly = true)
    public List<Post> getLatestPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // 팔로우한 유저들의 게시물만 조회
    @Transactional(readOnly = true)
    public List<Post> getPostsFromFollowedUsers(List<Long> followedUserIds) {
        return postRepository.findByUserIdInAndOrderByCreatedAtDesc(followedUserIds);
    }

    // http://localhost:8080/vlog/myblog/{uesrname}/posts 접속 시
    @Transactional(readOnly = true)
    public List<Post> getUserPosts(Long userId, Boolean privacySetting, Boolean temporarySetting) {
        if (privacySetting != null) {
            // null인 경우: 본인의 블로그를 보는 경우
            return postRepository.findByUserIdAndPrivacySettingAndTemporarySettingOrderByCreatedAtDesc(userId, privacySetting, temporarySetting);
        } else {
            // null이 아닌 경우: 본인이 아닌 사람이 보거나 로그아웃 상태에서 보는 경우
            return postRepository.findByUserIdAndTemporarySettingOrderByCreatedAtDesc(userId, temporarySetting);
        }
    }

    // 포스트 상세보기를 위한 메소드(블로그 주인이냐에 따라 다르게 액세스)
    @Transactional(readOnly = true)
    public Post getPostById(Long id, User user, User blogOwner) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시물을 찾지 못했습니다."));
        boolean isBlogOwner = user == null || !user.getId().equals(blogOwner.getId());

        if ((post.getPrivacySetting() || post.getTemporarySetting()) && isBlogOwner) {
            throw new NotBlogOwnerException("게시물의 작성자 또는 블로그 주인이 아닙니다.");
        }

        return post;
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시물을 찾지 못했습니다."));
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시물을 찾지 못했습니다."));

        commentService.deleteByPostId(postId);                          // 댓글 및 답글 삭제
        likeService.deleteByPostId(postId);                             // likes 삭제
        postTagService.deletePostTagsAndCleanupTagsByPostId(postId);    // postTags 삭제 및 tag 정리
        postRepository.delete(post);                                    // post 삭제
    }

    @Transactional(readOnly = true)
    public List<Post> getLikedPosts(Long userId) {
        return likeRepository.findLikedPostsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public String getUsernameByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getUsername() : null;
    }

    @Transactional(readOnly = true)
    public String getProfileImageByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getProfileImage() : null;
    }

    @Transactional(readOnly = true)
    public List<Post> getUserPostsByTag(Long userId, String tagName, Boolean privacySetting, Boolean temporarySetting) {
        return postRepository.findByUserIdAndTagName(userId, tagName, privacySetting, temporarySetting);
    }

    @Transactional(readOnly = true)
    public List<String> getTagsByUser(Long userId) {
        return postRepository.findTagsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<TagCount> getTagsWithCountByUser(Long userId, boolean isBlogOwner) {
        return postRepository.findTagsWithCountByUserId(userId, isBlogOwner);
    }

    // 시리즈와 연결된 게시물들 조회(게시물 주인이냐 아니냐에 따라 다르게 조회)
    @Transactional(readOnly = true)
    public List<Post> getPostsByBlogIdAndSeriesTitle(Long blogId, String title, boolean isBlogOwner) {
        if(isBlogOwner)     return postRepository.findPostsByBlogIdAndSeriesTitle(blogId, title, null);
        else                return postRepository.findPostsByBlogIdAndSeriesTitle(blogId, title, false);
    }

    // 탈퇴할 유저 게시물 삭제
    @Transactional
    public void deletePostsByUser(User user) {
        List<Post> byUserIdPost = postRepository.findByUserId(user.getId());
        for (Post post : byUserIdPost) {
            deletePost(post.getId());
        }
    }
}
