package neil.ui;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import neil.CommandResult;
import neil.Neil;

/**
 * Controls Neil's main chat window.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final CommandHistory commandHistory = new CommandHistory();
    private Neil neil;

    @FXML
    private void initialize() {
        assert scrollPane != null : "FXML must inject scrollPane";
        assert dialogContainer != null : "FXML must inject dialogContainer";
        assert userInput != null : "FXML must inject userInput";
        assert sendButton != null : "FXML must inject sendButton";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() -> userInput.getText().isBlank(),
                userInput.textProperty()));
    }

    /**
     * Supplies the command processor and displays its welcome message.
     *
     * @param neil command processor used by this window.
     */
    public void setNeil(Neil neil) {
        this.neil = neil;
        CommandResult welcomeResponse = neil.getWelcomeResponse();
        DialogBox welcomeDialog = welcomeResponse.isError()
                ? DialogBox.getNeilErrorDialog(welcomeResponse.message())
                : DialogBox.getNeilWelcomeDialog(welcomeResponse.message());
        dialogContainer.getChildren().add(welcomeDialog);
    }

    @FXML
    private void handleUserInput() {
        assert neil != null : "setNeil must be called before handling user input";

        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        commandHistory.add(input);
        CommandResult response = neil.getResponse(input);
        DialogBox responseDialog = createResponseDialog(response);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog
        );
        userInput.clear();

        if (neil.isExitCommand(input)) {
            Platform.exit();
        }
    }

    @FXML
    private void handleHistoryNavigation(KeyEvent event) {
        Optional<String> command;

        if (event.getCode() == KeyCode.UP) {
            command = commandHistory.getPrevious(userInput.getText());
        } else if (event.getCode() == KeyCode.DOWN) {
            command = commandHistory.getNext();
        } else {
            return;
        }

        command.ifPresent(value -> {
            userInput.setText(value);
            userInput.end();
        });
        event.consume();
    }

    private DialogBox createResponseDialog(CommandResult response) {
        if (response.isError()) {
            return DialogBox.getNeilErrorDialog(response.message());
        }
        if (response.isHelp()) {
            return DialogBox.getNeilHelpDialog(response.message());
        }
        return DialogBox.getNeilDialog(response.message());
    }
}
