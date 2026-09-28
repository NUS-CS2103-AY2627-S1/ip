package baron.controller;

import java.util.Objects;

import baron.core.Baron;
import baron.core.StorageWarningHandler;
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
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Baron baron;

    private final Image userImage = new Image(
            Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/user.jpg")));
    private final Image baronImage = new Image(
            Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/baron.jpg")));

    /**
     * Creates the FXML-controlled main window.
     */
    public MainWindow() {
    }

    /** Initializes the main window after its FXML fields have been injected. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the Baron instance that processes user messages.
     * Adds a dialog box to introduce Baron.
     *
     * @param baron The Baron instance.
     */
    public void setBaron(Baron baron) {
        this.baron = baron;
        String intro = baron.getIntro();
        if (!StorageWarningHandler.getInstance().getWarnings().isEmpty()) {
            intro += "\n\nWarning: Some saved data could not be restored:\n"
                    + String.join("\n", StorageWarningHandler.getInstance().getWarnings());
        }
        dialogContainer.getChildren().add(DialogBox.getBaronDialog(intro, baronImage));
    }

    /**
     * Adds dialog boxes for the user's input and Baron's reply, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = baron.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBaronDialog(response, baronImage));
        userInput.clear();
        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(_ -> Platform.exit());
            delay.play();
        }
    }
}
