package com.lightbox.transalator.translation;

import io.vertx.ext.web.client.WebClient;
import org.springframework.core.env.Environment;

/**
 * Translate text using Yandex API.
 */
public final class YandexApiTranslator implements Translator {

    /**
     * Http client.
     */
    private final WebClient webClient;

    /**
     * Api key for Yandex.
     */
    private final String apiKey;

    /**
     * Ctor.
     * @param webClient Http client
     * @param environment Spring environment
     */
    public YandexApiTranslator(final WebClient webClient, final Environment environment) {
        this.webClient = webClient;
        this.apiKey = environment.getProperty("api.key");
    }

    @Override
    public String translate(final String languageFrom, final String languageTo, final String text) {
        return null;
    }
}
