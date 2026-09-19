package bro.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * Unit tests for {@link DialogBox}.
 */
public class DialogBoxTest {
    private static boolean isJavaFxAvailable;

    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
            isJavaFxAvailable = true;
        } catch (IllegalStateException e) {
            // Platform already started
            isJavaFxAvailable = true;
        } catch (UnsupportedOperationException e) {
            // Headless Linux without display (e.g. CI without xvfb)
            isJavaFxAvailable = false;
        }
    }

    @BeforeEach
    public void setUp() {
        assumeTrue(isJavaFxAvailable, "Skipping test: JavaFX DISPLAY is unavailable.");
    }

    @Test
    public void getUserDialog_validInput_createsDialogWithCircularClipAndUserStyle() {
        Image img = new Image(DialogBoxTest.class.getResourceAsStream("/images/DaUser.png"));
        DialogBox db = DialogBox.getUserDialog("Hello bro!", img);
        assertNotNull(db);
        assertTrue(db.getStyleClass().contains("user-dialog"));
        assertEquals(Pos.BOTTOM_RIGHT, db.getAlignment());

        ImageView displayPicture = (ImageView) db.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .findFirst()
                .orElse(null);
        assertNotNull(displayPicture);
        assertNotNull(displayPicture.getClip());
        assertTrue(displayPicture.getClip() instanceof Circle);

        Circle circleClip = (Circle) displayPicture.getClip();
        assertEquals(25.0, circleClip.getRadius(), 0.001);
        assertEquals(25.0, circleClip.getCenterX(), 0.001);
        assertEquals(25.0, circleClip.getCenterY(), 0.001);
    }

    @Test
    public void getBroDialog_validInput_createsFlippedDialogWithCircularClipAndBroStyle() {
        Image img = new Image(DialogBoxTest.class.getResourceAsStream("/images/DaBro.png"));
        DialogBox db = DialogBox.getBroDialog("What's up bro!", img);
        assertNotNull(db);
        assertTrue(db.getStyleClass().contains("bro-dialog"));
        assertEquals(Pos.BOTTOM_LEFT, db.getAlignment());

        ImageView displayPicture = (ImageView) db.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .findFirst()
                .orElse(null);
        assertNotNull(displayPicture);
        assertNotNull(displayPicture.getClip());
        assertTrue(displayPicture.getClip() instanceof Circle);

        Circle circleClip = (Circle) displayPicture.getClip();
        assertEquals(25.0, circleClip.getRadius(), 0.001);
        assertEquals(25.0, circleClip.getCenterX(), 0.001);
        assertEquals(25.0, circleClip.getCenterY(), 0.001);
    }
}
