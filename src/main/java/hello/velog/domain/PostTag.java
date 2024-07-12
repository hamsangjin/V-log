package hello.velog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_tags")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public Long getTagId() {
        return tag.getId();
    }
}