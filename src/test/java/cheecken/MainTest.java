package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Exercises the real JavaFX scene on a desktop with isolated persistence.
 */
@EnabledIfSystemProperty(named = "cheecken.guiTest", matches = "true")
class MainTest {
    @TempDir
    private Path directory;

    /**
     * Verifies chat window with commands and resize: remains usable and persists tasks.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void chatWindow_commandsAndResize_remainsUsableAndPersistsTasks() throws Exception {
        Platform.startup(() -> Platform.setImplicitExit(false));
        FutureTask<Void> check = new FutureTask<>(() -> {
            Stage stage = new Stage();
            try {
                Path saved = directory.resolve("tasks.txt");
                Clock clock = Clock.fixed(Instant.parse("2026-09-07T15:30:00Z"), ZoneOffset.UTC);
                new Main(new Cheecken(saved.toString(), clock)).start(stage);
                Scene scene = stage.getScene();
                TextField input = (TextField) scene.lookup("#commandInput");
                Button send = (Button) scene.lookup("#sendButton");
                assertTrue(send.isDisabled());
                assertTrue(text(scene.getRoot()).contains("Hello! What can I help you remember?"));
                int before = text(scene.getRoot()).length();
                enter(input, "   ");
                assertEquals(before, text(scene.getRoot()).length());
                enter(input, "todo GUI test task");
                assertTrue(text(scene.getRoot()).contains("[T][ ] GUI test task"));
                assertEquals("", input.getText());
                input.setText("mark 1");
                send.fire();
                assertTrue(text(scene.getRoot()).contains("[T][X] GUI test task"));
                enter(input, "deadline submit report /by 15/10/2026 1800");
                enter(input, "event meeting /from 15/10/2026 0900 /to 15/10/2026 1000");
                assertTrue(text(scene.getRoot()).contains("Oct 15 2026 6:00 PM"));
                assertTrue(text(scene.getRoot()).contains("Oct 15 2026 9:00 AM"));
                enter(input, "deadline natural /by tomorrow");
                enter(input, "event weekday /from Mon 0900 /to MONDAY 1000");
                assertTrue(text(scene.getRoot()).contains("[D][ ] natural (by: Sep 08 2026)"));
                assertTrue(text(scene.getRoot()).contains("from: Sep 14 2026 9:00 AM to: Sep 14 2026 10:00 AM"));
                enter(input, "deadline invalid /by next week");
                assertTrue(text(scene.getRoot()).contains("No time how I set the task..."));
                enter(input, "wat");
                assertTrue(text(scene.getRoot()).contains("What do you want?"));
                scene.getRoot().applyCss();
                assertEquals(2, scene.getRoot().lookupAll(".error-message").size());
                for (Node node : scene.getRoot().lookupAll(".error-message")) {
                    Label error = (Label) node;
                    assertEquals(Color.web("#b42318"), error.getTextFill());
                    assertTrue(error.getFont().getStyle().contains("Bold"));
                    assertTrue(error.getFont().getStyle().contains("Italic"));
                }
                enter(input, "find GUI");
                assertTrue(text(scene.getRoot()).contains("Here are the matching tasks"));
                assertEquals(2, scene.getRoot().lookupAll(".error-message").size());
                TitledPane guide = (TitledPane) scene.lookup(".titled-pane");
                guide.setExpanded(true);
                assertTrue(text(scene.getRoot()).contains("Dates: d/M/yyyy"));
                assertTrue(text(scene.getRoot()).contains("today, tomorrow, now"));
                assertTrue(text(scene.getRoot()).contains("strictly next"));
                guide.setExpanded(false);
                enter(input, "todo " + "A longer task description ".repeat(8));
                for (int i = 0; i < 8; i++) {
                    enter(input, "list");
                }
                stage.setWidth(440);
                stage.setHeight(560);
                scene.getRoot().applyCss();
                scene.getRoot().layout();
                ScrollPane scroll = (ScrollPane) scene.lookup(".scroll-pane");
                assertEquals(1.0, scroll.getVvalue());
                assertTrue(input.getWidth() > 0);
                assertFalse(input.isDisabled());
                saveSnapshot(scene, directory.resolve("chat.png"));
                assertTrue(stage.isShowing());
                enter(input, "bye");
                assertTrue(text(scene.getRoot()).contains("Bye. Hope to see you again soon!"));
                assertFalse(stage.isShowing());
                assertTrue(Files.readString(saved).contains("T | 1 | GUI test task"));
                new Main(new Cheecken(saved.toString(), Clock.offset(clock, java.time.Duration.ofDays(1))))
                        .start(stage);
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                enter((TextField) stage.getScene().lookup("#commandInput"), "list");
                assertTrue(text(stage.getScene().getRoot()).contains("1.[T][X] GUI test task"));
                assertTrue(text(stage.getScene().getRoot()).contains("[D][ ] natural (by: Sep 08 2026)"));
                stage.setWidth(620);
                stage.setHeight(720);
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                saveSnapshot(stage.getScene(), Path.of(System.getProperty("java.io.tmpdir"), "cheecken-gui.png"));
                assertTrue(stage.isShowing());
                TextField reopenedInput = (TextField) stage.getScene().lookup("#commandInput");
                Button reopenedSend = (Button) stage.getScene().lookup("#sendButton");
                reopenedInput.setText("bye");
                reopenedSend.fire();
                assertFalse(stage.isShowing());
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(check);
        try {
            check.get(30, TimeUnit.SECONDS);
        } finally {
            Platform.exit();
        }
    }

    /**
     * Submits through the same action handler used by the Enter key.
     * @param input input supplied by the test case
     * @param command command name used in error guidance
     */
    private static void enter(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    /**
     * Collects visible label content for assertions without depending on screen coordinates.
     * @param node root node whose label text is collected
     * @return combined text of labels beneath the supplied node
     */
    private static String text(Node node) {
        StringBuilder result = new StringBuilder();
        if (node instanceof Label label) {
            result.append(label.getText());
        }
        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(child -> result.append(text(child)));
        }
        return result.toString();
    }

    /**
     * Saves the rendered scene for a visual review of wrapping and layout.
     * @param scene scene to capture
     * @param path destination for the saved image
     * @throws Exception if test setup, execution, or file access fails
     */
    private static void saveSnapshot(Scene scene, Path path) throws Exception {
        WritableImage snapshot = scene.snapshot(null);
        int width = (int) snapshot.getWidth();
        int height = (int) snapshot.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        ImageIO.write(image, "png", path.toFile());
    }
}
