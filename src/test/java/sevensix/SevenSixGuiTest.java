package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Exercises the real GUI controls with isolated storage and deterministic JavaFX dispatch. */
class SevenSixGuiTest {
    @TempDir
    private Path temporaryDirectory;
    private Stage stage;
    private VBox conversation;
    private TextField input;
    private Button send;
    private ScrollPane scroll;
    private String previousDataFile;

    /** Shares a single toolkit with the dialog tests. */
    @BeforeAll
    static void startToolkit() throws Exception {
        FxTestSupport.startToolkit();
    }

    /** Redirects default application storage for the duration of each test. */
    @BeforeEach
    void isolateStorage() {
        previousDataFile = System.getProperty("sevensix.data.file");
        System.setProperty("sevensix.data.file", temporaryDirectory.resolve("tasks.txt").toString());
    }

    /** Closes the test window and restores the process configuration even after a failed assertion. */
    @AfterEach
    void cleanUp() throws Exception {
        try {
            FxTestSupport.runOnFxThread(() -> {
                if (stage != null) {
                    stage.close();
                }
            });
        } finally {
            if (previousDataFile == null) {
                System.clearProperty("sevensix.data.file");
            } else {
                System.setProperty("sevensix.data.file", previousDataFile);
            }
        }
    }

    /** Startup displays the greeting, stylesheet, window limits, and usable input controls. */
    @Test
    void startupDisplaysConfiguredWindow() throws Exception {
        openWindow();
        FxTestSupport.runOnFxThread(() -> {
            assertTrue(stage.isShowing());
            assertEquals(Ui.APPLICATION_NAME, stage.getTitle());
            assertEquals(560.0, stage.getMinWidth());
            assertEquals(520.0, stage.getMinHeight());
            assertTrue(stage.getScene().getStylesheets().getFirst().endsWith("/css/main.css"));
            assertEquals(Ui.INPUT_PROMPT, input.getPromptText());
            assertEquals(Ui.SEND_BUTTON_LABEL, send.getText());
            assertTrue(send.isDefaultButton());
            assertTrue(scroll.isFitToWidth());
            assertEquals(ScrollPane.ScrollBarPolicy.NEVER, scroll.getHbarPolicy());
            assertEquals(1, conversation.getChildren().size());
            assertEquals(Ui.getGreeting(), messageAt(0).getText());
        });
    }

    /** Empty and whitespace-only submissions do not add conversation rows or alter the input. */
    @Test
    void blankInputIsIgnoredByBothControls() throws Exception {
        openWindow();
        FxTestSupport.runOnFxThread(() -> {
            send.fire();
            input.setText("   ");
            input.fireEvent(new ActionEvent());
            assertEquals(1, conversation.getChildren().size());
            assertEquals("   ", input.getText());
        });
    }

    /** A button submission persists a task and Enter produces a normal list reply. */
    @Test
    void bothControlsSubmitCommandsAndClearInput() throws Exception {
        openWindow();
        FxTestSupport.runOnFxThread(() -> {
            input.setText("todo read book");
            send.fire();
            assertEquals("", input.getText());
            assertEquals("todo read book", messageAt(1).getText());
            assertTrue(messageAt(2).getText().contains("[T][ ] read book"));
            assertTrue(messageAt(2).getStyleClass().contains("bot-message"));
            input.setText("list");
            input.fireEvent(new ActionEvent());
            assertEquals("", input.getText());
            assertEquals(5, conversation.getChildren().size());
            assertTrue(messageAt(4).getText().contains("1.[T][ ] read book"));
        });
        assertEquals("T | 0 | read book" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("tasks.txt")));
        FxTestSupport.runOnFxThread(() -> assertEquals(1.0, scroll.getVvalue()));
    }

    /** Invalid commands appear as highlighted errors and leave the composer ready for another command. */
    @Test
    void invalidCommandDisplaysErrorReply() throws Exception {
        openWindow();
        FxTestSupport.runOnFxThread(() -> {
            input.setText("nonsense");
            send.fire();
            assertEquals(3, conversation.getChildren().size());
            assertTrue(Ui.isErrorResponse(messageAt(2).getText()));
            assertTrue(messageAt(2).getStyleClass().contains("error-message"));
            assertEquals("", input.getText());
        });
    }

    /** A blocked storage path shows an error after the greeting without damaging the blocking file. */
    @Test
    void startupDisplaysStorageWarning() throws Exception {
        Path blocker = temporaryDirectory.resolve("blocker");
        Files.writeString(blocker, "preserve me");
        System.setProperty("sevensix.data.file", blocker.resolve("tasks.txt").toString());
        openWindow();
        FxTestSupport.runOnFxThread(() -> {
            assertEquals(2, conversation.getChildren().size());
            assertEquals(Ui.getGreeting(), messageAt(0).getText());
            assertTrue(messageAt(1).getText().contains(Ui.LOAD_FAILURE));
            assertTrue(messageAt(1).getStyleClass().contains("error-message"));
        });
        assertEquals("preserve me", Files.readString(blocker));
    }

    /** A growing conversation scrolls to its bottom after layout changes. */
    @Test
    void conversationGrowthScrollsToBottom() throws Exception {
        openWindow();
        FxTestSupport.runOnFxThread(() -> {
            scroll.setVvalue(0.0);
            double oldHeight = conversation.getHeight();
            conversation.getChildren().add(DialogBox.getBotDialog("Another reply\nwith another line"));
            stage.getScene().getRoot().applyCss();
            stage.getScene().getRoot().layout();
            assertFalse(oldHeight == conversation.getHeight());
            assertEquals(1.0, scroll.getVvalue());
        });
    }

    /** Creates a real application window, then locates controls by their existing stylesheet classes. */
    private void openWindow() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            stage = new Stage();
            new SevenSixGui().start(stage);
            conversation = (VBox) stage.getScene().lookup(".dialog-container");
            input = (TextField) stage.getScene().lookup(".command-field");
            send = (Button) stage.getScene().lookup(".send-button");
            scroll = (ScrollPane) stage.getScene().lookup(".conversation");
        });
    }

    /** Retrieves the text label without relying on whether a row includes an avatar.
     *
     * @param index the conversation row to inspect.
     * @return the message label in that row.
     */
    private Label messageAt(int index) {
        DialogBox dialog = (DialogBox) conversation.getChildren().get(index);
        return (Label) dialog.getChildren().getLast();
    }
}
