package stewie.ui.gui;

import java.util.List;
import java.util.function.Consumer;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import stewie.model.TaskList;
import stewie.parser.Parser;
import stewie.storage.StorageException;
import stewie.ui.Dialogue;

/**
 * Displays the conversation, composer, and interactive chat cards.
 */
final class ChatPanel extends VBox {
    private static final String[] QUICK_COMMANDS = {"todo plan my week", "list", "help"};
    private final TaskList taskList;
    private final GuiCommandHandler commandHandler;
    private final Runnable onTasksChanged;
    private final Consumer<StorageException> onStorageError;
    private final StackPane avatar;
    private final VBox conversation = new VBox(18);
    private final ScrollPane conversationScroll = createConversationScroll();
    private final TextField messageField = new TextField();
    private Timeline scrollAnimation;

    /**
     * Creates a chat view with its portrait and callbacks for changes and save errors.
     */
    ChatPanel(TaskList taskList, StackPane avatar, Runnable onTasksChanged,
            Consumer<StorageException> onStorageError) {
        this.taskList = taskList;
        this.commandHandler = new GuiCommandHandler(taskList);
        this.avatar = avatar;
        this.onTasksChanged = onTasksChanged;
        this.onStorageError = onStorageError;
        initializeLayout();
        addWelcomeMessage();
        if (!taskList.getLoadWarning().isEmpty()) {
            appendMessage(false, taskList.getLoadWarning());
        }
    }

    /**
     * Creates the central chat panel with its header, conversation, and composer.
     */
    private void initializeLayout() {
        getStyleClass().add("chat-panel");
        HBox header = createChatHeader();
        VBox.setVgrow(conversationScroll, Priority.ALWAYS);
        VBox composer = createComposer();
        getChildren().addAll(header, conversationScroll, composer);
    }

