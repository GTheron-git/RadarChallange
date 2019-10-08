package nl.gt.space.invaders;

import lombok.extern.slf4j.Slf4j;
import nl.gt.space.invaders.dto.Image;
import nl.gt.space.invaders.service.RadarMatcher;
import nl.gt.space.invaders.util.MdFileImageLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@Slf4j
public class Application {

	private static String invadersSection = "### Known space invaders";
	private static String radarSection = "### Example radar image";

	public static void main(String[] args) {
	    try {
			List<Image> spaceInvaders = MdFileImageLoader.getImagesFromMdFile("SpaceInvaders_2.0.md", invadersSection);
			for (Image i : spaceInvaders) System.out.println(i.print());
			List<Image> radarImages = MdFileImageLoader.getImagesFromMdFile("SpaceInvaders_2.0.md", radarSection);
			for (Image i : radarImages) System.out.println(i.print());

			RadarMatcher.matchKnownInvaders(radarImages.get(0), spaceInvaders);
		} catch (IOException e) {

		}

		SpringApplication.run(Application.class, args).close();
	}
}
