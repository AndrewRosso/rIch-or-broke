package ru.andrewrosso.richorbroke.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andrewrosso.richorbroke.enums.Compare;
import ru.andrewrosso.richorbroke.model.GifModel;
import ru.andrewrosso.richorbroke.service.GiphyService;
import ru.andrewrosso.richorbroke.service.impl.OpenExchangeRatesServiceImpl;

import java.util.Set;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class MainController {

    private final OpenExchangeRatesServiceImpl openExchangeRatesService;
    private final GiphyService giphyService;

    @Value("${giphy.rich}")
    private String richTag;
    @Value("${giphy.broke}")
    private String brokeTag;
    @Value("${giphy.identical}")
    private String identicalTag;
    @Value("${giphy.error}")
    private String errorTag;

    @GetMapping("/gif")
    public GifModel getGif(@RequestParam String currencyCode) {
        Compare gifKey = openExchangeRatesService.getCompareCurrencyRates(currencyCode);
        String gifTag = errorTag;
        try {
            switch (gifKey) {
                case HIGHER:
                    gifTag = richTag;
                    break;
                case LOWER:
                    gifTag = brokeTag;
                    break;
                case IDENTICAL:
                    gifTag = identicalTag;
                    break;
            }
        } catch (NullPointerException e) {
            System.out.println("Compare result is null!");
        }
        return giphyService.getGif(gifTag);
    }

    @GetMapping("/currency-codes")
    public Set<String> getCodes() {
        return openExchangeRatesService.getCurrencyCodes();
    }
}
