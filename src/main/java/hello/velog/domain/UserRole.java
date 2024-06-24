package hello.velog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_roles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@IdClass(UserRoleId.class)
public class UserRole {
    @Id @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}