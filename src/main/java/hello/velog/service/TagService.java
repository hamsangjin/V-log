package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public Tag findOrCreateTag(String name, Long blogId) {
        return tagRepository.findByNameAndBlogId(name, blogId)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    Blog blog = new Blog();
                    blog.setId(blogId);
                    newTag.setBlog(blog);
                    return saveTag(newTag);
                });
    }

    @Transactional
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    public List<Tag> processTags(String tagsString, Long blogId) {
        String[] tags = tagsString.split(",");
        List<Tag> tagSet = new ArrayList<>();
        for (String tag : tags) {
            tag = tag.trim();
            if (!tag.isEmpty()) {
                Tag existingTag = findOrCreateTag(tag, blogId);
                tagSet.add(existingTag);
            }
        }
        Collections.reverse(tagSet);
        return tagSet;
    }
}
