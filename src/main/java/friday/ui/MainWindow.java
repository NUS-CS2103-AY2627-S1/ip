package friday.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import friday.Friday;

/** Coordinates the command composer and scrollable conversation. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Label inputHint;
    private Friday friday;

    /** Focuses the composer after FXML has attached it to the scene. */
    @FXML
    public void initialize() {
        Platform.runLater(() -> userInput.requestFocus());
    }

    /** Injects the command engine and displays its welcome and any storage warning. */
    public void setFriday(Friday friday) {
        this.friday = friday;
        dialogContainer.getChildren().add(DialogBox.getFridayDialog(friday.getWelcomeResponse()));
        scrollToLatest();
    }

    /** Submits one command and keeps keyboard interaction available until the session ends. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || friday.hasExited()) {
            return;
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getFridayDialog(friday.getCommandResponse(input)));
        userInput.clear();
        if (friday.hasExited()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            inputHint.setText("Session ended · Reopen FRIDAY to continue");
        } else {
            userInput.requestFocus();
        }
        scrollToLatest();
    }

    /** Scrolls after layout, without binding the value or locking manual history navigation. */
    private void scrollToLatest() {
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }
}
