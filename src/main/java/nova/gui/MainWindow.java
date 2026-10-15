package nova.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nova.Nova;

/**
 * Controls Nova's main chat window.
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

    private Nova nova;

    /**
     * Keeps the newest dialog visible when the conversation grows.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects the window to Nova's command-processing logic and displays its greeting.
     *
     * @param nova Nova application used to process commands
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        dialogContainer.getChildren().add(DialogBox.getNovaDialog(nova.startGui()));
    }

    /**
     * Adds the user's input and Nova's response to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = nova.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getNovaDialog(response));
        userInput.clear();

        if (nova.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
