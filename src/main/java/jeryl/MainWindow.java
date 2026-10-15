package jeryl;

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
 * Controller for the main chat window: wires up the text field, send
 * button, and scrolling dialog list to a Jeryl instance's
 * getResponse(String).
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

    private Jeryl jeryl;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Jeryl instance this window sends user input to, and
     * shows its welcome message as the first chat bubble.
     */
    public void setJeryl(Jeryl jeryl) {
        this.jeryl = jeryl;
        dialogContainer.getChildren().add(DialogBox.getJerylDialog(jeryl.welcomeMessage()));
    }

    /**
     * Sends the text field's content to Jeryl, shows both the user's
     * input and Jeryl's response as dialog bubbles, and exits the
     * application shortly after a "bye" response is shown.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        Jeryl.Response response = jeryl.respond(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getJerylDialog(response));
        userInput.clear();

        if (jeryl.isExit(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
