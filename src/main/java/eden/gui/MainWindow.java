package eden.gui;

import eden.Eden;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls Eden's main chat window and passes user commands to the application.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Eden eden;

    /**
     * Creates the controller populated by the FXML loader.
     */
    public MainWindow() {
    }

    /**
     * Keeps the newest dialog visible whenever the conversation grows.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the application facade used to process commands and displays its greeting.
     *
     * @param eden Eden instance backing this window.
     */
    public void setEden(Eden eden) {
        this.eden = eden;
        dialogContainer.getChildren().add(
                DialogBox.getEdenDialog(
                        eden.getWelcomeMessage(), eden.isLastResponseError()));
    }

    /**
     * Displays the user's command and Eden's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.clear();
            return;
        }

        String response = eden.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getEdenDialog(response, eden.isLastResponseError()));
        userInput.clear();

        if (eden.isExit()) {
            Platform.exit();
        }
    }
}
