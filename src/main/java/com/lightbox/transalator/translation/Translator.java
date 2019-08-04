package com.lightbox.transalator.translation;

/**
 * Base interface for all translation API's implementations
 */
public interface Translator {

    /**
     * @param languageFrom Original language
     * @param languageTo   Language to translate
     * @param text         Text to translate
     * @return Translated text
     */
    String translate(String languageFrom, String languageTo, String text);
}
