package hello.velog.dto;

import hello.velog.domain.Series;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SeriesDTO {
    private Long id;
    private String title;

    public SeriesDTO(Series series) {
        this.id = series.getId();
        this.title = series.getTitle();
    }
}