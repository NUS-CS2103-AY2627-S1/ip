package chocolate.gui;

import chocolate.Chocolate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for Chocolate's main chat window.
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

    private Chocolate chocolate;

    /**
     * Makes the dialogue pane scroll to the newest message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the command-processing application and shows its greeting.
     *
     * @param chocolate Application instance used to process commands.
     */
    public void setChocolate(Chocolate chocolate) {
        this.chocolate = chocolate;
        addDialog(DialogBox.getChocolateDialog(
                "Hello! I'm Chocolate, your task chocolatier.\nWhat shall we sweeten up today?"));
        focusUserInput();
    }

    /**
     * Adds the user's command and Chocolate's reply to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = chocolate.getResponse(input);
        addDialog(DialogBox.getUserDialog(input));
        addDialog(DialogBox.getChocolateDialog(response));
        userInput.clear();
        focusUserInput();
    }

    /**
     * Adds a responsive dialog that can use up to 72% of the conversation width.
     *
     * @param dialogBox Dialog to add to the conversation.
     */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.bindMaximumBubbleWidth(scrollPane.widthProperty());
        dialogContainer.getChildren().add(dialogBox);
    }

    /**
     * Returns keyboard focus to the command field after the window has updated.
     */
    private void focusUserInput() {
        Platform.runLater(userInput::requestFocus);
    }
}
