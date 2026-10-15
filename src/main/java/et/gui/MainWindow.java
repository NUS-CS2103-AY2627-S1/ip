package et.gui;

import et.ET;
import et.task.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Displays ET as a JavaFX chat window and forwards commands to the application logic.
 */
public class MainWindow extends Application {
    /** The preferred width of the application window. */
    private static final int WINDOW_WIDTH = 720;

    /** The preferred height of the application window. */
    private static final int WINDOW_HEIGHT = 760;

    /** The maximum width of a message bubble. */
    private static final int MESSAGE_WIDTH = 480;

    /** The number of decorative stars displayed behind the conversation. */
    private static final int STAR_COUNT = 26;

    /** Advances each star horizontally through a deterministic position sequence. */
    private static final int STAR_HORIZONTAL_STEP = 37;

    /** Keeps horizontal star positions within the visible percentage range. */
    private static final int STAR_HORIZONTAL_PERIOD = 97;

    /** Keeps stars away from the left edge of the conversation area. */
    private static final int STAR_HORIZONTAL_OFFSET = 2;

    /** Advances each star vertically through a deterministic position sequence. */
    private static final int STAR_VERTICAL_STEP = 61;

    /** Keeps vertical star positions within the visible percentage range. */
    private static final int STAR_VERTICAL_PERIOD = 89;

    /** Keeps stars away from the top edge of the conversation area. */
    private static final int STAR_VERTICAL_OFFSET = 4;

    /** Converts a whole-number position into a proportional coordinate. */
    private static final double STAR_POSITION_SCALE = 100.0;

    /** Makes every fifth star larger and brighter than the others. */
    private static final int BRIGHT_STAR_INTERVAL = 5;

    /** The radius of a bright decorative star. */
    private static final double BRIGHT_STAR_RADIUS = 1.5;

    /** The radius of an ordinary decorative star. */
    private static final double STAR_RADIUS = 0.8;

    /** The time the farewell remains visible before the window closes. */
    private static final int EXIT_DELAY_MILLIS = 650;

    /** The chatbot instance shared by every interaction in this window. */
    private final ET et = new ET();

    /** Holds the conversation's messages in display order. */
    private final VBox dialogContainer = new VBox(12);

    /** Accepts the next command from the user. */
    private final TextField userInput = new TextField();

    /** Contains task controls that still refer to the latest displayed task numbers. */
    private final List<Button> activeTaskActionButtons = new ArrayList<>();

    /** Keeps the latest conversation messages visible. */
    private ScrollPane scrollPane;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        BorderPane root = createLayout();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

        stage.setTitle("ET • Your Curious Task Friend");
        stage.setMinWidth(540);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();

