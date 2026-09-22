package oz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Tests the GUI presentation components in {@link DialogBox}.
 */
public class DialogBoxTest {

    /** 1x1 pixel image used as dummy avatar for testing. */
    private static Image dummyImage;

    /**
     * Initializes the JavaFX toolkit once before running GUI unit tests.
     */
    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException exception) {
            // Toolkit has already been initialized
        }
        dummyImage = new WritableImage(1, 1);
    }

    @Test
    public void getUserDialog_validText_alignedRightWithoutAvatar() {
        String message = "User test message";
        DialogBox dialogBox = DialogBox.getUserDialog(message);

        assertEquals(Pos.TOP_RIGHT, dialogBox.getAlignment());
        assertEquals(1, dialogBox.getChildren().size());

        assertInstanceOf(TextArea.class, dialogBox.getChildren().get(0));

        TextArea messageArea = (TextArea) dialogBox.getChildren().get(0);
        assertEquals(message, messageArea.getText());
        assertTrue(messageArea.isWrapText());
        assertFalse(messageArea.isEditable());
        assertEquals(0.0, messageArea.getMinWidth());
        assertEquals("Copy message", messageArea.getContextMenu().getItems().get(0).getText());
        assertFalse(messageArea.getStyleClass().contains("reply-label"));

        messageArea.selectRange(0, 4);
        assertEquals("User", messageArea.getSelectedText());
    }

    @Test
    public void getOzDialog_validTextAndImage_alignedLeftWithImageViewFirst() {
        String message = "Oz reply message";
        DialogBox dialogBox = DialogBox.getOzDialog(message, dummyImage);

        assertEquals(Pos.TOP_LEFT, dialogBox.getAlignment());
        assertEquals(2, dialogBox.getChildren().size());

        // Due to flipping, ImageView is at index 0 and TextArea is at index 1
        assertInstanceOf(ImageView.class, dialogBox.getChildren().get(0));
        assertInstanceOf(TextArea.class, dialogBox.getChildren().get(1));

        ImageView imageView = (ImageView) dialogBox.getChildren().get(0);
        assertEquals(dummyImage, imageView.getImage());
        assertEquals(40.0, imageView.getFitWidth());
        assertEquals(40.0, imageView.getFitHeight());

        TextArea messageArea = (TextArea) dialogBox.getChildren().get(1);
        assertEquals(message, messageArea.getText());
        assertTrue(messageArea.isWrapText());
        assertTrue(messageArea.getStyleClass().contains("reply-label"));
    }

    @Test
    public void getUserDialog_multilineText_preservesTextAndFormatting() {
        String multilineMessage = "Here are the tasks:\n1. [T][ ] task 1\n2. [T][X] task 2";
        DialogBox dialogBox = DialogBox.getUserDialog(multilineMessage);

        TextArea messageArea = (TextArea) dialogBox.getChildren().get(0);
        assertEquals(multilineMessage, messageArea.getText());
    }

    @Test
    public void getOzDialog_responseTypes_appliesCorrectStyles() {
        DialogBox addDialog = DialogBox.getOzDialog("Added", dummyImage, ResponseType.ADD);
        TextArea addMessage = (TextArea) addDialog.getChildren().get(1);
        assertTrue(addMessage.getStyleClass().contains("add-label"));

        DialogBox markedDialog = DialogBox.getOzDialog("Marked", dummyImage, ResponseType.CHANGE_MARK);
        TextArea markedMessage = (TextArea) markedDialog.getChildren().get(1);
        assertTrue(markedMessage.getStyleClass().contains("marked-label"));

        DialogBox deleteDialog = DialogBox.getOzDialog("Deleted", dummyImage, ResponseType.DELETE);
        TextArea deleteMessage = (TextArea) deleteDialog.getChildren().get(1);
        assertTrue(deleteMessage.getStyleClass().contains("delete-label"));

        DialogBox errorDialog = DialogBox.getOzDialog("Error", dummyImage, ResponseType.ERROR);
        TextArea errorMessage = (TextArea) errorDialog.getChildren().get(1);
        assertTrue(errorMessage.getStyleClass().contains("error-label"));

        DialogBox defaultDialog = DialogBox.getOzDialog("Default", dummyImage, ResponseType.DEFAULT);
        TextArea defaultMessage = (TextArea) defaultDialog.getChildren().get(1);
        assertFalse(defaultMessage.getStyleClass().contains("add-label"));
        assertFalse(defaultMessage.getStyleClass().contains("marked-label"));
        assertFalse(defaultMessage.getStyleClass().contains("delete-label"));
        assertFalse(defaultMessage.getStyleClass().contains("error-label"));
    }

    @Test
    public void getOzDialog_longTaskList_contentFitsViewport() {
        String taskListMessage = """
                Here is the master task list:
                1. [D][ ] submit report (by: Oct 02 2026, 6pm)
                2. [E][ ] project meeting (from: Oct 02 2026, 2pm to: Oct 02 2026, 4pm)
                3. [R] tutorial (from: Oct 02 2026, 10am to: Oct 02 2026, 12pm; repeats: every 1 week)
                4. [D][ ] submit report (by: Oct 02 2026, 6pm)
                5. [E][ ] project meeting (from: Oct 02 2026, 2pm to: Oct 02 2026, 4pm)
                6. [R] tutorial (from: Oct 02 2026, 10am to: Oct 02 2026, 12pm; repeats: every 1 week)
                7. [R] tutorial (from: Oct 02 2026, 10am to: Oct 02 2026, 12pm; repeats: every 1 week)
                """.stripTrailing();
        DialogBox dialogBox = DialogBox.getOzDialog(taskListMessage, dummyImage);
        VBox root = new VBox(dialogBox);
        new Scene(root, 500, 800);

        root.resize(500, 800);
        root.applyCss();
        root.layout();
        root.layout();

        TextArea messageArea = (TextArea) dialogBox.getChildren().get(1);
        ScrollPane internalScrollPane = (ScrollPane) messageArea.lookup(".scroll-pane");
        Region textContent = (Region) messageArea.lookup(".content");
        assertNotNull(internalScrollPane);
        assertNotNull(textContent);
        double viewportHeight = internalScrollPane.getViewportBounds().getHeight();
        assertTrue(textContent.getHeight() <= viewportHeight, () ->
                String.format("Text content height %.1f exceeded viewport height %.1f",
                        textContent.getHeight(), viewportHeight));
    }
}
