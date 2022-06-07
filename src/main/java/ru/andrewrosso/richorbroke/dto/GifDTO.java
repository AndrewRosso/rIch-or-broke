package ru.andrewrosso.richorbroke.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class GifDTO {
    private Map<String, Object> data;
    private Map<String, Object> meta;

    public GifDTO(Map<String, Object> data) {
        this.data = data;
    }
}
