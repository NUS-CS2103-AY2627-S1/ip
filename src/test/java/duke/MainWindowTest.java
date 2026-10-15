package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Exercises the real FXML, CSS, and controls on the JavaFX thread with isolated storage.
 * Run with guiTest rather than the headless unit-test task.
 */
@Tag("gui")
public class MainWindowTest {
    @TempDir
    public Path tempDir;

    private Stage stage;
    private BorderPane root;
    private VBox messages;
    private TextField input;
    private Button send;

    @BeforeAll
    public static void startJavaFx() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void stopJavaFx() {
        Platform.exit();
    }

    @BeforeEach
    public void openWindow() throws Exception {
        runOnFxThread(() -> loadWindow(new Duke(tempDir.resolve("tasks.txt").toString())));
    }

    @AfterEach
    public void closeWindow() throws Exception {
        runOnFxThread(() -> stage.close());
    }

    @Test
    public void sendAndEnter_renderDistinctMessagesAndClearSuccessfulInput() throws Exception {
        runOnFxThread(() -> {
            submit("todo read book");
            assertTrue(card(1).getStyleClass().contains("user-message"));
            assertTrue(card(2).getStyleClass().contains("bot-message"));
            assertEquals("", input.getText());
            input.setText("list");
            input.fireEvent(new ActionEvent());
            assertTrue(body(4).getText().contains("1.[T][ ] read book"));
            assertFalse(body(4).getText().contains("____________________________________________________________"));
            root.applyCss();
            root.layout();
        });
        // The height listener schedules scrolling after the newly added cards have been laid out.
        runOnFxThread(() -> {
            assertEquals(1.0, ((ScrollPane) root.lookup("#dialogScroll")).getVvalue());
            saveScreenshot("conversation.png");
        });
    }

    @Test
    public void invalidCommand_isLabelledAndKeptForCorrection() throws Exception {
        runOnFxThread(() -> {
            submit("todo");
            assertTrue(card(2).getStyleClass().contains("error-message"));
            assertTrue(((Label) card(2).getChildren().get(0)).getText().contains("NEEDS A FIX"));
            assertEquals("todo", input.getText());
            assertFalse(input.isDisabled());
            saveScreenshot("error.png");
            submit("todo read book");
            assertFalse(card(4).getStyleClass().contains("error-message"));
            assertEquals("", input.getText());
        });
    }

    @Test
    public void blankCommand_showsOnlyAnErrorCard() throws Exception {
        runOnFxThread(() -> {
            submit("   ");
            assertEquals(2, messages.getChildren().size());
            assertTrue(card(1).getStyleClass().contains("error-message"));
            assertTrue(body(1).getText().contains("Please type a command"));
        });
    }

    @Test
    public void paddedBye_disablesComposerAndKeepsGoodbyeVisible() throws Exception {
        runOnFxThread(() -> {
            submit("  bye  ");
            assertTrue(input.isDisabled());
            assertTrue(send.isDisabled());
            assertTrue(body(2).getText().contains("Bye."));
            assertFalse(card(2).getStyleClass().contains("error-message"));
        });
    }

    @Test
    public void startupWarning_isStyledSeparatelyFromGreeting() throws Exception {
        Path dataFile = tempDir.resolve("corrupt.txt");
        Files.writeString(dataFile, "broken task\n");
        runOnFxThread(() -> {
            stage.close();
            loadWindow(new Duke(dataFile.toString()));
            assertFalse(card(0).getStyleClass().contains("error-message"));
            assertTrue(body(0).getText().contains("Hello! I'm geen."));
            assertTrue(card(1).getStyleClass().contains("error-message"));
        });
    }

    @Test
    public void resize_wrapsLongRepliesWithoutHorizontalOverflow() throws Exception {
        runOnFxThread(() -> {
            submit("todo " + "Prepare notes for the next meeting. ".repeat(12) + "x".repeat(100));
            stage.setWidth(840);
        });
        waitForLayout();
        double[] wideHeight = new double[1];
        runOnFxThread(() -> {
            root.applyCss();
            root.layout();
            assertTrue(root.getWidth() >= 800, "The wide-window test must actually resize the scene");
            wideHeight[0] = body(2).getHeight();
            saveScreenshot("wide.png");
            stage.setWidth(420);
        });
        waitForLayout();
        runOnFxThread(() -> {
            root.applyCss();
            root.layout();
            assertTrue(root.getWidth() <= 420, "The narrow-window test must actually resize the scene");
            assertTrue(body(2).getHeight() > wideHeight[0], "Narrow replies should wrap into more lines");
            for (int i = 0; i < messages.getChildren().size(); i++) {
                HBox row = (HBox) messages.getChildren().get(i);
                assertTrue(card(i).getWidth() <= row.getWidth() + 1, "Cards must fit the viewport");
                assertTrue(body(i).getWidth() <= card(i).getWidth(), "Text must stay inside its card");
            }
            assertEquals(ScrollPane.ScrollBarPolicy.NEVER,
                    ((ScrollPane) root.lookup("#dialogScroll")).getHbarPolicy());
        });
        runOnFxThread(() -> {
            assertEquals(1.0, ((ScrollPane) root.lookup("#dialogScroll")).getVvalue());
            saveScreenshot("narrow.png");
        });
    }

    /**
     * Loads the production layout, injecting a chatbot with a temporary save file.
     */
    private void loadWindow(Duke duke) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        loader.setControllerFactory(controllerClass -> new MainWindow(duke));
        root = loader.load();
        stage = new Stage();
        stage.setMinWidth(420);
        stage.setMinHeight(360);
        stage.setScene(new Scene(root));
        stage.setTitle("geen · GUI tests");
        stage.show();
        loader.<MainWindow>getController().focusInput();
        messages = (VBox) root.lookup("#dialogContainer");
        input = (TextField) root.lookup("#inputField");
        send = (Button) root.lookup("#sendButton");
    }

    private void submit(String command) {
        input.setText(command);
        send.fire();
    }

    private VBox card(int index) {
        return (VBox) ((HBox) messages.getChildren().get(index)).getChildren().get(0);
    }

    private Label body(int index) {
        return (Label) card(index).getChildren().get(1);
    }

    /**
     * Saves pixels from the rendered JavaFX layout for visual QA without extra JavaFX modules.
     */
    private void saveScreenshot(String name) throws Exception {
        root.applyCss();
        root.layout();
        WritableImage snapshot = root.snapshot(null, null);
        BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        Path output = Path.of("build/reports/gui-tests", name);
        Files.createDirectories(output.getParent());
        ImageIO.write(image, "png", output.toFile());
    }

    /**
     * Propagates JavaFX assertions and exceptions back to JUnit, with a bounded wait.
     */
    private static void runOnFxThread(CheckedAction action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    /**
     * Waits for a real layout pulse because native window resizing is asynchronous.
     */
    private void waitForLayout() throws Exception {
        FutureTask<Void> completed = new FutureTask<>(() -> null);
        Platform.runLater(() -> {
            Scene scene = stage.getScene();
            Runnable[] listener = new Runnable[1];
            listener[0] = () -> {
                scene.removePostLayoutPulseListener(listener[0]);
                completed.run();
            };
            scene.addPostLayoutPulseListener(listener[0]);
            Platform.requestNextPulse();
        });
        completed.get(10, TimeUnit.SECONDS);
    }

    /**
     * Allows FXML loading and screenshot writes to report checked exceptions in GUI test actions.
     */
    @FunctionalInterface
    private interface CheckedAction {
        void run() throws Exception;
    }
}
