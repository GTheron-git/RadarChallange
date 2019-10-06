package nl.gt.space.invaders.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties
public class ConstantsConfig {
    @Bean
    @Scope("application")
    @ConfigurationProperties(prefix = "constants")
    public Constants constants() {
        return new ConstantsImpl();
    }
}
