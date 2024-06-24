package hello.velog.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(nullable = false, unique = true)
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
    private String thumbnailImage = "/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/default-profile.png";

    @Column(name = "thumbnail_text", length = 100)
    private String thumbnailText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
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