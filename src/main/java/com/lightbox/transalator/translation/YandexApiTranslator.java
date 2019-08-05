package com.lightbox.transalator.translation;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Split words by white space.
     * Sends http request to translate each single word and store result in List of futures
     * Create language as concat of langFrom  with langTo because this format is used by Yandex
     * Merge futures result into single future
     * @param languageFrom Original language
     * @param languageTo   Language to translate
     * @param text         Text to translate
     * @return Result wrapped by future
     */
    @Override
    public Future<JsonObject> translate(final String languageFrom, final String languageTo, final String text) {
        final String[] words = text.split(" ");
        final String lang = String.format("%s-%s", languageFrom, languageTo);
        final Future<JsonObject> result = Future.future();
        final List<Future> futures = new ArrayList<>(words.length);
        this.collectFutures(words, lang, futures);
        this.joinFutures(lang, result, futures);
        return result;
    }

    /**
     * Sends http request for translation using each single word.
     * Store futures into given list of futures
     *
     * @param words   Array of words separated by ' '
     * @param lang    Language
     * @param futures List of futures to store result
     */
    private void collectFutures(final String[] words, final String lang, final List<Future> futures) {
        for (final String word : words) {
            final Future<JsonObject> response = Future.future();
            this.webClient.getAbs(String.format(YANDEX_API_URL, this.apiKey, word, lang))
                    .send(rslt -> {
                        if (rslt.succeeded()) {
                            response.complete(rslt.result().bodyAsJsonObject());
                        } else {
                            response.fail(new IllegalStateException(rslt.cause()));
                        }
                    });
            futures.add(response);
        }
    }

    /**
     * Join given list of futures and store result into given single future.
     *
     * @param lang    Language
     * @param result  Final future with result
     * @param futures List of futures
     */
    private void joinFutures(final String lang, final Future<JsonObject> result, final List<Future> futures) {
        CompositeFuture.join(futures).setHandler(handler -> {
            if (handler.succeeded()) {
                final List<JsonObject> list = handler.result().list();
                final JsonObject jsonObject = new JsonObject();
                jsonObject.put("code", list.get(0).getInteger("code"));
                jsonObject.put("lang", lang);
                jsonObject.put("text", new JsonArray(list.stream().map(jo -> jo.getJsonArray("text").getString(0)).collect(Collectors.toList())));
                result.complete(jsonObject);
            } else {
                result.fail(handler.cause());
            }
        });
    }

}
