package chai;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import chai.storage.Storage;
import chai.ui.MainWindow;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Generates the representative product screenshot used by the user guide. */
public final class UiScreenshotGenerator {
    /** Screenshot width matching Chai's default window width. */
    private static final int SCREENSHOT_WIDTH = 520;

    /** Screenshot height matching Chai's default window height. */
    private static final int SCREENSHOT_HEIGHT = 700;

    /** Prevents instantiation of this screenshot utility class. */
    private UiScreenshotGenerator() {
    }

    /**
     * Generates {@code docs/Ui.png} from the real FXML and CSS interface.
     *
     * @param args Command-line arguments, which this utility does not use.
     * @throws InterruptedException If interrupted while waiting for JavaFX.
     */
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch completion = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Platform.startup(() -> {
            try {
                generateScreenshot(Path.of("docs", "Ui.png"));
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                completion.countDown();
            }
        });

        completion.await();
        Platform.exit();
        if (failure.get() != null) {
            throw new IllegalStateException("Unable to generate UI screenshot", failure.get());
        }
    }

    /** Loads the production interface, adds representative messages, and captures it. */
    private static void generateScreenshot(Path outputPath) throws IOException, ChaiException {
        Path temporaryDataFile = Files.createTempDirectory("chai-screenshot").resolve("chai.txt");
        Chai chai = new Chai(new Storage(temporaryDataFile));
        FXMLLoader loader = new FXMLLoader(UiScreenshotGenerator.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        MainWindow controller = loader.getController();
        controller.setChai(chai);

        Stage stage = new Stage();
        stage.setTitle("Chai — Calm Task Companion");
        stage.setScene(new Scene(root, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT));
        stage.show();

        TextField userInput = (TextField) root.lookup("#userInput");
        Button sendButton = (Button) root.lookup("#sendButton");
        submitCommand(userInput, sendButton, "todo draft user guide");
        submitCommand(userInput, sendButton, "deadline submit iP /by 2026-09-25");
        submitCommand(userInput, sendButton, "list now");

        root.applyCss();
        root.layout();
        writePng(root.snapshot(null, new WritableImage(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT)), outputPath);
        stage.close();
    }

    /** Submits one command through the production text field and send button. */
    private static void submitCommand(TextField userInput, Button sendButton, String command) {
        userInput.setText(command);
        sendButton.fire();
    }

    /** Writes a JavaFX image as a PNG without requiring the JavaFX Swing module. */
    private static void writePng(WritableImage image, Path outputPath) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixelReader().getPixels(
                0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
        Files.createDirectories(outputPath.getParent());
        ImageIO.write(bufferedImage, "png", outputPath.toFile());
    }
}
