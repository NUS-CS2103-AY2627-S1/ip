import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controls the main StanVard JavaFX window.
 */
public class MainWindow {
    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final StanVard stanVard = new StanVard();

    /**
     * Displays the initial greeting after the FXML view is loaded.
     */
    @FXML
    private void initialize() {
        addMessage("Hello! I'm StanVard, your friendly task companion.");
        addMessage("What shall we tackle today?");
        userInput.requestFocus();
    }

    /**
     * Sends the current text field contents to StanVard.
     */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        addMessage("You: " + command);
        String response = stanVard.processCommand(command);
        if (!response.isEmpty()) {
            addMessage(response);
        }
        userInput.clear();

        if (command.equals("bye")) {
            sendButton.setDisable(true);
            userInput.setDisable(true);
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Adds one message row to the conversation.
     *
     * @param message message to display
     */
    private void addMessage(String message) {
        dialogContainer.getChildren().add(new DialogBox(message));
    }
}
