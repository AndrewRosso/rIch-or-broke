package ru.andrewrosso.richorbroke.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class DateUtil {
    private String datePattern;
    private String zoneId;

    public String getFormattedDate(Instant instant) {
        return formatterDate(instant, datePattern, zoneId);
    }

    public String getFormattedDate(long timestamp) {
        return formatterDate(timestamp, datePattern, zoneId);
    }

    private String formatterDate(Instant instant, String pattern, String zoneId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(pattern)
                .withZone(ZoneId.of(zoneId));
        return dateFormatter.format(instant);
    }

    private String formatterDate(long timestamp, String pattern, String zoneId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(pattern)
                .withZone(ZoneId.of(zoneId));
        return dateFormatter.format(Instant.ofEpochSecond(timestamp));
    }
}
