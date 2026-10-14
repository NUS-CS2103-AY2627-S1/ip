package milo.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import milo.Milo;

/**
 * Controls Milo's main chat window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Milo milo;

    /** Keeps the newest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Milo instance that processes commands.
     *
     * @param milo Milo instance shared by this window.
     */
    public void setMilo(Milo milo) {
        this.milo = milo;
        dialogContainer.getChildren().add(
                DialogBox.getMiloDialog("Hi, I'm Milo. What do you need to do?"));
    }

    /** Processes input submitted using either the text field or Send button. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        String response = milo.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        if (response.startsWith("OOPS!!! ")) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(response));
        } else {
            dialogContainer.getChildren().add(DialogBox.getMiloDialog(response));
        }
        userInput.clear();
        if (input.equals("bye")) {
            Platform.exit();
        }
    }
}
