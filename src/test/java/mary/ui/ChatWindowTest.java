package mary.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Exercises real JavaFX controls on the FX thread using isolated task files.
 * Run with guiTest on a machine with a graphical desktop.
 */
@Tag("gui")
class ChatWindowTest {
    @TempDir
    Path directory;
    private Stage stage;
    private ChatWindow chat;
    private TextField input;
    private Button send;

    /**
     * Keeps the visible identity, accessible emblem, and welcome consistent.
     */
    @Test
    void startup_catIdentity_displaysBrandAndWelcome() throws Exception {
        onFxThread(() -> {
            assertEquals("MARY", ((Label) chat.lookup("#brand-name")).getText());
            assertEquals("A smiling cat with pointed ears and whiskers",
                    chat.lookup("#brand-emblem").getAccessibleText());
            assertTrue(transcript().contains("I'm MARY, your purr-sonal task assistant."));
            assertTrue(chat.lookupAll(".sender").stream().map(Label.class::cast)
                    .anyMatch(label -> label.getText().equals("MARY")));
            TitledPane guide = (TitledPane) chat.lookup("#command-guide");
            assertEquals("Cat's guide / commands & examples", guide.getText());
            return null;
        });
    }

    /**
     * Checks that an incorrectly dispatched callback fails before changing session state.
     */
    @Test
    void submit_offFxThread_assertionFailsBeforeMutation() {
        assertTrue(ChatWindow.class.desiredAssertionStatus(), "Run tests with Java assertions enabled");
        assertFalse(Platform.isFxApplicationThread());
        AssertionError error = assertThrows(AssertionError.class, () -> {
            send.getOnAction().handle(new ActionEvent());
        });
        assertEquals("Command submission must run on the JavaFX thread", error.getMessage());
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Starts one JavaFX toolkit for the test class with a bounded startup wait.
     */
    @BeforeAll
    static void startToolkit() throws Exception {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        assertTrue(ready.await(15, TimeUnit.SECONDS), "JavaFX startup timed out");
    }

    /**
     * Stops the toolkit after all windows have been closed.
     */
    @AfterAll
    static void stopToolkit() {
        Platform.exit();
    }

    /**
     * Opens a fresh window and resolves its user-facing controls.
     */
    @BeforeEach
    void openWindow() throws Exception {
        onFxThread(() -> {
            chat = new ChatWindow(directory.resolve("tasks.txt").toString());
            stage = new Stage();
            stage.setScene(new Scene(chat, 760, 780));
            stage.show();
            chat.applyCss();
            chat.layout();
            input = (TextField) chat.lookup("#command-input");
            send = (Button) chat.lookup("#send-button");
            chat.focusInput();
            return null;
        });
    }

    /**
     * Closes each test window even if an assertion fails.
     */
    @AfterEach
    void closeWindow() throws Exception {
        onFxThread(() -> {
            if (stage != null) {
                stage.close();
            }
            return null;
        });
    }

    /**
     * Verifies the Send and text-field action paths, replies, persistence, and reload.
     */
    @Test
    void submit_sendAndEnter_routesCommandsAndReloadsSavedTasks() throws Exception {
        onFxThread(() -> {
            input.setText("todo read book");
            send.fire();
            assertTrue(transcript().contains("[T][ ] read book"));
            assertTrue(input.getText().isEmpty());
            enter("mark 1");
            enter("list");
            assertTrue(transcript().contains("1.[T][X] read book"));
            return null;
        });
        assertEquals("T | 1 | read book\n", Files.readString(directory.resolve("tasks.txt")).replace("\r\n", "\n"));
        onFxThread(() -> {
            stage.close();
            return null;
        });
        openWindow();
        onFxThread(() -> {
            enter("list");
            assertTrue(transcript().contains("1.[T][X] read book"));
            return null;
        });
    }

    /**
     * Checks that chronological sorting is available through the GUI and persists its order.
     */
    @Test
    void submit_sort_displaysNewNumbersAndSavesOrder() throws Exception {
        onFxThread(() -> {
            enter("todo read");
            enter("deadline return /by 2/12/2026 1800");
            enter("sort");
            assertTrue(transcript().contains("Tasks lined up chronologically"));
            assertTrue(transcript().contains("1.[D][ ] return (by: 2 Dec 2026 18:00)"));
            assertTrue(transcript().contains("2.[T][ ] read"));
            return null;
        });
        assertEquals("D | 0 | return | 2026-12-02T18:00\nT | 0 | read\n",
                Files.readString(directory.resolve("tasks.txt")).replace("\r\n", "\n"));
    }

    /**
     * Keeps blank and invalid input errors visible without preventing later commands.
     */
    @Test
    void submit_invalidInput_displaysErrorsAndRecovers() throws Exception {
        onFxThread(() -> {
            enter("");
            assertTrue(transcript().contains("Error: please enter a command or task."));
            enter("blah");
            assertTrue(transcript().contains("Error: I can't get my paws around that command"));
            enter("todo read");
            assertTrue(transcript().contains("[T][ ] read"));
            assertFalse(input.isDisabled());
            return null;
        });
    }

    /**
     * Confirms suggestions only fill the field and cannot overwrite an existing draft.
     */
    @Test
    void suggestions_fillCommand_preserveDraftAndShowGuide() throws Exception {
        onFxThread(() -> {
            Button suggestion = chat.lookupAll(".suggestion").stream().map(Button.class::cast)
                    .filter(button -> button.getText().equals("Add a to-do")).findFirst().orElseThrow();
            suggestion.fire();
            assertEquals("todo ", input.getText());
            assertTrue(suggestion.isDisabled());
            assertFalse(Files.exists(directory.resolve("tasks.txt")));
            TitledPane guide = (TitledPane) chat.lookup("#command-guide");
            guide.setExpanded(true);
            assertTrue(((Label) guide.getContent()).getText().contains("/from"));
            assertTrue(((Label) guide.getContent()).getText().contains("HHmm"));
            return null;
        });
    }

    /**
     * Ends the command session on bye while leaving its farewell readable until Close.
     */
    @Test
    void submit_bye_endsSessionAndOffersClose() throws Exception {
        onFxThread(() -> {
            enter("bye");
            assertTrue(transcript().contains("Time for a catnap. See you soon!"));
            assertTrue(input.isDisabled());
            assertEquals("Close", send.getText());
            send.fire();
            assertFalse(stage.isShowing());
            return null;
        });
    }

    /**
     * Shows a loading error in a bubble without damaging the corrupt data file.
     */
    @Test
    void startup_corruptData_showsErrorInTranscript() throws Exception {
        Files.writeString(directory.resolve("tasks.txt"), "broken\n");
        onFxThread(() -> {
            stage.close();
            return null;
        });
        openWindow();
        assertTrue(onFxThread(() -> transcript().contains("invalid record on line 1.")));
        onFxThread(() -> {
            enter("todo must not overwrite");
            assertTrue(transcript().contains("saving is disabled"));
            return null;
        });
        assertEquals("broken\n", Files.readString(directory.resolve("tasks.txt")));
    }

    /**
     * Verifies long replies wrap and repeated messages remain scrollable at narrow widths.
     */
    @Test
    void layout_longConversation_wrapsAndScrollsAtNarrowWidth() throws Exception {
        onFxThread(() -> {
            stage.setWidth(480);
            stage.setHeight(600);
            enter("todo " + "read a very interesting book ".repeat(20));
            for (int i = 0; i < 12; i++) {
                enter("list");
            }
            chat.applyCss();
            chat.layout();
            return null;
        });
        onFxThread(() -> {
            ScrollPane scroll = (ScrollPane) chat.lookup("#conversation");
            assertEquals(1.0, scroll.getVvalue());
            assertTrue(scroll.getContent().getBoundsInLocal().getHeight() > scroll.getViewportBounds().getHeight());
            for (var node : chat.lookupAll(".message-text")) {
                Label label = (Label) node;
                assertTrue(label.isWrapText());
                assertTrue(label.getWidth() < scroll.getViewportBounds().getWidth());
            }
            return null;
        });
    }

    /**
     * Saves a rendered JavaFX scene for visual inspection without capturing the desktop.
     */
    @Test
    void layout_sampleConversation_rendersPreview() throws Exception {
        onFxThread(() -> {
            enter("todo read book");
            enter("mark 1");
            chat.applyCss();
            chat.layout();
            return null;
        });
        onFxThread(() -> {
            WritableImage snapshot = chat.snapshot(null, null);
            int width = (int) snapshot.getWidth();
            int height = (int) snapshot.getHeight();
            int[] pixels = new int[width * height];
            snapshot.getPixelReader().getPixels(0, 0, width, height,
                    PixelFormat.getIntArgbInstance(), pixels, 0, width);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, width, height, pixels, 0, width);
            Path path = Path.of(System.getProperty("mary.guiSnapshot"));
            Files.createDirectories(path.getParent());
            assertTrue(ImageIO.write(image, "png", path.toFile()));
            return null;
        });
    }

    /**
     * Fires the same action JavaFX emits when Enter is pressed in the input field.
     */
    private void enter(String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    /**
     * Reads the transcript labels without requiring implementation-specific fields.
     */
    private String transcript() {
        VBox messages = (VBox) chat.lookup("#messages");
        return messages.lookupAll(".message-text").stream().map(Label.class::cast)
                .map(Label::getText).reduce("", (first, second) -> first + "\n" + second);
    }

    /**
     * Propagates FX-thread assertion failures to JUnit with a bounded wait.
     */
    private static <T> T onFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }
}
