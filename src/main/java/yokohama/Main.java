package yokohama;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import yokohama.exceptions.YokohamaException;
import yokohama.storage.Storage;
import yokohama.task.Deadline;
import yokohama.task.Event;
import yokohama.task.Task;
import yokohama.task.Todo;
import yokohama.utils.DateTimeHandler;

/**
 * Provides a Telegram-inspired graphical interface for Yokohama.
 */
public class Main extends Application {
    private static final String FILE_PATH = "data/todo_data.txt";
    private static final int MAX_COMMAND_PARTS = 2;
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_FROM_SEPARATOR = " /from ";
    private static final String EVENT_TO_SEPARATOR = " /to ";
    private static final int MESSAGE_SPACING = 10;
    private static final int COMPOSER_SPACING = 10;
    private static final int SIDEBAR_SPACING = 18;
    private static final int HEADER_SPACING = 2;
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 680;
    private static final int MINIMUM_WINDOW_WIDTH = 760;
    private static final int MINIMUM_WINDOW_HEIGHT = 520;
    private static final int MESSAGE_HORIZONTAL_PADDING = 64;

    private final ArrayList<Todo> tasks = new ArrayList<>();
    private final Storage storage = new Storage();
    private final VBox messages = new VBox(MESSAGE_SPACING);
    private final ScrollPane messagePane = new ScrollPane(messages);
    private final TextField commandField = new TextField();
    private String startupError;

    @Override
    public void start(Stage stage) {
        loadTasks();
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app");
        root.setLeft(createSidebar());
        root.setCenter(createChat());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/yokohama/style.css").toExternalForm());
        stage.setTitle("Yokohama");
        stage.setResizable(true);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> saveTasks());
        stage.show();

