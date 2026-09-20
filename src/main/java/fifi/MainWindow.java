package fifi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Handles chat interaction for the window described in MainWindow.fxml.
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

    private final Image userImage = new Image(MainWindow.class.getResource("/user.png").toExternalForm());
    private final Image fifiImage = new Image(MainWindow.class.getResource("/fifi.png").toExternalForm());
    private Fifi fifi;

    /**
     * Keeps the latest reply visible when the conversation grows.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects the chatbot and displays its welcome message and any startup warning.
     *
     * @param fifi chatbot that processes the window's commands
     */
    public void setFifi(Fifi fifi) {
        this.fifi = fifi;
        dialogContainer.getChildren().add(DialogBox.getFifiDialog("Hello! My name is Fifi ^^\nHow may I help?",
                fifiImage));
        fifi.getStartupError().ifPresent(error ->
                dialogContainer.getChildren().add(DialogBox.getFifiDialog(error, fifiImage)));
    }

    /**
     * Processes one command from either Enter or Send without submitting blank input.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        if (userText.isBlank() || fifi.isExit()) {
            return;
        }
        ChatResponse response = fifi.getChatResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getFifiDialog(response, fifiImage));
        userInput.clear();
        userInput.requestFocus();
        if (fifi.isExit()) {
            userInput.getScene().getWindow().hide();
        }
    }
}
