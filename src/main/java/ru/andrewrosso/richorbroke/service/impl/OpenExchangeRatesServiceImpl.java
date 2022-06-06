package ru.andrewrosso.richorbroke.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.andrewrosso.richorbroke.client.FeignOpenExchangeRatesClient;
import ru.andrewrosso.richorbroke.enums.Compare;
import ru.andrewrosso.richorbroke.model.CurrencyRates;
import ru.andrewrosso.richorbroke.service.OpenExchangeRatesService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@Data
@RequiredArgsConstructor
public class OpenExchangeRatesServiceImpl implements OpenExchangeRatesService {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_HOUR_PATTERN = "yyyy-MM-dd HH";
    private static final String HISTORICAL_REQUEST_ZONE_ID = "UTC";
    private Clock clockUTC = Clock.systemUTC();
    private CurrencyRates actualRates;
    private CurrencyRates yesterdayRates;
    private final FeignOpenExchangeRatesClient openExchangeRatesClient;
    @Value("${openexchangerates.app.id}")
    private String appId;
    @Value("${openexchangerates.base}")
    private String base;

    @Override
    public Set<String> getCurrencyCodes() {
        if (actualRates != null) {
            return actualRates.getRates().keySet();
        } else {
            return openExchangeRatesClient.getLatestCurrencyRates(appId, base).getRates().keySet();
        }
    }

    @Override
    public Compare getCompareCurrencyRates(String currencyCode) {
        checkAndUpdateCurrencyRates();

        Double currentValue = getCurrencyValue(actualRates, currencyCode);
        Double yesterdayValue = getCurrencyValue(yesterdayRates, currencyCode);

        switch (currentValue.compareTo(yesterdayValue)) {
            case 1:
                return Compare.HIGHER;
            case -1:
                return Compare.LOWER;
            default:
                return Compare.IDENTICAL;
        }
    }

    private Double getCurrencyValue(CurrencyRates currencyRates, String currencyCode) {
        if (currencyRates == null
                || currencyCode == null
                || !currencyRates.getRates().containsKey(currencyCode)) {
            throw new IllegalArgumentException();
        }
        return currencyRates.getRates().get(currencyCode);
    }

    @Override
    public void checkAndUpdateCurrencyRates() {
        Instant currentTime = Instant.now(clockUTC);
        checkAndUpdateActualRates(currentTime);
        checkAndUpdateYesterdayRates(currentTime);
    }

    private void checkAndUpdateActualRates(Instant time) {
        if (actualRates == null
                || !dateWithHourFormatterUTC(Instant.ofEpochSecond(actualRates.getTimestamp()))
                .equals(dateWithHourFormatterUTC(time))) {
            actualRates = openExchangeRatesClient.getLatestCurrencyRates(appId, base);
        }
    }

    private void checkAndUpdateYesterdayRates(Instant time) {
        if (yesterdayRates == null
                || !dateFormatterUTC(Instant.ofEpochSecond(yesterdayRates.getTimestamp()))
                .equals(getYesterdayDate(time))) {
            yesterdayRates = openExchangeRatesClient.getHistoricalCurrencyRates(getYesterdayDate(time), appId, base);
        }
    }

    private String getYesterdayDate(Instant time) {
        Instant yesterdayInst = time.minus(1, ChronoUnit.DAYS);

        return dateFormatterUTC(yesterdayInst);
    }

    private String dateFormatterUTC(Instant instant) {
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(DATE_PATTERN)
                .withZone(ZoneId.of(HISTORICAL_REQUEST_ZONE_ID));
        return dateFormatter.format(instant);
    }

    private String dateWithHourFormatterUTC(Instant instant) {
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern(DATE_HOUR_PATTERN)
                .withZone(ZoneId.of(HISTORICAL_REQUEST_ZONE_ID));
        return dateFormatter.format(instant);
    }
}
