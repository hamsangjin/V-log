package hello.velog.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "blogs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Blog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private String title;

    private String intro = "소개를 입력해주세요.";

    @OneToMany(mappedBy = "blog")
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "blog")
    private Set<Series> series = new HashSet<>();

    public Blog(User user, String title) {
        this.user = user;
        this.title = title;
    }
}