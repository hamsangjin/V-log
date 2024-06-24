package hello.velog.domain;

import lombok.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleId implements Serializable {
    private Long user;
    private Long role;
}