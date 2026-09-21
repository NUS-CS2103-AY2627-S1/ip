package quackie.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import quackie.Quackie;

/**
 * Controls Quackie's main chat window.
 */
public class MainWindow extends BorderPane {
    private static final String ERROR_PREFIX = "OOPS!!!";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private Label hintText;

    private Quackie quackie;

    /**
     * Keeps the newest message visible as the conversation grows.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot and displays its opening message.
     *
     * @param quackie chatbot that handles user commands
     */
    public void setQuackie(Quackie quackie) {
        this.quackie = quackie;
        dialogContainer.getChildren().add(
                DialogBox.getQuackieDialog(
                        "Quack! I'm Quackie, your task companion.\n"
                                + "What shall we get done today?"));
        userInput.requestFocus();
    }

    /**
     * Submits the current input and adds the resulting conversation to the window.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = quackie.getResponse(input);

        if (!input.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        }
        DialogBox responseDialog = response.startsWith(ERROR_PREFIX)
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getQuackieDialog(response);
        dialogContainer.getChildren().add(responseDialog);
        userInput.clear();

        if (quackie.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            hintText.setText("Session ended. Restart Quackie to begin another chat.");
        } else {
            userInput.requestFocus();
        }
    }
}
