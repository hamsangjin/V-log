package hello.velog.service;

import hello.velog.domain.PostTag;
import hello.velog.repository.PostTagRepository;
import hello.velog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostTagService {
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;

    public void deletePostTagsAndCleanupTagsByPostId(Long postId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        for (PostTag postTag : postTags) {
            Long tagId = postTag.getTagId();
            postTagRepository.delete(postTag);

            // Tag가 다른 PostTag에 사용되지 않으면 삭제
            if (postTagRepository.countByTag_Id(tagId) == 0) {
                tagRepository.deleteById(tagId);
            }
        }
    }
}
