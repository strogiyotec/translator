package com.lightbox.transalator.translators;

import com.lightbox.transalator.YandexTranslationTest;
import com.lightbox.transalator.translation.YandexApiTranslator;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for {@link com.lightbox.transalator.translation.YandexApiTranslator}.
 */
@RunWith(VertxUnitRunner.class)
public final class YandexApiRequestTest {

    /**
     * Vertx instance.
     */
    private Vertx vertx;

    /**
     * Init vertx.
     */
    @Before
    public void init() {
        this.vertx = Vertx.vertx();
    }

    /**
     * Stop vertx.
     */
    @After
    public void stop(final TestContext context) {
        this.vertx.close(context.asyncAssertSuccess());
    }

    /**
     * Test success translation.
     *
     * @param context Async context
     */
    @Test
    public void testSuccessResult(final TestContext context) {
        final Async async = context.async();
        new YandexApiTranslator(WebClient.create(this.vertx), YandexTranslationTest.API_KEY)
                .translate("en", "ru", "hello")
                .setHandler(handler -> {
                    if (handler.succeeded()) {
                        final JsonObject result = handler.result();
                        Assert.assertThat(result.getJsonArray("text").getString(0), CoreMatchers.is("привет"));
                        async.complete();
                    } else {
                        throw new IllegalStateException(handler.cause());
                    }
                });
    }
}
