package bob.gui;

import java.util.Objects;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * Configures avatar images with a consistent center crop and circular shape.
 */
final class AvatarView {

    private AvatarView() {
        // Utility class; do not instantiate.
    }

    /**
     * Applies a centered square crop and circular clip to an image view.
     *
     * @param imageView image view to configure.
     * @param image image to display.
     * @param size avatar width and height.
     */
    static void configure(ImageView imageView, Image image, double size) {
        Objects.requireNonNull(imageView);
        Objects.requireNonNull(image);
        if (size <= 0) {
            throw new IllegalArgumentException("Avatar size must be positive.");
        }

        double avatarRadius = size / 2;
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        imageView.setImage(image);
        imageView.setPreserveRatio(false);
        imageView.setViewport(calculateViewport(image.getWidth(), image.getHeight()));
        imageView.setClip(new Circle(avatarRadius, avatarRadius, avatarRadius));
    }

    /**
     * Calculates the centered square crop for an image.
     *
     * @param imageWidth source image width.
     * @param imageHeight source image height.
     * @return centered square viewport.
     */
    static Rectangle2D calculateViewport(double imageWidth, double imageHeight) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            throw new IllegalArgumentException("Image dimensions must be positive.");
        }

        double cropSize = Math.min(imageWidth, imageHeight);
        double cropX = (imageWidth - cropSize) / 2;
        double cropY = (imageHeight - cropSize) / 2;
        return new Rectangle2D(cropX, cropY, cropSize, cropSize);
    }
}
