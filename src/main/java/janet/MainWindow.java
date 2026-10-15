package janet;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
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

    private Janet janet;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.jpg"));
    private Image janetImage = new Image(this.getClass().getResourceAsStream("/images/janet.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Janet instance and displays a pending storage-recovery prompt in the chat.
     *
     * @param j Janet application that backs this window.
     */
    public void setJanet(Janet j) {
        this.janet = j;
        if (this.janet.isStorageRecoveryRequired()) {
            this.addJanetMessage(this.janet.getStorageRecoveryPrompt());
        }
    }

    /**
     * Displays an unrecoverable startup error and prevents commands from being submitted without Janet.
     *
     * @param errorMessage Error to display in Janet's chat dialog.
     */
    public void showStartupError(String errorMessage) {
        this.addJanetMessage(errorMessage);
        this.userInput.setDisable(true);
        this.sendButton.setDisable(true);
    }

    /**
     * Creates a new dialog box in response to user input and appends it to the dialog container.
     */
    @FXML
    private void handleUserInput() throws IOException {
        assert this.janet != null : "Janet should not be null when handling input";

        String input = userInput.getText();
        userInput.clear();

        if (this.janet.isStorageRecoveryRequired()) {
            this.handleStorageRecoveryInput(input);
            return;
        } else if (Parser.isExitCommand(Parser.formatString(input))) {
            Platform.exit();
            return;
        }

        this.handleNormalInput(input);
    }

    private void addResponseToDialogContainer(String input, String message) {
        this.addUserMessage(input);
        this.addJanetMessage(message);
    }

    private void handleNormalInput(String input) {
        try {
            TaskList.CommandResult response = Janet.getResponse(this.janet, input);
            Janet.processCommandResult(this.janet, response);
            this.addResponseToDialogContainer(input, response.message());
        } catch (IOException e) {
            this.addResponseToDialogContainer(input, String.format("IO failure: %s", e.getMessage()));
        } catch (JanetException e) {
            this.addResponseToDialogContainer(input, e.getMessage());
        }
    }

    private void handleStorageRecoveryInput(String input) throws IOException {
        if (!this.janet.isResolveStorageRecoveryRequest(input)) {
            this.addResponseToDialogContainer(input, "Janet will now close.");
            Platform.exit();
            return;
        }

        this.janet = this.janet.resolveStorageRecovery();
        this.addResponseToDialogContainer(input, "Saved tasks were cleared. Janet is ready for a fresh start.");
        return;
    }

    /**
     * Adds a Janet-only dialog, used for messages that do not have a user command counterpart.
     *
     * @param message Message to show in Janet's chat dialog.
     */
    private void addJanetMessage(String message) {
        this.addMessage(DialogBox.getJanetDialog(message, janetImage));
    }

    /**
     * Adds a user-only dialog to the dialog container.
     *
     * @param message Message to show in the user's chat dialog.
     */
    private void addUserMessage(String message) {
        this.addMessage(DialogBox.getUserDialog(message, userImage));
    }

    private void addMessage(DialogBox dialogBox) {
        dialogContainer.getChildren().add(dialogBox);
    }
}
