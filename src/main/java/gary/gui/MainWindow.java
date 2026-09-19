package gary.gui;

import gary.Gary;
import gary.ui.ChatResponse;
import gary.ui.ResponseType;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main Gary chatbot window.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1);
    private static final String WELCOME_MESSAGE = "Meow! I'm Gary.\nWhat can I do for you?";

    @FXML
    private VBox dialogContainer;
    @FXML
    private HBox inputBar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button sendButton;
    @FXML
    private TextField userInput;

    private Gary gary;

    @FXML
    private void initialize() {
        inputBar.addEventFilter(ScrollEvent.SCROLL, this::scrollConversation);
    }

    /**
     * Supplies the chatbot used to process input and shows its greeting.
     *
     * @param gary Chatbot to use.
     */
    public void setGary(Gary gary) {
        this.gary = gary;
        dialogContainer.getChildren().add(DialogBox.getGaryDialog(ChatResponse.normal(WELCOME_MESSAGE)));
        scrollToBottom();
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        ChatResponse response = gary.getChatResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getGaryDialog(response));
        userInput.clear();
        scrollToBottom();

        if (response.type() == ResponseType.FAREWELL) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    private void scrollConversation(ScrollEvent event) {
        double contentHeight = dialogContainer.getBoundsInLocal().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double scrollableHeight = contentHeight - viewportHeight;
        if (scrollableHeight <= 0) {
            return;
        }

        double valueRange = scrollPane.getVmax() - scrollPane.getVmin();
        double valueChange = -event.getDeltaY() * valueRange / scrollableHeight;
        double newValue = scrollPane.getVvalue() + valueChange;
        double clampedValue = Math.max(scrollPane.getVmin(), Math.min(scrollPane.getVmax(), newValue));
        scrollPane.setVvalue(clampedValue);
        event.consume();
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
    }
}
