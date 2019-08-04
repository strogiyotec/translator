package com.lightbox.transalator.unit;

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
public final class YandexTranslationTest {

    /**
     * Test api key for yandex.
     */
    private static final String API_KEY = "trnsl.1.1.20190804T055114Z.9f8638d18e86921e.9eec547274ef9995e9e39ad6f294d5f7b39c55f0";


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
     * Test translation
     */
    @Test
    public void testTranslation(final TestContext context) {
        final Async async = context.async();
        final WebClient webClient = WebClient.create(Vertx.vertx());
        webClient.getAbs(String.format("https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=en-ru", API_KEY, "hello"))
                .send(rslt -> {
                    if (rslt.succeeded()) {
                        final JsonObject jsonObject = rslt.result().bodyAsJsonObject();
                        Assert.assertThat(jsonObject.getJsonArray("text").getString(0), CoreMatchers.is("привет"));
                        async.complete();
                        webClient.close();
                    } else {
                        async.complete();
                        throw new IllegalStateException(rslt.cause());
                    }
                });
    }
}
