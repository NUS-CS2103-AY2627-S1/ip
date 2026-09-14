package arthur.ui;

import java.util.Objects;

import javafx.scene.image.Image;

/**
 * Utility methods for the UI.
 */
public class Utils {

    /**
     * Loads an image from the resources folder.
     *
     * @param image the path to the image.
     * @return The image object.
     * @throws NullPointerException if the image path is null.
     */
    public static Image loadImage(String image) {
        return new Image(Objects.requireNonNull(MainWindow.class.getResourceAsStream(image)));
    }
}
