package bro.gui;

import java.util.Objects;

import bro.Bro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Controls the chat window and connects JavaFX events to Bro's command API. */
public class MainWindow {
    @FXML
    private Button sendButton;
    @FXML
    private VBox dialogContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField userInput;

    private Bro bro;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the chatbot and displays its initial greeting. */
    public void setBro(Bro bro) {
        this.bro = Objects.requireNonNull(bro);
        dialogContainer.getChildren().add(DialogBox.getBroDialog(bro.getWelcomeMessage()));
        userInput.requestFocus();
    }

    /** Adds the user's message and Bro's response, then prepares for the next command. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = bro.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getBroDialog(response));
        userInput.clear();

        if (bro.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
