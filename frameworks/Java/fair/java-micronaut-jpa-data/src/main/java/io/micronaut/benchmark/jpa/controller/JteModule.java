package io.micronaut.benchmark.jpa.controller;

import gg.jte.TemplateEngine;
import gg.jte.runtime.TemplateLoader;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public final class JteModule {

    @Singleton
    public TemplateEngine templateEngine() {
        return TemplateEngine.createPrecompiled(TemplateLoader.CLASSPATH);
    }
}
