package jarvis;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides a JavaFX graphical interface for the Jarvis chatbot. */
public class JarvisGui extends Application {
    private static final String BYE_COMMAND = "bye";
    private static final String DELETE_COMMAND = "delete";
    private static final String FIND_COMMAND = "find";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String SNOOZE_COMMAND = "snooze";
    private static final String UNMARK_COMMAND = "unmark";
    private static final int COMMAND_BAR_SPACING = 10;
    private static final int HEADER_SPACING = 2;
    private static final int PANE_SPACING = 10;
    private static final int PADDING = 24;
    private static final int WINDOW_WIDTH = 920;
    private static final int WINDOW_HEIGHT = 600;

    /** Stores tasks between GUI interactions. */
    private final TaskStorage storage = new TaskStorage();
    /** The current tasks shown in the task list. */
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    /** Shows the conversation between the user and Jarvis. */
    private final TextArea conversation = new TextArea();
    /** Shows all saved tasks. */
    private final ListView<Task> taskList = new ListView<>(tasks);
    /** Shows the number of tasks currently stored. */
    private final Label taskCount = new Label();
    /** Accepts the next command from the user. */
    private final TextField commandInput = new TextField();

    /** Builds and displays the Jarvis window. */
    @Override
    public void start(Stage stage) {
        tasks.addAll(storage.load());
        updateTaskCount();

        configureConversation();
        configureTaskList();
        HBox commandBar = createCommandBar();
        VBox leftPane = createLeftPane(commandBar);
        VBox rightPane = createRightPane();

        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(rightPane);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/jarvis.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Jarvis");
        stage.setMinWidth(720);
        stage.setMinHeight(480);
        stage.show();
    }

    /** Configures the conversation area used to display messages. */
    private void configureConversation() {
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.getStyleClass().add("conversation");
        conversation.appendText("Hello! I'm Jarvis.\nWhat can I do for you?\n\n");
    }

