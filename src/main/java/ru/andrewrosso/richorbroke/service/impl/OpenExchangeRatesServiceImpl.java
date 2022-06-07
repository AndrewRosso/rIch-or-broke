package ru.andrewrosso.richorbroke.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.andrewrosso.richorbroke.client.FeignOpenExchangeRatesClient;
import ru.andrewrosso.richorbroke.constant.Compare;
import ru.andrewrosso.richorbroke.model.CurrencyRates;
import ru.andrewrosso.richorbroke.service.OpenExchangeRatesService;
import ru.andrewrosso.richorbroke.util.DateUtil;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@Getter
@Setter
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
            case 0:
                return Compare.IDENTICAL;
            default:
                return Compare.ERROR;
        }
    }

    private Double getCurrencyValue(CurrencyRates currencyRates, String currencyCode) {
        if (currencyRates == null) {
            throw new IllegalArgumentException("Currency Rates is illegal");
        }
        if (currencyCode == null || !currencyRates.getRates().containsKey(currencyCode)) {
            throw new IllegalArgumentException("Currency code is illegal");
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
        DateUtil dateHourUtil = new DateUtil(DATE_HOUR_PATTERN, HISTORICAL_REQUEST_ZONE_ID);

        if (actualRates == null
                || !dateHourUtil.getFormattedDate(actualRates.getTimestamp())
                .equals(dateHourUtil.getFormattedDate(time))) {
            actualRates = openExchangeRatesClient.getLatestCurrencyRates(appId, base);
        }
    }

    private void checkAndUpdateYesterdayRates(Instant time) {
        DateUtil dateUtil = new DateUtil(DATE_PATTERN, HISTORICAL_REQUEST_ZONE_ID);
        Instant yesterday = time.minus(1, ChronoUnit.DAYS);

        if (yesterdayRates == null
                || !dateUtil.getFormattedDate(yesterdayRates.getTimestamp())
                .equals(dateUtil.getFormattedDate(yesterday))) {
            yesterdayRates = openExchangeRatesClient
                    .getHistoricalCurrencyRates(dateUtil.getFormattedDate(yesterday), appId, base);
        }
    }
}
