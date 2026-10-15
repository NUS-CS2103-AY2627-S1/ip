package ip.ui;

import ip.collection.TaskList;
import ip.exception.TabbyException;
import ip.model.Task;
import ip.parser.Parser;
import ip.storage.Storage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides a conversational JavaFX interface for the Tabby task manager. */
public class TabbyGui extends Application {

    private final Storage storage = new Storage("data/tabby.txt");
    private final TaskList tasks = new TaskList();
    private final VBox conversation = new VBox(18);
    private final TextField commandField = new TextField();
    private final ScrollPane conversationScroll = new ScrollPane(conversation);

    /** Builds and displays the chat-style task manager window. */
    @Override
    public void start(Stage stage) {
        loadTasks();

        Label brand = new Label("TABBY");
        brand.getStyleClass().add("brand-name");
        Label title = new Label("Your personal task companion");
        title.getStyleClass().add("brand-subtitle");
        HBox header = new HBox(14, brand, title);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);
        header.getStyleClass().add("chat-header");

        conversation.getStyleClass().add("conversation");
        conversation.setPadding(new Insets(26, 34, 26, 34));
        conversationScroll.setFitToWidth(true);
        conversationScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversationScroll.getStyleClass().add("conversation-scroll");
        addAssistantMessage("Hello! I’m Tabby, your personal task companion.\n"
                + "Tell me what you would like to plan, and I’ll keep it organised.");
        if (tasks.size() > 0) {
            addAssistantMessage(taskSummary(tasks));
        }

        commandField.setPromptText("What would you like to plan?");
        commandField.setOnAction(event -> executeCommand());
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(event -> executeCommand());
        HBox.setHgrow(commandField, Priority.ALWAYS);
        HBox composer = new HBox(10, commandField, sendButton);
        composer.setAlignment(Pos.CENTER);
        composer.getStyleClass().add("composer");

