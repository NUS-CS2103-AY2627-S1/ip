package thomas.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import thomas.Thomas;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends VBox {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Thomas thomas;

    private Image thomasImage = new Image(this.getClass().getResourceAsStream("/images/Thomas.png"));

    /**
     * Keeps the newest dialog box in view as the conversation grows, and puts
     * the cursor in the input field so the user can type straight away.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // The field is not yet in a scene here, so the focus request has to
        // wait until the window is shown.
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Injects the chatbot the window talks to, and shows its greeting.
     * <p>
     * The greeting waits for the chatbot rather than going in
     * {@link #initialize()}: it is Thomas's first words, and until this method
     * runs there is no Thomas to say them. Any complaint about the save file
     * arrives with it, which is the only chance the user gets to hear that the
     * tasks on screen are not the tasks on disk.
     *
     * @param thomas The chatbot that answers what the user types.
     */
    public void setThomas(Thomas thomas) {
        this.thomas = thomas;
        dialogContainer.getChildren().add(
                DialogBox.getThomasDialog(thomas.getStartupMessage(), thomasImage, "", false));
    }

    /**
     * Answers one typed line: appends a dialog box echoing it and another
     * holding Thomas's reply, then clears the input field.
     * <p>
     * A {@code bye} needs no special reply: its farewell arrives as the
     * response like any other, and all that is left is to close the window
     * afterwards.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            // An empty line is a slip of the Enter key, not a command, so it
            // is not worth a pair of bubbles.
            return;
        }
        String response = thomas.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getThomasDialog(response, thomasImage, thomas.getCommandType(),
                        thomas.hasErrored())
        );
        userInput.clear();

        if (thomas.hasExited()) {
            // Closing straight away would take the window down before the
            // farewell is ever painted, so it is given time to be read first.
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
