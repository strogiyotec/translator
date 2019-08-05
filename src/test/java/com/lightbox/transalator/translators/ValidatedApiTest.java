package com.lightbox.transalator.translators;


import com.lightbox.transalator.translation.ValidatedApiRequest;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for {@link com.lightbox.transalator.translation.ValidatedApiRequest}.
 */
@RunWith(VertxUnitRunner.class)
public final class ValidatedApiTest {

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
     * Test success result.
     *
     * @param context Async context
     */
    public void testSuccess(final TestContext context) {
        final Async async = context.async();
        final ValidatedApiRequest request = new ValidatedApiRequest(new MockTranslator());
        request.translate("ru", "en", "Привет")
                .setHandler(handler -> {
                    if (handler.succeeded()) {
                        Assert.assertTrue(handler.result().isEmpty());
                        async.complete();
                    } else {
                        throw new IllegalStateException(handler.cause());
                    }
                });
    }

    /**
     * Test success validation.
     *
     * @param context Async context
     */
    @Test
    public void testValidationFailed(final TestContext context) {
        final Async async = context.async(3);
        final ValidatedApiRequest request = new ValidatedApiRequest(new MockTranslator());
        request.translate(null, "en", "hello").setHandler(handler -> {
            if (handler.failed()) {
                Assert.assertThat(handler.cause().getMessage(), CoreMatchers.containsString("Language from has to be present"));
                async.countDown();
            } else {
                throw new IllegalStateException("Should be failed");
            }
        });
        request.translate("en", null, "hello").setHandler(handler -> {
            if (handler.failed()) {
                Assert.assertThat(handler.cause().getMessage(), CoreMatchers.containsString("Language to has to be present"));
                async.countDown();
            } else {
                throw new IllegalStateException("Should be failed");
            }
        });
        request.translate("en", "ru", null).setHandler(handler -> {
            if (handler.failed()) {
                Assert.assertThat(handler.cause().getMessage(), CoreMatchers.containsString("Text has to be present"));
                async.countDown();
            } else {
                throw new IllegalStateException("Should be failed");
            }
        });
    }
}
