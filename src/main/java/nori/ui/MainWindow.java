package nori.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nori.Nori;

/**
 * Controls the conversation shown in Nori's main window.
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

    private Nori nori;

    /**
     * Connects the window to Nori and displays the greeting.
     *
     * @param nori Chatbot that processes commands.
     */
    public void setNori(Nori nori) {
        this.nori = nori;
        dialogContainer.getChildren().add(DialogBox.getNoriDialog(nori.getWelcomeMessage()));
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Sends the current text to Nori and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = nori.getResponse(input);
        DialogBox noriDialog = nori.wasLastResponseError()
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getNoriDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                noriDialog);
        userInput.clear();

        if (nori.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
