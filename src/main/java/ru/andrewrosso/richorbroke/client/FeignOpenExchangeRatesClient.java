package ru.andrewrosso.richorbroke.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.andrewrosso.richorbroke.model.CurrencyRates;

@FeignClient(value = "open-exchange-rates-api", url = "${openexchangerates.url.general}")
public interface FeignOpenExchangeRatesClient {

    @GetMapping("/latest.json")
    CurrencyRates getLatestCurrencyRates(
            @RequestParam("app_id") String appId,
            @RequestParam (value = "base", defaultValue = "USD") String base
    );

    @GetMapping("/historical/{date}.json")
    CurrencyRates getHistoricalCurrencyRates(
            @PathVariable String date,
            @RequestParam ("app_id") String appId,
            @RequestParam (value = "base", defaultValue = "USD") String base
    );
}
