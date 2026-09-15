package larp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Generates the representative GUI screenshot used by the product website.
 */
public class UiScreenshotGenerator extends Application {
    /**
     * Builds a representative Larp window and captures its complete scene.
     *
     * @param stage JavaFX stage used to lay out the interface.
     * @throws IOException If the FXML, stylesheet, or output file cannot be accessed.
     */
    @Override
    public void start(Stage stage) throws IOException {
        if (getParameters().getRaw().size() != 1) {
            throw new IllegalArgumentException("Expected the output PNG path");
        }
        String outputPath = getParameters().getRaw().get(0);
        URL viewUrl = Objects.requireNonNull(
                getClass().getResource("/view/MainWindow.fxml"),
                "MainWindow.fxml is missing");
        FXMLLoader loader = new FXMLLoader(viewUrl);
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 680, 720);
        URL stylesheetUrl = Objects.requireNonNull(
                getClass().getResource("/css/main.css"),
                "main.css is missing");
        scene.getStylesheets().add(stylesheetUrl.toExternalForm());

        VBox dialogContainer = (VBox) loader.getNamespace().get("dialogContainer");
        dialogContainer.getChildren().addAll(
                DialogBox.getLarpDialog("Hello, I'm Larp.\n"
                        + "Ready when you are. Add a task, check your list, or find something."),
                DialogBox.getUserDialog("deadline submit iP /by 2026-09-18"),
                DialogBox.getLarpDialog("Captured this task:\n"
                        + "  [D][ ] submit iP (by: Sep 18 2026)\n"
                        + "Now you have 3 tasks in the list."),
                DialogBox.getUserDialog("mark later"),
                DialogBox.getErrorDialog(
                        "Let's fix that: The task number must be a whole number."));

        stage.setTitle("Larp");
        stage.setScene(scene);
        stage.show();
        root.applyCss();
        root.layout();

        WritableImage image = scene.snapshot(null);
        writePng(image, new File(outputPath));
        stage.close();
        Platform.exit();
    }

    /**
     * Converts a JavaFX image into a PNG using only JDK image APIs.
     *
     * @param image JavaFX image to convert.
     * @param destination Output file.
     * @throws IOException If the PNG cannot be written.
     */
    private void writePng(WritableImage image, File destination) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(
                (int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        PixelReader pixels = image.getPixelReader();
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                bufferedImage.setRGB(x, y, pixels.getArgb(x, y));
            }
        }
        ImageIO.write(bufferedImage, "png", destination);
    }
}
