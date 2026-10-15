package waddles;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI window, defined in {@code /view/MainWindow.fxml}.
 * Displays the chat history and forwards each line the user submits to a
 * {@link Waddles} instance, showing whatever it responds with.
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

    private Waddles waddles;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot logic to drive this window, and shows its
     * greeting as the first message.
     *
     * @param waddles The chatbot instance to delegate user input to.
     */
    public void setWaddles(Waddles waddles) {
        this.waddles = waddles;
        addDialog(DialogBox.getWaddlesDialog(waddles.getWelcomeMessage()), false);
    }

    /**
     * Handles a submission from the input field (Enter key or Send button):
     * echoes the user's line, shows Waddles' response, then clears the
     * input field. Exits the application if the user typed "bye", mirroring
     * the CLI's exit behaviour.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = waddles.getResponse(input);
        boolean isError = response.startsWith(WaddlesException.ERROR_PREFIX);
        addDialog(DialogBox.getUserDialog(input), false);
        addDialog(DialogBox.getWaddlesDialog(response), isError);
        userInput.clear();

        if (input.trim().equals("bye")) {
            Platform.exit();
        }
    }

    /**
     * Adds a dialog box to the chat history, binding its bubble width to the
     * available window width so it resizes sensibly, and highlighting it if
     * it reports an error.
     *
     * @param box The dialog box to add.
     * @param isError Whether this dialog box shows an error response, and
     *                 should be visually flagged as such.
     */
    private void addDialog(DialogBox box, boolean isError) {
        box.bindBubbleWidth(scrollPane.widthProperty());
        if (isError) {
            box.markAsError();
        }
        dialogContainer.getChildren().add(box);
    }
}
