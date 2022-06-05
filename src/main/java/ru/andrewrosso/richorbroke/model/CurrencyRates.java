package ru.andrewrosso.richorbroke.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CurrencyRates {
    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private Map<String , Double> rates;
}
