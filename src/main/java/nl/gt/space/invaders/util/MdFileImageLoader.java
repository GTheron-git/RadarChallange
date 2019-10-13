package nl.gt.space.invaders.util;

import lombok.extern.slf4j.Slf4j;
import nl.gt.space.invaders.entity.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class MdFileImageLoader {

    private static final String IMAGE_REGEX = "^[-o]*$";
    private static String codeblockDelimitor = "~~~";
    private static final String sectionDelimitor = "#"; // assume === and --- underscores are not used for markdown in document


    public static List<Image> getImagesFromMdFile(String filename, String sectionMatchString) throws IOException {

        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        if (!advanceToSection(scanner, sectionMatchString)) {
            log.warn("Failed to find section [{}] in filename [{}]", filename, sectionMatchString);
            return null;
        }

        List<Image> result = getImagesInSection(scanner);

        if (Objects.isNull(result)) {
            log.warn("Failed to load Images from file [{}], from section [{}]", filename, sectionMatchString);
        }

        return result;
    }

    private static List<Image> getImagesInSection(Scanner sectionScanner) throws IOException {
        List<Image> imageList = new ArrayList();
        int imageId = 0;
        while (hasMoreImageStreams(sectionScanner)) {
            List<String> imageStringStream = getNextImageStream(sectionScanner);

            if (!validateImageStread(imageStringStream)) {
                log.warn("Validation of image stream failed. Ignoring image.");
                continue;
            }
            Image im = new Image(imageStringStream);
            im.setId(imageId++);
            imageList.add(im);
        }

        return imageList;
    }

    private static boolean validateImageStread(List<String> imageStream) {
        if (imageStream.isEmpty()) {
            log.warn("Image is empty. Rejecting");
            return false;
        }

        int cols = imageStream.get(0).length();
        for (int i = 0; i < imageStream.size(); ++i) {
            String currentLine = imageStream.get(i);
            if (cols != currentLine.length()) {
                log.warn("Image has uneven number of columns. Rejecting");
                return false;
            } else if (!currentLine.matches(IMAGE_REGEX)) {
                log.warn("Image data invalid: [{}]. Rejecting", currentLine);
                return false;
            }
        }

        return true;
    }

    private static boolean advanceToSection(Scanner scanner, String sectionMatchString) {
        while (scanner.hasNextLine()) {
            String nextSectionTitle = nextSection(scanner);
            if (nextSectionTitle.contains(sectionMatchString)) {
                log.debug("Matched section [{}]", sectionMatchString);
                return true;
            }
        }

        log.debug("Could not Find section [{}]", sectionMatchString);
        return false;
    }

    private static String nextSection(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            if (nextLine.contains(sectionDelimitor)) {
                log.debug("Found section [{}]", nextLine);
                return nextLine;
            }
        }
        return "";
    }

    private static List<String> getNextImageStream(Scanner scanner) {
        List<String> imageStringList = new ArrayList();

        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine().trim();

            if (nextLine.contains(codeblockDelimitor)) {
                break;
            }

            if (!nextLine.matches(IMAGE_REGEX)) {
                log.warn("Found invalid image data: [{}]. Ignoring", nextLine);
            } else {
                imageStringList.add(nextLine);
            }
        }

        return imageStringList;
    }

    private static boolean hasMoreImageStreams(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            if (nextLine.contains(codeblockDelimitor)) {
                return true;
            } else if (nextLine.contains(sectionDelimitor)) {
                return false;
            }
        }

        return false;
    }

}
