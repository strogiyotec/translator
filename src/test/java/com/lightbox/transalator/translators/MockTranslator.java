package com.lightbox.transalator.translators;

import com.lightbox.transalator.translation.Translator;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Mock Translator that always returns success result.
 */
final class MockTranslator implements Translator {

    @Override
    public Future<JsonObject> translate(final String languageFrom, final String languageTo, final String text) {
        return Future.succeededFuture(new JsonObject());
    }
}
