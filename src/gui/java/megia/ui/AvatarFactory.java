package megia.ui;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Creates the local default avatars used when no user image is selected.
 *
 * <p>The avatars are generated from JavaFX pixels, so the application has no remote image
 * dependency and does not need to attribute a third-party asset.</p>
 */
final class AvatarFactory {
    private static final int AVATAR_SIZE = 64;
    private static final double AVATAR_CENTER = AVATAR_SIZE / 2.0;

    private AvatarFactory() {
    }

    /**
     * Creates a friendly default user avatar.
     *
     * @return Generated user avatar image.
     */
    static Image createUserAvatar() {
        WritableImage image = createBaseImage(Color.web("#e7e5ff"));
        PixelWriter pixels = image.getPixelWriter();
        drawCircle(pixels, 32, 47, 17, Color.web("#5750a6"));
        drawCircle(pixels, 32, 27, 11, Color.web("#f2b889"));
        drawCircle(pixels, 32, 23, 11, Color.web("#3d315a"));
        drawCircle(pixels, 32, 29, 8, Color.web("#f2b889"));
        drawCircle(pixels, 28, 28, 1.5, Color.web("#3d315a"));
        drawCircle(pixels, 36, 28, 1.5, Color.web("#3d315a"));
        return image;
    }

    /**
     * Creates a friendly default Megia avatar.
     *
     * @return Generated assistant avatar image.
     */
    static Image createAssistantAvatar() {
        WritableImage image = createBaseImage(Color.web("#e4f5f2"));
        PixelWriter pixels = image.getPixelWriter();
        drawCircle(pixels, 32, 46, 17, Color.web("#2f8f91"));
        drawCircle(pixels, 32, 29, 13, Color.web("#ffffff"));
        drawRectangle(pixels, 20, 25, 44, 35, Color.web("#ffffff"));
        drawCircle(pixels, 27, 29, 2.5, Color.web("#24213b"));
        drawCircle(pixels, 37, 29, 2.5, Color.web("#24213b"));
        drawRectangle(pixels, 30, 12, 34, 18, Color.web("#2f8f91"));
        drawCircle(pixels, 32, 10, 3, Color.web("#f4c95d"));
        return image;
    }

    private static WritableImage createBaseImage(Color background) {
        WritableImage image = new WritableImage(AVATAR_SIZE, AVATAR_SIZE);
        PixelWriter pixels = image.getPixelWriter();
        for (int y = 0; y < AVATAR_SIZE; y++) {
            for (int x = 0; x < AVATAR_SIZE; x++) {
                double distance = Math.hypot(x - AVATAR_CENTER + 0.5, y - AVATAR_CENTER + 0.5);
                pixels.setColor(x, y, distance <= AVATAR_CENTER ? background : Color.TRANSPARENT);
            }
        }
        return image;
    }

    private static void drawCircle(PixelWriter pixels, double centerX, double centerY,
                                   double radius, Color color) {
        int startX = Math.max(0, (int) Math.floor(centerX - radius));
        int endX = Math.min(AVATAR_SIZE - 1, (int) Math.ceil(centerX + radius));
        int startY = Math.max(0, (int) Math.floor(centerY - radius));
        int endY = Math.min(AVATAR_SIZE - 1, (int) Math.ceil(centerY + radius));
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (Math.hypot(x - centerX, y - centerY) <= radius) {
                    pixels.setColor(x, y, color);
                }
            }
        }
    }

    private static void drawRectangle(PixelWriter pixels, int startX, int startY,
                                      int endX, int endY, Color color) {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                pixels.setColor(x, y, color);
            }
        }
    }
}
