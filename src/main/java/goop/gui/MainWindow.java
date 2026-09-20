package goop.gui;

import java.io.IOException;
import java.io.InputStream;

import goop.CommandResult;
import goop.Goop;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main chat window defined in {@code MainWindow.fxml}.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image goopImage = loadImage("/images/DaGoop.png");
    private Goop goop;

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the chatbot and displays its greeting.
     *
     * @param goop Chatbot that handles commands from this window.
     */
    public void setGoop(Goop goop) {
        this.goop = goop;
        dialogContainer.getChildren().add(
                DialogBox.getGoopDialog(goop.getWelcomeMessage(), goopImage));
        userInput.requestFocus();
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        CommandResult result = goop.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getGoopDialog(result.getResponse(), goopImage));
        userInput.clear();

        if (result.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Loads an avatar image from the application's resources.
     */
    private static Image loadImage(String resourcePath) {
        try (InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath)) {
            if (imageStream == null) {
                throw new IllegalStateException("Missing image resource: " + resourcePath);
            }
            return new Image(imageStream);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to load image resource: " + resourcePath, error);
        }
    }
}
