package ev;

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
 * Controls the main window of E.V.'s graphical user interface.
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

    private EV ev;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image evImage = new Image(this.getClass().getResourceAsStream("/images/DaEv.png"));

    /**
     * Binds the scroll pane to the conversation so it follows the newest message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot this window sends user input to, and shows its greeting.
     *
     * @param chatbot the E.V. instance backing this window.
     */
    public void setEv(EV chatbot) {
        this.ev = chatbot;
        dialogContainer.getChildren().add(DialogBox.getEvDialog(chatbot.getGreeting(), evImage));
    }

    /**
     * Adds the user's message and E.V.'s reply to the conversation, then clears the input box.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = ev.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getEvDialog(response, evImage)
        );

        userInput.clear();

        if (ev.isExit(input)) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));

            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
