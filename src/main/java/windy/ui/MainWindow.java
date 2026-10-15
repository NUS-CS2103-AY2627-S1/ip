package windy.ui;

import java.util.Objects;

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
import windy.Windy;

/**
 * Controls the main Windy GUI.
 */
public class MainWindow extends AnchorPane {

    private static final double EXIT_DELAY_SECONDS = 1.5;

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image windyImage = loadImage("/images/DaWindy.png");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Windy windy;

    /**
     * Initializes bindings after FXML fields have been injected.
     */
    @FXML
    private void initialize() {
        assert scrollPane != null : "Scroll pane should be injected by FXML";
        assert dialogContainer != null : "Dialog container should be injected by FXML";
        assert userInput != null : "User input should be injected by FXML";
        assert sendButton != null : "Send button should be injected by FXML";
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the Windy instance used to process commands.
     *
     * @param windy Windy application instance.
     */
    public void setWindy(Windy windy) {
        this.windy = windy;
        dialogContainer.getChildren().add(
                DialogBox.getWelcomeDialog(windy.getWelcomeMessage(), windyImage));
    }

    /**
     * Displays the user's command and Windy's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        assert windy != null : "Windy should be set before handling input";
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = windy.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getWindyDialog(response, windyImage));
        userInput.clear();

        if (windy.isExitRequested()) {
            scheduleExit();
        }
    }

    /**
     * Disables further input and closes the application after the farewell message is rendered.
     */
    private void scheduleExit() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    /**
     * Loads an image from the application's resources.
     *
     * @param resourcePath absolute resource path.
     * @return loaded image.
     */
    private Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath),
                "Image resource not found: " + resourcePath));
    }
}