        addDialog(et.getWelcomeMessage(), false);
        userInput.requestFocus();
    }

    /**
     * Creates the conversation area and command bar.
     *
     * @return the complete window layout
     */
    private BorderPane createLayout() {
        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createConversationArea());
        root.setBottom(createComposer());
        root.getStyleClass().add("app-shell");
        return root;
    }

    /**
     * Creates the scrollable conversation area and its decorative backdrop.
     *
     * @return the complete conversation area
     */
    private StackPane createConversationArea() {
        dialogContainer.setPadding(new Insets(24, 28, 28, 28));
        dialogContainer.setFillWidth(true);
        dialogContainer.getStyleClass().add("dialog-container");

        scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("dialog-scroll");
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollToLatestMessage());

        StackPane conversationArea = new StackPane(createSpaceBackdrop(), scrollPane);
        conversationArea.getStyleClass().add("conversation-area");
        return conversationArea;
    }

    /**
     * Creates the quick commands, command field, and send button.
     *
     * @return the complete command composer
     */
    private VBox createComposer() {
        userInput.setPromptText("Tell ET what to remember…  try “list”");
        userInput.setOnAction(event -> handleUserInput());
        userInput.getStyleClass().add("command-field");
        HBox.setHgrow(userInput, Priority.ALWAYS);

        Button sendButton = new Button("BEAM IT");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> handleUserInput());
        sendButton.getStyleClass().add("send-button");

        HBox inputBar = new HBox(10, userInput, sendButton);
        inputBar.setAlignment(Pos.CENTER);

        VBox composer = new VBox(9, createQuickCommands(), inputBar);
        composer.setPadding(new Insets(14, 20, 18, 20));
        composer.getStyleClass().add("composer");
        return composer;
    }

    /**
     * Creates shortcuts for common commands and command templates.
     *
     * @return the row of quick-command controls
     */
    private HBox createQuickCommands() {
        Label inputHint = new Label("EARTH SHORTCUTS");
        inputHint.getStyleClass().add("input-hint");

        Button listButton = createQuickCommandButton("LIST", "Show all tasks");
        listButton.setOnAction(event -> submitCommand("list"));

        Button sortButton = createQuickCommandButton("SORT", "Show tasks in chronological order");
        sortButton.setOnAction(event -> submitCommand("sort"));

        Button todoButton = createQuickCommandButton("+ TODO", "Prepare a to-do command");
        todoButton.setOnAction(event -> prepareTaskCommand("todo <task name>"));

        Button deadlineButton = createQuickCommandButton("+ DEADLINE", "Prepare a deadline command");
        deadlineButton.setOnAction(event -> prepareTaskCommand(
                "deadline <task name> /by <date/time>"));

        Button eventButton = createQuickCommandButton("+ EVENT", "Prepare an event command");
        eventButton.setOnAction(event -> prepareTaskCommand(
                "event <task name> /from <start date/time> /to <end date/time>"));

        Button findButton = createQuickCommandButton("FIND", "Search tasks by name");
        findButton.setOnAction(event -> prepareTaskCommand("find <task name>"));

        HBox quickCommands = new HBox(7, inputHint, listButton, sortButton, todoButton,
                deadlineButton, eventButton, findButton);
        quickCommands.setAlignment(Pos.CENTER_LEFT);
        quickCommands.getStyleClass().add("quick-commands");
        return quickCommands;
    }

    /**
     * Creates a consistently styled quick-command button.
     *
     * @param text the short command label
     * @param helpText the explanation shown on hover
     * @return the configured quick-command button
     */
    private Button createQuickCommandButton(String text, String helpText) {
        Button button = new Button(text);
        button.setTooltip(new Tooltip(helpText));
        button.getStyleClass().add("quick-command");
        return button;
    }

    /**
     * Places a structured task command in the input field and selects its task-name placeholder.
     *
     * @param commandTemplate the task command containing a task-name placeholder
     */
    private void prepareTaskCommand(String commandTemplate) {
        String taskNamePlaceholder = "<task name>";
        int taskNameStart = commandTemplate.indexOf(taskNamePlaceholder);
        assert taskNameStart >= 0 : "Task command template must contain a task-name placeholder";

        userInput.setText(commandTemplate);
        userInput.selectRange(taskNameStart, taskNameStart + taskNamePlaceholder.length());
        userInput.requestFocus();
    }

    /**
     * Creates the branded title and connection-status area.
     *
     * @return the application header
     */
    private VBox createHeader() {
        Label brandMark = new Label("ET");
        brandMark.getStyleClass().add("brand-mark");

        Label kicker = new Label("CURIOUS VISITOR • TASK COLLECTOR");
        kicker.getStyleClass().add("app-kicker");

        Label title = new Label("ET");
        title.getStyleClass().add("app-title");

        VBox identity = new VBox(1, kicker, title);

        Label statusDot = new Label("♥");
        statusDot.getStyleClass().add("status-dot");
        Label statusText = new Label("HOME SIGNAL GLOWING");
        statusText.getStyleClass().add("status-text");
        HBox status = new HBox(7, statusDot, statusText);
        status.setAlignment(Pos.CENTER);
        status.getStyleClass().add("status-pill");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox titleRow = new HBox(12, brandMark, identity, spacer, status);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label tagline = new Label("Organizing Earth, one tiny mission at a time.");
        tagline.getStyleClass().add("tagline");

        VBox header = new VBox(9, titleRow, tagline);
        header.setPadding(new Insets(18, 22, 16, 22));
        header.getStyleClass().add("app-header");
        return header;
    }

    /**
     * Creates a decorative star field that sits behind the conversation.
     *
     * @return the non-interactive backdrop
     */
    private Pane createSpaceBackdrop() {
        Pane backdrop = new Pane();
        backdrop.setMouseTransparent(true);
        backdrop.getStyleClass().add("space-backdrop");

        Circle moonGlow = new Circle(86);
        moonGlow.centerXProperty().bind(backdrop.widthProperty().subtract(36));
        moonGlow.setCenterY(44);
        moonGlow.getStyleClass().add("moon-glow");
        backdrop.getChildren().add(moonGlow);

        for (int starIndex = 0; starIndex < STAR_COUNT; starIndex++) {
            backdrop.getChildren().add(createStar(starIndex, backdrop));
        }

        return backdrop;
    }

    /**
     * Creates one star at a stable proportional position in the backdrop.
     *
     * @param starIndex the zero-based position in the star sequence
     * @param backdrop the pane whose dimensions determine the star position
     * @return the configured decorative star
     */
    private Circle createStar(int starIndex, Pane backdrop) {
        double horizontalRatio = ((starIndex * STAR_HORIZONTAL_STEP) % STAR_HORIZONTAL_PERIOD
                + STAR_HORIZONTAL_OFFSET) / STAR_POSITION_SCALE;
        double verticalRatio = ((starIndex * STAR_VERTICAL_STEP) % STAR_VERTICAL_PERIOD
                + STAR_VERTICAL_OFFSET) / STAR_POSITION_SCALE;
        boolean isBright = starIndex % BRIGHT_STAR_INTERVAL == 0;

        Circle star = new Circle(isBright ? BRIGHT_STAR_RADIUS : STAR_RADIUS);
        star.centerXProperty().bind(backdrop.widthProperty().multiply(horizontalRatio));
        star.centerYProperty().bind(backdrop.heightProperty().multiply(verticalRatio));
        star.getStyleClass().add(isBright ? "star-bright" : "star");
        return star;
    }

    /** Sends a non-blank user command to ET and displays both sides of the exchange. */
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        userInput.clear();
        submitCommand(input);
    }

    /**
     * Sends a command from the composer or an on-screen shortcut to ET.
     *
     * @param command the complete command to execute
     */
    private void submitCommand(String command) {
        String input = command.trim();
        if (input.isEmpty()) {
            return;
        }

        disableTaskActions();
        addDialog(input, true);
        ET.CommandResult result = et.getCommandResult(input);
        addDialog(result.response(), false);
        if (result.shouldExit()) {
            closeAfterFarewell();
        }
    }

    /** Closes the application after leaving enough time for ET's farewell to appear. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        PauseTransition exitDelay = new PauseTransition(Duration.millis(EXIT_DELAY_MILLIS));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    /** Scrolls the conversation to its latest message after JavaFX lays out new content. */
    private void scrollToLatestMessage() {
        scrollPane.setVvalue(scrollPane.getVmax());
    }

    /**
     * Adds one user or ET message to the conversation.
     *
     * @param message the text to display
     * @param isUser whether the message came from the user
     */
    private void addDialog(String message, boolean isUser) {
        Label avatar = new Label(isUser ? "YOU" : "ET");
        avatar.getStyleClass().addAll("avatar", isUser ? "user-avatar" : "et-avatar");

        Region bubble = isUser ? createUserBubble(message) : createEtBubble(message);

        Label author = new Label(isUser ? "YOU  //  EARTH FRIEND" : "ET  //  VISITOR");
        author.getStyleClass().addAll("message-author",
                isUser ? "user-message-author" : "et-message-author");

        VBox messageContent = new VBox(5, author, bubble);
        messageContent.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox dialog = isUser
                ? new HBox(11, spacer, messageContent, avatar)
                : new HBox(11, avatar, messageContent, spacer);
        dialog.setAlignment(Pos.TOP_CENTER);
        dialog.getStyleClass().add("dialog-row");

        dialogContainer.getChildren().add(dialog);
    }

    /**
     * Creates a text bubble for a command entered by the user.
     *
     * @param message the command text
     * @return the styled user bubble
     */
    private Label createUserBubble(String message) {
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMaxWidth(MESSAGE_WIDTH);
        bubble.setMinHeight(Region.USE_PREF_SIZE);
        bubble.setAlignment(Pos.CENTER_RIGHT);
        bubble.getStyleClass().add("user-bubble");
        return bubble;
    }

    /**
     * Creates an ET response bubble with graphical cards for any task lines.
     *
     * @param message ET's response text
     * @return the styled ET response bubble
     */
    private VBox createEtBubble(String message) {
        VBox bubble = new VBox(7);
        bubble.setMaxWidth(MESSAGE_WIDTH);
        bubble.setMinHeight(Region.USE_PREF_SIZE);
        bubble.getStyleClass().add("et-bubble");

        for (String line : message.split("\\R", -1)) {
            Optional<TaskDisplayParser.TaskDisplay> taskDisplay = TaskDisplayParser.parseTaskLine(line);
            if (taskDisplay.isPresent()) {
                bubble.getChildren().add(createTaskCard(taskDisplay.get()));
            } else {
                bubble.getChildren().add(createResponseLine(line));
            }
        }
        return bubble;
    }

    /**
     * Creates a wrapped line of ordinary ET response text.
     *
     * @param line the response line to display
     * @return the styled text label
     */
    private Label createResponseLine(String line) {
        Label responseLine = new Label(line.strip());
        responseLine.setWrapText(true);
        responseLine.setMaxWidth(MESSAGE_WIDTH);
        responseLine.setMinHeight(Region.USE_PREF_SIZE);
        responseLine.getStyleClass().add("response-line");
        return responseLine;
    }

    /**
     * Creates a visual card containing a task number, type, status, and description.
     *
     * @param taskDisplay the parsed task details
     * @return the graphical task card
     */
    private HBox createTaskCard(TaskDisplayParser.TaskDisplay taskDisplay) {
        HBox taskCard = new HBox(9);
        taskCard.setAlignment(Pos.CENTER_LEFT);
        taskCard.getStyleClass().add("task-card");
        if (taskDisplay.isDone()) {
            taskCard.getStyleClass().add("task-card-complete");
        }

        if (!taskDisplay.taskNumber().isEmpty()) {
            Label taskNumber = new Label(taskDisplay.taskNumber());
            taskNumber.getStyleClass().add("task-number");
            taskCard.getChildren().add(taskNumber);
        }

        taskCard.getChildren().addAll(createTaskTypeIcon(taskDisplay.taskType()),
                createStatusIndicator(taskDisplay.isDone()));

        VBox taskDetails = createTaskDetails(taskDisplay);
        HBox.setHgrow(taskDetails, Priority.ALWAYS);
        taskCard.getChildren().add(taskDetails);

        if (!taskDisplay.taskNumber().isEmpty()) {
            taskCard.getChildren().add(createTaskActions(taskDisplay));
        }
        return taskCard;
    }

    /**
     * Creates the description and any schedule badges for a task card.
     *
     * @param taskDisplay the parsed task details
     * @return the vertically arranged task information
     */
    private VBox createTaskDetails(TaskDisplayParser.TaskDisplay taskDisplay) {
        Label description = new Label(taskDisplay.description());
        description.setWrapText(true);
        description.setMinHeight(Region.USE_PREF_SIZE);
        description.setMaxWidth(MESSAGE_WIDTH - 220);
        description.getStyleClass().add("task-description");

        VBox taskDetails = new VBox(6, description);
        taskDetails.getStyleClass().add("task-details");
        if (!taskDisplay.scheduleDetails().isEmpty()) {
            FlowPane scheduleDetails = new FlowPane();
            scheduleDetails.setHgap(6);
            scheduleDetails.setVgap(5);
            scheduleDetails.getStyleClass().add("schedule-details");
            for (TaskDisplayParser.ScheduleDetail scheduleDetail : taskDisplay.scheduleDetails()) {
                scheduleDetails.getChildren().add(createScheduleBadge(scheduleDetail));
            }
            taskDetails.getChildren().add(scheduleDetails);
        }
        return taskDetails;
    }

    /**
     * Creates one labelled schedule badge for a deadline or event.
     *
     * @param scheduleDetail the label and formatted date or time
     * @return the styled schedule badge
     */
    private HBox createScheduleBadge(TaskDisplayParser.ScheduleDetail scheduleDetail) {
        Label label = new Label(scheduleDetail.label());
        label.getStyleClass().add("schedule-label");

        Label value = new Label(scheduleDetail.value());
        value.getStyleClass().add("schedule-value");

        HBox badge = new HBox(5, label, value);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.getStyleClass().add("schedule-badge");
        return badge;
    }

    /**
     * Creates completion and deletion controls for a numbered task card.
     *
     * @param taskDisplay the task targeted by the controls
     * @return the row of task action buttons
     */
    private HBox createTaskActions(TaskDisplayParser.TaskDisplay taskDisplay) {
        String statusCommand = taskDisplay.isDone() ? "unmark " : "mark ";
        String statusLabel = taskDisplay.isDone() ? "Mark as not done" : "Mark as done";
        String statusGlyph = taskDisplay.isDone() ? "↶" : "✓";

        Button statusButton = createTaskActionButton(statusGlyph, statusLabel, "task-action-status");
        statusButton.setOnAction(event -> submitCommand(statusCommand + taskDisplay.taskNumber()));

        Button deleteButton = createTaskActionButton("×", "Delete task", "task-action-delete");
        deleteButton.setOnAction(event -> submitCommand("delete " + taskDisplay.taskNumber()));

        activeTaskActionButtons.add(statusButton);
        activeTaskActionButtons.add(deleteButton);

        HBox taskActions = new HBox(5, statusButton, deleteButton);
        taskActions.setAlignment(Pos.CENTER_RIGHT);
        taskActions.getStyleClass().add("task-actions");
        return taskActions;
    }

    /**
     * Creates an accessible icon button for a task action.
     *
     * @param glyph the symbol displayed by the button
     * @param accessibleText the action description used by assistive tools and the tooltip
     * @param styleClass the action-specific style class
     * @return the configured task action button
     */
    private Button createTaskActionButton(String glyph, String accessibleText, String styleClass) {
        Button button = new Button(glyph);
        button.setAccessibleText(accessibleText);
        button.setTooltip(new Tooltip(accessibleText));
        button.getStyleClass().addAll("task-action", styleClass);
        return button;
    }

    /** Disables task controls whose displayed numbers may become stale after another command. */
    private void disableTaskActions() {
        for (Button taskActionButton : activeTaskActionButtons) {
            taskActionButton.setDisable(true);
        }
        activeTaskActionButtons.clear();
    }

    /**
     * Creates the icon that distinguishes a to-do, deadline, or event.
     *
     * @param taskType the category represented by the icon
     * @return the styled task-type icon
     */
    private StackPane createTaskTypeIcon(TaskType taskType) {
        SVGPath glyph = new SVGPath();
        String styleClass;
        String accessibleText;
        switch (taskType) {
        case TODO:
            glyph.setContent("M3 4 L5 6 L8 2 M10 4 L15 4 M3 10 L5 12 L8 8 M10 10 L15 10");
            styleClass = "todo-type";
            accessibleText = "To-do";
            break;
        case DEADLINE:
            glyph.setContent("M9 2 A7 7 0 1 1 8.99 2 M9 5 L9 9 L12 11");
            styleClass = "deadline-type";
            accessibleText = "Deadline";
            break;
        case EVENT:
            glyph.setContent("M3 5 L15 5 M5 2 L5 5 M13 2 L13 5 M3 3 L15 3 L15 15 L3 15 Z");
            styleClass = "event-type";
            accessibleText = "Event";
            break;
        default:
            throw new IllegalArgumentException("Unknown task type");
        }
        glyph.getStyleClass().add("task-type-glyph");

        StackPane typeIcon = new StackPane(glyph);
        typeIcon.getStyleClass().addAll("task-type-icon", styleClass);
        typeIcon.setAccessibleText(accessibleText);
        return typeIcon;
    }

    /**
     * Creates an empty completion ring or a filled ring with a checkmark.
     *
     * @param isDone whether the task is complete
     * @return the graphical completion indicator
     */
    private StackPane createStatusIndicator(boolean isDone) {
        Circle ring = new Circle(9);
        ring.getStyleClass().add("status-ring");

        StackPane statusIndicator = new StackPane(ring);
        statusIndicator.getStyleClass().addAll("task-status",
                isDone ? "task-status-complete" : "task-status-incomplete");
        statusIndicator.setAccessibleText(isDone ? "Completed" : "Not completed");

        if (isDone) {
            SVGPath checkmark = new SVGPath();
            checkmark.setContent("M4 9 L7 12 L14 5");
            checkmark.getStyleClass().add("task-checkmark");
            statusIndicator.getChildren().add(checkmark);
        }
        return statusIndicator;
    }
}
