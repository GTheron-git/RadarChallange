package nl.gt.space.invaders.service;

import nl.gt.space.invaders.entity.Image;

import java.util.List;

public interface ImageService {
    Image getKnownInvader(int index);
    List<Image> getKnownInvaders();

    Image getRadarImage(int index);
    List<Image> getRadarImages();
}
