package autoservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "autoservice.service",
        "autoservice.repository"
})
public class ServiceConfig {
}

