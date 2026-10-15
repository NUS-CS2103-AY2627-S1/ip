package rex.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import rex.Rex;

/**
 * Controls the main window: takes what the user types and adds the exchange to
 * the conversation.
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

    private Rex rex;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image rexImage = new Image(this.getClass().getResourceAsStream("/images/DaRex.png"));

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Hands this window the Rex it should talk to, and shows his greeting.
     *
     * The greeting belongs here rather than in initialize() because it needs
     * Rex, and Rex is not available until after the FXML has been loaded.
     *
     * @param rex the chatbot answering the user.
     */
    public void setRex(Rex rex) {
        this.rex = rex;
        dialogContainer.getChildren().add(DialogBox.getRexDialog(rex.start(), rexImage));
    }

    /**
     * Shows what the user typed and Rex's reply, then clears the input box.
     *
     * A bye command closes the window once the farewell has been shown, so the
     * user sees it rather than the window vanishing mid-sentence.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = rex.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getRexDialog(response, rexImage)
        );
        userInput.clear();

        if (rex.isExit()) {
            Platform.exit();
        }
    }
}
