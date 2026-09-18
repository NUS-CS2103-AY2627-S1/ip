package waffles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * The entry point and coordinator for the Waffles chatbot.
 */
public class Waffles {
    /** Handles user interaction. */
    private final Ui ui;

    /** Loads and saves tasks. */
    private final Storage storage;

    /** Interprets commands. */
    private final Parser parser;

    /** Owns the current task collection. */
    private final TaskList tasks;

    /** Creates Waffles with its user interface, parser, storage, and task list. */
    public Waffles() {
        this(new Storage());
    }

    /**
     * Creates Waffles using a supplied storage implementation.
     *
     * @param storage the storage used for this chatbot's task data
     */
    public Waffles(Storage storage) {
        ui = new Ui();
        this.storage = Objects.requireNonNull(storage);
        parser = new Parser();
        tasks = new TaskList(storage.loadTasks());
    }

    /** Runs the chatbot until the user enters {@code bye} or input ends. */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String command = normalizeCommand(ui.readCommand());
            if (!handleCommand(command, ui)) {
                break;
            }
        }
        ui.showDivider();
    }

    /**
     * Processes one chatbot command and returns the text response for a GUI.
     *
     * @param command the command entered by the user
     * @return the response without the CLI divider lines
     */
    public String getResponse(String command) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (PrintStream responseStream = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            handleCommand(normalizeCommand(command), new Ui(responseStream));
        }
        return responseBytes.toString(StandardCharsets.UTF_8).replace(Ui.SEPARATOR, "").trim();
    }

    /** Processes one command and reports whether the chatbot should continue. */
    private boolean handleCommand(String command, Ui output) {
        try {
            if (command.isBlank()) {
                throw new IllegalArgumentException("please enter a command, such as `list` or `todo read book`.");
            } else if (command.equals("bye")) {
                output.showGoodbye();
                return false;
            } else if (command.equals("list")) {
                output.showTaskList(tasks);
            } else if (parser.isMarkCommand(command)) {
                handleMarkCommand(command, output);
            } else if (parser.isDeleteCommand(command)) {
                handleDeleteCommand(command, output);
            } else if (parser.isFindCommand(command)) {
                String keyword = parser.parseFindKeyword(command);
                output.showMatchingTasks(tasks.findTasks(keyword));
            } else if (parser.isTaskCommand(command)) {
                Task newTask = parser.parseTask(command);
                assert newTask != null : "Parser must return a task for a recognized task command";
                List<Task> conflicts = tasks.findSchedulingConflicts(newTask);
                if (!conflicts.isEmpty()) {
                    output.showSchedulingWarning(conflicts);
                }
                tasks.addTask(newTask);
                storage.saveTasks(tasks.asList());
                output.showTaskAdded(newTask, tasks.size());
            } else {
                throw new IllegalArgumentException(
                        "I don't recognise that command. Try todo, deadline, event, list, "
                                + "mark, unmark, delete, find, or bye.");
            }
        } catch (IllegalArgumentException exception) {
            output.showError(exception.getMessage());
        }
        return true;
    }

    /** Handles a mark or unmark command after parsing its task number. */
    private void handleMarkCommand(String command, Ui output) {
        int taskNumber = parser.parseTaskNumber(command);
        int taskIndex = taskNumber - 1;
        validateTaskIndex(taskIndex);

        boolean shouldMarkAsDone = command.startsWith("mark ");
        Task task = tasks.getTask(taskIndex);
        if (shouldMarkAsDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.saveTasks(tasks.asList());
        output.showMarkResult(task, shouldMarkAsDone);
    }

    /** Handles a delete command after parsing its task number. */
    private void handleDeleteCommand(String command, Ui output) {
        int taskNumber = parser.parseTaskNumber(command);
        int taskIndex = taskNumber - 1;
        validateTaskIndex(taskIndex);

        Task removedTask = tasks.removeTask(taskIndex);
        storage.saveTasks(tasks.asList());
        output.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Checks whether a zero-based task index exists.
     *
     * @param taskIndex the index to check
     */
    private void validateTaskIndex(int taskIndex) {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new IllegalArgumentException(
                    "that task number is out of range. Pick a number from 1 to " + tasks.size() + ".");
        }
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task index must refer to an existing task";
    }

    /** Starts Waffles. */
    public static void main(String... args) {
        new Waffles().run();
    }

    /** Trims user input and treats repeated whitespace as one separator. */
    private String normalizeCommand(String command) {
        return command == null ? "" : command.trim().replaceAll("\\s+", " ");
    }
}
