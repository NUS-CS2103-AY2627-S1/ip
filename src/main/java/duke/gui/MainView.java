package duke.gui;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Displays the window layout, command composer, and conversation messages.
 *
 * <p>Callbacks pass user actions to {@link Main}, which handles commands and task storage.</p>
 */
final class MainView extends BorderPane {
    private final Runnable onSubmit;

    private final Consumer<KeyCode> onNavigateHistory;

    private final Consumer<String> onSelectSuggestion;

    private VBox messageList;

    private ScrollPane chatScrollPane;

    private TextField commandField;

    private Label statusText;

    private Label taskStats;

    /**
     * Creates the window controls with callbacks for command submission, history, and shortcuts.
     */
    MainView(Runnable onSubmit, Consumer<KeyCode> onNavigateHistory, Consumer<String> onSelectSuggestion) {
        this.onSubmit = onSubmit;
        this.onNavigateHistory = onNavigateHistory;
        this.onSelectSuggestion = onSelectSuggestion;
        getStyleClass().add("app-shell");
        setTop(createHeader());
        setCenter(createChatPanel());
        setBottom(createComposer());
    }

    /**
     * Creates the compact application header.
     */
    private Node createHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("top-bar");

        QuestIcon emblem = new QuestIcon(34, false);
        emblem.getStyleClass().add("brand-mark");

        Label appName = new Label("WANGSA");
        appName.getStyleClass().add("brand-name");
        Label appSubtitle = new Label("YOUR TASK COMPANION");
        appSubtitle.getStyleClass().add("brand-subtitle");
        VBox brand = new VBox(1, appName, appSubtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(emblem, brand, spacer, createStatusPanel());
        return header;
    }

    /**
     * Aligns the status indicator with its heading and both text lines in a shared column.
     */
    private Node createStatusPanel() {
        Circle statusDot = new Circle(4, Color.web("#8ee7bd"));
        statusDot.getStyleClass().add("status-dot");
        statusText = new Label("SYSTEM READY");
        statusText.getStyleClass().add("status-text");
        taskStats = new Label("0 TASKS  ·  0 DONE");
        taskStats.getStyleClass().add("task-stats");
        GridPane status = new GridPane();
        status.setHgap(8);
        status.setVgap(3);
        status.setAlignment(Pos.CENTER_RIGHT);
        status.add(statusDot, 0, 0);
        status.add(statusText, 1, 0);
        status.add(taskStats, 1, 1);
        return status;
    }

    /**
     * Creates the conversation panel from its heading and scrollable message list.
     */
    private Node createChatPanel() {
        VBox chatPanel = new VBox(12);
        chatPanel.getStyleClass().add("chat-panel");
        chatPanel.setPadding(new Insets(14, 24, 8, 24));
        chatPanel.getChildren().addAll(createChatHeading(), createChatScrollPane());
        return chatPanel;
    }

    /**
     * Creates the conversation heading and live indicator.
     */
    private Node createChatHeading() {
        HBox headingRow = new HBox(8);
        headingRow.setAlignment(Pos.CENTER_LEFT);
        Label heading = new Label("CHAT");
        heading.getStyleClass().add("section-kicker");
        Label divider = new Label("/");
        divider.getStyleClass().add("heading-divider");
        Label headingHint = new Label("One task at a time");
        headingHint.getStyleClass().add("heading-hint");
        Region headingSpacer = new Region();
        HBox.setHgrow(headingSpacer, Priority.ALWAYS);
        Label liveBadge = new Label("●  LIVE");
        liveBadge.getStyleClass().add("live-badge");
        headingRow.getChildren().addAll(heading, divider, headingHint, headingSpacer, liveBadge);

        return headingRow;
    }

    /**
     * Creates a message list that fills the conversation viewport.
     */
    private ScrollPane createChatScrollPane() {
        messageList = new VBox(12);
        messageList.setId("messages");
        messageList.setMaxWidth(Double.MAX_VALUE);
        messageList.setPadding(new Insets(8, 0, 12, 0));
        messageList.setFillWidth(true);

        chatScrollPane = new ScrollPane(messageList);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.getStyleClass().add("chat-scroll");
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);

