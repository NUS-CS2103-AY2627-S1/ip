package larper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import larper.ui.DialogBox;

/**
 * Shows the JavaFX chat window for the Larper application.
 * The window routes user commands through the same command engine used by the console interface.
 */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 980;
    private static final int WINDOW_HEIGHT = 720;
    private static final int MIN_WINDOW_WIDTH = 820;
    private static final int MIN_WINDOW_HEIGHT = 560;
    private static final int TASK_PANEL_WIDTH = 300;
    private static final int INPUT_HEIGHT = 48;
    private static final int SEND_BUTTON_WIDTH = 88;
    private static final int HEADER_AVATAR_SIZE = 38;

    private static final Pattern TASK_PREFIX_PATTERN = Pattern.compile("^\\[([TDE])\\]\\[([ X])\\]\\s+(.*)$");
    private static final Pattern TASK_TAG_PATTERN = Pattern.compile("\\s*\\[#([A-Za-z0-9]+)\\]");

    private static final String ROOT_STYLE = "-fx-background-color: linear-gradient(to bottom right, #fbf7ef, #eee0ce);"
            + " -fx-font-family: 'Avenir Next';";
    private static final String PANEL_STYLE = "-fx-background-color: #fffdf8;"
            + " -fx-background-radius: 26;"
            + " -fx-border-color: rgba(94, 61, 43, 0.18);"
            + " -fx-border-radius: 26;"
            + " -fx-effect: dropshadow(gaussian, rgba(94, 61, 43, 0.20), 20, 0.18, 0, 8);";
    private static final String SCROLL_STYLE = "-fx-background: transparent;"
            + " -fx-background-color: transparent;"
            + " -fx-border-color: transparent;";
    private static final String TITLE_STYLE = "-fx-text-fill: #3f2b24;"
            + " -fx-font-size: 23;"
            + " -fx-font-weight: 800;";
    private static final String SUBTITLE_STYLE = "-fx-text-fill: #8b7a70;"
            + " -fx-font-size: 12;";
    private static final String TASK_CARD_STYLE = "-fx-background-color: #ffffff;"
            + " -fx-background-radius: 12;"
            + " -fx-border-color: #e7d9c8;"
            + " -fx-border-radius: 12;";
    private static final String DONE_TASK_CARD_STYLE = "-fx-background-color: #f5f1eb;"
            + " -fx-background-radius: 12;"
            + " -fx-border-color: #e2d8cd;"
            + " -fx-border-radius: 12;";
    private static final String TASK_NUMBER_STYLE = "-fx-background-color: #a36a4c;"
            + " -fx-background-radius: 999;"
            + " -fx-text-fill: white;"
            + " -fx-font-size: 12;"
            + " -fx-font-weight: 800;";
    private static final String TASK_TYPE_STYLE = "-fx-background-color: #f1dfb2;"
            + " -fx-background-radius: 999;"
            + " -fx-padding: 2 7 2 7;"
            + " -fx-text-fill: #6f4b32;"
            + " -fx-font-size: 10.5;"
            + " -fx-font-weight: 800;";
    private static final String DONE_TASK_TYPE_STYLE = "-fx-background-color: #e5ded5;"
            + " -fx-background-radius: 999;"
            + " -fx-padding: 2 7 2 7;"
            + " -fx-text-fill: #8b8076;"
            + " -fx-font-size: 10.5;"
            + " -fx-font-weight: 800;";
    private static final String TASK_TITLE_STYLE = "-fx-text-fill: #3f2b24;"
            + " -fx-font-size: 12.5;"
            + " -fx-font-weight: 700;";
    private static final String DONE_TASK_TITLE_STYLE = "-fx-text-fill: #9b9289;"
            + " -fx-font-size: 12.5;"
            + " -fx-strikethrough: true;";
    private static final String TASK_SECONDARY_STYLE = "-fx-text-fill: #8c7d73;"
            + " -fx-font-size: 11;";
    private static final String DONE_TASK_SECONDARY_STYLE = "-fx-text-fill: #aaa19a;"
            + " -fx-font-size: 11;";
    private static final String TASK_STATUS_STYLE = "-fx-text-fill: #9c6a32;"
            + " -fx-font-size: 10.5;"
            + " -fx-font-weight: 800;";
    private static final String DONE_TASK_STATUS_STYLE = "-fx-text-fill: #8f8a84;"
            + " -fx-font-size: 10.5;";
    private static final String CHAT_HEADER_STYLE = "-fx-background-color: #fffdf8;"
            + " -fx-background-radius: 26 26 0 0;"
            + " -fx-border-color: transparent transparent #efe6dc transparent;";
    private static final String INPUT_FIELD_STYLE = "-fx-background-color: #ffffff;"
            + " -fx-background-radius: 18;"
            + " -fx-border-color: #e7dacd;"
            + " -fx-border-radius: 18;"
            + " -fx-padding: 0 16 0 16;"
            + " -fx-font-size: 13;"
            + " -fx-text-fill: #3f2b24;";
    private static final String FOCUSED_INPUT_FIELD_STYLE = "-fx-background-color: #ffffff;"
            + " -fx-background-radius: 18;"
            + " -fx-border-color: #d7a734;"
            + " -fx-border-width: 2;"
            + " -fx-border-radius: 18;"
            + " -fx-padding: 0 16 0 16;"
            + " -fx-font-size: 13;"
            + " -fx-text-fill: #3f2b24;";
    private static final String SEND_BUTTON_STYLE = "-fx-background-color: #a36a4c;"
            + " -fx-background-radius: 18;"
            + " -fx-text-fill: white;"
            + " -fx-font-weight: 800;"
            + " -fx-font-size: 13;";
    private static final String TOGGLE_STYLE = "-fx-text-fill: #8b7a70;"
            + " -fx-font-size: 12;";
    private static final String EMPTY_TASK_STYLE = "-fx-text-fill: #9b8b82;"
            + " -fx-font-size: 13;"
            + " -fx-padding: 18;";

    private Larper larper;
    private VBox dialogContainer;
    private VBox taskListContainer;
    private ScrollPane scrollPane;
    private Label taskCountLabel;
    private CheckBox autoScrollToggle;
    private TextField userInput;
    private Button sendButton;
    private ArrayList<String> commandHistory;
    private int historyIndex;
    private Image larperImage;
    private Image userImage;

    /**
     * Starts the JavaFX application with the Larper chat interface.
     *
     * @param stage Primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        assert stage != null : "JavaFX should provide a primary stage.";
        larper = new Larper(Larper.getDataPath());
        loadDialogImages();
        initializeTaskListContainer();
        initializeDialogContainer();
        initializeScrollPane();
        initializeAutoScrollToggle();
        initializeInputControls();

        HBox root = createRootPane();
        addDialog(larper.getWelcomeMessage(), false, false);
        refreshTaskList();
        configureStage(stage, root);
    }

    private void loadDialogImages() {
        larperImage = loadImage("/images/larper.png");
        userImage = loadImage("/images/user.png");
    }

    private void initializeTaskListContainer() {
        taskListContainer = new VBox(10);
        taskListContainer.setPadding(new Insets(4, 6, 4, 0));
        taskListContainer.setFillWidth(true);
    }

    private void initializeDialogContainer() {
        dialogContainer = new VBox(12);
        dialogContainer.setPadding(new Insets(16, 18, 16, 18));
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogContainer.setFillWidth(true);
    }

    private void initializeScrollPane() {
        scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(SCROLL_STYLE);
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> scrollToLatestMessage());
    }

    private void initializeAutoScrollToggle() {
        autoScrollToggle = new CheckBox("Auto-scroll");
        autoScrollToggle.setSelected(true);
        autoScrollToggle.setStyle(TOGGLE_STYLE);
        autoScrollToggle.setOnAction(event -> scrollToLatestMessage());
    }

    private void initializeInputControls() {
        commandHistory = new ArrayList<>();
        historyIndex = 0;

        userInput = new TextField();
        userInput.setPromptText("Drop a command... (help for commands)");
        userInput.setOnAction(event -> handleUserInput());
        userInput.setOnKeyPressed(this::handleHistoryNavigation);
        userInput.setPrefHeight(INPUT_HEIGHT);
        userInput.setStyle(INPUT_FIELD_STYLE);
        userInput.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            userInput.setStyle(isFocused ? FOCUSED_INPUT_FIELD_STYLE : INPUT_FIELD_STYLE);
        });

        sendButton = new Button("Cook");
        sendButton.setOnAction(event -> handleUserInput());
        sendButton.setPrefWidth(SEND_BUTTON_WIDTH);
        sendButton.setPrefHeight(INPUT_HEIGHT);
        sendButton.setStyle(SEND_BUTTON_STYLE);
    }

    private HBox createRootPane() {
        HBox root = new HBox(16);
        root.setPadding(new Insets(22));
        root.setStyle(ROOT_STYLE);
        root.getChildren().addAll(createTaskPanel(), createChatPanel());
        return root;
    }

    private VBox createTaskPanel() {
        VBox taskPanel = new VBox(14);
        taskPanel.setPadding(new Insets(22, 16, 18, 18));
        taskPanel.setPrefWidth(TASK_PANEL_WIDTH);
        taskPanel.setMinWidth(240);
        taskPanel.setMaxWidth(360);
        taskPanel.setStyle(PANEL_STYLE);

        Label title = new Label("THE AGENDA");
        title.setStyle(TITLE_STYLE);
        taskCountLabel = new Label();
        taskCountLabel.setWrapText(true);
        taskCountLabel.setMaxWidth(Double.MAX_VALUE);
        taskCountLabel.setMinHeight(Region.USE_PREF_SIZE);
        taskCountLabel.setStyle(SUBTITLE_STYLE);

        VBox header = new VBox(2, title, taskCountLabel);
        header.setFillWidth(true);
        ScrollPane taskScrollPane = new ScrollPane(taskListContainer);
        taskScrollPane.setFitToWidth(true);
        taskScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        taskScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        taskScrollPane.setStyle(SCROLL_STYLE);
        VBox.setVgrow(taskScrollPane, Priority.ALWAYS);

        taskPanel.getChildren().addAll(header, taskScrollPane);
        return taskPanel;
    }

    private VBox createChatPanel() {
        VBox chatPanel = new VBox();
        chatPanel.setStyle(PANEL_STYLE);
        chatPanel.getChildren().addAll(createChatHeader(), scrollPane, createInputBar());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        HBox.setHgrow(chatPanel, Priority.ALWAYS);
        return chatPanel;
    }

    private HBox createChatHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 18, 12, 18));
        header.setStyle(CHAT_HEADER_STYLE);

        VBox titleBlock = new VBox(1);
        Label title = new Label("Larper");
        title.setStyle("-fx-text-fill: #3f2b24; -fx-font-size: 15; -fx-font-weight: 800;");
        Label subtitle = new Label("Productivity expert (allegedly)");
        subtitle.setStyle(SUBTITLE_STYLE);
        titleBlock.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(createRoundImageView(larperImage, HEADER_AVATAR_SIZE), titleBlock, spacer,
                autoScrollToggle);
        return header;
    }

    private HBox createInputBar() {
        HBox inputBar = new HBox(10);
        inputBar.setAlignment(Pos.CENTER);
        inputBar.setPadding(new Insets(12, 18, 18, 18));
        inputBar.getChildren().addAll(userInput, sendButton);
        HBox.setHgrow(userInput, Priority.ALWAYS);
        return inputBar;
    }

    private void configureStage(Stage stage, HBox root) {
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Larper");
        stage.setResizable(true);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.centerOnScreen();
        stage.show();
        stage.toFront();
        stage.requestFocus();
        Platform.runLater(() -> stage.setAlwaysOnTop(false));
    }

    /**
     * Handles one user command from the JavaFX input field.
     */
    private void handleUserInput() {
        assert larper != null : "Larper engine should be initialized before handling GUI input.";
        assert userInput != null : "User input field should be initialized before handling GUI input.";
        assert sendButton != null : "Send button should be initialized before handling GUI input.";
        String input = userInput.getText().trim();
        if (input.isBlank()) {
            return;
        }

        rememberCommand(input);
        addDialog(input, true, false);
        userInput.clear();

        LarperResponse response = larper.getResponse(input);
        addDialog(response.getMessage(), false, response.isError());
        refreshTaskList();
        if (response.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /**
     * Adds one user or Larper message to the chat history.
     *
     * @param message Message text to display.
     * @param isUser Whether the message was entered by the user.
     * @param isError Whether the message should use the error style.
     */
    private void addDialog(String message, boolean isUser, boolean isError) {
        assert message != null : "Dialog message should not be null.";
        assert dialogContainer != null : "Dialog container should be initialized before adding messages.";
        DialogBox dialogBox;
        if (isUser) {
            dialogBox = DialogBox.getUserDialog(message, userImage);
        } else if (isError) {
            dialogBox = DialogBox.getErrorDialog(message, larperImage);
        } else {
            dialogBox = DialogBox.getLarperDialog(message, larperImage);
        }
        assert dialogBox != null : "Dialog factory should return a dialog box.";
        dialogBox.prefWidthProperty().bind(dialogContainer.widthProperty().subtract(6));
        dialogContainer.getChildren().add(dialogBox);
    }

    private void refreshTaskList() {
        assert larper != null : "Larper engine should be initialized before reading tasks.";
        assert taskListContainer != null : "Task list container should be initialized before refresh.";
        ArrayList<String> taskSummaries = larper.getTaskSummaries();
        taskListContainer.getChildren().clear();
        taskCountLabel.setText(formatTaskCount(taskSummaries.size()));

        if (taskSummaries.isEmpty()) {
            Label emptyState = new Label("No public commitments yet. Declare one from the chat.");
            emptyState.setWrapText(true);
            emptyState.setStyle(EMPTY_TASK_STYLE);
            taskListContainer.getChildren().add(emptyState);
            return;
        }

        for (int i = 0; i < taskSummaries.size(); i++) {
            taskListContainer.getChildren().add(createTaskCard(i + 1, taskSummaries.get(i)));
        }
    }

    private HBox createTaskCard(int taskNumber, String taskSummary) {
        assert taskNumber >= 1 : "Task number should be one-based.";
        assert taskSummary != null : "Task summary should not be null.";
        TaskDisplay task = parseTaskSummary(taskSummary);

        Label numberLabel = createTaskNumberLabel(taskNumber);
        VBox taskText = createTaskText(task);
        return createTaskCardLayout(numberLabel, taskText, task.isDone);
    }

    private Label createTaskNumberLabel(int taskNumber) {
        Label numberLabel = new Label(String.valueOf(taskNumber));
        numberLabel.setMinSize(26, 26);
        numberLabel.setPrefSize(26, 26);
        numberLabel.setAlignment(Pos.CENTER);
        numberLabel.setStyle(TASK_NUMBER_STYLE);
        return numberLabel;
    }

    private VBox createTaskText(TaskDisplay task) {
        VBox taskText = new VBox(3, createTaskMetaRow(task), createTaskTitleLabel(task));
        addSecondaryTextIfPresent(taskText, task);
        return taskText;
    }

    private HBox createTaskCardLayout(Label numberLabel, VBox taskText, boolean isDone) {
        HBox taskCard = new HBox(9, numberLabel, taskText);
        taskCard.setAlignment(Pos.TOP_LEFT);
        taskCard.setPadding(new Insets(8, 9, 8, 9));
        taskCard.setStyle(isDone ? DONE_TASK_CARD_STYLE : TASK_CARD_STYLE);
        HBox.setHgrow(taskText, Priority.ALWAYS);
        return taskCard;
    }

    private HBox createTaskMetaRow(TaskDisplay task) {
        HBox metaRow = new HBox(6, createTaskTypeLabel(task), createTaskStatusLabel(task));
        metaRow.setAlignment(Pos.CENTER_LEFT);
        return metaRow;
    }

    private Label createTaskTypeLabel(TaskDisplay task) {
        Label typeLabel = new Label(task.type);
        typeLabel.setStyle(task.isDone ? DONE_TASK_TYPE_STYLE : TASK_TYPE_STYLE);
        return typeLabel;
    }

    private Label createTaskStatusLabel(TaskDisplay task) {
        Label statusLabel = new Label(task.isDone ? "Done" : "Open");
        statusLabel.setStyle(task.isDone ? DONE_TASK_STATUS_STYLE : TASK_STATUS_STYLE);
        return statusLabel;
    }

    private Label createTaskTitleLabel(TaskDisplay task) {
        Label taskTitle = new Label(task.title);
        taskTitle.setWrapText(true);
        taskTitle.setMaxWidth(Double.MAX_VALUE);
        taskTitle.setStyle(task.isDone ? DONE_TASK_TITLE_STYLE : TASK_TITLE_STYLE);
        return taskTitle;
    }

    private void addSecondaryTextIfPresent(VBox taskText, TaskDisplay task) {
        if (task.secondaryText.isBlank()) {
            return;
        }

        Label secondaryLabel = new Label(task.secondaryText);
        secondaryLabel.setWrapText(true);
        secondaryLabel.setStyle(task.isDone ? DONE_TASK_SECONDARY_STYLE : TASK_SECONDARY_STYLE);
        taskText.getChildren().add(secondaryLabel);
    }

    private TaskDisplay parseTaskSummary(String taskSummary) {
        Matcher prefixMatcher = TASK_PREFIX_PATTERN.matcher(taskSummary);
        if (!prefixMatcher.matches()) {
            return new TaskDisplay("Task", false, taskSummary, "");
        }

        String type = formatTaskType(prefixMatcher.group(1));
        boolean isDone = prefixMatcher.group(2).equals("X");
        String body = prefixMatcher.group(3);
        String tags = extractTags(body);
        String bodyWithoutTags = TASK_TAG_PATTERN.matcher(body).replaceAll("").trim();
        String details = extractDetails(bodyWithoutTags);
        String title = removeDetails(bodyWithoutTags);
        String secondaryText = combineSecondaryText(formatDetails(details), tags);
        return new TaskDisplay(type, isDone, title, secondaryText);
    }

    private String formatTaskType(String typeIcon) {
        return switch (typeIcon) {
        case "T" -> "Todo";
        case "D" -> "Deadline";
        case "E" -> "Event";
        default -> "Task";
        };
    }

    private String extractTags(String taskText) {
        Matcher tagMatcher = TASK_TAG_PATTERN.matcher(taskText);
        ArrayList<String> tags = new ArrayList<>();
        while (tagMatcher.find()) {
            tags.add("#" + tagMatcher.group(1));
        }
        return String.join("  ", tags);
    }

    private String extractDetails(String taskText) {
        int detailsStart = taskText.indexOf(" (");
        if (detailsStart == -1 || !taskText.endsWith(")")) {
            return "";
        }
        return taskText.substring(detailsStart + 2, taskText.length() - 1);
    }

    private String removeDetails(String taskText) {
        int detailsStart = taskText.indexOf(" (");
        if (detailsStart == -1 || !taskText.endsWith(")")) {
            return taskText;
        }
        return taskText.substring(0, detailsStart).trim();
    }

    private String formatDetails(String details) {
        if (details.startsWith("by: ")) {
            return "Due: " + details.substring("by: ".length());
        }
        if (details.startsWith("from: ")) {
            return "From " + details.substring("from: ".length()).replace(" to: ", " to ");
        }
        return details;
    }

    private String combineSecondaryText(String details, String tags) {
        if (details.isBlank()) {
            return tags;
        }
        if (tags.isBlank()) {
            return details;
        }
        return details + "  " + tags;
    }

    private String formatTaskCount(int taskCount) {
        assert taskCount >= 0 : "Task count should not be negative.";
        if (taskCount == 0) {
            return "0 commitments. Suspiciously peaceful.";
        }
        if (taskCount == 1) {
            return "1 commitment made";
        }
        if (taskCount >= 5) {
            return taskCount + " commitments. Ambitious narrative.";
        }
        return taskCount + " commitments made";
    }

    private void rememberCommand(String input) {
        assert input != null && !input.isBlank() : "Only non-empty commands should be stored in history.";
        if (commandHistory.isEmpty() || !commandHistory.get(commandHistory.size() - 1).equals(input)) {
            commandHistory.add(input);
        }
        historyIndex = commandHistory.size();
    }

    private void handleHistoryNavigation(KeyEvent event) {
        assert event != null : "Key event should not be null.";
        if (commandHistory == null || commandHistory.isEmpty()) {
            return;
        }
        if (event.getCode() == KeyCode.UP) {
            historyIndex = Math.max(0, historyIndex - 1);
            showCommandFromHistory();
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            historyIndex = Math.min(commandHistory.size(), historyIndex + 1);
            showCommandFromHistory();
            event.consume();
        }
    }

    private void showCommandFromHistory() {
        if (historyIndex == commandHistory.size()) {
            userInput.clear();
        } else {
            userInput.setText(commandHistory.get(historyIndex));
            userInput.positionCaret(userInput.getText().length());
        }
    }

    /**
     * Scrolls to the latest message when auto-scroll is enabled.
     */
    private void scrollToLatestMessage() {
        if (autoScrollToggle != null && autoScrollToggle.isSelected()) {
            assert scrollPane != null : "Scroll pane should be initialized before auto-scrolling.";
            scrollPane.setVvalue(1.0);
        }
    }

    private ImageView createRoundImageView(Image image, int size) {
        assert image != null : "Image should exist before creating a display picture.";
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setClip(new Circle(size / 2.0, size / 2.0, size / 2.0));
        return imageView;
    }

    /**
     * Returns an image loaded from the application resources.
     *
     * @param imagePath Absolute resource path of the image.
     * @return Image loaded from the resource path.
     */
    private Image loadImage(String imagePath) {
        assert imagePath != null && imagePath.startsWith("/") : "Image path should be an absolute resource path.";
        InputStream imageStream = Main.class.getResourceAsStream(imagePath);
        assert imageStream != null : "Image resource should exist before creating an Image.";
        return new Image(imageStream);
    }

    private static class TaskDisplay {
        private final String type;
        private final boolean isDone;
        private final String title;
        private final String secondaryText;

        TaskDisplay(String type, boolean isDone, String title, String secondaryText) {
            assert type != null && !type.isBlank() : "Task display type should be available.";
            assert title != null && !title.isBlank() : "Task display title should be available.";
            assert secondaryText != null : "Task display secondary text should be an empty string when absent.";
            this.type = type;
            this.isDone = isDone;
            this.title = title;
            this.secondaryText = secondaryText;
        }
    }
}
