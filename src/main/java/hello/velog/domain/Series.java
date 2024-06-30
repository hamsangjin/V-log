package hello.velog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "likes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Series {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @OneToMany(mappedBy = "series")
    private Set<Post> posts = new HashSet<>();
}
