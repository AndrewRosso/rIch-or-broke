package ru.andrewrosso.richorbroke.service;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.andrewrosso.richorbroke.client.FeignOpenExchangeRatesClient;
import ru.andrewrosso.richorbroke.enums.Compare;
import ru.andrewrosso.richorbroke.model.CurrencyRates;
import ru.andrewrosso.richorbroke.service.impl.OpenExchangeRatesServiceImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static ru.andrewrosso.richorbroke.enums.Compare.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenExchangeRatesServiceImplTest {
    /**
     * FIXED_CURRENT_TIME - фиксированное время на часах большее на 50 минут,
     * чем в timestamp у тестового актуального обменного курса
     *
     * FIXED_CURRENT_DAY - фиксированное время на часах c разницей менее одного дня,
     * чем в timestamp у тестового вчерашнего обменного курса
     */
    private static final String FIXED_CURRENT_TIME = "2022-06-05T20:50:19Z";
    private static final String FIXED_CURRENT_DAY = "2022-06-05T10:50:19Z";
    private static final String BASE_CURRENCY = "USD";
    private CurrencyRates testActualRates;
    private CurrencyRates testYesterdayRates;
    @InjectMocks
    private OpenExchangeRatesServiceImpl openExchangeRatesService;
    @Mock
    private FeignOpenExchangeRatesClient openExchangeRatesClient;

    @Before
    public void createTestRates() {
        testActualRates = CurrencyRates.builder()
                .timestamp(1654459200)
                .base(BASE_CURRENCY)
                .rates(ImmutableMap.of("RUB", 23.03, "SOL", 9.03, "TUR", 10.00))
                .build();

        testYesterdayRates = CurrencyRates.builder()
                .timestamp(1654387186)
                .base(BASE_CURRENCY)
                .rates(ImmutableMap.of("RUB", 20.03, "SOL", 10.03, "TUR", 10.00))
                .build();
    }

    @After
    public void clearCurrencyRates() {
        openExchangeRatesService.setActualRates(null);
        openExchangeRatesService.setYesterdayRates(null);
        openExchangeRatesService.setClockUTC(Clock.systemUTC());
    }

    @Test
    public void shouldReturnNullActualAndYesterdayRatesBeforeFirstUpdate() {
        assertNull(openExchangeRatesService.getActualRates());
        assertNull(openExchangeRatesService.getYesterdayRates());
    }

    @Test
    public void shouldUpdateActualAndYesterdayRates() {
        Mockito.when(openExchangeRatesClient.getLatestCurrencyRates(any(), any()))
                .thenReturn(testActualRates);
        Mockito.when(openExchangeRatesClient.getHistoricalCurrencyRates(any(), any(), any()))
                .thenReturn(testYesterdayRates);

        openExchangeRatesService.checkAndUpdateCurrencyRates();

        assertEquals(testActualRates, openExchangeRatesService.getActualRates());
        assertEquals(testYesterdayRates, openExchangeRatesService.getYesterdayRates());
    }

    @Test
    public void shouldNotUpdateActualRatesIfOneHourHasNotPassedAfterLastUpdate() {
        openExchangeRatesService.setActualRates(testActualRates);
        openExchangeRatesService.setClockUTC(Clock.fixed(Instant.parse(FIXED_CURRENT_TIME), ZoneId.of("UTC")));

        openExchangeRatesService.checkAndUpdateCurrencyRates();

        assertEquals(testActualRates, openExchangeRatesService.getActualRates());
    }

    @Test
    public void shouldNotUpdateYesterdayRatesIfNextDayHasNotComeAfterLastUpdate() {
        openExchangeRatesService.setYesterdayRates(testYesterdayRates);
        openExchangeRatesService.setClockUTC(Clock.fixed(Instant.parse(FIXED_CURRENT_DAY), ZoneId.of("UTC")));

        openExchangeRatesService.checkAndUpdateCurrencyRates();

        assertEquals(testYesterdayRates, openExchangeRatesService.getYesterdayRates());
    }

    @Test
    public void shouldReturnCurrencyCodes() {
        Set<String> expectedSet = new HashSet<>(Arrays.asList("RUB", "SOL","TUR"));
        Mockito.when(openExchangeRatesClient.getLatestCurrencyRates(any(), any()))
                .thenReturn(testActualRates);

        Set<String> actualSet = openExchangeRatesService.getCurrencyCodes();

        assertEquals(expectedSet, actualSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCurrencyCodeIsNull() {
        openExchangeRatesService.getCompareCurrencyRates(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenCurrencyCodeIsInvalid() {
        openExchangeRatesService.setActualRates(testActualRates);
        openExchangeRatesService.setYesterdayRates(testYesterdayRates);

        openExchangeRatesService.getCompareCurrencyRates("LOL");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenActualRatesIsNull() {
        openExchangeRatesService.setYesterdayRates(testYesterdayRates);
        openExchangeRatesService.getCompareCurrencyRates("RUB");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenYesterdayRatesIsNull() {
        openExchangeRatesService.setYesterdayRates(testActualRates);
        openExchangeRatesService.getCompareCurrencyRates("RUB");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenActualAndYesterdayRatesIsNull() {
        openExchangeRatesService.getCompareCurrencyRates("RUB");
    }

    @Test
    public void shouldReturnCompareHigherWhenActualCurrencyRateHigherThanYesterday() {
        Mockito.when(openExchangeRatesClient.getLatestCurrencyRates(any(), any()))
                .thenReturn(testActualRates);
        Mockito.when(openExchangeRatesClient.getHistoricalCurrencyRates(any(), any(), any()))
                .thenReturn(testYesterdayRates);

        Compare actualResult = openExchangeRatesService.getCompareCurrencyRates("RUB");
        assertEquals(HIGHER, actualResult);
    }

    @Test
    public void shouldReturnCompareLowerWhenActualCurrencyRateLowerThanYesterday() {
        Mockito.when(openExchangeRatesClient.getLatestCurrencyRates(any(), any()))
                .thenReturn(testActualRates);
        Mockito.when(openExchangeRatesClient.getHistoricalCurrencyRates(any(), any(), any()))
                .thenReturn(testYesterdayRates);

        Compare actualResult = openExchangeRatesService.getCompareCurrencyRates("SOL");
        assertEquals(LOWER, actualResult);
    }

    @Test
    public void shouldReturnCompareIdenticalWhenActualCurrencyRateIdenticalAtTheYesterday() {
        Mockito.when(openExchangeRatesClient.getLatestCurrencyRates(any(), any()))
                .thenReturn(testActualRates);
        Mockito.when(openExchangeRatesClient.getHistoricalCurrencyRates(any(), any(), any()))
                .thenReturn(testYesterdayRates);

        Compare actualResult = openExchangeRatesService.getCompareCurrencyRates("TUR");
        assertEquals(IDENTICAL, actualResult);
    }
}
