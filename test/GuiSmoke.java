package ari.smoke;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import ari.gui.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Runs local JavaFX smoke checks against the packaged classes and resources.
 * Run only from a fresh temporary working directory; snapshots contain test tasks.
 */
public class GuiSmoke {
    public static void main(String[] args) throws Exception {
        if (Files.exists(Path.of("data"))) {
            throw new IllegalStateException("Use a fresh directory without data");
        }
        Path output = Path.of(args[0]);
        Files.createDirectories(output);
        AtomicReference<Stage> window = new AtomicReference<>();
        Platform.startup(() -> {
        });
        try {
            onFx(() -> {
                Stage stage = new Stage();
                window.set(stage);
                new Main().start(stage);
            });
            Thread.sleep(250);
            onFx(() -> checkInitial(window.get(), output));
            Thread.sleep(250);
            onFx(() -> checkMinimum(window.get(), output));
            Thread.sleep(250);
            onFx(() -> checkWideAndExit(window.get(), output));
        } catch (Exception | AssertionError error) {
            Platform.exit();
            throw error;
        }
        System.out.println("PASS: blank input, submission, error cue, focus, layouts, saved data; bye requested");
    }

    private static void checkInitial(Stage stage, Path output) throws IOException {
        Scene scene = stage.getScene();
        TextField input = (TextField) scene.lookup("#userInput");
        Button send = (Button) scene.lookup("#sendButton");
        input.setText("   ");
        require(send.isDisabled(), "Whitespace-only send must be disabled");
        submit(input, "deadline finish revision /by 2026-09-18");
        submit(input, "todo practise sequence diagrams");
        submit(input, "list");
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        snapshot(scene, output.resolve("Ui.png"));
        submit(input, "bye extra");
        require(stage.isShowing(), "Invalid exit must not close the window");
        require(scene.getRoot().lookupAll(".error-reply").stream().anyMatch(node -> node instanceof Label
                && ((Label) node).getText().startsWith("Please check:")), "Error must have a text cue");
        require(scene.getFocusOwner() == input, "Submission must restore input focus");
        stage.setWidth(420);
        stage.setHeight(480);
    }

    private static void checkMinimum(Stage stage, Path output) throws IOException {
        Scene scene = stage.getScene();
        TextField input = (TextField) scene.lookup("#userInput");
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        require(scene.getWidth() < 420 && scene.getHeight() < 480, "Resize has not reached the scene");
        require(input.localToScene(input.getBoundsInLocal()).getMaxY() <= scene.getHeight(), "Input clipped");
        snapshot(scene, output.resolve("minimum-error.png"));
        stage.setWidth(900);
        stage.setHeight(720);
    }

    private static void checkWideAndExit(Stage stage, Path output) throws IOException {
        Scene scene = stage.getScene();
        TextField input = (TextField) scene.lookup("#userInput");
        require(scene.getWidth() > 800, "Wide resize has not reached the scene");
        submit(input, "todo " + "a long description that should wrap naturally ".repeat(5));
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        snapshot(scene, output.resolve("wide-long.png"));
        require(Files.readString(Path.of("data/ari.txt")).contains("finish revision"), "Autosave missing");
        submit(input, "bye");
    }

    /** Runs one phase on the JavaFX thread, allowing real resize pulses between phases. */
    private static void onFx(CheckedAction action) throws Exception {
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable error) {
                failure.set(error);
            } finally {
                finished.countDown();
            }
        });
        if (!finished.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("GUI phase timed out");
        }
        if (failure.get() != null) {
            throw new AssertionError("GUI phase failed", failure.get());
        }
    }

    /** Allows I/O assertions inside an FX-thread test phase. */
    private interface CheckedAction {
        void run() throws Exception;
    }

    private static void submit(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    private static void snapshot(Scene scene, Path path) throws IOException {
        WritableImage image = scene.snapshot(null);
        BufferedImage buffered = new BufferedImage((int) image.getWidth(), (int) image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < buffered.getHeight(); y++) {
            for (int x = 0; x < buffered.getWidth(); x++) {
                buffered.setRGB(x, y, image.getPixelReader().getArgb(x, y));
            }
        }
        ImageIO.write(buffered, "png", path.toFile());
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
