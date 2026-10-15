package nob.ui;

import java.net.URL;
import java.nio.file.Path;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import nob.exception.NobException;
import nob.parser.Parser;
import nob.storage.Storage;
import nob.task.Task;
import nob.task.TaskList;

/**
 * Controls Nob's main chatbot window and handles user commands.
 */
public class MainWindow {
    /** The maximum number of tasks Nob can keep. */
    private static final int MAX_TASKS = 100;

    /** The file used to persist Nob's tasks. */
    private static final Path DATA_FILE = Path.of("data", "nob.txt");

    /** The classpath location of Nob's avatar. */
    private static final String NOB_AVATAR_PATH = "/images/nob-avatar.png";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    /** Stores the user's tasks while the application is running. */
    private TaskList tasks;

    /** Saves and loads the user's task list. */
    private Storage storage;

    /** Picture displayed beside Nob's messages, when one is available. */
    private Image nobAvatar;

    /** Initializes data and bindings after the FXML controls have been injected. */
    @FXML
    public void initialize() {
        assert scrollPane != null : "MainWindow.fxml must inject scrollPane";
        assert dialogContainer != null : "MainWindow.fxml must inject dialogContainer";
        assert userInput != null : "MainWindow.fxml must inject userInput";
        initialiseTasks();
        nobAvatar = loadAvatar(NOB_AVATAR_PATH);
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
        dialogContainer.getChildren().add(DialogBox.getNobDialog(
                "WASSUP! I'm Nob :)\nHow can I help you? Type help to see my commands.", nobAvatar));
        userInput.requestFocus();
    }

    /** Loads saved tasks or creates an empty list when loading fails. */
    private void initialiseTasks() {
        storage = new Storage(DATA_FILE);
        Task[] loadedTasks = new Task[MAX_TASKS];
        try {
            tasks = new TaskList(loadedTasks, storage.loadTasks(loadedTasks));
        } catch (NobException exception) {
            tasks = new TaskList(loadedTasks, 0);
        }
    }

    /** Adds the user's message and Nob's response to the conversation. */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(command));
        String response;
        try {
            response = executeCommand(command);
        } catch (NobException exception) {
            response = exception.getMessage();
        }
        dialogContainer.getChildren().add(DialogBox.getNobDialog(response, nobAvatar));
        userInput.clear();

        if (command.equals("bye")) {
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /** Executes one supported Nob command and returns Nob's reply. */
    private String executeCommand(String command) throws NobException {
        if (command.equals("bye")) {
            return "Goodbye! Hope to see you soon mate!";
        }
        if (command.equals("help")) {
            return "Here are the commands you can use:\n"
                    + "list\ntodo DESCRIPTION\ndeadline DESCRIPTION /by DATE_OR_TIME\n"
                    + "event DESCRIPTION /from START /to END\nfind KEYWORD\n"
                    + "mark TASK_NUMBER\nunmark TASK_NUMBER\ndelete TASK_NUMBER\nclear\nbye";
        }
        if (command.equals("clear")) {
            tasks.clear();
            saveTasks();
            return "Noted. I've cleared the entire task list.";
        }
        if (command.equals("list")) {
            return getTaskListResponse();
        }
        return executeTaskCommand(command);
    }

    /** Executes a command that queries or modifies tasks and returns Nob's reply. */
    private String executeTaskCommand(String command) throws NobException {
        if (command.equals("find") || command.startsWith("find ")) {
            return findTasks(command);
        } else if (command.startsWith("mark ")) {
            return updateTaskStatus(command, true);
        } else if (command.startsWith("unmark ")) {
            return updateTaskStatus(command, false);
        } else if (command.startsWith("delete ")) {
            return deleteTask(command);
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return addTask(Parser.parseTodo(command));
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return addTask(Parser.parseDeadline(command));
        } else if (command.equals("event") || command.startsWith("event ")) {
            return addTask(Parser.parseEvent(command));
        }
        return "That isn't any of the commands I know. Type help to see the available commands.";
    }

    /** Adds a task, saves the new list, and returns a confirmation. */
    private String addTask(Task task) {
        if (!tasks.addTask(task)) {
            return "I can only store up to " + MAX_TASKS + " tasks.";
        }
        saveTasks();
        return "Alrighty. I've added this task:\n  " + task
                + "\nNow you have " + tasks.getTaskCount() + " tasks in the list.";
    }

    /** Changes a task's completion state, saves it, and returns a confirmation. */
    private String updateTaskStatus(String command, boolean isDone) throws NobException {
        Task task = tasks.markTask(Parser.parseTaskNumber(command), isDone);
        saveTasks();
        String message = isDone
                ? "LET'S GOOO! I've marked this task as done:"
                : "Okay... I've marked this task as not done yet:";
        return message + "\n  " + task;
    }

    /** Deletes the selected task, saves the list, and returns a confirmation. */
    private String deleteTask(String command) throws NobException {
        Task task = tasks.deleteTask(Parser.parseTaskNumber(command));
        saveTasks();
        return "Noted. I've removed the task:\n  " + task
                + "\nNow you have " + tasks.getTaskCount() + " tasks left in the list.";
    }

    /** Returns all tasks as a numbered chatbot response. */
    private String getTaskListResponse() throws NobException {
        if (tasks.getTaskCount() == 0) {
            return "Your task list is empty right now. Add a task using todo, deadline, or event.";
        }

        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int index = 1; index <= tasks.getTaskCount(); index++) {
            response.append('\n').append(index).append('.').append(tasks.getTask(index));
        }
        return response.toString();
    }

    /** Returns tasks whose descriptions contain a case-insensitive keyword. */
    private String findTasks(String command) throws NobException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new NobException("Use: find KEYWORD\n(e.g. find book)");
        }

        StringBuilder response = new StringBuilder();
        for (int index = 1; index <= tasks.getTaskCount(); index++) {
            Task task = tasks.getTask(index);
            if (task.hasKeyword(keyword)) {
                if (response.isEmpty()) {
                    response.append("Here are the matching tasks in your list:");
                }
                response.append('\n').append(index).append('.').append(task);
            }
        }
        return response.isEmpty() ? "No matching tasks found." : response.toString();
    }

    /** Saves tasks to disk. */
    private void saveTasks() {
        storage.saveTasks(tasks.getTasks(), tasks.getTaskCount());
    }

    /** Returns an avatar image, or {@code null} when the resource does not exist. */
    private Image loadAvatar(String resourcePath) {
        URL resource = MainWindow.class.getResource(resourcePath);
        return resource == null ? null : new Image(resource.toExternalForm());
    }
}
