package nl.gt.space.invaders.config.storage;

import lombok.Data;
import nl.gt.space.invaders.entity.Image;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class ImageStorage {
    private List<Image> knownInvaders;
    private List<Image> radarImages;

    public ImageStorage() {
        this.knownInvaders = new ArrayList<>();
        this.radarImages = new ArrayList<>();
    }
}
