package slotbot.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slotbot.SlotBot;

@Tag("gui")
public class MainWindowTest {
    @Test
    public void handleUserInput_overflowingReplies_scrollsToLastLine(@TempDir Path directory) throws Exception {
        Parent root = createScrollingView(directory);

        for (int i = 0; i < 4; i++) {
            submitLongTask(root, i);
            resizeView(root, i > 1 ? 400 : 540);
            assertLatestReplyIsVisible(root);
        }

        resizeView(root, 360);
        assertManualScrollPositionIsPreserved(root);
    }

    /**
     * Creates a laid-out view for testing conversation scrolling.
     */
    private static Parent createScrollingView(Path directory) throws Exception {
        return runOnFxThread(() -> {
            SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));
            Parent view = loadView(bot, () -> { });
            Scene scene = new Scene(view, 540, 420);
            addStylesheet(scene);
            view.resize(540, 420);
            view.applyCss();
            view.layout();
            return view;
        });
    }

    /**
     * Submits a long task using alternating input methods and window widths.
     */
    private static void submitLongTask(Parent root, int submission) throws Exception {
        runOnFxThread(() -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            scroll.setVvalue(0);

            TextField input = (TextField) root.lookup("#userInput");
            input.setText("todo " + "A long task description that wraps onto multiple lines. ".repeat(8));
            if (submission % 2 == 0) {
                input.fireEvent(new ActionEvent());
            } else {
                Button send = (Button) root.lookup("#sendButton");
                send.fire();
            }
            return null;
        });
    }

    /**
     * Resizes a populated view so existing messages must be laid out again.
     */
    private static void resizeView(Parent root, int width) throws Exception {
        runOnFxThread(() -> {
            root.resize(width, 420);
            root.applyCss();
            root.layout();
            return null;
        });
    }

    /**
     * Verifies that autoscrolling reveals the newest reply without preventing manual scrolling.
     */
    private static void assertLatestReplyIsVisible(Parent root) throws Exception {
        // Run after the queued autoscroll callback and finish the next layout pass.
        runOnFxThread(() -> {
            root.applyCss();
            root.layout();
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            VBox messages = (VBox) root.lookup("#dialogContainer");
            Node latest = messages.getChildren().getLast();
            Node viewport = scroll.lookup(".viewport");
            Bounds replyBounds = latest.localToScene(latest.getBoundsInLocal());
            Bounds visibleBounds = viewport.localToScene(viewport.getBoundsInLocal());

            assertTrue(replyBounds.getMaxY() <= visibleBounds.getMaxY() + 1,
                    "The newest reply must not extend below the viewport");
            assertEquals(scroll.getVmax(), scroll.getVvalue(), 0.001);

            scroll.setVvalue(0);
            assertEquals(0, scroll.getVvalue(), 0.001, "Manual scrolling must remain available");
            return null;
        });
    }

    /**
     * Verifies that resizing does not override a deliberate manual scroll.
     */
    private static void assertManualScrollPositionIsPreserved(Parent root) throws Exception {
        runOnFxThread(() -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertEquals(0, scroll.getVvalue(), 0.001, "Manual scrolling must remain available");
            return null;
        });
    }

    /**
     * Runs an operation on the JavaFX thread without opening a window.
     */
    private static <T> T runOnFxThread(Callable<T> operation) throws Exception {
        FutureTask<T> task = new FutureTask<>(operation);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }

    @BeforeAll
    public static void startToolkit() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        assertTrue(ready.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void handleUserInput_buttonsAndActions_updateConversationAndExit(@TempDir Path directory)
            throws Exception {
        CountDownLatch closed = new CountDownLatch(1);
        FutureTask<Void> interaction = new FutureTask<>(() -> exerciseWindow(directory, closed));

        Platform.runLater(interaction);
        interaction.get(15, TimeUnit.SECONDS);
        assertTrue(closed.await(5, TimeUnit.SECONDS));
    }

    /**
     * Exercises the main input paths and closes the window if an assertion fails.
     */
    private static Void exerciseWindow(Path directory, CountDownLatch closed) throws Exception {
        Stage stage = new Stage();
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));
        try {
            Parent root = loadView(bot, () -> {
                stage.close();
                closed.countDown();
            });
            Scene scene = new Scene(root);
            addStylesheet(scene);
            stage.setScene(scene);
            stage.show();
            root.applyCss();
            root.layout();

            TextField input = (TextField) root.lookup("#userInput");
            Button send = (Button) root.lookup("#sendButton");
            VBox messages = (VBox) root.lookup("#dialogContainer");
            assertBlankInputIsIgnored(input, send, messages);
            assertTodoCanBeAdded(input, scene, messages);
            assertListCanBeDisplayed(input, send, messages);
            assertExitInputIsValidated(input, send, messages, bot);
        } catch (Throwable failure) {
            stage.close();
            throw failure;
        }
        return null;
    }

    private static void assertBlankInputIsIgnored(TextField input, Button send, VBox messages) {
        assertEquals(1, messages.getChildren().size());
        input.setText("   ");
        send.fire();
        assertEquals(1, messages.getChildren().size());
    }

    private static void assertTodoCanBeAdded(TextField input, Scene scene, VBox messages) {
        input.setText("todo read book");
        input.fireEvent(new ActionEvent());
        assertEquals(3, messages.getChildren().size());
        assertEquals("", input.getText());
        assertEquals(input, scene.getFocusOwner());
        HBox userRow = (HBox) messages.getChildren().get(1);
        HBox replyRow = (HBox) messages.getChildren().get(2);
        assertTrue(userRow.getStyleClass().contains("user-row"));
        assertTrue(replyRow.getStyleClass().contains("bot-row"));
        assertEquals(Pos.TOP_RIGHT, userRow.getAlignment());
        assertEquals(Pos.TOP_LEFT, replyRow.getAlignment());

        VBox replyContent = (VBox) replyRow.getChildren().getFirst();
        Label reply = (Label) replyContent.getChildren().get(1);
        assertTrue(reply.getText().contains("[T][ ] read book"));
        assertTrue(reply.getStyleClass().contains("bot-message"));
    }

    private static void assertListCanBeDisplayed(TextField input, Button send, VBox messages) {
        input.setText("list");
        send.fire();
        assertEquals(5, messages.getChildren().size());
    }

    private static void assertExitInputIsValidated(TextField input, Button send, VBox messages, SlotBot bot) {
        input.setText("bye extra");
        send.fire();
        assertFalse(bot.isExiting());
        HBox errorRow = (HBox) messages.getChildren().getLast();
        VBox errorContent = (VBox) errorRow.getChildren().getFirst();
        Label errorMessage = (Label) errorContent.getChildren().get(1);
        assertTrue(errorMessage.getStyleClass().contains("error-message"));

        input.setText(" bye ");
        send.fire();
        assertTrue(bot.isExiting());
        assertTrue(input.isDisabled());
        assertTrue(send.isDisabled());
    }

    /**
     * Loads the main view and connects it to the supplied bot and exit action.
     */
    private static Parent loadView(SlotBot bot, Runnable onExit) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow controller = loader.getController();
        controller.setBot(bot, onExit);
        return root;
    }

    private static void addStylesheet(Scene scene) {
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
    }
}
