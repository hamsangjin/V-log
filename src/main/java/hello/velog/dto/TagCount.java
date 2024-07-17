package hello.velog.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TagCount {
    private Long id;
    private String name;
    private long count;
}