package jason.gui;

import jason.Jason;
import jason.command.CommandWords;
import jason.ui.Ui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Controls the main JavaFX window for Jason. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Jason jason;

    /** Initializes automatic scrolling for the conversation area. */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Jason instance used to process GUI commands.
     *
     * @param jason Jason application instance shared by the GUI.
     */
    public void setJason(Jason jason) {
        this.jason = jason;
        dialogContainer.getChildren().add(DialogBox.getJasonDialog(Ui.HELP_MESSAGE));
    }

    /** Handles commands submitted through the text field or Send button. */
    @FXML
    private void handleUserInput() {
        String message = userInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        String response = jason.getResponse(message);
        String commandType = jason.getLastCommandType();
        dialogContainer.getChildren().add(DialogBox.getUserDialog(message));
        if (!response.isBlank()) {
            DialogBox responseDialog = commandType == null
                    ? DialogBox.getJasonErrorDialog(response)
                    : DialogBox.getJasonDialog(response, commandType);
            dialogContainer.getChildren().add(responseDialog);
        }
        userInput.clear();
        if (message.equals(CommandWords.EXIT)) {
            Platform.exit();
        }
    }
}
