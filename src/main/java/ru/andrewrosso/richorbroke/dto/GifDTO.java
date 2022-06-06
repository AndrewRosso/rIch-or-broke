package ru.andrewrosso.richorbroke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class GifDTO {
    private Map<String, Object> data;
}
