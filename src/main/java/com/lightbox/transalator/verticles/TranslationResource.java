package com.lightbox.transalator.verticles;

import com.lightbox.transalator.db.TranslationSave;
import com.lightbox.transalator.translation.Translator;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * Web resource for translation endpoints.
 */
@AllArgsConstructor
@Slf4j
public final class TranslationResource extends AbstractVerticle {

    /**
     * Translator.
     */
    private final Translator translator;

    /**
     * Http server port.
     */
    private final int port;

    /**
     * Save translation.
     */
    private final TranslationSave translationSave;

    /**
     * Ctor.
     *
     * @param translator      Translator instance
     * @param environment     Spring environment
     * @param translationSave Translation save
     */
    public TranslationResource(final Translator translator, final Environment environment, final TranslationSave translationSave) {
        this(
                translator,
                Integer.parseInt(environment.getProperty("port")),
                translationSave
        );
    }

    @Override
    public void start() {
        final Router router = Router.router(this.vertx);
        this.configureRouter(router);
        this.vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(this.port, handler -> {
                    if (handler.succeeded()) {
                        log.info("Server was started in port {}", this.port);
                    } else {
                        log.error("Can't start http server in port {}", this.port, handler.cause());
                    }
                });
    }

    /**
     * Create rest end point for translation.
     *
     * @param router Vertx router
     */
    private void configureRouter(final Router router) {
        router.route(HttpMethod.GET, "/translate")
                .handler(handler -> {
                    final HttpServerRequest request = handler.request();
                    final HttpServerResponse response = handler.response();
                    final Future<JsonObject> translation = this.translator.translate(
                            request.getParam("languageFrom"),
                            request.getParam("languageTo"),
                            request.getParam("text")
                    );
                    translation.setHandler(translationHandler -> {
                        if (translationHandler.succeeded()) {
                            final JsonObject result = translationHandler.result();
                            this.translationSave.save(
                                    new JsonObject().put("languageFrom", request.getParam("languageFrom"))
                                            .put("languageTo", request.getParam("languageTo"))
                                            .put("text", request.getParam("text"))
                                            .put("translatedText", result.getJsonArray("text").getString(0))
                            ).setHandler(saveHandler -> {
                                if (saveHandler.succeeded()) {
                                    response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
                                            .setStatusCode(HttpResponseStatus.OK.code())
                                            .end(result.toString());
                                } else {
                                    response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN.toString())
                                            .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                                            .end(saveHandler.cause().getMessage());
                                }
                            });

                        } else {
                            response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN.toString())
                                    .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                                    .end(translationHandler.cause().getMessage());
                        }
                    });
                });
    }


}
