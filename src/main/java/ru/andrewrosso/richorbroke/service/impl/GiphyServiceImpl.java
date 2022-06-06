package ru.andrewrosso.richorbroke.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.andrewrosso.richorbroke.client.FeignGiphyClient;
import ru.andrewrosso.richorbroke.dto.GifDTO;
import ru.andrewrosso.richorbroke.model.GifModel;
import ru.andrewrosso.richorbroke.service.GiphyService;

@Service
@Data
@RequiredArgsConstructor
public class GiphyServiceImpl implements GiphyService {

    private final FeignGiphyClient giphyClient;
    @Value("${giphy.api.key}")
    private String apiKey;

    @Override
    public GifModel getGif(String tag) {
        GifDTO gifDTO = giphyClient.getRandomGif(apiKey, tag);
        if (gifDTO == null) {
            throw new NullPointerException();
        }
        GifModel gifModel = new GifModel(gifDTO);
        gifModel.setGifTag(tag);

        return gifModel;
    }
}
