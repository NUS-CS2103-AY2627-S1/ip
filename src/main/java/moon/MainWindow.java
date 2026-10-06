package moon;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Controls Moon's main JavaFX window and forwards user commands to the engine. */
public class MainWindow {
    @FXML
    private VBox dialogContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private MoonEngine moon;

    /** Connects this controller to Moon's command engine. */
    public void setMoon(MoonEngine moon) {
        this.moon = moon;
        addDialog("Yo, I'm Moon 🌙 — your task sidekick.\n"
                + "Let's get this list sorted. What are we doing?", false);
        if (moon.hasLoadingError()) {
            addDialog("Oof! I couldn't load your task list. Starting fresh for now.", false);
        }
    }

    /** Sends the command currently entered in the text field. */
    @FXML
    private void handleUserInput() {
        assert moon != null : "The main window must be connected to Moon before use.";
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        addDialog(command, true);
        addDialog(moon.getResponse(command).strip(), false);
        if (command.equals("bye")) {
            Platform.exit();
            return;
        }
        userInput.clear();
        userInput.requestFocus();
    }

    private void addDialog(String message, boolean isUserMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            DialogBox dialogBox = new DialogBox(message, isUserMessage);
            loader.setRoot(dialogBox);
            loader.setController(dialogBox);
            dialogContainer.getChildren().add(loader.load());
            dialogBox.setAlignment(isUserMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            scrollToBottom();
        } catch (IOException exception) {
            addFallbackDialog(message, isUserMessage);
        }
    }

    private void addFallbackDialog(String message, boolean isUserMessage) {
        Label fallback = new Label(message);
        fallback.setWrapText(true);
        fallback.getStyleClass().addAll("message-bubble", isUserMessage ? "user-message" : "moon-message");
        if (message.startsWith("Oof!")) {
            fallback.getStyleClass().add("error-message");
        }
        dialogContainer.getChildren().add(fallback);
        scrollToBottom();
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
