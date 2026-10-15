package stephen.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import stephen.Chatbot;
import stephen.ChatbotResponse;

/** Controls Orbit's main chat window. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextArea userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Label feedbackLabel;

    private Chatbot chatbot;

    /** Configures keyboard input and automatic scrolling after FXML injection. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollToLatestMessage());
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleInputKey);
        userInput.textProperty().addListener((observable, oldText, newText) -> {
            if (!newText.isBlank()) {
                feedbackLabel.setText("");
            }
        });
        Platform.runLater(userInput::requestFocus);
    }

    /** Injects the chatbot and displays its startup message. */
    public void setChatbot(Chatbot chatbot) {
        this.chatbot = chatbot;
        dialogContainer.getChildren().add(DialogBox.getOrbitDialog(chatbot.getStartupMessage()));
    }

    /** Sends the current non-empty input through the chatbot. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            feedbackLabel.setText("Type a command before sending.");
            userInput.requestFocus();
            return;
        }

        ChatbotResponse response = chatbot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getOrbitDialog(response.message(), response.isError()));
        feedbackLabel.setText("");
        userInput.clear();
        userInput.requestFocus();
        scrollToLatestMessage();

        if (response.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            feedbackLabel.setText("Orbit signing off...");
            PauseTransition delay = new PauseTransition(Duration.millis(700));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    /** Sends on Enter while leaving Shift+Enter available for a new line. */
    private void handleInputKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
            event.consume();
            handleUserInput();
        }
    }

    /** Schedules scrolling after JavaFX has laid out newly added messages. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
