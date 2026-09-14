package groot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import groot.exception.GrootException;
import groot.testutil.FxTestRuntime;
import groot.testutil.IsolatedProcess;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Tests real JavaFX resources and control actions in isolated desktop processes.
 */
public class GuiTest {
    @TempDir
    public Path directory;

    @Test
    public void window_commandsAndErrors_rendersResponsesAndKeepsState() throws Exception {
        assertEquals("passed\n", IsolatedProcess.run(directory, WindowScenario.class, ""));
    }

    @Test
    public void dialog_speakerAndErrorStyles_preservesContentAndAlignment() throws Exception {
        assertEquals("passed\n", IsolatedProcess.run(directory, DialogScenario.class, ""));
    }

    @Test
    public void start_invalidSavedData_wrapsStorageFailure() throws Exception {
        Files.createDirectories(directory.resolve("data"));
        Files.writeString(directory.resolve("data/groot.txt"), "D | 0 | bad | invalid\n");
        assertEquals("passed\n", IsolatedProcess.run(directory, StartupFailureScenario.class, ""));
    }

    /**
     * Exercises startup and both submission controls against the real application.
     */
    public static class WindowScenario {
        /**
         * Checks controls, errors, input clearing, and scrolling on the JavaFX thread.
         */
        public static void main(String[] args) throws Exception {
            FxTestRuntime.run(() -> {
                Stage stage = new Stage();
                double[] requestedSize = new double[2];
                // Check the requested size before the window manager fits it to the available screen.
                stage.setOnShowing(event -> {
                    requestedSize[0] = stage.getWidth();
                    requestedSize[1] = stage.getHeight();
                });
                try {
                    new Main().start(stage);
                    assertTrue(stage.isShowing());
                    assertEquals(1000, requestedSize[0]);
                    assertEquals(720, requestedSize[1]);
                    assertEquals("Groot", stage.getTitle());
                    assertEquals(800, stage.getMinWidth());
                    assertTrue(stage.getWidth() >= 800 && stage.getWidth() <= 1000);
                    assertEquals(600, stage.getMinHeight());
                    assertTrue(stage.getHeight() >= 600 && stage.getHeight() <= 720);
                    Parent root = stage.getScene().getRoot();
                    TextField input = (TextField) root.lookup("#userInput");
                    Button send = (Button) root.lookup("#sendButton");
                    VBox dialogs = (VBox) root.lookup("#dialogContainer");
                    ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
                    assertNotNull(input);
                    assertFalse(scroll.vvalueProperty().isBound());
                    assertTrue(root.lookup("#welcomeCard").isVisible());
                    assertTrue(send.isDisabled());
                    input.setText("   ");
                    input.fireEvent(new ActionEvent());
                    assertEquals(0, dialogs.getChildren().size());
                    input.setText("  unknown  ");
                    input.fireEvent(new ActionEvent());
                    assertEquals("", input.getText());
                    assertEquals(2, dialogs.getChildren().size());
                    assertFalse(root.lookup("#welcomeCard").isManaged());
                    Label firstError = (Label) dialogs.getChildren().get(1).lookup("#dialog");
                    assertEquals("Error: Oops! I don't recognise that command.", firstError.getText());
                    assertTrue(firstError.getStyleClass().contains("error-label"));
                    String[] commands = {"todo first", "todo", "list", "help"};
                    for (String command : commands) {
                        input.setText(command);
                        send.fire();
                        assertEquals("", input.getText());
                    }
                    assertEquals(10, dialogs.getChildren().size());
                    Label added = (Label) dialogs.getChildren().get(3).lookup("#dialog");
                    Label rejected = (Label) dialogs.getChildren().get(5).lookup("#dialog");
                    Label listed = (Label) dialogs.getChildren().get(7).lookup("#dialog");
                    assertFalse(added.getStyleClass().contains("error-label"));
                    assertTrue(rejected.getStyleClass().contains("error-label"));
                    assertFalse(listed.getStyleClass().contains("error-label"));
                    assertEquals("Here are the tasks in your list:\n 1.[T][ ] first", listed.getText());
                    root.applyCss();
                    root.layout();
                    assertEquals(Color.web("#991b1b"), firstError.getTextFill());
                    assertTrue(firstError.getFont().getStyle().contains("Bold"));
                    scroll.setVvalue(0);
                    assertEquals(0, scroll.getVvalue());
                    assertFalse(((ImageView) root.lookup("#grootArtwork")).getImage().isError());
                    assertEquals("1 task · 0 completed", ((Label) root.lookup("#taskCount")).getText());

                } finally {
                    stage.close();
                }
            });
            System.out.println("passed");
        }
    }

    /**
     * Checks dialog factories and the applied stylesheet without screenshot comparisons.
     */
    public static class DialogScenario {
        /**
         * Verifies text, image identity, speaker order, wrapping, and error colors.
         */
        public static void main(String[] args) throws Exception {
            FxTestRuntime.run(() -> {
                DialogBox user = DialogBox.getUserDialog("hello");
                DialogBox reply = DialogBox.getGrootDialog("reply");
                DialogBox error = DialogBox.getErrorDialog("  bad command \n");
                VBox root = new VBox(user, reply, error);
                new Scene(root, 600, 400);
                root.applyCss();
                root.layout();
                assertEquals(Pos.TOP_RIGHT, user.getAlignment());
                assertEquals(Pos.TOP_LEFT, reply.getAlignment());
                assertTrue(user.getChildren().getFirst() instanceof Label);
                assertEquals("G", ((Label) reply.getChildren().getFirst()).getText());
                assertEquals("You", ((Label) user.getChildren().getLast()).getText());
                Label userLabel = (Label) user.lookup("#dialog");
                Label replyLabel = (Label) reply.lookup("#dialog");
                Label errorLabel = (Label) error.lookup("#dialog");
                assertEquals("hello", userLabel.getText());
                assertEquals("reply", replyLabel.getText());
                assertTrue(userLabel.isWrapText());
                assertTrue(replyLabel.isWrapText());
                assertFalse(userLabel.getStyleClass().contains("reply-label"));
                assertTrue(replyLabel.getStyleClass().contains("reply-label"));
                assertEquals("Error: bad command", errorLabel.getText());
                assertEquals(Color.web("#fff1f2"), errorLabel.getBackground().getFills().getFirst().getFill());
                assertEquals(Color.web("#b91c1c"), errorLabel.getBorder().getStrokes().getFirst().getTopStroke());
                assertEquals(Color.web("#991b1b"), errorLabel.getTextFill());
                assertTrue(errorLabel.getFont().getStyle().contains("Bold"));
                assertFalse(replyLabel.getStyleClass().contains("error-label"));
            });
            System.out.println("passed");
        }
    }

    /**
     * Verifies startup failure before the primary window is shown.
     */
    public static class StartupFailureScenario {
        /**
         * Checks that malformed storage remains available as the startup exception's cause.
         */
        public static void main(String[] args) throws Exception {
            FxTestRuntime.run(() -> {
                Stage stage = new Stage();
                try {
                    IllegalStateException error = assertThrows(
                            IllegalStateException.class, () -> new Main().start(stage));
                    assertEquals("Unable to start Groot", error.getMessage());
                    assertTrue(error.getCause() instanceof GrootException);
                    assertFalse(stage.isShowing());
                } finally {
                    stage.close();
                }
            });
            System.out.println("passed");
        }
    }
}
