package jiji.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;

/**
 * Renders and captures a representative, high-resolution snapshot of the Jiji GUI window
 * to {@code docs/Ui.png} for the course product website.
 */
public class UiSnapshotGenerator {

    private static final int WINDOW_WIDTH = 450;
    private static final int WINDOW_HEIGHT = 670;
    private static final int TITLE_BAR_HEIGHT = 34;
    private static final double RENDER_SCALE = 3.0;

    private static boolean isJavaFxInitialized = false;

    /**
     * Initializes the JavaFX toolkit if not already initialized.
     */
    private static synchronized void initJavaFx() {
        if (!isJavaFxInitialized) {
            try {
                Platform.startup(() -> { });
                isJavaFxInitialized = true;
            } catch (IllegalStateException e) {
                // Platform was already started
                isJavaFxInitialized = true;
            }
        }
    }

    /**
     * Generates a representative UI snapshot of the Jiji application window.
     *
     * @throws Exception If an error occurs during snapshot generation.
     */
    @Test
    public void generateUiScreenshot() throws Exception {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(),
                "Skipping GUI snapshot in headless environment");

        initJavaFx();
        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] failure = new Throwable[1];

        Platform.runLater(() -> {
            try {
                captureWindowSnapshot();
            } catch (Throwable t) {
                failure[0] = t;
            } finally {
                latch.countDown();
            }
        });

        boolean completed = latch.await(15, TimeUnit.SECONDS);
        assertTrue(completed, "Timed out generating GUI snapshot");
        if (failure[0] != null) {
            throw new RuntimeException("Snapshot generation failed", failure[0]);
        }

        File uiImage = new File("docs/Ui.png");
        assertTrue(uiImage.exists(), "docs/Ui.png should have been created");
        assertTrue(uiImage.length() > 1000, "docs/Ui.png should not be empty");
    }

    /**
     * Constructs the full window graph with macOS-style title bar and chat conversation,
     * then snapshots and saves the output to {@code docs/Ui.png}.
     *
     * @throws Exception If loading or image writing fails.
     */
    private void captureWindowSnapshot() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
        AnchorPane mainContent = fxmlLoader.load();

        // Retrieve dialogContainer, scrollPane, and userInput from mainContent
        ScrollPane scrollPane = (ScrollPane) mainContent.lookup("#scrollPane");
        VBox dialogContainer = (VBox) scrollPane.getContent();
        TextField userInput = (TextField) mainContent.lookup("#userInput");

        Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
        Image jijiImage = new Image(getClass().getResourceAsStream("/images/DaJiji.png"));

        // Populate realistic sample conversation showcasing personality and features
        dialogContainer.getChildren().addAll(
                DialogBox.getJijiDialog("Purr... Welcome back! Jiji saved a warm spot for you ₍^ ᵕ ᵕ ^₎ฅ\n"
                        + "What shall we gently tackle together today?", jijiImage),
                DialogBox.getUserDialog("todo read chapter 4 of CS2103T textbook", userImage),
                DialogBox.getJijiDialog("Tucked away safely! I've nestled this task into your list:\n"
                        + "  [T][ ] read chapter 4 of CS2103T textbook\n"
                        + "That makes 1 tasks in our cozy bundle. ₍^. .^₎", jijiImage),
                DialogBox.getUserDialog("deadline submit project proposal /by tomorrow 2359", userImage),
                DialogBox.getJijiDialog("Tucked away safely! I've nestled this task into your list:\n"
                        + "  [D][ ] submit project proposal (by: tomorrow 2359)\n"
                        + "That makes 2 tasks in our cozy bundle. ₍^. .^₎", jijiImage),
                DialogBox.getUserDialog("mark 1", userImage),
                DialogBox.getJijiDialog("Paws up! Marked this task as done:\n"
                        + "  [T][X] read chapter 4 of CS2103T textbook\n"
                        + "Wonderful job! Time for a gentle stretch. ₍^ ᵕ ᵕ ^₎ฅ", jijiImage),
                DialogBox.getUserDialog("deadline return book", userImage),
                DialogBox.getJijiDialog("OOPS! ^๑_๑^ ੭ A deadline task requires a description "
                        + "and a '/by' time.", jijiImage, true)
        );

        userInput.setText("schedule today");

        // Build Title Bar with macOS window controls and title
        HBox titleBar = createTitleBar();

        VBox windowContainer = new VBox();
        windowContainer.setPrefWidth(WINDOW_WIDTH);
        windowContainer.setPrefHeight(WINDOW_HEIGHT);
        windowContainer.setStyle("-fx-background-color: #f8fafc; "
                + "-fx-border-color: #cbd5e1; -fx-border-width: 1px; "
                + "-fx-border-radius: 8px; -fx-background-radius: 8px;");

        mainContent.setPrefWidth(WINDOW_WIDTH);
        mainContent.setPrefHeight(WINDOW_HEIGHT - TITLE_BAR_HEIGHT);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        windowContainer.getChildren().addAll(titleBar, mainContent);

        Scene scene = new Scene(windowContainer, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

        windowContainer.applyCss();
        windowContainer.layout();
        scrollPane.setVvalue(1.0);
        windowContainer.layout();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setTransform(Transform.scale(RENDER_SCALE, RENDER_SCALE));
        WritableImage snapshot = windowContainer.snapshot(params, null);

        saveImage(snapshot, new File("docs/Ui.png"));
    }

    /**
     * Creates a native-style window title bar with circular window controls and centered title.
     *
     * @return An HBox representing the title bar.
     */
    private HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPrefHeight(TITLE_BAR_HEIGHT);
        titleBar.setStyle("-fx-background-color: #f1f5f9; "
                + "-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1px 0; "
                + "-fx-background-radius: 8px 8px 0 0;");
        titleBar.setPadding(new Insets(0, 14, 0, 14));

        // Window buttons (red, yellow, green)
        HBox buttons = new HBox(7);
        buttons.setAlignment(Pos.CENTER_LEFT);
        Circle red = new Circle(5.5, Color.web("#ef4444"));
        Circle yellow = new Circle(5.5, Color.web("#f59e0b"));
        Circle green = new Circle(5.5, Color.web("#10b981"));
        buttons.getChildren().addAll(red, yellow, green);

        // Centered title label
        Label titleLabel = new Label("Jiji ₍^ ᵕ ᵕ ^₎ - Cozy Feline Assistant");
        titleLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; "
                + "-fx-text-fill: #334155; -fx-font-family: 'Segoe UI', 'Helvetica Neue', sans-serif;");

        StackPane titleStack = new StackPane();
        titleStack.getChildren().add(titleLabel);
        HBox.setHgrow(titleStack, Priority.ALWAYS);

        Region spacer = new Region();
        spacer.setPrefWidth(buttons.getChildren().size() * 18);

        titleBar.getChildren().addAll(buttons, titleStack, spacer);
        return titleBar;
    }

    /**
     * Converts a JavaFX {@link WritableImage} to PNG format and writes it to disk.
     *
     * @param image JavaFX WritableImage.
     * @param target Destination file.
     * @throws Exception If an I/O error occurs.
     */
    private void saveImage(WritableImage image, File target) throws Exception {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader pixelReader = image.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
            }
        }

        if (target.getParentFile() != null && !target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        ImageIO.write(bufferedImage, "png", target);
    }
}
