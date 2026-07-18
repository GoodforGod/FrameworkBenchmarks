package org.springframework.benchmark.jdbctemplate.config;

import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class JteConfig {

    @Value("${jte.source-dir:src/main/jte}")
    private String jteSourceDir;

    @Bean
    public TemplateEngine templateEngine() {
        return TemplateEngine.create(
            new DirectoryCodeResolver(Path.of(jteSourceDir)),
            gg.jte.ContentType.Html
        );
    }
}
