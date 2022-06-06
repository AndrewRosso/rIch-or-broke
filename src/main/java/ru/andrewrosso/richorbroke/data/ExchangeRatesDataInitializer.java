package ru.andrewrosso.richorbroke.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.andrewrosso.richorbroke.service.OpenExchangeRatesService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class ExchangeRatesDataInitializer {
    private final OpenExchangeRatesService openExchangeRatesService;

    @PostConstruct
    public void firstInitialize() {
        openExchangeRatesService.checkAndUpdateCurrencyRates();
    }
}
