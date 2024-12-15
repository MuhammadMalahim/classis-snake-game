package snake.res;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Utility class for loading resources such as images or input streams.
 */
public class ResourceLoader {

    /**
     * Loads a resource as an InputStream.
     *
     * @param resName the name of the resource to load
     * @return an InputStream for the resource, or null if not found
     */
    public static InputStream loadResource(String resName) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(resName);
    }

    /**
     * Loads an image from the specified resource.
     *
     * @param resName the name of the resource containing the image
     * @return the loaded Image
     * @throws IOException if the image cannot be read
     */
    public static Image loadImage(String resName) throws IOException {
        URL url = ResourceLoader.class.getClassLoader().getResource(resName);
        return ImageIO.read(url);
    }
}
