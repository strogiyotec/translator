package com.lightbox.transalator;

import com.lightbox.transalator.db.SaveYandexTranslation;
import com.lightbox.transalator.translation.ValidatedApiRequest;
import com.lightbox.transalator.translation.YandexApiTranslator;
import com.lightbox.transalator.verticles.TranslationResource;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Main class for translation service.
 */
@SpringBootApplication
@AllArgsConstructor
public class TranslatorApplication implements CommandLineRunner {

    /**
     * Spring environment.
     */
    private final Environment environment;

    /**
     * Jdbc template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Transaction manager.
     */
    private final PlatformTransactionManager platformTransactionManager;

    /**
     * Main method.
     *
     * @param args Program argc
     */
    public static void main(final String[] args) {
        SpringApplication.run(TranslatorApplication.class, args);
    }

    /**
     * Run vertx after spring configuration.
     *
     * @param args Command line argc
     */
    @Override
    public final void run(final String... args) {
        final Vertx vertx = Vertx.vertx();
        final WebClient webClient = WebClient.create(vertx);
        final TranslationResource resource = new TranslationResource(
                new ValidatedApiRequest(
                        new YandexApiTranslator(
                                webClient,
                                this.environment
                        )
                ),
                this.environment,
                new SaveYandexTranslation(this.jdbcTemplate, this.platformTransactionManager, vertx)
        );
        vertx.deployVerticle(resource);
    }
}
