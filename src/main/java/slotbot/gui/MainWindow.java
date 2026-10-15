package slotbot.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import slotbot.BotResponse;
import slotbot.SlotBot;

/**
 * Presents the conversation and forwards submitted commands to SlotBot.
 */
public class MainWindow {
    private static final double SCROLL_POSITION_TOLERANCE = 0.001;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private SlotBot bot;
    private Runnable closeWindow;
    private boolean isPinnedToBottom = true;

    /**
     * Keeps a conversation at the bottom when wrapping changes its height.
     */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) ->
                isPinnedToBottom = isScrolledToBottom());
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            if (isPinnedToBottom) {
                scrollToLatestMessage();
            }
        });
    }

    /**
     * Connects the chatbot and displays its greeting and startup warnings.
     *
     * @param bot Chatbot for this conversation.
     * @param closeWindow Action that closes the window after goodbye.
     */
    public void setBot(SlotBot bot, Runnable closeWindow) {
        this.bot = bot;
        this.closeWindow = closeWindow;

        addMessage(bot.getWelcome(), false, false);
    }

    /**
     * Places keyboard focus in the command field.
     */
    public void focusInput() {
        userInput.requestFocus();
    }

    /**
     * Submits one nonblank command and displays its response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank() || bot.isExiting()) {
            userInput.clear();
            focusInput();
            return;
        }

        addMessage(input, true, false);
        BotResponse botResponse = bot.getResponseDetails(input);
        addMessage(botResponse.text(), false, botResponse.isError());
        userInput.clear();

        if (bot.isExiting()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition farewell = new PauseTransition(Duration.seconds(1));
            farewell.setOnFinished(event -> closeWindow.run());
            farewell.play();
        } else {
            focusInput();
        }
    }

    /**
     * Adds a wrapping message bubble and scrolls to the latest response.
     *
     * @param text Message to display.
     * @param isUser Whether the user sent this message.
     * @param isError Whether the message reports a command error.
     */
    private void addMessage(String text, boolean isUser, boolean isError) {
        String displayedText = isUser ? text : text.replaceAll("(?m)^_{60}\\R?", "").strip();
        dialogContainer.getChildren().add(new MessageBubble(displayedText, isUser, isError));

        isPinnedToBottom = true;
        scrollToLatestMessage();
    }

    /**
     * Scrolls to the latest message after JavaFX finishes laying out the view.
     */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            // Finish the viewport and content layout before calculating the bottom position.
            Parent root = scrollPane.getScene().getRoot();
            root.applyCss();
            root.layout();
            scrollPane.setVvalue(scrollPane.getVmax());
        });
    }

    /**
     * Returns whether the conversation is at or close to its bottom edge.
     */
    private boolean isScrolledToBottom() {
        double distanceFromBottom = scrollPane.getVmax() - scrollPane.getVvalue();
        return distanceFromBottom <= SCROLL_POSITION_TOLERANCE;
    }
}