        Label hints = new Label("todo | deadline | event | list | mark | unmark | delete | find | help | bye");
        hints.getStyleClass().add("command-hints");
        VBox footer = new VBox(8, composer, hints);
        footer.getStyleClass().add("chat-footer");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");
        root.setTop(header);
        root.setCenter(conversationScroll);
        root.setBottom(footer);
        Scene scene = new Scene(root, 820, 680);
        scene.getStylesheets().add(getClass().getResource("/tabby.css").toExternalForm());
        stage.setTitle("Tabby | Your task companion");
        stage.setScene(scene);
        stage.setMinWidth(560);
        stage.setMinHeight(500);
        stage.show();
        scrollToLatest();
    }

    /** Loads saved tasks and reports a recoverable storage problem in the chat. */
    private void loadTasks() {
        try {
            storage.load().forEach(tasks::add);
        } catch (TabbyException exception) {
            addAssistantMessage("I couldn’t load the saved tasks, so I started with an empty list.\n"
                    + "You can continue safely and I’ll save new tasks normally.");
        }
    }

    /** Reads the composer input, executes it, and clears the composer. */
    private void executeCommand() {
        String input = commandField.getText().trim();
        if (input.isEmpty()) {
            addErrorMessage("Please enter a command. Try ‘help’ if you need a list of options.");
            return;
        }
        addUserMessage(input);
        try {
            dispatchCommand(input);
        } catch (TabbyException exception) {
            addErrorMessage(exception.getMessage());
        }
        commandField.clear();
        commandField.requestFocus();
    }

    /** Routes a valid input to the method responsible for carrying it out. */
    private void dispatchCommand(String input) throws TabbyException {
        if (input.equals("help")) {
            showHelp();
        } else if (input.equals("list")) {
            showAllTasks();
        } else if (isCommand(input, "find")) {
            findTasks(input);
        } else if (isCommand(input, "mark")) {
            updateTask(input, true);
        } else if (isCommand(input, "unmark")) {
            updateTask(input, false);
        } else if (isCommand(input, "delete")) {
            deleteTask(input);
        } else if (isCommand(input, "todo")) {
            addTask(Parser.parseTodo(input), "Added to your plans:");
        } else if (isCommand(input, "deadline")) {
            addTask(Parser.parseDeadline(input), "Added your deadline:");
        } else if (isCommand(input, "event")) {
            addTask(Parser.parseEvent(input), "Added your event:");
        } else if (input.equals("bye")) {
            sayGoodbye();
        } else {
            throw new TabbyException("I didn’t recognise that command. Try ‘help’ to see what I can do.");
        }
    }

    /** Adds the supported command list to the conversation. */
    private void showHelp() {
        addAssistantMessage("You can use: list, find KEYWORD, todo DESCRIPTION, deadline DESCRIPTION /by DATE, "
                + "event DESCRIPTION /from DATE /to DATE, mark NUMBER, unmark NUMBER, delete NUMBER, or bye.");
    }

    /** Adds all current tasks to the conversation. */
    private void showAllTasks() {
        addAssistantMessage(taskSummary(tasks));
    }

    /** Finds tasks matching the keyword in a command. */
    private void findTasks(String input) throws TabbyException {
        String keyword = input.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new TabbyException("Please specify a keyword to find.");
        }
        addAssistantMessage(taskSummary(new TaskList(tasks.find(keyword))));
    }

    /** Deletes the task identified by a command and persists the result. */
    private void deleteTask(String input) throws TabbyException {
        int index = Parser.parseTaskIndex(input, tasks.size());
        Task removed = tasks.delete(index);
        saveTasks();
        addAssistantMessage("Done — I removed:\n" + removed);
    }

    /** Adds and persists a parsed task, then reports the result. */
    private void addTask(Task task, String message) throws TabbyException {
        tasks.add(task);
        saveTasks();
        addAssistantMessage(message + "\n" + task);
    }

    /** Adds the assistant's exit message to the conversation. */
    private void sayGoodbye() {
        addAssistantMessage("See you later! Your tasks are safely saved.");
    }

    /** Updates a task's completion status and persists the change. */
    private void updateTask(String input, boolean markDone) throws TabbyException {
        int index = Parser.parseTaskIndex(input, tasks.size());
        Task task = changeTaskStatus(index, markDone);
        saveTasks();
        showStatusChange(task, markDone);
    }

    /** Changes the completion status of the task at the supplied index. */
    private Task changeTaskStatus(int index, boolean markDone) {
        Task task = tasks.get(index);
        if (markDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        return task;
    }

    /** Persists the current task list. */
    private void saveTasks() throws TabbyException {
        storage.save(tasks);
    }

    /** Reports a successful completion-status change. */
    private void showStatusChange(Task task, boolean markDone) {
        String message = markDone ? "Nice — I marked this task as done:\n"
                : "Okay — I marked this task as not done:\n";
        addAssistantMessage(message + task);
    }

    /** Returns whether input is exactly a command or starts with its argument. */
    private boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /** Formats a task list for display in an assistant message. */
    private String taskSummary(TaskList source) {
        if (source.size() == 0) {
            return "You don’t have any matching tasks yet. Add one whenever you’re ready!";
        }
        StringBuilder summary = new StringBuilder("Here is what you have planned:\n");
        for (int i = 0; i < source.size(); i++) {
            summary.append(i + 1).append(". ").append(source.get(i)).append("\n");
        }
        return summary.toString().trim();
    }

    /** Adds a left-aligned assistant message to the conversation. */
    private void addAssistantMessage(String message) {
        conversation.getChildren().add(createMessage(message, false));
        scrollToLatest();
    }

    /** Adds a styled error message to the conversation. */
    private void addErrorMessage(String message) {
        HBox bubble = createMessage("I couldn’t do that: " + message, false);
        bubble.getStyleClass().add("error-message");
        conversation.getChildren().add(bubble);
        scrollToLatest();
    }

    /** Adds a right-aligned user message to the conversation. */
    private void addUserMessage(String message) {
        conversation.getChildren().add(createMessage(message, true));
        scrollToLatest();
    }

    /** Creates a chat row containing an avatar and message bubble. */
    private HBox createMessage(String message, boolean user) {
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMaxWidth(600);
        bubble.getStyleClass().addAll("message-bubble", user ? "user-bubble" : "assistant-bubble");
        Label avatar = new Label(user ? "You" : "T");
        avatar.getStyleClass().addAll("avatar", user ? "user-avatar" : "assistant-avatar");
        HBox row = new HBox(10, avatar, bubble);
        row.setAlignment(Pos.TOP_LEFT);
        row.getStyleClass().add(user ? "user-message" : "assistant-message");
        if (user) {
            row.setAlignment(Pos.TOP_RIGHT);
            row.getChildren().setAll(bubble, avatar);
        }
        return row;
    }

    /** Scrolls the conversation to its newest message. */
    private void scrollToLatest() {
        javafx.application.Platform.runLater(() -> conversationScroll.setVvalue(1.0));
    }

    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
