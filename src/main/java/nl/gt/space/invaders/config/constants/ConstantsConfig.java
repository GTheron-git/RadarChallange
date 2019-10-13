package nl.gt.space.invaders.config.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
@EnableConfigurationProperties
public class ConstantsConfig {
    @Bean
    @ApplicationScope
    @ConfigurationProperties(prefix = "constants")
    public Constants constants() {
        return new ConstantsImpl();
    }
}
