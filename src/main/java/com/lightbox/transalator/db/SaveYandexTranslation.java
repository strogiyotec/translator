package com.lightbox.transalator.db;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Save translation from yandex.
 */
@AllArgsConstructor
@Slf4j
public final class SaveYandexTranslation implements TranslationSave {

    /**
     * Jdbc template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * TransactionManager.
     */
    private final PlatformTransactionManager platformTransactionManager;

    /**
     * Vertx instance.
     */
    private final Vertx vertx;

    @Override
    public Future<Void> save(final JsonObject translation) {
        final Future<Void> future = Future.future();
        this.vertx.executeBlocking(promise -> {
            final DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            final TransactionStatus transaction = this.platformTransactionManager.getTransaction(defaultTransactionDefinition);
            try {
                this.jdbcTemplate.update(
                        "INSERT INTO translations(language_from,language_to,original_text,translated_text) VALUES (?,?,?,?)",
                        translation.getString("languageFrom"),
                        translation.getString("languageTo"),
                        translation.getString("text"),
                        translation.getString("translatedText")
                );
                this.platformTransactionManager.commit(transaction);
                promise.complete();
            } catch (final Exception exc) {
                log.error("Can't save translation {}", transaction.toString(), exc);
                this.platformTransactionManager.rollback(transaction);
                promise.fail(exc);
            }
        }, result -> {
            if (result.succeeded()) {
                future.complete((Void) result.result());
            } else {
                future.fail(result.cause());
            }
        });
        return future;
    }
}
