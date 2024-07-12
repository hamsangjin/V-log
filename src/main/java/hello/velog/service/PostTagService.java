package hello.velog.service;

import hello.velog.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostTagService {
    private final PostTagRepository postTagRepository;
}
