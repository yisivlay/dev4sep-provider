package com.dev4sep.base;

import com.dev4sep.base.config.boot.WebApplicationConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

public class ServerApplication extends SpringBootServletInitializer {

    @Import({WebApplicationConfiguration.class})
    public static final class Configuration {
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(Configuration.class);
    }

    public static void main(String[] args) {
        configureApplication(new SpringApplicationBuilder(ServerApplication.class)).run(args);
    }

}
