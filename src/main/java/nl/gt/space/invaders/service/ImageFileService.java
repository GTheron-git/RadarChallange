package nl.gt.space.invaders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gt.space.invaders.config.constants.Constants;
import nl.gt.space.invaders.config.storage.ImageStorage;
import nl.gt.space.invaders.entity.Image;
import nl.gt.space.invaders.util.ImageUtil;
import nl.gt.space.invaders.util.MdFileImageLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageFileService implements ImageService {

    private final ImageStorage imageStorage;
    private final Constants constants;

    @PostConstruct
    public void init() {
        validateStartupParameters();

        try {
            List<Image> spaceInvaders = MdFileImageLoader
                    .getImagesFromMdFile(constants.getFilename(), constants.getInvaderSectionIdentifier());
            if (Objects.isNull(spaceInvaders) || spaceInvaders.size() == 0) {
                throw new IllegalStateException("Loaded 0 space invaders");
            } else {
                log.info("Loaded [{}] invaders", spaceInvaders.size());
                spaceInvaders.stream().forEach(invader -> log.info("{}", ImageUtil.printChars(invader)));
                imageStorage.setKnownInvaders(spaceInvaders);
            }

            List<Image> radarImages = MdFileImageLoader
                    .getImagesFromMdFile(constants.getFilename(), constants.getRadarSectionIdentifier());
            if (Objects.isNull(radarImages) || radarImages.size() == 0) {
                throw new IllegalStateException("Loaded 0 radar images");
            } else {
                log.info("Loaded [{}] radar images", radarImages.size());
                radarImages.stream().forEach(rImage -> log.info("{}", ImageUtil.printChars(rImage)));
                imageStorage.setRadarImages(radarImages);
            }
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong loading data from markdown file");
        }
    }

    private void validateStartupParameters() {
        if (Objects.isNull(constants.getFilename()) || constants.getFilename().isEmpty()) {
            throw new IllegalStateException(
                    "Filename for reading markdown document is not specified in application properties");
        }
        if (Objects.isNull(constants.getInvaderSectionIdentifier()) || constants.getInvaderSectionIdentifier()
                .isEmpty()) {
            throw new IllegalStateException("Section title to match invaders in " + constants.getFilename()
                                            + " is not specified in application properties");
        }
        if (Objects.isNull(constants.getRadarSectionIdentifier()) || constants.getRadarSectionIdentifier().isEmpty()) {
            throw new IllegalStateException("Section title to match radar images in " + constants.getFilename()
                                            + " is not specified in application properties");
        }
    }

    @Override
    public List<Image> getKnownInvaders() {
        return imageStorage.getKnownInvaders();
    }

    @Override
    public List<Image> getRadarImages() {
        return imageStorage.getRadarImages();
    }
}
