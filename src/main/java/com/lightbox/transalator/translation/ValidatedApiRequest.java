package com.lightbox.transalator.translation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * Decorator for {@link Translator} that validates user input.
 */
@AllArgsConstructor
public final class ValidatedApiRequest implements Translator {

    /**
     * Max length for translated text.
     */
    private static final int MAX_LENGTH = 500;

    /**
     * Original translator.
     */
    private final Translator origin;

    /**
     * All params have to be present , language would be validated in yandex side.
     *
     * @param languageFrom Original language
     * @param languageTo   Language to translate
     * @param text         Text to translate
     * @return Translated text
     */
    @Override
    public Future<JsonObject> translate(final String languageFrom, final String languageTo, final String text) {
        if (StringUtils.isEmpty(languageFrom)) {
            return Future.failedFuture(new IllegalStateException("Language from has to be present"));
        }
        if (StringUtils.isEmpty(languageTo)) {
            return Future.failedFuture(new IllegalStateException("Language to has to be present"));
        }
        if (StringUtils.isEmpty(text)) {
            return Future.failedFuture(new IllegalStateException("Text has to be present"));
        }
        if (text.length() > MAX_LENGTH) {
            return Future.failedFuture(new IllegalStateException(String.format("Text's length has to be less than %d", MAX_LENGTH)));
        }
        return this.origin.translate(languageFrom, languageTo, text);
    }
}
