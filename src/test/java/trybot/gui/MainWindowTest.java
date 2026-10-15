package trybot.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import trybot.TryBot;

/** Exercises the real FXML controls and checks wrapping at compact and wide window sizes. */
public class MainWindowTest {
    @TempDir
    public Path directory;

    private BorderPane root;

    @BeforeAll
    public static void startJavaFx() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        assertTrue(started.await(15, TimeUnit.SECONDS));
        Platform.setImplicitExit(false);
    }

    @Test
    public void conversation_editSuggestResizeAndExit_preservesUsableControls() throws Exception {
        CountDownLatch closed = new CountDownLatch(1);
        AtomicLong goodbyeStarted = new AtomicLong();
        runOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            loader.setControllerFactory(type -> new MainWindow(closed::countDown));
            root = loader.load();
            MainWindow controller = loader.getController();
            controller.setTryBot(new TryBot(directory.resolve("tasks.txt").toString()));
            new Scene(root, 520, 700);
            root.applyCss();
            root.layout();
            assertNull(root.getTop(), "The conversation should use the space previously occupied by the logo.");
            VBox composer = (VBox) root.getBottom();
            assertTrue(composer.getBackground().getFills().stream()
                    .allMatch(fill -> fill.getFill().equals(Color.TRANSPARENT)),
                    "The city background should show through the bottom controls.");
            TextField input = (TextField) root.lookup("#userInput");
            Button send = (Button) root.lookup("#sendButton");
            VBox messages = (VBox) root.lookup("#dialogContainer");
            assertTrue(send.isDisabled());

            input.setText("todo");
            send.fire();
            assertEquals("todo", input.getText());
            assertEquals("todo", input.getSelectedText());
            assertEquals(3, messages.getChildren().size());
            assertFalse(messages.getChildren().getLast().lookupAll(".error-message").isEmpty());
            assertEquals(1, root.getBackground().getImages().size());
            assertTrue(root.getBackground().getImages().getFirst().getImage().getUrl()
                    .endsWith("/images/background.png"));
            ImageView botPortrait = (ImageView) messages.getChildren().getFirst().lookup("#displayPicture");
            ImageView userPortrait = (ImageView) messages.getChildren().get(1).lookup("#displayPicture");
            assertTrue(botPortrait.getImage().getUrl().endsWith("/images/trybot.png"));
            assertTrue(userPortrait.getImage().getUrl().endsWith("/images/human.png"));
            assertFalse(botPortrait.getImage().isError());
            assertFalse(userPortrait.getImage().isError());

            for (Node node : root.lookupAll(".suggestion")) {
                Button suggestion = (Button) node;
                if (suggestion.getText().equals("Commands")) {
                    suggestion.fire();
                }
            }
            assertEquals("todo", input.getText(), "Help must preserve the draft.");

            input.setText("todo prepare questions for the project meeting and review the reading notes");
            input.fireEvent(new ActionEvent());
            assertTrue(input.getText().isEmpty());
            input.setText("mark 99");
            send.fire();
        });

        // Let the controller's queued layout and scroll callbacks run before inspecting the view.
        runOnFxThread(() -> {
            TextField input = (TextField) root.lookup("#userInput");
            Button send = (Button) root.lookup("#sendButton");
            VBox messages = (VBox) root.lookup("#dialogContainer");
            for (int width : new int[]{520, 360, 900}) {
                root.resize(width, width == 360 ? 420 : 700);
                root.applyCss();
                root.layout();
                ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
                assertEquals(0.0, scroll.getLayoutY(), "The conversation should start at the top of the window.");
                messages.layout();
                scroll.layout();
                scroll.setVvalue(1.0);
                root.layout();
                assertTrue(input.getWidth() > 100);
                assertTrue(messages.getWidth() <= scroll.getViewportBounds().getWidth() + 1);
                for (Node portrait : messages.lookupAll("#displayPicture")) {
                    assertTrue(portrait.getBoundsInParent().getMinX() >= 0);
                    assertTrue(portrait.getBoundsInParent().getMaxX() <= messages.getWidth());
                }
                for (Node node : messages.lookupAll(".message-text")) {
                    Label text = (Label) node;
                    assertTrue(text.getWidth() < width);
                    assertTrue(text.getHeight() + 1 >= text.prefHeight(text.getWidth()),
                            "Wrapped message must have enough height to show all its text.");
                }
                Node latest = messages.getChildren().getLast();
                assertTrue(latest.localToScene(latest.getBoundsInLocal()).getMaxY()
                        <= scroll.localToScene(scroll.getBoundsInLocal()).getMaxY(),
                        "The newest reply must be visible above the composer.");
                saveSnapshot(root, width);
                scroll.setVvalue(0.0);
                assertEquals(0.0, scroll.getVvalue(), "Conversation must remain freely scrollable.");
            }

            input.setText("bye");
            goodbyeStarted.set(System.nanoTime());
            send.fire();
            assertTrue(input.isDisabled());
            assertTrue(send.isDisabled());
            assertTrue(root.lookup("#suggestions").isDisabled());
            Label farewell = (Label) messages.getChildren().getLast().lookup("#dialog");
            assertEquals("You did great today! Thanks for sharing your to-do list with me. See you soon!",
                    farewell.getText());
            assertEquals("Closing in 5 seconds. See you soon!", ((Label) root.lookup("#inputHint")).getText());
        });
        assertFalse(closed.await(2, TimeUnit.SECONDS), "The farewell must remain visible before closing.");
        runOnFxThread(() -> assertTrue(root.isVisible(), "JavaFX must stay responsive during the delay."));
        assertTrue(closed.await(6, TimeUnit.SECONDS), "The application should close automatically.");
        assertTrue(System.nanoTime() - goodbyeStarted.get() >= TimeUnit.MILLISECONDS.toNanos(4900),
                "The farewell should be displayed for five seconds.");
    }

    private static void saveSnapshot(BorderPane root, int width) throws Exception {
        WritableImage snapshot = root.snapshot(null, null);
        BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        Path destination = Path.of("build", "reports", "gui", "trybot-" + width + ".png");
        Files.createDirectories(destination.getParent());
        ImageIO.write(image, "png", destination.toFile());
    }

    private static void runOnFxThread(FxAction action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        task.get(30, TimeUnit.SECONDS);
    }

    /** Allows assertions and FXML loading to run on the JavaFX application thread. */
    private interface FxAction {
        void run() throws Exception;
    }
}
