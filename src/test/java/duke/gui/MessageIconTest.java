package duke.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.shape.Polyline;
import org.junit.jupiter.api.Test;

/** Checks the speaker symbols without loading images or starting a graphical desktop. */
class MessageIconTest {
    @Test
    void constructor_assistant_drawsRightArrowWithAccessibleLabel() {
        MessageIcon icon = new MessageIcon(false);

        assertEquals("Wangsa reply", icon.getAccessibleText());
        assertTrue(icon.getStyleClass().contains("assistant-icon"));
        assertEquals(1, icon.getChildren().size());
        Polyline arrow = assertInstanceOf(Polyline.class, icon.getChildren().getFirst());
        assertEquals(0, arrow.getRotate());
        assertEquals(6, arrow.getPoints().size());
    }

    @Test
    void constructor_user_drawsLeftArrowWithAccessibleLabel() {
        MessageIcon icon = new MessageIcon(true);

        assertEquals("Your message", icon.getAccessibleText());
        assertTrue(icon.getStyleClass().contains("user-icon"));
        assertEquals(1, icon.getChildren().size());
        Polyline arrow = assertInstanceOf(Polyline.class, icon.getChildren().getFirst());
        assertEquals(180, arrow.getRotate());
    }

    @Test
    void constructor_bothSpeakers_keepsMatchingFixedSize() {
        for (boolean isUser : new boolean[]{false, true}) {
            MessageIcon icon = new MessageIcon(isUser);
            assertEquals(36, icon.getMinWidth());
            assertEquals(36, icon.getPrefWidth());
            assertEquals(36, icon.getMaxWidth());
            assertEquals(36, icon.getMinHeight());
            assertEquals(36, icon.getPrefHeight());
            assertEquals(36, icon.getMaxHeight());
        }
    }
}
