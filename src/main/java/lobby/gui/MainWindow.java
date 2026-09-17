package lobby.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lobby.Lobby;

/**
 * Controls Lobby's main chat window.
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

    private Lobby lobby;

    /**
     * Configures behavior that only depends on controls declared in FXML.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Connects the window to Lobby's command-processing logic.
     *
     * @param lobby Lobby instance that processes commands.
     */
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
        dialogContainer.getChildren().add(DialogBox.getLobbyDialog(lobby.getStartupMessage()));
    }

    /**
     * Sends the current input to Lobby and displays both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || lobby == null) {
            return;
        }

        Lobby.Response response = lobby.processCommand(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                response.isError() ? DialogBox.getErrorDialog(response.text().trim())
                        : DialogBox.getLobbyDialog(response.text().trim()));
        if (!response.shouldRetainInput()) {
            userInput.clear();
        }
        userInput.requestFocus();

        if (input.equalsIgnoreCase("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
