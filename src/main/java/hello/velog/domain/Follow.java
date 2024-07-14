package hello.velog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follows")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Follow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followee_id")
    private User followee;
}