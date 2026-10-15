package trybot.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import trybot.TryBot;
import trybot.ui.ChatReply;

/**
 * Keeps the conversation, command suggestions, and input controls in sync.
 */
public class MainWindow {
    private static final Duration GOODBYE_DELAY = Duration.seconds(5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private FlowPane suggestions;
    @FXML
    private Label inputHint;

    private TryBot tryBot;
    private final Runnable exitAction;

    /** Creates a controller that closes the JavaFX application after its farewell. */
    public MainWindow() {
        this(Platform::exit);
    }

    /** Allows tests to observe shutdown without terminating their JavaFX runtime. */
    MainWindow(Runnable exitAction) {
        this.exitAction = exitAction;
    }

    /**
     * Connects the window to the application logic and welcomes the user.
     *
     * @param tryBot application that handles commands.
     */
    public void setTryBot(TryBot tryBot) {
        this.tryBot = tryBot;
        addBotMessage(new ChatReply("Hi, I'm TryBot! Let's make a little room for what matters.\n\n"
                + "What's one thing on your to-do list? Try: todo read a chapter\n\n"
                + "You can add dates and tags as we go. Choose My list to pick up where you left off.",
                false, false, false));
        Platform.runLater(userInput::requestFocus);
    }

    @FXML
    private void initialize() {
        sendButton.disableProperty().bind(userInput.textProperty().isEmpty().or(userInput.disabledProperty()));
    }

    /** Sends one command, retaining invalid input so the user can correct it. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || tryBot == null || userInput.isDisabled()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        ChatReply reply = tryBot.getChatReply(input);
        addBotMessage(reply);
        if (reply.hasError()) {
            inputHint.setText("Edit your command and try again. I'm here to help.");
            userInput.selectAll();
        } else {
            userInput.clear();
            inputHint.setText("Enter to send · Add a task, date, or tag at your own pace");
        }
        userInput.requestFocus();

        if (reply.isExit()) {
            userInput.setDisable(true);
            suggestions.setDisable(true);
            inputHint.setText("Closing in 5 seconds. See you soon!");
            // A JavaFX timer leaves the farewell visible without freezing the window.
            PauseTransition goodbyePause = new PauseTransition(GOODBYE_DELAY);
            goodbyePause.setOnFinished(event -> exitAction.run());
            goodbyePause.play();
        }
    }

    @FXML
    private void handleAddTask() {
        if (userInput.getText().isBlank()) {
            userInput.setText("todo ");
        }
        userInput.requestFocus();
        userInput.positionCaret(userInput.getLength());
    }

    @FXML
    private void handleList() {
        sendSuggestion("list");
    }

    @FXML
    private void handleHelp() {
        sendSuggestion("help");
    }

    /** Runs a read-only suggestion while preserving the command being drafted. */
    private void sendSuggestion(String command) {
        if (tryBot == null) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(command));
        addBotMessage(tryBot.getChatReply(command));
        userInput.requestFocus();
    }

    private void addBotMessage(ChatReply reply) {
        dialogContainer.getChildren().add(DialogBox.getTryBotDialog(reply));
        // Scroll only after a new reply, leaving older messages freely scrollable between commands.
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }
}
