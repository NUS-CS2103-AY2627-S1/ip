package bloop.gui;

import bloop.Duke;
import bloop.task.Task;
import bloop.ui.Ui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides the JavaFX graphical interface for the Orbit task companion. */
public class BloopApp extends Application {
    private static final String BOT_BUBBLE_STYLE = "-fx-background-color: #1e293b; -fx-background-radius: 14; "
            + "-fx-border-color: #334155; -fx-border-radius: 14; -fx-padding: 10 12;";
    private static final String USER_BUBBLE_STYLE = "-fx-background-color: #7c3aed; -fx-background-radius: 14; "
            + "-fx-padding: 10 12;";
    private static final String ERROR_BUBBLE_STYLE = "-fx-background-color: #3f1d2e; -fx-background-radius: 14; "
            + "-fx-border-color: #fb7185; -fx-border-radius: 14; -fx-padding: 10 12;";

    private final VBox messages = new VBox(10);
    private final ScrollPane conversationPane = new ScrollPane(messages);
    private final TextField commandField = new TextField();
    private final Button sendButton = new Button("Launch");
    private final ListView<String> taskListView = new ListView<>();
    private Duke duke;

    /** Starts the Bloop graphical application. */
    @Override
    public void start(Stage stage) {
        duke = new Duke("data/duke.txt", new Ui(this::showBloopMessage, this::showErrorMessage));

        VBox header = createHeader();
        configureConversation();
        VBox taskPanel = createTaskPanel();
        HBox inputBar = createInputBar();

        SplitPane content = new SplitPane(conversationPane, taskPanel);
        content.setDividerPositions(0.70);
        content.setStyle("-fx-background-color: #e2e8f0;");

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(content);
        root.setBottom(inputBar);

        stage.setTitle("Orbit");
        stage.setMinWidth(560);
        stage.setMinHeight(420);
        stage.setScene(new Scene(root, 780, 560));
        stage.show();
        refreshTaskList();
        if (duke.startConversation()) {
            commandField.requestFocus();
        } else {
            commandField.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Creates the compact application header. */
    private VBox createHeader() {
        Label title = new Label("Orbit");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #e0e7ff;");
        Label instructions = new Label("Mission control for your tasks - Try: todo read book, list, mark 1");
        instructions.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        VBox header = new VBox(4, title, instructions);
        header.setPadding(new Insets(14, 16, 12, 16));
        header.setStyle("-fx-background-color: #0f172a; "
                + "-fx-border-color: transparent transparent #334155 transparent;");
        return header;
    }

    /** Configures the scrolling, space-efficient conversation area. */
    private void configureConversation() {
        messages.setPadding(new Insets(16));
        messages.setFillWidth(true);
        messages.setMinHeight(Region.USE_PREF_SIZE);
        messages.setStyle("-fx-background-color: #0f172a;");
        conversationPane.setFitToWidth(true);
        conversationPane.setFitToHeight(false);
        conversationPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationPane.setStyle("-fx-background: #0f172a; -fx-background-color: #0f172a; "
                + "-fx-border-color: transparent;");
    }

    /** Creates the resizable task summary panel. */
    private VBox createTaskPanel() {
        Label tasksTitle = new Label("Tasks");
        tasksTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #e0e7ff;");
        Label emptyTasks = new Label("No tasks yet\nAdd one to get started.");
        emptyTasks.setStyle("-fx-text-fill: #94a3b8; -fx-text-alignment: center;");
        taskListView.setPlaceholder(emptyTasks);
        taskListView.setStyle("-fx-background-color: transparent; -fx-control-inner-background: #111827; "
                + "-fx-text-background-color: #e2e8f0;");
        VBox taskPanel = new VBox(10, tasksTitle, taskListView);
        taskPanel.setPadding(new Insets(16, 12, 12, 12));
        taskPanel.setPrefWidth(220);
        taskPanel.setMinWidth(170);
        taskPanel.setStyle("-fx-background-color: #111827;");
        VBox.setVgrow(taskListView, Priority.ALWAYS);
        return taskPanel;
    }

    /** Creates the command entry controls. */
    private HBox createInputBar() {
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submitCommand());
        sendButton.setStyle("-fx-background-color: #7c3aed; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 8; -fx-padding: 7 15;");
        commandField.setPromptText("Set a course, e.g. todo revise notes");
        commandField.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #f8fafc; "
                + "-fx-prompt-text-fill: #94a3b8;");
        commandField.setOnAction(event -> submitCommand());
        HBox inputBar = new HBox(8, commandField, sendButton);
        inputBar.setAlignment(Pos.CENTER_LEFT);
        inputBar.setPadding(new Insets(10, 12, 12, 12));
        inputBar.setStyle("-fx-background-color: #111827; "
                + "-fx-border-color: #334155 transparent transparent transparent;");
        HBox.setHgrow(commandField, Priority.ALWAYS);
        return inputBar;
    }

    /** Processes the command typed by the user. */
    private void submitCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        addMessage(command, true, false);
        commandField.clear();
        boolean shouldExit = duke.processCommand(command);
        refreshTaskList();
        if (shouldExit) {
            Platform.exit();
        }
    }

    /** Displays a normal Bloop response. */
    private void showBloopMessage(String message) {
        addMessage(message, false, false);
    }

    /** Displays a command error in a prominent message card. */
    private void showErrorMessage(String message) {
        addMessage("Orbit needs a course correction\n" + message, false, true);
    }

    /** Adds a card aligned according to the fixed user-and-app conversation roles. */
    private void addMessage(String message, boolean isUserMessage, boolean isError) {
        Label messageText = new Label(message);
        messageText.setWrapText(true);
        messageText.maxWidthProperty().bind(conversationPane.widthProperty().subtract(88));
        messageText.setStyle("-fx-font-size: 13px; -fx-text-fill: "
                + (isUserMessage ? "white;" : isError ? "#ffe4e6;" : "#e2e8f0;"));

        VBox bubble = new VBox(messageText);
        bubble.maxWidthProperty().bind(conversationPane.widthProperty().subtract(64));
        bubble.setStyle(isUserMessage ? USER_BUBBLE_STYLE : isError ? ERROR_BUBBLE_STYLE : BOT_BUBBLE_STYLE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = isUserMessage ? new HBox(spacer, bubble) : new HBox(bubble, spacer);
        row.setAlignment(isUserMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messages.getChildren().add(row);
        Platform.runLater(() -> conversationPane.setVvalue(1.0));
    }

    /** Refreshes the task summary from Bloop's current task list. */
    private void refreshTaskList() {
        taskListView.getItems().clear();
        int taskNumber = 1;
        for (Task task : duke.getTasks().asList()) {
            taskListView.getItems().add(taskNumber + ". " + task);
            taskNumber++;
        }
    }
}
