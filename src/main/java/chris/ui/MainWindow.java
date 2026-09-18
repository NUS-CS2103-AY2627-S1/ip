package chris.ui;

import chris.Chris;
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
 * Controls the main Chris chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private final Image chrisImage = new Image(getClass().getResourceAsStream("/images/Chris.png"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Chris chris;

    /**
     * Keeps the latest message visible as the dialog area grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot and displays its welcome message.
     *
     * @param chris Chatbot instance.
     */
    public void setChris(Chris chris) {
        this.chris = chris;
        dialogContainer.getChildren().add(
                DialogBox.getChrisWelcomeDialog(chris.getWelcomeMessage(), chrisImage));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = chris.getResponse(input);
        DialogBox responseDialog = isErrorResponse(response)
                ? DialogBox.getChrisErrorDialog(response, chrisImage)
                : DialogBox.getChrisDialog(response, chrisImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();

        if (chris.isExitRequested()) {
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1.0));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    private boolean isErrorResponse(String response) {
        return response.startsWith("OOPS!!!");
    }
}
