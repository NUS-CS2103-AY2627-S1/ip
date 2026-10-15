package zeus.gui;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import zeus.Zeus;

/** Controls the main Zeus chat window defined in FXML. */
public class MainWindow {
    /** Scrollable area containing the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** Container holding user and Zeus dialog boxes. */
    @FXML
    private VBox dialogContainer;

    /** Field in which the user enters a command. */
    @FXML
    private TextField userInput;

    /** Button used to submit the current command. */
    @FXML
    private Button sendButton;

    /** Command-processing application instance. */
    private Zeus zeus;

    /** Keeps the most recent dialog visible as the conversation grows. */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, previousHeight, currentHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the Zeus instance and displays its opening message.
     *
     * @param zeus Zeus instance that processes commands.
     */
    public void setZeus(Zeus zeus) {
        this.zeus = Objects.requireNonNull(zeus);
        dialogContainer.getChildren().add(DialogBox.createZeusDialog(zeus.getWelcomeMessage()));
        userInput.requestFocus();
    }

    /** Adds the user's command and Zeus's response to the conversation. */
    @FXML
    private void handleUserInput() {
        assert zeus != null : "Zeus must be injected before input is handled.";
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = zeus.getResponse(input);
        DialogBox responseDialog = zeus.wasLastResponseError()
                ? DialogBox.createErrorDialog(response)
                : DialogBox.createZeusDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input),
                responseDialog);
        userInput.clear();

        if (zeus.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
