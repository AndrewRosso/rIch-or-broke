package ru.andrewrosso.richorbroke.service;

import ru.andrewrosso.richorbroke.model.GifModel;

public interface GiphyService {
    GifModel getGif(String tag);
}
