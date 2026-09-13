package chimpanzinibananini.ui;

import chimpanzinibananini.ChimpanziniBananini;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main chatbot window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private ChimpanziniBananini chatbot;

    /**
     * Configures scrolling after the FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot that handles commands entered in this window.
     */
    public void setChatbot(ChimpanziniBananini chatbot) {
        this.chatbot = chatbot;
    }

    /** Adds the user's command and the chatbot's response to the dialog area. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = chatbot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getChatbotDialog(response));
        userInput.clear();
        if (input.strip().equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> userInput.getScene().getWindow().hide());
            delay.play();
        }
    }
}
