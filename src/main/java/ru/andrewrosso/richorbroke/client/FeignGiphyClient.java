package ru.andrewrosso.richorbroke.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.andrewrosso.richorbroke.dto.GifDTO;

@FeignClient(value = "giphy-client", url = "${giphy.url.general}")
public interface FeignGiphyClient {

    @GetMapping("/random")
    GifDTO getRandomGif(
            @RequestParam("api_key") String apiKey,
            @RequestParam("tag") String tag
    );
}
