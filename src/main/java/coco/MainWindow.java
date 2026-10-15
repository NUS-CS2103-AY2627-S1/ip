package coco;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI window: a scrollable dialog log, a text
 * field, and a send button.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Coco coco;

    /** Keeps the dialog log scrolled to the newest message. */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Coco instance this window talks to, and shows its
     * greeting as the first message.
     *
     * @param coco Coco instance to use.
     */
    public void setCoco(Coco coco) {
        this.coco = coco;
        dialogContainer.getChildren().add(DialogBox.getCocoDialog(coco.greet()));
    }

    /**
     * Sends the text field's contents to Coco and shows both the user's
     * message and Coco's reply, then closes the window shortly after a
     * "bye" command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        boolean isExit = Parser.parseCommand(input) == Command.BYE;
        String response = coco.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getCocoDialog(response));
        userInput.clear();

        if (isExit) {
            PauseTransition delay = new PauseTransition(EXIT_DELAY);
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
