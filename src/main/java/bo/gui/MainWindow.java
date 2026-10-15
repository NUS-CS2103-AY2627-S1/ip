package bo.gui;

import bo.Bo;
import bo.BoException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls the main FXML window and routes user commands to Bo. */
public final class MainWindow extends AnchorPane {
    /** The scrollable area containing the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** The vertical container holding conversation entries. */
    @FXML
    private VBox dialogContainer;

    /** The text field where the user enters commands. */
    @FXML
    private TextField userInput;

    /** The button that submits the current command. */
    @FXML
    private Button sendButton;

    /** The application logic shared by the GUI and CLI. */
    private Bo bo;

    /** Creates the controller used by the main FXML view. */
    public MainWindow() {
    }

    /** Keeps the conversation scrolled to the latest response. */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the application logic and displays the startup messages.
     *
     * @param bo the application logic used to execute commands.
     */
    public void setBo(Bo bo) {
        this.bo = bo;
        addBoDialog("Hello! I'm Bo.\nWhat can I do for you?");
        if (!bo.getStartupWarning().isEmpty()) {
            addBoDialog(bo.getStartupWarning());
        }
    }

    /** Handles both pressing Enter and clicking Send. */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().strip();
        if (command.isEmpty()) {
            return;
        }

        addUserDialog(command);
        userInput.clear();
        if (command.equals("bye")) {
            addBoDialog("Bye. Hope to see you again soon!");
            userInput.setDisable(true);
            sendButton.setDisable(true);
            return;
        }

        try {
            addBoDialog(bo.executeCommand(command));
        } catch (BoException exception) {
            addBoDialog("OOPS!!! " + exception.getMessage());
        }
    }

    /** Adds a user-authored command to the conversation. */
    private void addUserDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.getUserDialog(message));
    }

    /** Adds a response from Bo to the conversation. */
    private void addBoDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.getBoDialog(message));
    }
}
