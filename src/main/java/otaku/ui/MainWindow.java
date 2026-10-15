package otaku.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import otaku.Otaku;

/** Controls the main chat window defined in {@code MainWindow.fxml}. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Otaku otaku;

    /** Keeps the latest message visible as the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /** Supplies the chatbot and displays its initial greeting. */
    public void setOtaku(Otaku otaku) {
        this.otaku = otaku;
        dialogContainer.getChildren().add(DialogBox.getOtakuGreetingDialog(otaku.getGreeting()));
        Platform.runLater(userInput::requestFocus);
    }

    /** Sends non-blank input from either Enter or the Send button to Otaku. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || otaku == null) {
            return;
        }
        Otaku.CommandResponse response = otaku.getCommandResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input), DialogBox.getOtakuDialog(response.message(), response.isError()));
        userInput.clear();
        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
