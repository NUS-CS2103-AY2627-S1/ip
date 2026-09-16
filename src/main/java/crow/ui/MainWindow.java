package crow.ui;

import crow.Crow;
import crow.parser.Parser;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controls Crow's main JavaFX window.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInputField;
    private Crow crow;

    /**
     * Configures behavior that depends on controls injected from FXML.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot used to generate responses.
     *
     * @param crow Crow chatbot instance.
     */
    public void setCrow(Crow crow) {
        this.crow = crow;
    }

    /**
     * Adds the user's message and Crow's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String userCommand = userInputField.getText();
        String crowResponse = crow.getResponse(userCommand);
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(userCommand),
                DialogBox.createCrowDialog(crowResponse));
        userInputField.clear();

        if (isExitCommand(userCommand)) {
            Platform.exit();
        }
    }

    /**
     * Checks whether a command should close the JavaFX application.
     *
     * @param command User command.
     * @return {@code true} when the command is {@code bye}.
     */
    private static boolean isExitCommand(String command) {
        return Parser.isExitCommand(command);
    }
}
