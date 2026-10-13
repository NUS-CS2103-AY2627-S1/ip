package baymax.ui;

import java.io.IOException;
import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/** Loads optional cosmetic resources without preventing the application from starting. */
public final class UiAssets {
    private static final int AVATAR_SIZE = 48;

    private UiAssets() {
        // Utility class.
    }

    /** Returns the packaged font, or the system font when it is missing or unreadable. */
    public static Font loadFont(String resourcePath) {
        try (InputStream stream = UiAssets.class.getResourceAsStream(resourcePath)) {
            if (stream != null) {
                Font font = Font.loadFont(stream, 14.0);
                if (font != null) {
                    return font;
                }
            }
        } catch (IOException | IllegalArgumentException exception) {
            // A missing or damaged cosmetic resource must not prevent startup.
        }
        System.err.println("Unable to load font " + resourcePath + "; using the system font.");
        return Font.getDefault();
    }

    /** Returns the packaged avatar, or a generated placeholder if loading fails. */
    public static Image loadAvatar(String resourcePath) {
        try (InputStream stream = UiAssets.class.getResourceAsStream(resourcePath)) {
            if (stream != null) {
                Image image = new Image(stream);
                if (!image.isError() && image.getWidth() > 0 && image.getHeight() > 0) {
                    return image;
                }
            }
        } catch (IOException | IllegalArgumentException exception) {
            // The placeholder has no dependency on another packaged image.
        }
        System.err.println("Unable to load avatar " + resourcePath + "; using a placeholder.");
        return createPlaceholder();
    }

    /** Draws a simple face so a usable avatar exists even when all image files are missing. */
    private static Image createPlaceholder() {
        WritableImage image = new WritableImage(AVATAR_SIZE, AVATAR_SIZE);
        for (int y = 0; y < AVATAR_SIZE; y++) {
            for (int x = 0; x < AVATAR_SIZE; x++) {
                boolean isEye = y >= 19 && y <= 25 && ((x >= 12 && x <= 18) || (x >= 30 && x <= 36));
                boolean isBridge = y == 22 && x >= 18 && x <= 30;
                image.getPixelWriter().setColor(x, y,
                        isEye || isBridge ? Color.DARKSLATEGRAY : Color.LIGHTGRAY);
            }
        }
        return image;
    }
}
