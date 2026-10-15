package bob.gui;

import java.util.Objects;

import bob.Bob;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main GUI view.
 */
public final class MainWindow extends BorderPane {
    private static final double EXIT_DELAY_SECONDS = 0.5;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    private Bob bob;

    private final Image bobImage = new Image(Objects.requireNonNull(
            this.getClass().getResourceAsStream("/images/DaBob.png")));

    /**
     * Initializes automatic scrolling when conversation content changes.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollToLatestMessage());
    }

    /**
     * Injects the Bob instance and displays the initial greeting.
     *
     * @param bob the Bob instance to interact with
     */
    public void setBob(Bob bob) {
        assert bob != null : "Bob instance cannot be null";
        this.bob = bob;
        dialogContainer.getChildren().add(
                DialogBox.getBobDialog(this.bob.getGreeting(), bobImage));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Handles the user input event, generating Bob's response and updating the
     * dialog container.
     */
    @FXML
    private void handleUserInput() {
        assert bob != null : "Bob instance must be initialized before handling user input";
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        String response = bob.getResponse(input);
        DialogBox responseDialog = bob.isLastResponseError()
                ? DialogBox.getErrorDialog(response, bobImage)
                : DialogBox.getBobDialog(response, bobImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();

        if (bob.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    /**
     * Scrolls to the newest message after JavaFX completes the pending layout pass.
     */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
