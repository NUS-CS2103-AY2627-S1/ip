package laby;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/** Controls the main FXML view and connects it to Laby's command logic. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField inputField;

    private final Image userImage = new Image(
            MainWindow.class.getResourceAsStream("/images/chief.jpg"));
    private final Image labyImage = new Image(
            MainWindow.class.getResourceAsStream("/images/laby.jpg"));

    private Laby laby;

    /** Keeps the newest message visible as the conversation grows. */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the application logic and displays the opening message.
     *
     * @param laby Application logic used to execute commands.
     */
    public void setLaby(Laby laby) {
        this.laby = laby;
        appendLabyMessage("Hello Chief. Laby is your personal assistant.");
    }

    /**
     * Executes the command in the input field and displays the response.
     */
    @FXML
    private void handleUserInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        appendUserMessage(input);
        appendLabyMessage(laby.executeCommand(input));
        inputField.clear();

        if (input.equalsIgnoreCase("bye")) {
            Platform.exit();
        }
    }

    /**
     * Adds one message to the conversation area.
     *
     * @param message Message to display.
     */
    private void appendUserMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.getUserDialog(message, userImage));
    }

    /**
     * Adds a Laby response to the conversation.
     *
     * @param message Laby response to display.
     */
    private void appendLabyMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.getLabyDialog(message, labyImage));
    }
}
