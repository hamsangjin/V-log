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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final PostTagRepository postTagRepository;
    private final SeriesService seriesService;
    private final TagService tagService;
    private final CommentRepository commentRepository;


    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<Post> findPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getUserPosts(Long userId, Boolean privacySetting, Boolean temporarySetting) {
        List<Post> posts;
        if (privacySetting != null) {
            posts = postRepository.findByUserIdAndPrivacySettingAndTemporarySettingOrderByCreatedAtDesc(
                    userId, privacySetting, temporarySetting);
        } else {
            posts = postRepository.findByUserIdAndTemporarySettingOrderByCreatedAtDesc(userId, temporarySetting);
        }

        return posts.stream().peek(post -> {
            post.setTags(new ArrayList<>(tagRepository.findByPostsId(post.getId())));
            post.setLikes(new ArrayList<>(likeRepository.findByPostId(post.getId())));
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // 포스트 상세보기를 위한 메소드
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Invalid post ID"));

        commentRepository.deleteByPostId(postId);

        // likes 삭제
        likeRepository.deleteByPostId(postId);

        // postTags 삭제 및 tag 정리
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        for (PostTag postTag : postTags) {
            Long tagId = postTag.getTagId();
            postTagRepository.delete(postTag);
            // Tag가 다른 PostTag에 사용되지 않으면 삭제
            if (postTagRepository.countByTag_Id(tagId) == 0) {
                tagRepository.deleteById(tagId);
            }
        }

        // post 삭제
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<Post> getLikedPosts(Long userId) {
        return likeRepository.findLikedPostsByUserId(userId);
    }

    @Transactional
    public String handleThumbnailImageUpload(MultipartFile thumbnailImageFile) throws IOException {
        String thumbnailImagePath = "/images/post/default-image.png";
        if (!thumbnailImageFile.isEmpty()) {
            String uploadDir = "/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/post/";
            String uuid = UUID.randomUUID().toString();
            String originalFilename = thumbnailImageFile.getOriginalFilename();
            String storedFilename = uuid + "_" + originalFilename;

            File destFile = new File(uploadDir + storedFilename);
            thumbnailImageFile.transferTo(destFile);

            thumbnailImagePath = "/images/post/" + storedFilename;
        }
        return thumbnailImagePath;
    }

    @Transactional(readOnly = true)
    public List<Post> getTrendingPosts() {
        return postRepository.findAllByOrderByLikesDesc();
    }

    @Transactional(readOnly = true)
    public List<Post> getLatestPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsFromFollowedUsers(List<Long> followedUserIds) {
        return postRepository.findByUserIdInAndOrderByCreatedAtDesc(followedUserIds);
    }

    @Transactional(readOnly = true)
    public String getUsernameByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getUsername() : null;
    }

    @Transactional
    public void createNewPost(User user, String title, String content, MultipartFile thumbnailImageFile, String thumbnailText, Long seriesId, String newSeries, String tagsString, boolean privacySetting, boolean temporarySetting) throws IOException {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setThumbnailText(thumbnailText);
        post.setPrivacySetting(privacySetting);
        post.setTemporarySetting(temporarySetting);

        Series series = seriesService.handleSeriesCreationOrUpdate(newSeries, seriesId, user);
        post.setSeries(series);

        List<Tag> tags = tagService.processTags(tagsString, user.getBlog().getId());
        post.setTags(tags);

        String thumbnailImagePath = handleThumbnailImageUpload(thumbnailImageFile);
        post.setThumbnailImage(thumbnailImagePath);

        post.setUserId(user.getId());

        savePost(post);
    }

    @Transactional
    public void updatePost(User user, Long postId, String title, String content, MultipartFile thumbnailImageFile, String thumbnailText, Long seriesId, String newSeries, String tagsString, boolean privacySetting, boolean temporarySetting) throws IOException {
        Post post = getPostById(postId);

        post.setTitle(title);
        post.setContent(content);
        post.setThumbnailText(thumbnailText);
        post.setPrivacySetting(privacySetting);
        post.setTemporarySetting(temporarySetting);

        Series series = seriesService.handleSeriesCreationOrUpdate(newSeries, seriesId, user);
        post.setSeries(series);

        List<Tag> tags = tagService.processTags(tagsString, user.getBlog().getId());
        post.setTags(tags);

        if (!thumbnailImageFile.isEmpty()) {
            String thumbnailImagePath = handleThumbnailImageUpload(thumbnailImageFile);
            post.setThumbnailImage(thumbnailImagePath);
        }

        savePost(post);
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
}
