package hello.velog.controller;

import hello.velog.domain.*;
import hello.velog.dto.SeriesDTO;
import hello.velog.service.SeriesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class PostRestController {
    private final SeriesService seriesService;

    @PostMapping("/createseries")
    @ResponseBody
    public Map<String, Object> createSeries(@RequestBody Series series, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        series.setBlog(user.getBlog());
        Series savedSeries = seriesService.saveSeries(series);
        SeriesDTO seriesDTO = new SeriesDTO(savedSeries);

        response.put("success", true);
        response.put("series", seriesDTO);
        return response;
    }
}