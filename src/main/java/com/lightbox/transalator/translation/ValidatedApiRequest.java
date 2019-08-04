package com.lightbox.transalator.translation;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Decorator for {@link Translator} that validates user input.
 */
@AllArgsConstructor
public final class ValidatedApiRequest implements Translator {

    /**
     * Max length for translated text.
     */
    private static final int MAX_LENGTH = 1000;

    /**
     * Original translator.
     */
    private final Translator origin;

    /**
     * List of supported languages.
     */
    private final List<String> languages;

    /**
     * LanguageFrom and LanguageTo have to be present in list of languages.
     * Text has to be present and length should be less than MAX_LENGTH
     *
     * @param languageFrom Original language
     * @param languageTo   Language to translate
     * @param text         Text to translate
     * @return Translated text
     */
    @Override
    public Promise<JsonObject> translate(final String languageFrom, final String languageTo, final String text) {
        if (!this.languages.contains(languageFrom)) {
            throw new IllegalStateException(
                    String.format("Api doesn't support language from [%s]", languageFrom)
            );
        }
        if (!this.languages.contains(languageTo)) {
            throw new IllegalStateException(
                    String.format("Api doesn't support language to [%s]", languageTo)
            );
        }
        if (StringUtils.isEmpty(text)) {
            throw new IllegalStateException("Text can't be empty");
        }
        if (text.length() > MAX_LENGTH) {
            throw new IllegalStateException("Text's length has to be less than 1000");
        }
        return this.origin.translate(languageFrom, languageTo, text);
    }
}
