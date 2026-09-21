package aster.gui;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import aster.command.Parser;
import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.Task;
import aster.task.TaskList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the tutorial-style conversation window for Aster.
 */
@SuppressWarnings("unused")
public class MainWindow extends AnchorPane {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private final Image userImage = loadImage("/images/default-user.png");
    private final Image asterImage = loadImage("/images/aster-bot.png");
    private final Storage storage = new Storage(Paths.get("data", "aster.txt").toString());
    private TaskList tasks;
    private boolean lastResponseWasError;

    /** Loads an image bundled with the application. */
    private static Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath));
    }

    /**
     * Initializes the task list and keeps new messages visible.
     */
    @FXML
    public void initialize() {
        tasks = loadTasks();
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Handles commands entered through the tutorial UI.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = process(input);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage),
                DialogBox.getAsterDialog(response, asterImage, lastResponseWasError));
        userInput.clear();
    }

    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load().toArray(Task[]::new));
        } catch (AsterException exception) {
            return new TaskList();
        }
    }

    private String process(String input) {
        lastResponseWasError = false;
        try {
            Parser.ParsedCommand parsedCommand = Parser.parse(input);
            return switch (parsedCommand.getCommand()) {
                case TODO -> addTodo(parsedCommand.getArguments());
                case DEADLINE -> addDeadline(parsedCommand.getArguments());
                case EVENT -> addEvent(parsedCommand.getArguments());
                case LIST -> formatTasks("Your constellation of tasks:", tasks.getAll());
                case ONDATE -> showTasksOnDate(parsedCommand.getArguments());
                case MARK -> markTask(parsedCommand.getArguments());
                case UNMARK -> unmarkTask(parsedCommand.getArguments());
                case DELETE -> deleteTask(parsedCommand.getArguments());
                case FIND -> findTasks(parsedCommand.getArguments());
                case TAG -> tagTask(parsedCommand.getArguments());
                case BYE -> closeWindow();
            };
        } catch (AsterException exception) {
            lastResponseWasError = true;
            return exception.getUserMessage();
        }
    }

    private String addTodo(String arguments) throws AsterException {
        Task task = new aster.task.Todo(Parser.parseTodoDescription(arguments));
        tasks.add(task);
        storage.save(tasks);
        return taskAdded(task);
    }

    private String addDeadline(String arguments) throws AsterException {
        Parser.DeadlineDetails details = Parser.parseDeadlineDetails(arguments);
        Task task = new aster.task.Deadline(details.getDescription(), details.getBy());
        tasks.add(task);
        storage.save(tasks);
        return taskAdded(task);
    }

    private String addEvent(String arguments) throws AsterException {
        Parser.EventDetails details = Parser.parseEventDetails(arguments);
        Task task = new aster.task.Event(details.getDescription(), details.getFrom(), details.getTo());
        tasks.add(task);
        storage.save(tasks);
        return taskAdded(task);
    }

    private String taskAdded(Task task) {
        return "A new star is on your chart:\n  " + task
                + "\nYour orbit now holds " + tasks.size() + " tasks.";
    }

    private String showTasksOnDate(String arguments) throws AsterException {
        LocalDate date = Parser.parseOnDate(arguments);
        return formatTasks("Here are the tasks on " + date.format(DISPLAY_DATE_FORMAT) + ":",
                tasks.findTasksOnDate(date), "No tasks are scheduled for "
                        + date.format(DISPLAY_DATE_FORMAT) + ".");
    }

    private String markTask(String arguments) throws AsterException {
        Task task = tasks.get(Parser.parseTaskNumber(arguments, tasks.size(), "mark") - 1);
        task.markAsDone();
        storage.save(tasks);
        return "Nice! I've marked this task as done:\n  " + task;
    }

    private String unmarkTask(String arguments) throws AsterException {
        Task task = tasks.get(Parser.parseTaskNumber(arguments, tasks.size(), "unmark") - 1);
        task.markAsNotDone();
        storage.save(tasks);
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    private String deleteTask(String arguments) throws AsterException {
        Task task = tasks.remove(Parser.parseTaskNumber(arguments, tasks.size(), "delete") - 1);
        storage.save(tasks);
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String findTasks(String arguments) throws AsterException {
        return formatTasks("These tasks match your signal:",
                tasks.find(Parser.parseFindKeyword(arguments)));
    }

    private String tagTask(String arguments) throws AsterException {
        Parser.TagDetails details = Parser.parseTagDetails(arguments);
        Task task = tasks.get(Parser.parseTaskNumber(details.getTaskNumberText(), tasks.size(), "tag") - 1);
        task.addTag(details.getTag());
        storage.save(tasks);
        return "Constellation note added:\n  " + task;
    }

    private String formatTasks(String heading, List<Task> taskList) {
        return formatTasks(heading, taskList, "Your task list is empty.");
    }

    private String formatTasks(String heading, List<Task> taskList, String emptyMessage) {
        if (taskList.isEmpty()) {
            return "The sky is clear—no tasks here yet.";
        }
        return IntStream.range(0, taskList.size())
                .mapToObj(index -> (index + 1) + "." + taskList.get(index))
                .collect(Collectors.joining("\n", heading + "\n", ""));
    }

    private String closeWindow() {
        Platform.exit();
        return "Until next time - keep your goals in orbit!";
    }
}
