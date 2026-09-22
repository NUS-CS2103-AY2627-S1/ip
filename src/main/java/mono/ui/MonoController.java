package mono.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mono.MonoBot;
import mono.exception.MonoException;
import mono.parser.InputParser;
import mono.tool.Tool;
import mono.tool.ToolRegistry;
import mono.tool.ToolSignal;

/**
 * Connects Mono's command tools to the JavaFX conversation window.
 */
public class MonoController {
    @FXML
    private VBox conversationBox;

    @FXML
    private ScrollPane conversationScroll;

    @FXML
    private TextField commandField;

    @FXML
    private Button sendButton;

    @FXML
    private Label statusLabel;

    private MonoBot bot;
    private ToolRegistry registry;

    /**
     * Creates the chatbot state and displays the initial greeting after FXML injection.
     */
    @FXML
    private void initialize() {
        assert this.conversationBox != null
                && this.conversationScroll != null
                && this.commandField != null
                && this.sendButton != null
                && this.statusLabel != null
                : "FXML must inject all controls before initialization";
        this.registry = new ToolRegistry();
        this.bot = new MonoBot(new ConversationUi(this::appendBotMessage));
        this.conversationBox.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollToLatestMessage());
        this.bot.greet();
        this.commandField.requestFocus();
    }

    /** Executes the command entered in the input field. */
    @FXML
    private void handleSend() {
        String command = this.commandField.getText();
        appendUserMessage(command);
        this.commandField.clear();

        try {
            InputParser.ToolInput input = InputParser.parse(command);
            Tool tool = this.registry.get(input.name());
            assert tool != null : "The registry must return a tool for a known command";
            ToolSignal signal = tool.invoke(input.arguments(), this.bot);
            assert signal != null : "Every tool invocation must return a control signal";
            if (signal == ToolSignal.EXIT) {
                endSession();
            }
        } catch (MonoException exception) {
            appendBotMessage("Error: " + exception.getMessage());
        }
    }

    /**
     * Places a suggested command in the input field without executing it.
     *
     * @param event action event from a command suggestion button
     */
    @FXML
    private void handleCommandSuggestion(ActionEvent event) {
        assert event != null && event.getSource() instanceof Button
                : "Command suggestions must originate from a button";
        Button source = (Button) event.getSource();
        this.commandField.setText(source.getText());
        this.commandField.requestFocus();
        this.commandField.positionCaret(this.commandField.getText().length());
    }

    /** Adds a user command to the conversation. */
    private void appendUserMessage(String message) {
        appendMessage("You", message.isBlank() ? " " : message, true);
    }

    /** Adds a response from Mono to the conversation. */
    private void appendBotMessage(String message) {
        if (Platform.isFxApplicationThread()) {
            appendMessage("Mono", message, false);
        } else {
            Platform.runLater(() -> appendMessage("Mono", message, false));
        }
    }

    /** Adds a styled message row to the conversation view. */
    private void appendMessage(String author, String message, boolean fromUser) {
        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add("message-author");

        Label messageLabel = new Label(message);
        // Keep multiline fixed-width content, such as the greeting banner, aligned.
        messageLabel.setWrapText(!message.contains("\n"));
        messageLabel.getStyleClass().add("message-text");

        VBox bubble = new VBox(4, authorLabel, messageLabel);
        bubble.getStyleClass().add("message-bubble");
        bubble.getStyleClass().add(fromUser ? "user-bubble" : "bot-bubble");
        bubble.setMaxWidth(680);

        HBox row = new HBox(bubble);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getStyleClass().add("message-row");
        HBox.setHgrow(bubble, Priority.NEVER);

        this.conversationBox.getChildren().add(row);
        scrollToLatestMessage();
    }

    /** Scrolls to the newest message after JavaFX has laid out the row. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> this.conversationScroll.setVvalue(1.0));
    }

    /** Disables command entry after the user says goodbye. */
    private void endSession() {
        this.commandField.setDisable(true);
        this.sendButton.setDisable(true);
        this.statusLabel.setText("Session ended • close the window when you are ready");
    }
}