        addMessages(false,
                "Welcome to Yokohama! Your personal task assistant is online.",
                "Try: todo buy milk\n"
                        + "Or: deadline submit report /by 9/3/2026 [2359]\n"
                        + "Use list, schedule M/d/yyyy, mark <number>, unmark <number>, "
                        + "delete <number>, or find <word>.");
        if (startupError != null) {
            addMessage(startupError, false, true);
        }
    }

    private VBox createSidebar() {
        Label logo = new Label("✈  Yokohama");
        logo.getStyleClass().add("logo");
        Label search = new Label("⌕  Search");
        search.getStyleClass().add("search");
        Label taskChat = new Label("✓  My Tasks\n     personal task assistant");
        taskChat.getStyleClass().add("chat-item");
        Label hint = new Label("Your saved tasks are kept\nlocally on this device.");
        hint.getStyleClass().add("sidebar-hint");
        return new VBox(SIDEBAR_SPACING, logo, search, taskChat, hint);
    }

    private BorderPane createChat() {
        Label title = new Label("My Tasks");
        title.getStyleClass().add("chat-name");
        Label status = new Label("online");
        status.getStyleClass().add("chat-status");
        VBox header = new VBox(HEADER_SPACING, title, status);
        header.getStyleClass().add("chat-header");

        messages.getStyleClass().add("messages");
        messagePane.setFitToWidth(true);
        messagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messagePane.getStyleClass().add("message-pane");

        commandField.setPromptText("Message Yokohama…");
        commandField.setOnAction(event -> sendCommand());
        HBox.setHgrow(commandField, Priority.ALWAYS);
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(event -> sendCommand());
        HBox composer = new HBox(COMPOSER_SPACING, commandField, sendButton);
        composer.setAlignment(Pos.CENTER);
        composer.getStyleClass().add("composer");

        BorderPane chat = new BorderPane();
        chat.setTop(header);
        chat.setCenter(messagePane);
        chat.setBottom(composer);
        return chat;
    }

    private void sendCommand() {
        String command = commandField.getText();
        if (command.isBlank()) {
            return;
        }
        addMessage(command, true);
        commandField.clear();
        CommandResponse response = handleCommand(command);
        addMessage(response.text(), false, response.error());
    }

    private CommandResponse handleCommand(String input) {
        try {
            validateCommandText(input);
            String[] parts = input.split(" ", MAX_COMMAND_PARTS);
            String action = parts[0].toLowerCase();
            String payload = parts.length == MAX_COMMAND_PARTS ? parts[1].trim() : "";
            String response = switch (action) {
                case "todo" -> addTodo(payload);
                case "deadline" -> addDeadline(payload);
                case "event" -> addEvent(payload);
                case "list" -> listTasks();
                case "find" -> findTasks(payload);
                case "schedule" -> viewSchedule(payload);
                case "mark" -> changeTask(payload, true);
                case "unmark" -> changeTask(payload, false);
                case "delete" -> deleteTask(payload);
                case "exit" -> {
                    saveTasks();
                    Platform.exit();
                    yield "Your tasks are saved. See you next time!";
                }
                default -> throw new IllegalArgumentException(
                        "I don't recognise that command. Try todo, list, deadline, event, or schedule.");
            };
            return new CommandResponse(response, false);
        } catch (DateTimeException exception) {
            return new CommandResponse(
                    "⚠ Invalid date or time. Use M/d/yyyy HHmm, such as 9/3/2026 2359. "
                            + "For deadlines, the time may be omitted.",
                    true);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return new CommandResponse("⚠ " + exception.getMessage(), true);
        }
    }

    private String addTodo(String description) {
        validateDescription(description, "A todo");
        Todo task = new Task(description, false);
        requireUnique(task);
        tasks.add(task);
        assert tasks.getLast() == task : "A newly added todo should be the last task";
        saveTasks();
        return "Added a task:\n" + tasks.getLast();
    }

    private String addDeadline(String payload) {
        requireSeparatorCount(payload, "/by", DEADLINE_SEPARATOR);
        String[] details = payload.split(DEADLINE_SEPARATOR, MAX_COMMAND_PARTS);
        require(details.length == 2 && !details[0].isBlank() && !details[1].isBlank(),
                "Use: deadline <description> /by M/d/yyyy [HHmm]");
        validateDescription(details[0].trim(), "A deadline");
        LocalDateTime by = DateTimeHandler.convertToDeadlineDateTime(details[1].trim());
        Todo deadline = new Deadline(details[0].trim(), false, by);
        requireUnique(deadline);
        tasks.add(deadline);
        assert tasks.getLast() == deadline : "A newly added deadline should be the last task";
        saveTasks();
        return "Added a deadline:\n" + tasks.getLast();
    }

    private String addEvent(String payload) {
        requireSeparatorCount(payload, "/from", EVENT_FROM_SEPARATOR);
        requireSeparatorCount(payload, "/to", EVENT_TO_SEPARATOR);
        int fromIndex = payload.indexOf(EVENT_FROM_SEPARATOR);
        int toIndex = payload.indexOf(EVENT_TO_SEPARATOR);
        require(fromIndex > 0 && toIndex > fromIndex,
                "Use: event <description> /from M/d/yyyy HHmm /to M/d/yyyy HHmm");
        validateDescription(payload.substring(0, fromIndex).trim(), "An event");
        LocalDateTime from = DateTimeHandler.convertToLocalDateTime(
                payload.substring(fromIndex + EVENT_FROM_SEPARATOR.length(), toIndex).trim());
        LocalDateTime to = DateTimeHandler.convertToLocalDateTime(
                payload.substring(toIndex + EVENT_TO_SEPARATOR.length()).trim());
        Todo event = new Event(payload.substring(0, fromIndex).trim(), false, from, to);
        requireUnique(event);
        tasks.add(event);
        assert tasks.getLast() == event : "A newly added event should be the last task";
        saveTasks();
        return "Added an event:\n" + tasks.getLast();
    }

    private String listTasks() {
        if (tasks.isEmpty()) {
            return "🎉 Your list is empty. Time to relax!";
        }
        StringBuilder result = new StringBuilder("Your tasks:\n");
        for (int index = 0; index < tasks.size(); index++) {
            result.append(index + 1).append(". ").append(tasks.get(index)).append('\n');
        }
        return result.toString().trim();
    }

    private String findTasks(String keyword) {
        require(!keyword.isEmpty(), "Provide a keyword to find matching tasks.");
        require(!keyword.contains("|") && !keyword.matches(".*\\p{Cntrl}.*"),
                "The search keyword contains unsupported characters.");
        String matchingTasks = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).hasKeyword(keyword))
                .mapToObj(index -> (index + 1) + ". " + tasks.get(index))
                .collect(Collectors.joining("\n"));
        return matchingTasks.isEmpty()
                ? "No tasks match that keyword." : "Matching tasks:\n" + matchingTasks;
    }

    private String viewSchedule(String dateInput) {
        require(!dateInput.isEmpty(), "Use: schedule M/d/yyyy");
        LocalDate date = DateTimeHandler.convertToLocalDate(dateInput);
        String scheduledTasks = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).occursOn(date))
                .mapToObj(index -> (index + 1) + ". " + tasks.get(index))
                .collect(Collectors.joining("\n"));
        return scheduledTasks.isEmpty()
                ? "No tasks are scheduled for " + dateInput + "."
                : "Schedule for " + dateInput + ":\n" + scheduledTasks;
    }

    private String changeTask(String number, boolean completed) {
        Todo task = getTask(number);
        boolean changed = completed ? task.markComplete() : task.unmarkComplete();
        saveTasks();
        return changed ? (completed ? "✅ Marked as done:\n" : "↩ Marked as not done:\n") + task
                : completed ? "That task is already completed." : "That task is not completed yet.";
    }

    private String deleteTask(String number) {
        Todo task = tasks.remove(getIndex(number));
        saveTasks();
        return "Deleted:\n" + task;
    }

    private Todo getTask(String number) {
        int index = getIndex(number);
        assert index >= 0 && index < tasks.size() : "getIndex must return a valid task index";
        Todo task = tasks.get(index);
        assert task != null : "The task list must not contain null entries";
        return task;
    }

    private int getIndex(String number) {
        try {
            int index = Integer.parseInt(number) - 1;
            require(index >= 0 && index < tasks.size(), "There is no task with that number.");
            return index;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Task number must be a number.");
        }
    }

    private void loadTasks() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                ArrayList<Todo> savedTasks = storage.loadFile(file);
                for (Todo task : savedTasks) {
                    requireUnique(task);
                    tasks.add(task);
                }
            } catch (IOException | YokohamaException | IllegalArgumentException exception) {
                startupError = "⚠ Could not load saved tasks: " + exception.getMessage()
                        + " New tasks will not replace the damaged file until it is fixed.";
            }
        }
    }

    private void saveTasks() {
        try {
            storage.writeToFile(FILE_PATH, tasks);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save tasks. Check that the data file is writable.");
        }
    }

    private void addMessage(String text, boolean isUser) {
        addMessage(text, isUser, false);
    }

    private void addMessage(String text, boolean isUser, boolean isError) {
        assert text != null : "Messages sent to the interface must have text";
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.maxWidthProperty().bind(Bindings.max(
                messagePane.widthProperty().subtract(MESSAGE_HORIZONTAL_PADDING), 0));
        bubble.getStyleClass().add(isError
                ? "error-bubble" : isUser ? "user-bubble" : "assistant-bubble");
        HBox row = new HBox(bubble);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messages.getChildren().add(row);
        Platform.runLater(() -> messagePane.setVvalue(1));
    }

    /**
     * Adds one or more message bubbles with the same sender styling.
     *
     * @param isUser Whether the messages were sent by the user.
     * @param texts Message texts to display; Java collects these into an array.
     */
    private void addMessages(boolean isUser, String... texts) {
        for (String text : texts) {
            addMessage(text, isUser);
        }
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateCommandText(String input) {
        require(!input.isEmpty(), "Enter a command.");
        require(input.equals(input.trim()), "Remove leading and trailing spaces.");
        require(input.indexOf('\t') < 0 && input.indexOf('\n') < 0 && input.indexOf('\r') < 0,
                "Use single spaces between command parts.");
        require(!input.contains("  "), "Use only one space between command parts.");
        require(input.matches("[A-Za-z]+(?: .+)?"),
                "Commands must start with a command name using letters only.");
    }

    private void validateDescription(String description, String itemName) {
        require(!description.isBlank(), itemName + " description cannot be empty.");
        require(!description.contains("|") && !description.matches(".*\\p{Cntrl}.*"),
                "The description contains unsupported characters.");
    }

    private void requireSeparatorCount(String payload, String marker, String separator) {
        int first = payload.indexOf(separator);
        require(first >= 0 && first == payload.lastIndexOf(separator),
                "The " + marker + " parameter must be specified exactly once.");
    }

    private void requireUnique(Todo candidate) {
        String candidateData = canonicalData(candidate);
        require(tasks.stream().noneMatch(task -> canonicalData(task).equals(candidateData)),
                "A task with the same details already exists.");
    }

    private String canonicalData(Todo task) {
        return task.toDbString().replace(" | 1 | ", " | 0 | ");
    }

    private record CommandResponse(String text, boolean error) {
    }
}
