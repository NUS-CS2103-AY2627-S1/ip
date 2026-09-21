package thaisbot.gui;

import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Visual window for the chatbot interface.
 */
public class StudyPlannerWindow extends BorderPane {
    private static final String QUICK_TODO = "todo read a book #fun";
    private static final String QUICK_DEADLINE = "deadline submit project /by 2026-09-10 #work";
    private static final String QUICK_FIND = "find book";
    private static final String QUICK_LIST = "list";

    private final VBox messageBox = new VBox(12);
    private final ScrollPane scrollPane = new ScrollPane(messageBox);
    private final TextField inputField = new TextField();
    private final Consumer<String> commandHandler;

    /**
     * Creates the main chatbot window.
     * @param commandHandler handler called when the user submits a command
     */
    public StudyPlannerWindow(Consumer<String> commandHandler) {
        assert commandHandler != null;
        this.commandHandler = commandHandler;
        setPadding(new Insets(20));
        setTop(createHeader());
        setCenter(createChatPane());
        setBottom(createInputPane());
        getStyleClass().add("root-pane");
    }

    /**
     * Adds multiple bot messages to the conversation.
     * @param messages messages to display
     */
    public void appendMessages(String... messages) {
        assert messages != null;
        for (String message : messages) {
            appendMessage(message);
        }
    }

    /**
     * Adds multiple bot messages from a list to the conversation.
     * @param messages messages to display
     */
    public void appendMessages(List<String> messages) {
        assert messages != null;
        appendMessages(messages.toArray(new String[0]));
    }

    /**
     * Adds a single bot message to the conversation.
     * @param message message to display
     */
    public void appendBotMessage(String message) {
        messageBox.getChildren().add(createMessageRow(message, false));
        scrollToBottom();
    }

    /**
     * Adds a single user message to the conversation.
     * @param message message to display
     */
    public void appendUserMessage(String message) {
        messageBox.getChildren().add(createMessageRow(message, true));
        scrollToBottom();
    }

    private void appendMessage(String message) {
        if (message.startsWith("Oops — Thai's Bot got tangled up:")) {
            appendErrorMessage(message);
            return;
        }
        appendBotMessage(message);
    }

    private Node createHeader() {
        VBox header = new VBox(8);
        header.getStyleClass().addAll("header", "card");

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("\uD83E\uDD16\uD83C\uDF93");
        icon.getStyleClass().add("title-icon");

        VBox titleText = new VBox(2);
        Label title = new Label("Thai's Bot");
        title.getStyleClass().add("title");

        Label subtitle = new Label("A playful task companion with a bright academic vibe.");
        subtitle.getStyleClass().add("subtitle");
        subtitle.setWrapText(true);

        titleText.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label("Studio mode");
        status.getStyleClass().add("status-pill");

        titleRow.getChildren().addAll(icon, titleText, spacer, status);

        HBox quickActions = new HBox(8);
        quickActions.getStyleClass().add("quick-actions");
        quickActions.getChildren().addAll(
                createQuickActionButton("Add task", QUICK_TODO),
                createQuickActionButton("Add deadline", QUICK_DEADLINE),
                createQuickActionButton("Find task", QUICK_FIND),
                createQuickActionButton("Show list", QUICK_LIST)
        );

        header.getChildren().addAll(titleRow, quickActions);
        return header;
    }

    private Node createChatPane() {
        messageBox.setFillWidth(true);
        messageBox.setPadding(new Insets(18, 8, 18, 8));

        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().addAll("chat-scroll", "card");
        messageBox.heightProperty().addListener((observable, oldValue, newValue) -> scrollToBottom());

        return scrollPane;
    }

    private Node createInputPane() {
        HBox inputPane = new HBox(10);
        inputPane.getStyleClass().addAll("input-pane", "card");

        inputField.setPromptText("Try: todo read book #fun");
        inputField.getStyleClass().add("command-field");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setOnAction(event -> submitCommand());

        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(event -> submitCommand());

        inputPane.getChildren().addAll(inputField, sendButton);
        return inputPane;
    }

    private Button createQuickActionButton(String label, String command) {
        Button button = new Button(label);
        button.getStyleClass().add("quick-button");
        button.setOnAction(event -> {
            inputField.setText(command);
            inputField.requestFocus();
            inputField.positionCaret(command.length());
        });
        return button;
    }

    private void submitCommand() {
        String commandText = inputField.getText().trim();
        if (commandText.isEmpty()) {
            return;
        }
        inputField.clear();
        appendUserMessage(commandText);
        commandHandler.accept(commandText);
    }

    private Node createMessageRow(String message, boolean user) {
        HBox row = new HBox(10);
        row.setAlignment(user ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        Label avatar = new Label(user ? "\uD83D\uDE42" : "\uD83E\uDD16\uD83C\uDF93");
        avatar.getStyleClass().add(user ? "user-avatar" : "bot-avatar");

        Label bubble = new Label(message);
        bubble.getStyleClass().add(user ? "user-bubble" : "bot-bubble");
        if (message.startsWith("Oops — Thai's Bot got tangled up:")) {
            bubble.getStyleClass().add("error-bubble");
        }
        bubble.setWrapText(true);
        bubble.setMaxWidth(560);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (user) {
            row.getChildren().addAll(spacer, bubble, avatar);
        } else {
            row.getChildren().addAll(avatar, bubble, spacer);
        }
        return row;
    }

    private void appendErrorMessage(String message) {
        appendBotMessage(message);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
