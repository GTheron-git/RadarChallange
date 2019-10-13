package nl.gt.space.invaders.config;

import nl.gt.space.invaders.service.ImageService;
import nl.gt.space.invaders.service.ImageTestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public ImageService imageService() {
        return new ImageTestService();
    }
}
