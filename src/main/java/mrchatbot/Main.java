package mrchatbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mrchatbot.ui.ProfileAvatar;

/**
 * Provides the JavaFX graphical user interface for Mr Chatbot.
 */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 520;
    private static final int WINDOW_HEIGHT = 640;
    private static final String DEFAULT_PROMPT = "Type a command";
    private static final String TODO_TEMPLATE = "todo <description>";
    private static final String DEADLINE_TEMPLATE = "deadline <description> /by <yyyy-mm-dd>";
    private static final String EVENT_TEMPLATE = "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>";

    private static final String RECURRING_DAILY_TEMPLATE = "recurring <description> /every day";
    private static final String RECURRING_WEEKLY_TEMPLATE = "recurring <description> /every week";

    private MrChatbotEngine engine;
    private VBox dialogContainer;
    private TextField userInput;
    private Label inputSuggestion;
    private Label recurringGuidance;

    /**
     * Builds and shows the chatbot window.
     */
    @Override
    public void start(Stage stage) {
        ScrollPane scrollPane = createDialogPane();
        VBox inputPanel = createInputPanel();

        VBox root = new VBox(scrollPane, inputPanel);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setStyle("-fx-font-family: Arial; -fx-background-color: #f5f7fb;");

        initialiseEngine();
        addBotMessage("Hello! I'm Mr Chatbot, your personal companion.\nWhat can I do for you, Mr User?");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Mr Chatbot");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates the scrolling chat history and keeps the newest message visible.
     */
    private ScrollPane createDialogPane() {
        dialogContainer = new VBox(12);
        dialogContainer.setPadding(new Insets(16));

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        return scrollPane;
    }

    /**
     * Creates the command input panel with shortcut buttons and a send action.
     */
    private VBox createInputPanel() {
        StackPane guidedInput = createGuidedInput();
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> handleUserInput());

        FlowPane suggestionBar = createSuggestionBar();
        recurringGuidance = new Label();
        recurringGuidance.setWrapText(true);
        recurringGuidance.setStyle("-fx-text-fill: #475569; -fx-padding: 0 12 10 12;");
        recurringGuidance.setVisible(false);
        recurringGuidance.setManaged(false);

        HBox inputArea = new HBox(8, guidedInput, sendButton);
        inputArea.setPadding(new Insets(12));
        HBox.setHgrow(guidedInput, Priority.ALWAYS);

        VBox inputPanel = new VBox(8, suggestionBar, inputArea, recurringGuidance);
        inputPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d7de; -fx-border-width: 1 0 0 0;");
        return inputPanel;
    }

    /**
     * Creates the text input and its command-syntax hint overlay.
     */
    private StackPane createGuidedInput() {
        userInput = new TextField();
        userInput.setPromptText(DEFAULT_PROMPT);
        userInput.setOnAction(event -> handleUserInput());
        userInput.textProperty().addListener((unused, oldText, newText) -> updateInputGuidance(newText));
        userInput.setStyle("-fx-background-color: transparent; -fx-border-color: #0ea5e9; -fx-border-radius: 4;"
                + " -fx-background-radius: 4; -fx-padding: 6 8 6 8;");

        inputSuggestion = new Label();
        inputSuggestion.setMouseTransparent(true);
        inputSuggestion.setStyle("-fx-text-fill: #9ca3af; -fx-padding: 7 9 7 9;");

        StackPane guidedInput = new StackPane(inputSuggestion, userInput);
        guidedInput.setAlignment(Pos.CENTER_LEFT);
        return guidedInput;
    }

    /**
     * Creates shortcut chips for common command templates.
     */
    private FlowPane createSuggestionBar() {
        FlowPane suggestionBar = new FlowPane(8, 8);
        suggestionBar.setPadding(new Insets(10, 12, 0, 12));
        suggestionBar.getChildren().add(createSuggestionButton("todo", "todo"));
        suggestionBar.getChildren().add(createSuggestionButton("deadline", "deadline"));
        suggestionBar.getChildren().add(createSuggestionButton("event", "event"));
        suggestionBar.getChildren().add(createSuggestionButton("daily task", RECURRING_DAILY_TEMPLATE));
        suggestionBar.getChildren().add(createSuggestionButton("recurring task", RECURRING_WEEKLY_TEMPLATE));
        suggestionBar.getChildren().add(createSuggestionButton("list", "list"));
        suggestionBar.getChildren().add(createSuggestionButton("help", "help"));
        return suggestionBar;
    }

    /**
     * Creates one clickable command-template suggestion.
     */
    private Button createSuggestionButton(String label, String template) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: #f6f8fa; -fx-background-radius: 8; -fx-border-color: #d0d7de;"
                + " -fx-border-radius: 8;");
        button.setOnAction(event -> applySuggestion(template));
        return button;
    }

    /**
     * Applies a command template and selects its first placeholder for replacement.
     */
    private void applySuggestion(String template) {
        userInput.setText(template);
        int placeholderIndex = template.indexOf('<');
        if (placeholderIndex == -1) {
            userInput.positionCaret(template.length());
        } else {
            int placeholderEnd = template.indexOf('>', placeholderIndex);
            userInput.selectRange(placeholderIndex, placeholderEnd + 1);
        }
        userInput.requestFocus();
    }

    /**
     * Updates the input placeholder and syntax hint using the command currently being typed.
     */
    private void updateInputGuidance(String input) {
        String suggestion = commandSuggestion(input);
        inputSuggestion.setText(suggestion);
        boolean isRecurring = input.stripLeading().toLowerCase().startsWith("recurring");
        recurringGuidance.setVisible(isRecurring);
        recurringGuidance.setManaged(isRecurring);
        recurringGuidance.setText("Repeat daily: /every day. Repeat weekly: /every week. "
                + "Weekly tasks can add /on monday (or any full weekday); otherwise use today's weekday.");
        if (input.isBlank()) {
            userInput.setPromptText(DEFAULT_PROMPT);
        } else {
            userInput.setPromptText("");
        }
    }

    /**
     * Returns the grey command guidance shown inside the input box.
     */
    private String commandSuggestion(String input) {
        String lowerCaseInput = input.toLowerCase();
        if (input.isBlank()) {
            return "";
        }
        if (RECURRING_DAILY_TEMPLATE.startsWith(lowerCaseInput)) {
            return RECURRING_DAILY_TEMPLATE;
        }
        if (lowerCaseInput.startsWith("recurring ") && !lowerCaseInput.contains(" /every")) {
            return appendGuidance(input, "/every day or /every week");
        }
        if (lowerCaseInput.startsWith("deadline") && !lowerCaseInput.contains(" /by ")) {
            return appendGuidance(input, "/by <yyyy-mm-dd>");
        }
        if (lowerCaseInput.startsWith("event") && !lowerCaseInput.contains(" /from ")) {
            return appendGuidance(input, "/from <yyyy-mm-dd> /to <yyyy-mm-dd>");
        }
        if (lowerCaseInput.startsWith("event") && !lowerCaseInput.contains(" /to ")) {
            return appendGuidance(input, "/to <yyyy-mm-dd>");
        }
        if (lowerCaseInput.equals("todo")) {
            return TODO_TEMPLATE;
        }
        if (TODO_TEMPLATE.startsWith(lowerCaseInput)) {
            return TODO_TEMPLATE;
        }
        if (DEADLINE_TEMPLATE.startsWith(lowerCaseInput)) {
            return DEADLINE_TEMPLATE;
        }
        if (EVENT_TEMPLATE.startsWith(lowerCaseInput)) {
            return EVENT_TEMPLATE;
        }
        return "";
    }

    /**
     * Appends guidance after the current input with one separating space.
     */
    private String appendGuidance(String input, String guidance) {
        if (input.endsWith(" ")) {
            return input + guidance;
        }
        return input + " " + guidance;
    }

    /**
     * Processes the text entered by the user.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        userInput.clear();
        addUserMessage(input);

        String response = engine.getResponse(input);
        addBotMessage(response);

        if (input.equalsIgnoreCase("bye")) {
            userInput.setDisable(true);
            Platform.runLater(Platform::exit);
        }
    }

    /**
     * Loads the chatbot engine used by the GUI.
     */
    private void initialiseEngine() {
        engine = new MrChatbotEngine();
        if (engine.getStartupError() != null) {
            addBotMessage(engine.getStartupError());
        }
    }

    /**
     * Adds a user message bubble to the chat history.
     */
    private void addUserMessage(String message) {
        addMessage(message, true);
    }

    /**
     * Adds a chatbot message bubble to the chat history.
     */
    private void addBotMessage(String message) {
        addMessage(message, false);
    }

    /**
     * Adds one styled message bubble to the chat history.
     */
    private void addMessage(String message, boolean isUser) {
        String backgroundColor = isUser ? "#dbeafe" : "#ffffff";
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(360);
        messageLabel.setStyle("-fx-background-color: " + backgroundColor
                + "; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #d0d7de;"
                + " -fx-border-radius: 8;");

        ProfileAvatar avatar = new ProfileAvatar(isUser);
        HBox messageRow = isUser ? new HBox(10, messageLabel, avatar) : new HBox(10, avatar, messageLabel);
        messageRow.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        dialogContainer.getChildren().add(messageRow);
    }
}
