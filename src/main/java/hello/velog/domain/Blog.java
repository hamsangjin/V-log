package hello.velog.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blogs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Blog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, length = 100)
    private String intro;
}