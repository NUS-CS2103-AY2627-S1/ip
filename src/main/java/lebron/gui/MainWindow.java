package lebron.gui;

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
import lebron.LeBron;

/**
 * Controller for the main chat window. Wires the text field/send button to
 * {@link LeBron#getResponse(String)} and renders the conversation as dialog boxes.
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

    private LeBron leBron;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.jpg"));
    private final Image leBronImage = new Image(this.getClass().getResourceAsStream("/images/lebron.jpg"));

    /**
     * Binds the scroll pane so it automatically follows the bottom of the conversation
     * as new dialog boxes are added. Called by the FXML loader after injection.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the LeBron instance this window talks to, and shows its greeting.
     *
     * @param leBron The LeBron instance to converse with.
     */
    public void setLeBron(LeBron leBron) {
        this.leBron = leBron;
        dialogContainer.getChildren().add(DialogBox.getLeBronDialog(leBron.getGreeting(), leBronImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * LeBron's reply, then appends them to the dialog container. Clears the user
     * input afterwards, and closes the window if the input was an exit command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = leBron.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getLeBronDialog(response, leBronImage)
        );
        userInput.clear();

        if (leBron.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
