import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import kiwi.Kiwi;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends BorderPane {
    private static final double EXIT_DELAY_SECONDS = 1;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Kiwi kiwi;

    private final Image kiwiImage = new Image(this.getClass().getResourceAsStream("/images/DaKiwi.png"));

    /**
     * Scrolls to the latest message when the conversation grows, and focuses the input field.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });
        userInput.requestFocus();
    }

    /**
     * Injects the Kiwi instance and shows the opening greeting.
     *
     * @param kiwi the chatbot to send commands to.
     */
    public void setKiwi(Kiwi kiwi) {
        this.kiwi = kiwi;
        dialogContainer.getChildren().add(
                DialogBox.getKiwiDialog(kiwi.getWelcomeMessage(), kiwiImage));
    }

    /**
     * Echoes the user's command and appends Kiwi's reply (or an error row).
     * Ignores blank input so an accidental Enter does not add empty bubbles.
     * Closes the window after {@code bye}, once the goodbye message has been shown.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.clear();
            return;
        }

        String response = kiwi.getResponse(input);
        DialogBox reply;
        if (kiwi.isError()) {
            reply = DialogBox.getErrorDialog(response, kiwiImage);
        } else {
            reply = DialogBox.getKiwiDialog(response, kiwiImage);
        }
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input), reply);
        userInput.clear();

        if (kiwi.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
