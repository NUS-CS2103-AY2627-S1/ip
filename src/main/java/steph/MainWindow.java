package steph;

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
 * Controller for {@code MainWindow.fxml}. Wires the Send button and the text
 * field to {@link #handleUserInput()}, which feeds each line to {@link Steph}
 * and shows the user's message and Steph's reply as two {@link DialogBox}
 * bubbles in the scrolling conversation.
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

    private Steph steph;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/UserPfp.png"));
    private final Image stephImage = new Image(this.getClass().getResourceAsStream("/images/StephPfp.png"));

    /**
     * Binds the scroll pane so it always shows the newest message: whenever the
     * dialog container grows taller, the view scrolls to the bottom.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Steph} back end and greets the user with Steph's
     * welcome message.
     *
     * @param s The Steph instance that answers commands.
     */
    public void setSteph(Steph s) {
        steph = s;
        dialogContainer.getChildren().add(
                DialogBox.getStephDialog(Steph.getWelcomeMessage(), stephImage, "", false));
    }

    /**
     * Handles one line of user input: shows it as a bubble, asks Steph for a
     * reply, shows the reply tinted by the command type, and clears the field.
     * If the command ends the session ("bye"), the window closes after a short
     * pause so the farewell stays visible briefly.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = steph.getResponse(input);
        String commandType = steph.getCommandType();
        boolean isError = steph.isLastResponseError();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getStephDialog(response, stephImage, commandType, isError));
        userInput.clear();

        if (steph.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
