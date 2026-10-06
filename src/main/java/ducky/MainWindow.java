package ducky;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI window: a scrollable chat log plus an input row.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Ducky ducky;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.jpg"));
    private final Image duckyImage = new Image(getClass().getResourceAsStream("/images/DaDucky.jpg"));

    /**
     * Binds the scroll pane to always show the newest message.
     */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Connects this window to a chatbot instance and shows its welcome message.
     *
     * @param ducky the chatbot backing this window.
     */
    public void setDucky(Ducky ducky) {
        this.ducky = ducky;
        dialogContainer.getChildren().add(DialogBox.getWelcomeDialog(ducky.getWelcomeMessage(), duckyImage));
    }

    /**
     * Sends the text in the input field to Ducky and displays both the user's input
     * and Ducky's reply as dialog boxes. Closes the window shortly after a {@code bye} command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = ducky.getResponse(input);
        DialogBox responseDialog = ducky.lastResponseWasError()
                ? DialogBox.getErrorDialog(response, duckyImage)
                : DialogBox.getSuccessDialog(response, duckyImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog
        );
        userInput.clear();

        if (ducky.isExit()) {
            PauseTransition delay = new PauseTransition(EXIT_DELAY);
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
