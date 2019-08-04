package com.lightbox.transalator.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Save translation to db.
 */
public interface TranslationSave {

    /**
     * Save given translation.
     *
     * @param translation Translation to save
     * @return Future after db save
     */
    Future<Void> save(JsonObject translation);
}
