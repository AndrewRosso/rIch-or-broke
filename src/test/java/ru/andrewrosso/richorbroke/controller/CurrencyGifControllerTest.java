package ru.andrewrosso.richorbroke.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.andrewrosso.richorbroke.dto.GifDTO;
import ru.andrewrosso.richorbroke.constant.Compare;
import ru.andrewrosso.richorbroke.model.GifModel;
import ru.andrewrosso.richorbroke.service.impl.GiphyServiceImpl;
import ru.andrewrosso.richorbroke.service.impl.OpenExchangeRatesServiceImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CurrencyGifController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CurrencyGifControllerTest {

    private static final String RICH_TAG = "rich";
    private static final String ERROR_TAG = "error";

    private static final String TEST_CODE = "testCode";
    private static final String CURRENCY_CODE = "currencyCode";
    private static Set<String> testCurrencyCodes;
    private static GifModel testGif;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpenExchangeRatesServiceImpl exchangeRatesService;
    @MockBean
    private GiphyServiceImpl giphyService;

    @BeforeClass
    public static void createTestData() {
        testCurrencyCodes = new HashSet<>(Arrays.asList("RUB", "SOL", "TUR"));

        Map<String, Object> testOriginal = ImmutableMap
                .of("url", "testGifUrl", "field", "someTestField");
        Map<String, Object> testImages = ImmutableMap
                .of("original", testOriginal, "field", "someTestField");
        Map<String, Object> testData = ImmutableMap
                .of("title", "testTitle", "images", testImages);
        GifDTO testGifDTO = new GifDTO(testData);
        testGif = new GifModel(testGifDTO);
    }

    @Test
    public void shouldCreateMockMvc() {
        Assert.assertNotNull(mockMvc);
    }

    @Test
    public void shouldGetCodes() throws Exception {
        Mockito.when(exchangeRatesService.getCurrencyCodes())
                .thenReturn(testCurrencyCodes);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/app/currency-codes")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]", is("TUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]", is("SOL")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2]", is("RUB")));
    }

    @Test
    public void shouldGetGifByRichTag() throws Exception {
        testGif.setGifTag(RICH_TAG);
        Mockito.when(exchangeRatesService.getCompareCurrencyRates(any()))
                .thenReturn(Compare.HIGHER);
        Mockito.when(giphyService.getGif(RICH_TAG))
                .thenReturn(testGif);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/app/gif")
                        .param(CURRENCY_CODE, TEST_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("testTitle")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url", is("testGifUrl")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gifTag", is(RICH_TAG)));
    }

    @Test
    public void shouldGetGifByErrorTag() throws Exception {
        testGif.setGifTag(ERROR_TAG);
        Mockito.when(exchangeRatesService.getCompareCurrencyRates(any()))
                .thenReturn(Compare.ERROR);
        Mockito.when(giphyService.getGif(ERROR_TAG))
                .thenReturn(testGif);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/app/gif")
                        .param(CURRENCY_CODE, TEST_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("testTitle")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url", is("testGifUrl")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gifTag", is(ERROR_TAG)));
    }
}
