package jassabot;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import jassabot.exception.JassaBotException;
import jassabot.parser.CommandType;
import jassabot.parser.Parser;
import jassabot.storage.Storage;
import jassabot.storage.StorageException;
import jassabot.task.Deadline;
import jassabot.task.Event;
import jassabot.task.Task;
import jassabot.task.TaskList;
import jassabot.task.Todo;
import jassabot.ui.Ui;

/**
 * Starts the JassaBot chatbot application.
 */
public class JassaBot {
    private final Storage storage;
    private final Ui ui;

    private final TaskList tasks;
    private final List<String> loadingWarnings;
    private final StringBuilder response = new StringBuilder();
    private CommandType commandType = CommandType.UNKNOWN;
    private boolean isExitRequested;

    /**
     * Creates a chatbot that stores tasks at the given relative file path.
     *
     * @param filePath relative path of the task data file.
     */
    public JassaBot(Path filePath) {
        storage = new Storage(filePath);
        ui = new Ui(line -> response.append(line).append("\n"));
        Storage.LoadResult loadResult = storage.loadTasks();
        tasks = new TaskList(loadResult.getTasks());
        loadingWarnings = loadResult.getWarnings();
    }

    /**
     * Runs the chatbot, storing entered tasks, listing them on request, and exiting when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new JassaBot(Path.of("data", "jassabot.txt")).run();
    }

    /**
     * Runs the command loop until the user exits or closes the input stream.
     */
    public void run() {
        Ui console = new Ui();
        console.showWelcome();
        console.showLoadingWarnings(loadingWarnings);

        while (!isExitRequested && console.hasNextCommand()) {
            console.showResponse(getResponse(console.readCommand()));
        }
        if (!isExitRequested) {
            console.showInputClosed();
        }
    }

    /**
     * Displays every task in its current list position.
     *
     * @param tasks tasks currently stored by the application.
     */
    private void showTaskList(TaskList tasks) {
        ui.showTaskList(tasks.asList());
    }

