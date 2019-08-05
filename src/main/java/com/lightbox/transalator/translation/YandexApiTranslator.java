package com.lightbox.transalator.translation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;

/**
 * Translate text using Yandex API.
 */
@AllArgsConstructor
public final class YandexApiTranslator implements Translator {

    /**
     * Yandex api url.
     */
    private static final String YANDEX_API_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=%s";

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
     *
     * @param webClient   Http client
     * @param environment Spring environment
     */
    public YandexApiTranslator(final WebClient webClient, final Environment environment) {
        this(
                webClient,
                environment.getProperty("api.key")
        );
    }

    @Override
    public Future<JsonObject> translate(final String languageFrom, final String languageTo, final String text) {
        final Future<JsonObject> response = Future.future();
        final String lang = String.format("%s-%s", languageFrom, languageTo);
        this.webClient.getAbs(String.format(YANDEX_API_URL,this.apiKey,text,lang))
                .send(rslt -> {
                    if (rslt.succeeded()) {
                        response.complete(rslt.result().bodyAsJsonObject());
                    } else {
                        response.fail(new IllegalStateException(rslt.cause()));
                    }
                });
        return response;
    }
}
