package com.lightbox.transalator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for translation service.
 */
@SpringBootApplication
public class TranslatorApplication {

    /**
     * Ctor.
     */
    private TranslatorApplication() {

    }

    /**
     * Main method.
     * @param args Program argc
     */
    public static void main(final String[] args) {
        SpringApplication.run(TranslatorApplication.class, args);
    }

}
