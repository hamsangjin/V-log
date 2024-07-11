package hello.velog.service;

import hello.velog.domain.Blog;
import hello.velog.domain.Tag;
import hello.velog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag findOrCreateTag(String name, Long blogId) {
        // Optional 객체에서 태그를 찾거나, 없다면 새 태그를 생성합니다.
        return tagRepository.findByNameAndBlogId(name, blogId)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    // 여기서는 예시를 위해 Blog 객체 생성 로직을 간단히 처리합니다.
                    // 실제로는 Blog를 찾거나 다른 방식으로 관리해야 할 수 있습니다.
                    Blog blog = new Blog(); // 적절한 Blog 생성 또는 찾기 로직이 필요
                    blog.setId(blogId);
                    newTag.setBlog(blog);
                    return saveTag(newTag); // 새 태그를 저장하고 반환
                });
    }

    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }
}