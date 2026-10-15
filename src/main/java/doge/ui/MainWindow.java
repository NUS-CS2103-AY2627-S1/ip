package doge.ui;

import doge.Doge;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    private Doge doge;

    /** Initializes automatic scrolling for the conversation area. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the Doge instance used to process user commands.
     *
     * @param doge application logic used by this window.
     */
    public void setDoge(Doge doge) {
        this.doge = doge;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Doge's reply, then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = doge.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getDogeDialog(response, isErrorResponse(response))
        );
        userInput.clear();

        if (input.trim().equals("bye")) {
            closeWindow();
        }
    }

    /** Closes the JavaFX window after the bye command has saved the current tasks. */
    private void closeWindow() {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        stage.close();
    }

    /** Returns whether Doge's response is a validation or command error. */
    private boolean isErrorResponse(String response) {
        String lowerCaseResponse = response.toLowerCase();
        return lowerCaseResponse.startsWith("oops") || lowerCaseResponse.startsWith("error");
    }
}