        return chatScrollPane;
    }

    /**
     * Creates the command composer at the bottom of the window.
     */
    private Node createComposer() {
        VBox composer = new VBox(9);
        composer.getStyleClass().add("composer");
        composer.getChildren().addAll(createCheatsheet(), createComposerRow(), createSuggestions());
        return composer;
    }

    /**
     * Creates the input field and send action on a shared row.
     */
    private Node createComposerRow() {
        HBox composerRow = new HBox(10);
        composerRow.setAlignment(Pos.CENTER_LEFT);

        commandField = createCommandField();
        HBox.setHgrow(commandField, Priority.ALWAYS);
        composerRow.getChildren().addAll(commandField, createSendButton());
        return composerRow;
    }

    /**
     * Creates the command field with submission and history keyboard actions.
     */
    private TextField createCommandField() {
        TextField field = new TextField();
        field.setPromptText("Type a command, then press Enter");
        field.setAccessibleText("Wangsa command input");
        field.setMinWidth(0);
        field.setMinHeight(48);
        field.setPrefHeight(48);
        field.setMaxHeight(48);
        field.getStyleClass().add("command-field");
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSubmit.run();
                event.consume();
            } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                onNavigateHistory.accept(event.getCode());
                event.consume();
            }
        });
        return field;
    }

    /**
     * Creates the default action for submitting a command.
     */
    private Button createSendButton() {
        Button sendButton = new Button("SEND  ↗");
        sendButton.setMinWidth(92);
        sendButton.setMinHeight(48);
        sendButton.setPrefHeight(48);
        sendButton.setMaxHeight(48);
        sendButton.getStyleClass().add("send-button");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> onSubmit.run());
        return sendButton;
    }

    /**
     * Creates command shortcuts and the keyboard reminder.
     */
    private Node createSuggestions() {
        HBox suggestions = new HBox(7);
        suggestions.setAlignment(Pos.CENTER_LEFT);
        Label suggestionLabel = new Label("TRY");
        suggestionLabel.getStyleClass().add("try-label");
        suggestions.getChildren().addAll(
                suggestionLabel,
                createSuggestion("list", "list"),
                createSuggestion("new todo", "todo "),
                createSuggestion("find", "find "),
                createSuggestion("mark #", "mark "),
                createSuggestion("ask AI", "@ai "));
        Region suggestionSpacer = new Region();
        HBox.setHgrow(suggestionSpacer, Priority.ALWAYS);
        Label keyboardHint = new Label("ENTER TO SEND");
        keyboardHint.getStyleClass().add("keyboard-hint");
        suggestions.getChildren().addAll(suggestionSpacer, keyboardHint);

        return suggestions;
    }

    /**
     * Creates the compact command reference above the composer.
     */
    private Node createCheatsheet() {
        GridPane cheatsheet = new GridPane();
        cheatsheet.setHgap(10);
        cheatsheet.setVgap(3);
        cheatsheet.getStyleClass().add("cheatsheet");
        ColumnConstraints category = new ColumnConstraints(48);
        ColumnConstraints examples = new ColumnConstraints();
        examples.setMinWidth(0);
        examples.setHgrow(Priority.ALWAYS);
        cheatsheet.getColumnConstraints().addAll(category, examples);
        addCheatsheetRow(cheatsheet, 0, "ADD", "todo <description>  ·  deadline <description> /by <yyyy-MM-dd>");
        addCheatsheetRow(cheatsheet, 1, "EVENT", "event <description> /from <start> /to <end>");
        addCheatsheetRow(cheatsheet, 2, "TASKS",
                "list  ·  find <keyword>  ·  sort  ·  mark <#>  ·  unmark <#>  ·  delete <#>");
        addCheatsheetRow(cheatsheet, 3, "HELP", "help  ·  @ai <question>  ·  bye");
        return cheatsheet;
    }

    /**
     * Aligns command groups in real columns so wrapped text keeps its indentation.
     */
    private void addCheatsheetRow(GridPane grid, int row, String category, String examples) {
        Label heading = new Label(category);
        heading.getStyleClass().add("cheatsheet-title");
        Label commands = new Label(examples);
        commands.setMinWidth(0);
        commands.setMaxWidth(Double.MAX_VALUE);
        commands.setWrapText(true);
        commands.getStyleClass().add("cheatsheet-text");
        grid.addRow(row, heading, commands);
    }

    /**
     * Creates a small command suggestion button.
     */
    private Button createSuggestion(String label, String command) {
        Button button = new Button(label);
        button.getStyleClass().add("suggestion-chip");
        button.setOnAction(event -> onSelectSuggestion.accept(command));
        return button;
    }

    /**
     * Returns the command as entered, including leading and trailing spaces.
     */
    String getCommand() {
        return commandField.getText();
    }

    /**
     * Clears the composer after a command is submitted.
     */
    void clearCommand() {
        commandField.clear();
    }

    /**
     * Places a command in the composer and moves the caret to its end.
     */
    void setCommand(String command) {
        commandField.setText(command);
        commandField.positionCaret(command.length());
    }

    /**
     * Focuses the command field so the user can continue typing.
     */
    void requestCommandFocus() {
        commandField.requestFocus();
    }

    /**
     * Updates the storage status and task counts in the header.
     */
    void setStatus(String status, String stats) {
        statusText.setText(status);
        taskStats.setText(stats);
    }

    /**
     * Adds a conversation message and reveals the beginning of its content.
     */
    void appendMessage(Node message) {
        messageList.getChildren().add(message);
        scrollToLatestMessage();
    }

    /**
     * Replaces a pending response in place so the answer stays beside its question.
     */
    void replaceMessage(Node pendingMessage, Node answer) {
        int messageIndex = messageList.getChildren().indexOf(pendingMessage);
        messageList.getChildren().set(messageIndex, answer);
        scrollToLatestMessage();
    }

    /**
     * Reveals the start of a long reply so users can read it from the beginning.
     */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            chatScrollPane.getScene().getRoot().applyCss();
            chatScrollPane.getScene().getRoot().layout();
            double scrollableHeight = chatScrollPane.getContent().getBoundsInLocal().getHeight()
                    - chatScrollPane.getViewportBounds().getHeight();
            double lastMessageTop = messageList.getChildren().getLast().getBoundsInParent().getMinY();
            chatScrollPane.setVvalue(scrollableHeight <= 0 ? 0 : Math.min(1, lastMessageTop / scrollableHeight));
        });
    }

}