    /**
     * Creates the chat heading with the assistant's identity and a right-aligned section hint.
     */
    private HBox createChatHeader() {
        HBox header = new HBox(14);
        header.getStyleClass().add("chat-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox identity = createAssistantIdentity();
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Label headerHint = new Label("TASKS / CHAT");
        headerHint.getStyleClass().add("header-hint");
        header.getChildren().addAll(avatar, identity, headerSpacer, headerHint);
        return header;
    }

    /**
     * Creates the assistant's display name and online status for the chat header.
     */
    private VBox createAssistantIdentity() {
        VBox identity = new VBox(3);
        Label title = new Label("Stewie Assistant");
        title.getStyleClass().add("chat-title");
        HBox status = new HBox(6);
        status.setAlignment(Pos.CENTER_LEFT);
        Circle onlineDot = new Circle(4, Color.web("#5de39b"));
        Label statusText = new Label("online · awaiting your agenda");
        statusText.getStyleClass().add("muted-label");
        status.getChildren().addAll(onlineDot, statusText);
        identity.getChildren().addAll(title, status);
        return identity;
    }

    /**
     * Creates the scrollable message area.
     *
     * @return A scroll pane containing the conversation.
     */
    private ScrollPane createConversationScroll() {
        conversation.getStyleClass().add("conversation");
        conversation.setPadding(new Insets(28, 48, 28, 48));

        ScrollPane scrollPane = new ScrollPane(conversation);
        scrollPane.getStyleClass().add("conversation-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    /**
     * Creates the command composer at the bottom of the chat panel.
     *
     * @return The composer controls.
     */
    private VBox createComposer() {
        VBox composer = new VBox(12);
        composer.getStyleClass().add("composer");
        composer.getChildren().addAll(createQuickCommands(), createInputRow());
        return composer;
    }

    /**
     * Creates command suggestions that send immediately and restore focus to the message field.
     */
    private HBox createQuickCommands() {
        HBox quickCommands = new HBox(8);
        quickCommands.setAlignment(Pos.CENTER_LEFT);
        for (String command : QUICK_COMMANDS) {
            Button chip = new Button(command);
            chip.getStyleClass().add("quick-chip");
            chip.setOnAction(event -> {
                messageField.setText(command);
                sendMessage();
                messageField.requestFocus();
            });
            quickCommands.getChildren().add(chip);
        }
        return quickCommands;
    }

    /**
     * Creates the message entry row with matching Enter-key and Send-button actions.
     */
    private HBox createInputRow() {
        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        messageField.setPromptText("Your next task, if you please...");
        messageField.getStyleClass().add("message-field");
        messageField.setOnAction(event -> sendMessage());
        HBox.setHgrow(messageField, Priority.ALWAYS);

        Button sendButton = new Button("➤");
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(event -> sendMessage());
        inputRow.getChildren().addAll(messageField, sendButton);
        return inputRow;
    }

    /**
     * Adds Stewie's initial greeting and command suggestions.
     */
    private void addWelcomeMessage() {
        appendMessage(false, Dialogue.GREETING);
        appendMessage(false, "We shall start simply: `todo buy groceries`, `list`, or `find groceries`.");
    }

    /**
     * Sends the text currently in the composer to the task command handler.
     */
    private void sendMessage() {
        String rawInput = messageField.getText().trim();
        if (rawInput.isEmpty()) {
            return;
        }

        appendMessage(true, rawInput);
        messageField.clear();
        GuiCommandResult result = commandHandler.handle(Parser.normalize(rawInput));
        for (String message : result.messages()) {
            appendMessage(false, message);
        }
        appendTaskCards(result.tasks(), result.isInteractive());
        onTasksChanged.run();
        scrollToBottom();
    }

    /**
     * Displays every current task as an interactive card.
     *
     * @param response The assistant message shown above the cards.
     */
    private void showTaskList(String response) {
        appendMessage(false, response);
        String[] tasks = taskList.produceTaskList();
        if (tasks.length == 0) {
            appendMessage(false, Dialogue.EMPTY);
            return;
        }

        appendTaskCards(List.of(tasks), true);
    }

    /**
     * Creates one task card for the conversation.
     *
     * @param index The one-based task number.
     * @param taskText The task text from the task list.
     * @param isInteractive Whether the card should expose task actions.
     * @return The task card.
     */
    private HBox createTaskCard(int index, String taskText, boolean isInteractive) {
        HBox card = TaskCardFactory.create(index, taskText);
        if (isInteractive) {
            addChatTaskControls(card, index, TaskCardFactory.isDone(taskText));
        }
        return card;
    }

    /**
     * Adds actions that share the revision captured when their card was created.
     */
    private void addChatTaskControls(HBox card, int index, boolean isDone) {
        long cardRevision = taskList.getRevision();
        card.getChildren().addAll(createChatStatusCheckbox(index, isDone, cardRevision),
                createChatDeleteButton(index, cardRevision));
    }

    /**
     * Creates the completion control for a chat task card.
     */
    private CheckBox createChatStatusCheckbox(int index, boolean isDone, long cardRevision) {
        CheckBox doneBox = new CheckBox();
        doneBox.setSelected(isDone);
        doneBox.getStyleClass().add("task-check");
        doneBox.setOnAction(event -> handleChatStatusChange(index, isDone, cardRevision, doneBox));
        return doneBox;
    }

    /**
     * Applies a chat status change, restoring the old checkbox when the card is stale or saving fails.
     */
    private void handleChatStatusChange(int index, boolean isDone, long cardRevision, CheckBox doneBox) {
        if (!isCurrentCard(cardRevision)) {
            doneBox.setSelected(isDone);
            return;
        }
        try {
            changeTaskStatus(index - 1, doneBox.isSelected());
        } catch (StorageException exception) {
            doneBox.setSelected(isDone);
            onStorageError.accept(exception);
            return;
        }
        onTasksChanged.run();
        showTaskList("Status revised. Our little operation advances:");
        scrollToBottom();
    }

    /**
     * Saves a completion state using a zero-based task index.
     */
    private void changeTaskStatus(int index, boolean isDone) {
        if (isDone) {
            taskList.markAsDone(index);
        } else {
            taskList.markAsUndone(index);
        }
    }

    /**
     * Creates a delete control for the revision shown in a chat card.
     */
    private Button createChatDeleteButton(int index, long cardRevision) {
        Button deleteButton = new Button("×");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(event -> handleChatDelete(index, cardRevision));
        return deleteButton;
    }

    /**
     * Deletes a task only while the card still identifies the current task list.
     */
    private void handleChatDelete(int index, long cardRevision) {
        if (!isCurrentCard(cardRevision)) {
            return;
        }
        try {
            taskList.deleteTask(index - 1);
        } catch (StorageException exception) {
            onStorageError.accept(exception);
            return;
        }
        onTasksChanged.run();
        showTaskList("Dismissed from the agenda. Here is what remains:");
    }

    /**
     * Adds a message bubble to the conversation.
     *
     * @param isUser Whether the message came from the user.
     * @param text The message text.
     */
    private void appendMessage(boolean isUser, String text) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(570);
        message.getStyleClass().add("message-bubble");
        message.getStyleClass().add(isUser ? "user-bubble" : "assistant-bubble");

        HBox row = new HBox(message);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getStyleClass().add("message-row");
        conversation.getChildren().add(row);
    }

    /**
     * Rejects stale chat controls before their old task numbers can affect another task.
     */
    private boolean isCurrentCard(long cardRevision) {
        if (cardRevision == taskList.getRevision()) {
            return true;
        }
        showTaskList("That task card is out of date. Use the refreshed list below.");
        scrollToBottom();
        return false;
    }

    /**
     * Smoothly scrolls the conversation to its newest content after measuring new cards.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> {
            if (scrollAnimation != null) {
                scrollAnimation.stop();
            }
            // Measure newly added cards before scrolling to the updated bottom edge.
            applyCss();
            layout();
            scrollAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(conversationScroll.vvalueProperty(), conversationScroll.getVvalue())),
                    new KeyFrame(Duration.millis(450),
                            new KeyValue(conversationScroll.vvalueProperty(), conversationScroll.getVmax(),
                                    Interpolator.EASE_BOTH)));
            scrollAnimation.play();
        });
    }

    /**
     * Appends a group only when the response contains tasks, preserving search cards without actions.
     */
    private void appendTaskCards(List<String> tasks, boolean isInteractive) {
        if (tasks.isEmpty()) {
            return;
        }
        VBox taskGroup = new VBox(8);
        taskGroup.getStyleClass().add("task-group");
        for (int index = 0; index < tasks.size(); index++) {
            taskGroup.getChildren().add(createTaskCard(index + 1, tasks.get(index), isInteractive));
        }
        conversation.getChildren().add(taskGroup);
    }
}
