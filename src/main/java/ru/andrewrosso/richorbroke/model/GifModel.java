package ru.andrewrosso.richorbroke.model;

import lombok.Data;
import ru.andrewrosso.richorbroke.dto.GifDTO;

import java.util.Map;


@Data
public class GifModel {
    private String title;
    private String url;
    private String gifTag;

    public GifModel(GifDTO gifDTO) {
        this.title = (String) gifDTO.getData().get("title");
        Map<String, Object> images = (Map<String, Object>) gifDTO.getData().get("images");
        Map<String, Object> original = (Map<String, Object>) images.get("original");
        this.url = (String) original.get("url");
    }
}
