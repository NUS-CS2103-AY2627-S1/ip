package ev.gui;

import ev.EV;
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
 * Controller for the chat window.
 *
 * <p>It owns no chatbot logic: it passes each line the user types to {@link EV} and turns
 * whatever comes back into a dialog box.
 */
public class MainWindow extends AnchorPane {

    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private EV ev;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image evImage = new Image(this.getClass().getResourceAsStream("/images/ev.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives the window a chatbot to talk to, and shows its greeting.
     *
     * @param ev the chatbot, not yet started.
     */
    public void setEv(EV ev) {
        this.ev = ev;
        dialogContainer.getChildren().add(DialogBox.getEvDialog(ev.start(), evImage));
    }

    /**
     * Shows what the user typed and how EV answered, then clears the input box.
     * A farewell closes the window shortly after it has been read.
     */
    @FXML
    private void handleUserInput() {
        assert ev != null : "setEv must run before the window accepts input";

        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = ev.getResponse(input);
        DialogBox reply = ev.isErrorResponse()
                ? DialogBox.getErrorDialog(response, evImage)
                : DialogBox.getEvDialog(response, evImage);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), reply);
        userInput.clear();

        if (ev.isExit()) {
            dialogContainer.getChildren().add(DialogBox.getEvDialog(ev.getFarewell(), evImage));
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition pause = new PauseTransition(FAREWELL_PAUSE);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