    /**
     * Displays tasks whose descriptions contain the keyword from a user command.
     *
     * @param command complete find command entered by the user.
     * @param tasks tasks currently stored by the application.
     * @throws JassaBotException if no search keyword was supplied.
     */
    private void findTasks(String command, TaskList tasks) throws JassaBotException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new JassaBotException("Please enter a keyword to find.");
        }
        ui.showMatchingTasks(tasks.find(keyword));
    }

    /**
     * Marks the task selected by a user command as completed.
     *
     * @param command complete mark command entered by the user.
     * @param tasks tasks currently stored by the application.
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved.
     */
    private void markTask(String command, TaskList tasks) throws JassaBotException {
        String number = command.substring("mark".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            commandType = CommandType.UNKNOWN;
            ui.showInvalidTaskNumber();
            return;
        }

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        tasks.mark(index);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            if (!wasDone) {
                tasks.unmark(index);
            }
            // A failed save must restore the completion state promised by the rollback message.
            assert task.isDone() == wasDone : "Mark rollback must restore the previous completion state.";
            throw createSaveException();
        }
        ui.showTaskMarked(task);
    }

    /**
     * Marks the task selected by a user command as incomplete.
     *
     * @param command complete unmark command entered by the user.
     * @param tasks tasks currently stored by the application.
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved.
     */
    private void unmarkTask(String command, TaskList tasks) throws JassaBotException {
        String number = command.substring("unmark".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            commandType = CommandType.UNKNOWN;
            ui.showInvalidTaskNumber();
            return;
        }

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        tasks.unmark(index);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            if (wasDone) {
                tasks.mark(index);
            }
            // This also covers unmarking a task that was already incomplete.
            assert task.isDone() == wasDone : "Unmark rollback must restore the previous completion state.";
            throw createSaveException();
        }
        ui.showTaskUnmarked(task);
    }

    /**
     * Deletes the task selected by a user command.
     *
     * @param command complete delete command entered by the user.
     * @param tasks tasks currently stored by the application.
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved.
     */
    private void deleteTask(String command, TaskList tasks) throws JassaBotException {
        String number = command.substring("delete".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            commandType = CommandType.UNKNOWN;
            ui.showInvalidTaskNumber();
            return;
        }

        Task removedTask = tasks.remove(index);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            tasks.add(index, removedTask);
            throw createSaveException();
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Validates a deadline command and adds the resulting task.
     *
     * @param command complete deadline command entered by the user.
     * @param tasks tasks currently stored by the application.
     * @throws JassaBotException if the description, due-time marker, or date is invalid.
     */
    private void addDeadline(String command, TaskList tasks)
            throws JassaBotException {
        int byIndex = findMarker(command, " /by");
        String description = byIndex == -1 ? command.substring("deadline".length()).trim()
                : command.substring("deadline".length(), byIndex).trim();

        if (description.isEmpty()) {
            throw new JassaBotException("The description of a deadline cannot be empty.");
        }
        if (byIndex == -1) {
            throw new JassaBotException("A deadline needs '/by' followed by its due time.");
        }
        String byDateTimeText = command.substring(byIndex + " /by".length()).trim();
        if (byDateTimeText.isEmpty()) {
            throw new JassaBotException("A deadline needs '/by' followed by its due time.");
        }

        LocalDateTime by = Parser.parseDateTime(byDateTimeText);
        addTask(new Deadline(description, by), tasks);
    }

    /**
     * Validates an event command and adds the resulting task.
     *
     * @param command complete event command entered by the user.
     * @param tasks tasks currently stored by the application.
     * @throws JassaBotException if the description, time markers, or dates are invalid.
     */
    private void addEvent(String command, TaskList tasks)
            throws JassaBotException {
        int fromIndex = findMarker(command, " /from");
        int toIndex = findMarker(command, " /to");
        String description = fromIndex == -1 ? command.substring("event".length()).trim()
                : command.substring("event".length(), fromIndex).trim();

        if (description.isEmpty()) {
            throw new JassaBotException("The description of an event cannot be empty.");
        }
        if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
            throw new JassaBotException("An event needs both '/from' and '/to' time markers.");
        }
        String fromDateTimeText = command.substring(
                fromIndex + " /from".length(), toIndex).trim();
        String toDateTimeText = command.substring(toIndex + " /to".length()).trim();
        if (fromDateTimeText.isEmpty() || toDateTimeText.isEmpty()) {
            throw new JassaBotException(
                    "An event needs non-empty times after both '/from' and '/to'.");
        }

        LocalDateTime from = Parser.parseDateTime(fromDateTimeText);
        LocalDateTime to = Parser.parseDateTime(toDateTimeText);
        addTask(new Event(description, from, to), tasks);
    }

    /**
     * Validates a todo command and adds the resulting task.
     *
     * @param command complete todo command entered by the user.
     * @param tasks tasks currently stored by the application.
     * @throws JassaBotException if the description is missing.
     */
    private void addTodo(String command, TaskList tasks)
            throws JassaBotException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new JassaBotException("The description of a todo cannot be empty.");
        }

        addTask(new Todo(description), tasks);
    }

    /**
     * Adds and saves a validated task, removing it again if persistence fails.
     *
     * @param task Task to add.
     * @param tasks Tasks currently stored by the application.
     * @throws JassaBotException If the changed list cannot be saved.
     */
    private void addTask(Task task, TaskList tasks) throws JassaBotException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            // Rollback removes the last task, so it must still be the task just appended.
            assert tasks.get(tasks.size() - 1) == task : "Rollback must remove the new task.";
            tasks.remove(tasks.size() - 1);
            throw createSaveException();
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Finds a command marker only when it ends at whitespace or at the end of the command.
     *
     * @param command complete user command.
     * @param marker marker including its required leading space.
     * @return index of the marker, or {@code -1} if it is absent.
     */
    private static int findMarker(String command, String marker) {
        // Callers supply fixed markers; an empty marker would prevent the search from advancing.
        assert marker != null && !marker.isEmpty() : "A command marker must be non-empty.";
        int searchFrom = 0;
        while (searchFrom < command.length()) {
            int markerIndex = command.indexOf(marker, searchFrom);
            if (markerIndex == -1) {
                return -1;
            }
            int afterMarker = markerIndex + marker.length();
            if (afterMarker == command.length()
                    || Character.isWhitespace(command.charAt(afterMarker))) {
                return markerIndex;
            }
            searchFrom = afterMarker;
        }
        return -1;
    }

    /**
     * Creates the shared user-facing error used after a failed transactional save.
     *
     * @return save error that explains the rollback.
     */
    private static JassaBotException createSaveException() {
        return new JassaBotException(
                "I couldn't save your tasks, so no changes were made.");
    }

    /**
     * Converts a user-entered, one-based task number into a valid array index.
     *
     * @param number the task number entered by the user.
     * @param numberOfTasks the number of tasks currently stored.
     * @return the zero-based task index, or {@code -1} if the number is invalid.
     */
    private static int getTaskIndex(String number, int numberOfTasks) {
        // The count comes from TaskList.size(), independently of the user's task-number input.
        assert numberOfTasks >= 0 : "The task count must not be negative.";
        try {
            int taskNumber = Integer.parseInt(number.trim());
            if (taskNumber < 1 || taskNumber > numberOfTasks) {
                return -1;
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Executes one command using the same task list and storage in either interface.
     * Returns display text without reading standard input or printing to the console.
     *
     * @param input Complete user input, including any surrounding whitespace.
     * @return Confirmation or recoverable error message.
     */
    public String getResponse(String input) {
        response.setLength(0);
        commandType = CommandType.UNKNOWN;
        if (isExitRequested) {
            ui.showGoodbye();
            return response.toString().stripTrailing();
        }
        String command = input.trim();
        try {
            if (command.isEmpty()) {
                throw new JassaBotException("Please enter a command.");
            }
            commandType = Parser.parseCommandType(command);
            executeCommand(command);
        } catch (JassaBotException e) {
            commandType = CommandType.UNKNOWN;
            ui.showError(e.getMessage());
        }
        // Every command handler, including an error handler, owes the user a visible response.
        assert !response.toString().isBlank() : "Every command must produce a response.";
        return response.toString().stripTrailing();
    }

    /**
     * Dispatches a recognized command to its task operation or exit action.
     *
     * @param command Trimmed user command whose type has already been parsed.
     * @throws JassaBotException If the command is unknown or its operation fails.
     */
    private void executeCommand(String command) throws JassaBotException {
        switch (commandType) {
            case HELP -> ui.showHelp();
            case BYE -> {
                isExitRequested = true;
                ui.showGoodbye();
            }
            case LIST -> showTaskList(tasks);
            case MARK -> markTask(command, tasks);
            case UNMARK -> unmarkTask(command, tasks);
            case DELETE -> deleteTask(command, tasks);
            case FIND -> findTasks(command, tasks);
            case DEADLINE -> addDeadline(command, tasks);
            case EVENT -> addEvent(command, tasks);
            case TODO -> addTodo(command, tasks);
            default -> throw new JassaBotException(
                    "I couldn't understand that command. Try help to see what you can do.");
        }
    }

    /**
     * Returns the GUI greeting together with any warnings from loading saved tasks.
     */
    public String getWelcome() {
        response.setLength(0);
        ui.showWelcome();
        ui.showLoadingWarnings(loadingWarnings);
        return response.toString().stripTrailing();
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public boolean isExitRequested() {
        return isExitRequested;
    }
}
