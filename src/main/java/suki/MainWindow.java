package suki;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main window.
 *
 * <p>The fields marked {@code @FXML} are filled in by the FXML loader from the
 * matching {@code fx:id} values in {@code MainWindow.fxml}, so they are null
 * until {@link #initialize()} runs.
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

    /** The chatbot that answers what the user types. */
    private Suki suki;

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives this window the chatbot to talk to, and shows its greeting.
     *
     * <p>The greeting is shown here rather than in {@code initialize} because
     * there is no chatbot to ask for it until this point.
     *
     * @param suki the chatbot instance to use
     */
    public void setSuki(Suki suki) {
        this.suki = suki;
        dialogContainer.getChildren().add(DialogBox.getSukiDialog(suki.getGreeting()));
    }

    /**
     * Shows the user's message and Suki's reply as a pair of dialog boxes, then
     * clears the input field ready for the next command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = suki.getResponse(input);
        DialogBox responseBox = response.startsWith("OOPS!!!")
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getSukiDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseBox
        );
        userInput.clear();

        if (Suki.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition farewellDelay = new PauseTransition(Duration.seconds(1));
            farewellDelay.setOnFinished(event -> Platform.exit());
            farewellDelay.play();
        }
    }
}
