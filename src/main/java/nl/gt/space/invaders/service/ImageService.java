package nl.gt.space.invaders.service;

import nl.gt.space.invaders.entity.Image;

import java.util.List;

public interface ImageService {
    List<Image> getKnownInvaders();
    List<Image> getRadarImages();
}
