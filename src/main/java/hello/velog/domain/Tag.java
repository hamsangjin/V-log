package hello.velog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "tags")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();
}