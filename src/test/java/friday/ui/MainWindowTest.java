package friday.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import friday.Friday;

/** Opt-in desktop acceptance checks; ordinary CI does not require a graphical display. */
@EnabledIfEnvironmentVariable(named = "FRIDAY_GUI_TESTS", matches = "true")
class MainWindowTest {
    @TempDir
    Path temporaryDirectory;

    @BeforeAll
    static void startToolkit() throws Exception {
        FutureTask<Void> ready = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(ready);
        ready.get(10, TimeUnit.SECONDS);
    }

    @AfterAll
    static void stopToolkit() {
        Platform.exit();
    }

    @Test
    void conversation_resizeSubmitScrollAndExit_remainsUsable() throws Exception {
        Stage stage = onFx(() -> createWindow(new Friday(temporaryDirectory.resolve("friday.txt"))));
        try {
            awaitWindowResize();
            onFx(() -> {
                Parent root = stage.getScene().getRoot();
                VBox messages = (VBox) root.lookup("#dialogContainer");
                TextField input = (TextField) root.lookup("#userInput");
                input.setText("   ");
                input.fireEvent(new ActionEvent());
                assertEquals(1, messages.getChildren().size());
                return null;
            });
            submit(stage, "todo Review the mission briefing", true);
            submit(stage, "todo", false);
            onFx(() -> {
                Parent root = stage.getScene().getRoot();
                assertTrue(root.lookupAll(".speaker").stream()
                        .anyMatch(node -> ((Label) node).getText().contains("Command error")));
                assertTrue(root.lookup("#userInput").isFocused());
                snapshot(stage, "default");
                return null;
            });
            submit(stage, "list", true);
            submit(stage, "todo " + "LongDescriptionWithoutSpaces".repeat(8), true);
            submit(stage, "help", false);
            for (int[] size : new int[][] {{360, 440}, {800, 760}}) {
                onFx(() -> {
                    stage.setWidth(size[0]);
                    stage.setHeight(size[1]);
                    return null;
                });
                awaitWindowResize();
                onFx(() -> {
                    assertEquals(size[0], stage.getScene().getWidth(), 2);
                    Parent root = stage.getScene().getRoot();
                    root.applyCss();
                    root.layout();
                    ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
                    VBox messages = (VBox) root.lookup("#dialogContainer");
                    assertTrue(messages.getWidth() <= scroll.getViewportBounds().getWidth() + 1);
                    for (Node row : messages.getChildren()) {
                        assertTrue(row.getBoundsInParent().getMaxX() <= messages.getWidth() + 1);
                        for (Node label : ((Parent) row).lookupAll(".label")) {
                            Label text = (Label) label;
                            assertTrue(text.getHeight() + 1 >= text.prefHeight(text.getWidth()));
                        }
                    }
                    Node input = root.lookup("#userInput");
                    assertTrue(input.localToScene(input.getBoundsInLocal()).getMaxY()
                            <= stage.getScene().getHeight());
                    scroll.setVvalue(1);
                    snapshot(stage, size[0] == 360 ? "minimum" : "enlarged");
                    scroll.setVvalue(0);
                    return null;
                });
                onFx(() -> {
                    ScrollPane scroll = (ScrollPane) stage.getScene().getRoot().lookup("#scrollPane");
                    assertEquals(0, scroll.getVvalue(), 0.01);
                    return null;
                });
            }
            submit(stage, "bye", true);
            onFx(() -> {
                Parent root = stage.getScene().getRoot();
                assertTrue(root.lookup("#userInput").isDisabled());
                assertTrue(root.lookup("#sendButton").isDisabled());
                assertEquals(1, ((ScrollPane) root.lookup("#scrollPane")).getVvalue());
                snapshot(stage, "ended");
                return null;
            });
        } finally {
            onFx(() -> { stage.close(); return null; });
        }
    }

    @Test
    void welcome_corruptData_displaysWarningAndAcceptsInput() throws Exception {
        Path file = temporaryDirectory.resolve("friday.txt");
        Files.writeString(file, "corrupt");
        Stage stage = onFx(() -> createWindow(new Friday(file)));
        try {
            submit(stage, "todo Retained in memory", false);
            onFx(() -> {
                Parent root = stage.getScene().getRoot();
                assertEquals(2, root.lookupAll(".warning").size());
                assertFalse(root.lookup("#userInput").isDisabled());
                snapshot(stage, "warning");
                return null;
            });
            assertEquals("corrupt", Files.readString(file));
        } finally {
            onFx(() -> { stage.close(); return null; });
        }
    }

    /** Loads the production layout with isolated storage. */
    private Stage createWindow(Friday friday) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        loader.<MainWindow>getController().setFriday(friday);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("FRIDAY | Mission Control");
        stage.setWidth(480);
        stage.setHeight(640);
        stage.setMinWidth(360);
        stage.setMinHeight(440);
        stage.show();
        stage.requestFocus();
        return stage;
    }

    /** Exercises both the text field action and Send button, then waits for deferred scrolling. */
    private void submit(Stage stage, String command, boolean isEnter) throws Exception {
        onFx(() -> {
            Parent root = stage.getScene().getRoot();
            TextField input = (TextField) root.lookup("#userInput");
            input.setText(command);
            if (isEnter) {
                input.fireEvent(new ActionEvent());
            } else {
                ((Button) root.lookup("#sendButton")).fire();
            }
            assertEquals("", input.getText());
            return null;
        });
        onFx(() -> null);
    }

    /** Saves actual JavaFX rendering for human visual inspection. */
    private void snapshot(Stage stage, String name) throws Exception {
        WritableImage image = stage.getScene().snapshot(null);
        BufferedImage pixels = new BufferedImage((int) image.getWidth(), (int) image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < pixels.getHeight(); y++) {
            for (int x = 0; x < pixels.getWidth(); x++) {
                pixels.setRGB(x, y, image.getPixelReader().getArgb(x, y));
            }
        }
        Path folder = Path.of("build", "gui-review");
        Files.createDirectories(folder);
        ImageIO.write(pixels, "png", folder.resolve(name + ".png").toFile());
    }

    /** Allows the native window manager to deliver its asynchronous resize event. */
    private void awaitWindowResize() throws Exception {
        FutureTask<Void> resized = new FutureTask<>(() -> null);
        onFx(() -> {
            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(event -> resized.run());
            pause.play();
            return null;
        });
        resized.get(5, TimeUnit.SECONDS);
    }

    /** Runs scene-graph work on the JavaFX thread and propagates assertion failures. */
    private static <T> T onFx(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }
}
