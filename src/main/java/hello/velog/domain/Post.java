package hello.velog.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private Long views = 0L;

    @Column(name = "privacy_setting", nullable = false)
    private Boolean privacySetting = false;

    @Column(name = "temporary_setting", nullable = false)
    private Boolean temporarySetting = false;

    @Column(name = "thumbnail_image")
    private String thumbnailImage = "/images/post/default-thumbnail.png";

    @Column(name = "thumbnail_text", length = 100)
    private String thumbnailText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "post")
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostTag> postTags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    public Post(Long userId, String title, String content, Boolean privacySetting, Boolean temporarySetting, String thumbnailImage, String thumbnailText) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.privacySetting = privacySetting;
        this.temporarySetting = temporarySetting;
        this.thumbnailImage = thumbnailImage;
        this.thumbnailText = thumbnailText;
    }
}