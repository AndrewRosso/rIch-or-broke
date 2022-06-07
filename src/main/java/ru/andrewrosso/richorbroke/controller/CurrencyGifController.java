package ru.andrewrosso.richorbroke.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andrewrosso.richorbroke.constant.Compare;
import ru.andrewrosso.richorbroke.model.GifModel;
import ru.andrewrosso.richorbroke.service.GiphyService;
import ru.andrewrosso.richorbroke.service.impl.OpenExchangeRatesServiceImpl;

import java.util.Set;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class CurrencyGifController {

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
        String gifTag;
        Compare gifKey = openExchangeRatesService.getCompareCurrencyRates(currencyCode);
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
            default:
                gifTag = errorTag;
        }
        return giphyService.getGif(gifTag);
    }

    @GetMapping("/currency-codes")
    public Set<String> getCodes() {
        return openExchangeRatesService.getCurrencyCodes();
    }
}
