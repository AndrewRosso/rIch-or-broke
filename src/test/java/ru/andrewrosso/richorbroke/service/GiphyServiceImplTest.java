package ru.andrewrosso.richorbroke.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.andrewrosso.richorbroke.client.FeignGiphyClient;
import ru.andrewrosso.richorbroke.dto.GifDTO;
import ru.andrewrosso.richorbroke.model.GifModel;
import ru.andrewrosso.richorbroke.service.impl.GiphyServiceImpl;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class GiphyServiceImplTest {

    private GifDTO testGifDTO;
    @InjectMocks
    private GiphyServiceImpl giphyService;
    @Mock
    private FeignGiphyClient giphyClient;

    @Before
    public void createTestGifDTO() {
        Map<String, Object> testOriginal = ImmutableMap
                .of("url", "testGifUrl", "field", "someTestField");
        Map<String, Object> testImages = ImmutableMap
                .of("original", testOriginal, "field", "someTestField");
        Map<String, Object> testData = ImmutableMap
                .of("title", "testTitle", "images", testImages);

        testGifDTO = new GifDTO(testData);
    }

    @Test
    public void shouldGetGif() {
        Mockito.when(giphyClient.getRandomGif(any(), any())).thenReturn(testGifDTO);
        GifModel expectedGifModel = new GifModel(testGifDTO);
        expectedGifModel.setGifTag("testTag");

        GifModel actualGifModel = giphyService.getGif("testTag");

        assertEquals(expectedGifModel, actualGifModel);
    }

    @Test
    public void shouldGetGifWithNullTag() {
        Mockito.when(giphyClient.getRandomGif(any(), any())).thenReturn(testGifDTO);
        GifModel expectedGifModel = new GifModel(testGifDTO);
        expectedGifModel.setGifTag(null);

        GifModel actualGifModel = giphyService.getGif(null);

        assertEquals(expectedGifModel, actualGifModel);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhen() {
        Mockito.when(giphyClient.getRandomGif(any(), any())).thenReturn(null);

        giphyService.getGif("testTag");
    }
}
