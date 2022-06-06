package ru.andrewrosso.richorbroke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GifDTO {
    private Map<String, Object> data;
}
