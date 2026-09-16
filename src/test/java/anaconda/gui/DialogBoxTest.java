package anaconda.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import anaconda.Anaconda.ResponseStatus;
import anaconda.testutil.JavaFxTestSupport;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Tests the distinct command and response layouts using the real FXML and CSS.
 */
public class DialogBoxTest {
    @BeforeAll
    public static void setUpToolkit() throws InterruptedException {
        JavaFxTestSupport.startToolkit();
    }

    @Test
    public void getUserDialog_command_showsPromptWithoutAvatar() throws Exception {
        JavaFxTestSupport.runOnFxThread(() -> {
            DialogBox box = DialogBox.getUserDialog("todo read book");
            assertEquals(Pos.TOP_LEFT, box.getAlignment());
            assertEquals(3, box.getChildren().size());
            assertEquals(">", ((Label) box.getChildren().get(0)).getText());
            assertEquals("todo read book", ((Label) box.getChildren().get(1)).getText());
            assertEquals("OK", ((Label) box.getChildren().get(2)).getText());
            assertFalse(box.getChildren().stream().anyMatch(ImageView.class::isInstance));
            return null;
        });
    }

    @Test
    public void getUserDialog_emptyCommand_preservesEmptyText() throws Exception {
        JavaFxTestSupport.runOnFxThread(() -> {
            DialogBox box = DialogBox.getUserDialog("");
            assertEquals("", ((Label) box.getChildren().get(1)).getText());
            return null;
        });
    }

    @Test
    public void getUserDialog_longCommand_wrapsWithinNarrowWindow() throws Exception {
        JavaFxTestSupport.runOnFxThread(() -> {
            String command = "todo " + "a long task description ".repeat(12);
            DialogBox box = DialogBox.getUserDialog(command);
            box.setResponseStatus(ResponseStatus.WARNING);
            VBox root = new VBox(box);
            new Scene(root, 380, 600);
            root.applyCss();
            root.layout();
            Label label = (Label) box.getChildren().get(1);
            assertEquals(command, label.getText());
            assertTrue(label.getHeight() > 40);
            assertTrue(label.getBoundsInParent().getMaxX() <= box.getWidth());
            assertTrue(label.getFont().getFamily().toLowerCase().contains("mono"));
            Label status = (Label) box.getChildren().get(2);
            assertEquals("Check input", status.getText());
            assertTrue(status.getBoundsInParent().getMaxX() <= box.getWidth());
            assertTrue(label.getBoundsInParent().getMaxX() <= status.getBoundsInParent().getMinX());
            assertTrue(status.getWidth() >= status.prefWidth(-1));
            return null;
        });
    }

    @Test
    public void getAnacondaDialog_multilineResponse_keepsImageAndReplyText() throws Exception {
        JavaFxTestSupport.runOnFxThread(() -> {
            Image image = new Image(getClass().getResourceAsStream("/images/Anaconda.png"));
            assertFalse(image.isError());
            DialogBox box = DialogBox.getAnacondaDialog("Here's what you've got:\n1.[T][ ] read book", image);
            assertEquals(Pos.TOP_LEFT, box.getAlignment());
            assertSame(image, ((ImageView) box.getChildren().get(0)).getImage());
            assertEquals(99.0, ((ImageView) box.getChildren().get(0)).getFitWidth());
            assertEquals(99.0, ((ImageView) box.getChildren().get(0)).getFitHeight());
            Label label = (Label) box.getChildren().get(1);
            assertEquals("Here's what you've got:\n1.[T][ ] read book", label.getText());
            assertTrue(label.getStyleClass().contains("reply-label"));
            assertTrue(label.isWrapText());
            box.fadeDisplayPicture();
            assertEquals(0.5, box.getChildren().get(0).getOpacity());
            assertEquals(1.0, label.getOpacity());
            return null;
        });
    }
}
