package com.lightbox.transalator.translation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Base interface for all translation API's implementations.
 */
public interface Translator {

    /**
     * @param languageFrom Original language
     * @param languageTo   Language to translate
     * @param text         Text to translate
     * @return Promise with translated text
     */
    Future<JsonObject> translate(String languageFrom, String languageTo, String text);
}
