package min.ui.gui;

import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import min.Min;
import min.ui.Messages;

/**
 * Controller for the main GUI.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1);
    private static final String USER_IMAGE_PATH = "/images/UserAvatar.png";
    private static final String MIN_IMAGE_PATH = "/images/MinAvatar.png";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Min min;

    private final Image userImage = loadImage(USER_IMAGE_PATH);
    private final Image minImage = loadImage(MIN_IMAGE_PATH);

    /**
     * Loads an image bundled with the application.
     *
     * @param resourcePath The path of the image within the resources folder.
     * @return The loaded image.
     * @throws IllegalStateException If the image is missing from the build, which
     *         would otherwise surface only as an unexplained startup failure.
     */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath);
        if (imageStream == null) {
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }

        return new Image(imageStream);
    }

    /** Keeps the conversation pane scrolled to the latest dialog. */
    @FXML
    public void initialize() {
        assert this.scrollPane != null : "scrollPane must be injected from FXML.";
        assert this.dialogContainer != null : "dialogContainer must be injected from FXML.";
        assert this.userInput != null : "userInput must be injected from FXML.";
        assert this.sendButton != null : "sendButton must be injected from FXML.";

        // Scroll to the newest dialog whenever one is added. A listener is used rather
        // than binding vvalue, because a bound property cannot be set by anything else
        // and that would stop the user scrolling with a trackpad or mouse wheel.
        this.dialogContainer.heightProperty().addListener(
                (observable, oldHeight, newHeight) -> this.scrollPane.setVvalue(1.0));
        this.userInput.setPromptText(Messages.INPUT_PROMPT);
    }

    /**
     * Provides the Min instance used to process commands.
     *
     * @param min The Min instance.
     */
    public void setMin(Min min) {
        assert min != null : "MainWindow requires a Min instance.";

        this.min = min;
        this.dialogContainer.getChildren().add(
                DialogBox.createMinDialog(
                        this.min.getWelcomeMessage(), this.minImage));
    }

    /**
     * Displays the user's input and Min's response in dialog boxes.
     * Clears the input field after processing.
     */
    @FXML
    private void handleUserInput() {
        assert this.min != null : "setMin must be called before handling user input.";

        String input = this.userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = this.min.getResponse(input);
        this.dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input, this.userImage),
                DialogBox.createMinDialog(response, this.minImage)
        );
        this.userInput.clear();

        if (this.min.isExitCommand(input)) {
            exitAfterDelay();
        }
    }

    /** Disables further input and closes the application after a short delay. */
    private void exitAfterDelay() {
        this.userInput.setDisable(true);
        this.sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
