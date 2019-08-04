package com.lightbox.transalator.verticles;

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
     * Http server port
     */
    private final int port;

    /**
     * Ctor.
     *
     * @param translator  Translator instance
     * @param environment Spring environment
     */
    public TranslationResource(final Translator translator, final Environment environment) {
        this(
                translator,
                Integer.parseInt(environment.getProperty("port"))
        );
    }

    @Override
    public void start() {
        final Router router = Router.router(this.vertx);
        TranslationResource.configureRouter(router, translator);
        this.vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(this.port, handler -> {
                    if (handler.succeeded()) {
                        log.info("Server was started in port {}", this.port);
                    } else {
                        log.error("Can't start http server in port {}", this.port, handler.cause());
                    }
                })
    }

    /**
     * Create rest end point for translation.
     *
     * @param router     Vertx router
     * @param translator Translator to use in order to translate
     */
    private static void configureRouter(final Router router, final Translator translator) {
        router.route(HttpMethod.GET, "/translate")
                .handler(handler -> {
                    final HttpServerRequest request = handler.request();
                    final HttpServerResponse response = handler.response();
                    final Future<JsonObject> translation = translator.translate(request.getParam("languageFrom"), request.getParam("languageTo"), request.getParam("text"));
                    translation.setHandler(translationHandler -> {
                        if (translationHandler.succeeded()) {
                            response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
                                    .setStatusCode(HttpResponseStatus.OK.code())
                                    .end(translationHandler.result().toString());
                        } else {
                            response.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
                                    .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                                    .end(translationHandler.cause().getMessage());
                        }
                    });
                });
    }


}
