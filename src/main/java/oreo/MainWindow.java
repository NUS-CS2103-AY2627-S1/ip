package oreo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Controls the conversation display and input area in Oreo's main window. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Oreo oreo;

    /** Connects the controller to the chatbot and shows its greeting. */
    public void setOreo(Oreo oreo) {
        this.oreo = oreo;
        addDialog(oreo.getWelcomeMessage(), false, false);
        Platform.runLater(() -> userInput.requestFocus());
    }

    /** Sends the command in the input field when the user presses Enter or Send. */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty() || oreo == null) {
            return;
        }

        addDialog(command, true, false);
        userInput.clear();
        String response = oreo.getResponse(command);
        addDialog(response, false, oreo.wasLastResponseAnError());

        if (oreo.hasExited()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Adds a responsive conversation item and scrolls the conversation to it. */
    private void addDialog(String message, boolean isUserMessage, boolean isErrorMessage) {
        Label dialog = new Label(message);
        dialog.setWrapText(true);
        dialog.setMaxWidth(Double.MAX_VALUE);
        dialog.getStyleClass().addAll("dialog", isUserMessage ? "user-message" : "bot-message");
        HBox row = new HBox(dialog);
        row.getStyleClass().addAll("dialog-row", isUserMessage ? "user-row" : "bot-row");
        row.setAlignment(isUserMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        if (isUserMessage) {
            dialog.maxWidthProperty().bind(dialogContainer.widthProperty().multiply(0.78));
        } else {
            HBox.setHgrow(dialog, Priority.ALWAYS);
        }
        if (isErrorMessage) {
            dialog.getStyleClass().add("error-message");
        }
        if (message.startsWith("Here are the tags and their tasks:")) {
            dialog.getStyleClass().add("tag-summary");
        }
        dialogContainer.getChildren().add(row);
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