    /** Configures how tasks are rendered in the task list. */
    private void configureTaskList() {
        taskList.setPlaceholder(new Label("No tasks yet"));
        taskList.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                setText(empty || task == null ? null : getItemText(getIndex(), task));
            }
        });
    }

    /** Creates the input bar used to submit commands. */
    private HBox createCommandBar() {
        commandInput.setPromptText("Try: todo read a book, list, or bye");
        commandInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submitCommand();
            }
        });
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submitCommand());

        HBox commandBar = new HBox(COMMAND_BAR_SPACING, commandInput, sendButton);
        commandBar.setAlignment(Pos.CENTER);
        HBox.setHgrow(commandInput, Priority.ALWAYS);
        return commandBar;
    }

    /** Creates the left pane containing the conversation and command input. */
    private VBox createLeftPane(HBox commandBar) {
        Label title = new Label("Jarvis");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Your personal task assistant");
        subtitle.getStyleClass().add("subtitle");
        VBox header = new VBox(HEADER_SPACING, title, subtitle);
        VBox leftPane = new VBox(PANE_SPACING, header, conversation, commandBar);
        leftPane.setPadding(new Insets(PADDING));
        VBox.setVgrow(conversation, Priority.ALWAYS);
        return leftPane;
    }

    /** Creates the right pane containing the saved tasks. */
    private VBox createRightPane() {
        Label tasksTitle = new Label("Your tasks");
        tasksTitle.getStyleClass().add("section-title");
        VBox rightPane = new VBox(PANE_SPACING, tasksTitle, taskList);
        taskCount.getStyleClass().add("task-count");
        updateTaskCount();
        rightPane.getChildren().add(1, taskCount);
        rightPane.setPadding(new Insets(PADDING, PADDING, PADDING, 0));
        VBox.setVgrow(taskList, Priority.ALWAYS);
        return rightPane;
    }

    /** Executes the command in the input field and refreshes the task list. */
    private void submitCommand() {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        conversation.appendText("You: " + command + "\n");
        commandInput.clear();
        List<String> responses = processCommand(command);
        for (String response : responses) {
            conversation.appendText("Jarvis: " + response + "\n");
        }
        conversation.appendText("\n");
        conversation.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("error"),
                responses.stream().anyMatch(response -> response.startsWith("Oops:")));
        updateTaskCount();
        taskList.refresh();
    }

    /** Refreshes the task count shown above the task list. */
    private void updateTaskCount() {
        taskCount.setText(tasks.size() + (tasks.size() == 1 ? " task" : " tasks"));
    }

    /** Processes one command and returns the messages that belong in the conversation. */
    private List<String> processCommand(String command) {
        try {
            if (command.equals(BYE_COMMAND)) {
                return List.of("Bye. Hope to see you again soon!");
            }
            if (command.equals(LIST_COMMAND)) {
                return processListCommand();
            }
            if (isCommand(command, FIND_COMMAND)) {
                return processFindCommand(command);
            }
            if (isCommand(command, DELETE_COMMAND)) {
                return processDeleteCommand(command);
            }
            if (isCommand(command, MARK_COMMAND) || isCommand(command, UNMARK_COMMAND)) {
                return processStatusCommand(command);
            }
            if (isCommand(command, SNOOZE_COMMAND)) {
                return processSnoozeCommand(command);
            }
            return processCreateCommand(command);
        } catch (JarvisException exception) {
            return List.of("Oops: " + exception.getMessage());
        }
    }

    /** Returns the response for a list command. */
    private List<String> processListCommand() {
        if (tasks.isEmpty()) {
            return List.of("Your task list is empty.");
        }
        return List.of("Here are the tasks in your list:");
    }

    /** Returns the response for a find command after validating its keyword. */
    private List<String> processFindCommand(String command) throws JarvisException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        List<Task> matchingTasks = Jarvis.findMatchingTasks(tasks, keyword);
        if (matchingTasks.isEmpty()) {
            return List.of("No matching tasks found.");
        }
        List<String> responses = new ArrayList<>();
        responses.add("Here are the matching tasks in your list:");
        for (int index = 0; index < matchingTasks.size(); index++) {
            responses.add((index + 1) + ". " + matchingTasks.get(index));
        }
        return responses;
    }

    /** Deletes the selected GUI task and returns the confirmation message. */
    private List<String> processDeleteCommand(String command) throws JarvisException {
        int taskNumber = Jarvis.parseTaskNumber(command, DELETE_COMMAND);
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new JarvisException("There is no task with that number.");
        }
        Task removedTask = tasks.remove(taskNumber - 1);
        storage.save(tasks);
        return List.of("Removed: " + removedTask);
    }

    /** Snoozes a deadline task and returns the confirmation message. */
    private List<String> processSnoozeCommand(String command) throws JarvisException {
        int taskIndex = Jarvis.snoozeTask(command, tasks);
        Deadline deadline = (Deadline) tasks.get(taskIndex);
        storage.save(tasks);
        return List.of("Snoozed task " + (taskIndex + 1) + " until "
                + deadline.getFormattedBy() + ": " + deadline);
    }

    /** Updates a GUI task's completion status and returns the confirmation message. */
    private List<String> processStatusCommand(String command) throws JarvisException {
        String action = isCommand(command, MARK_COMMAND) ? MARK_COMMAND : UNMARK_COMMAND;
        int taskNumber = Jarvis.parseTaskNumber(command, action);
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new JarvisException("There is no task with that number.");
        }
        Task task = tasks.get(taskNumber - 1);
        if (action.equals(MARK_COMMAND)) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks);
        return List.of("Updated: " + task);
    }

    /** Creates a GUI task and returns the confirmation message. */
    private List<String> processCreateCommand(String command) throws JarvisException {
        Task task = Jarvis.parseTask(command);
        if (tasks.stream().anyMatch(existing -> Jarvis.isDuplicate(existing, task))) {
            throw new JarvisException("That task is already in your list.");
        }
        tasks.add(task);
        storage.save(tasks);
        return List.of("Added: " + task);
    }

    /** Returns whether a command is an action alone or an action with arguments. */
    private boolean isCommand(String command, String commandName) {
        return command.equals(commandName) || command.startsWith(commandName + " ");
    }

    /** Returns the numbered text displayed for one task. */
    private String getItemText(int index, Task task) {
        return (index + 1) + ". " + task;
    }

    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
