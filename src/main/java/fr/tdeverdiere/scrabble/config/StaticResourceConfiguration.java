package fr.tdeverdiere.scrabble.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    private Logger LOGGER = LoggerFactory.getLogger(StaticResourceConfiguration.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        LOGGER.info("client resource path : client");

        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:client/"
                )
                .resourceChain(true);
    }
}
