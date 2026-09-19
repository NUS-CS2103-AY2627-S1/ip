package bro.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bro.Bro;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Unit tests for {@link MainWindow} layout and resizing behavior.
 */
public class MainWindowTest {
    private static boolean isJavaFxAvailable;

    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
            isJavaFxAvailable = true;
        } catch (IllegalStateException e) {
            // Platform already started
            isJavaFxAvailable = true;
        } catch (UnsupportedOperationException e) {
            // Headless Linux without display (e.g. CI without xvfb)
            isJavaFxAvailable = false;
        }
    }

    @BeforeEach
    public void setUp() {
        assumeTrue(isJavaFxAvailable, "Skipping test: JavaFX DISPLAY is unavailable.");
    }

    @Test
    public void fxmlLayout_anchorsAndProperties_configuredForResponsiveResizing() throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
                AnchorPane mainWindow = fxmlLoader.load();
                MainWindow controller = fxmlLoader.getController();
                assertNotNull(mainWindow);
                assertNotNull(controller);

                TextField userInput = (TextField) mainWindow.lookup("#userInput");
                Button sendButton = (Button) mainWindow.lookup("#sendButton");
                ScrollPane scrollPane = (ScrollPane) mainWindow.lookup("#scrollPane");

                assertNotNull(userInput);
                assertNotNull(sendButton);
                assertNotNull(scrollPane);

                // ScrollPane resizing configuration
                assertTrue(scrollPane.isFitToWidth());
                assertEquals(0.0, AnchorPane.getTopAnchor(scrollPane), 0.001);
                assertEquals(0.0, AnchorPane.getLeftAnchor(scrollPane), 0.001);
                assertEquals(0.0, AnchorPane.getRightAnchor(scrollPane), 0.001);
                assertEquals(43.0, AnchorPane.getBottomAnchor(scrollPane), 0.001);

                // SendButton anchors to the bottom-right corner
                assertEquals(1.0, AnchorPane.getBottomAnchor(sendButton), 0.001);
                assertEquals(0.0, AnchorPane.getRightAnchor(sendButton), 0.001);

                // UserInput stretches between left edge and the SendButton
                assertEquals(1.0, AnchorPane.getBottomAnchor(userInput), 0.001);
                assertEquals(0.0, AnchorPane.getLeftAnchor(userInput), 0.001);
                assertEquals(76.0, AnchorPane.getRightAnchor(userInput), 0.001);

                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void setBro_validInstance_addsWelcomeMessageToDialogContainer() throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
                AnchorPane mainWindow = fxmlLoader.load();
                MainWindow controller = fxmlLoader.getController();

                ScrollPane scrollPane = (ScrollPane) mainWindow.lookup("#scrollPane");
                assertNotNull(scrollPane);
                VBox dialogContainer = (VBox) scrollPane.getContent();
                assertNotNull(dialogContainer);
                assertTrue(dialogContainer.getChildren().isEmpty());

                controller.setBro(new Bro());
                assertFalse(dialogContainer.getChildren().isEmpty());

                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }
}
