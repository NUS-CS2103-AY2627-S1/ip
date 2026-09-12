package bob.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/** Tests calculation of consistently cropped avatar viewports. */
class AvatarViewTest {

    @Test
    void calculateViewport_portraitImage_returnsCenteredSquare() {
        Rectangle2D viewport = AvatarView.calculateViewport(600, 804);

        assertEquals(new Rectangle2D(0, 102, 600, 600), viewport);
    }

    @Test
    void calculateViewport_landscapeImage_returnsCenteredSquare() {
        Rectangle2D viewport = AvatarView.calculateViewport(400, 300);

        assertEquals(new Rectangle2D(50, 0, 300, 300), viewport);
    }

    @Test
    void calculateViewport_nonPositiveDimension_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> AvatarView.calculateViewport(0, 300));
        assertThrows(IllegalArgumentException.class, () -> AvatarView.calculateViewport(300, 0));
    }

    @Test
    void configure_validImageAndSize_configuresCropAndCircularClip() {
        Image image = new Image(AvatarViewTest.class.getResourceAsStream("/images/huahua.png"));
        ImageView imageView = new ImageView();

        AvatarView.configure(imageView, image, 42);

        assertEquals(42, imageView.getFitWidth());
        assertEquals(42, imageView.getFitHeight());
        assertEquals(image, imageView.getImage());
        assertEquals(AvatarView.calculateViewport(image.getWidth(), image.getHeight()), imageView.getViewport());
        assertTrue(imageView.getClip() instanceof Circle);
    }

    @Test
    void configure_nullOrInvalidArguments_expectedExceptionThrown() {
        Image image = new Image(AvatarViewTest.class.getResourceAsStream("/images/huahua.png"));
        ImageView imageView = new ImageView();

        assertThrows(NullPointerException.class, () -> AvatarView.configure(null, image, 42));
        assertThrows(NullPointerException.class, () -> AvatarView.configure(imageView, null, 42));
        assertThrows(IllegalArgumentException.class, () -> AvatarView.configure(imageView, image, 0));
    }
}
