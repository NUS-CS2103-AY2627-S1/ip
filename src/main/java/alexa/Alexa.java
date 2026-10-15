package alexa;

import java.io.IOException;
import java.nio.file.Path;
import java.util.OptionalInt;

/**
 * Coordinates task commands, storage, and the console or graphical user interface.
 */
public class Alexa {
    /** Command words recognized by Alexa. */
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";

    /** The task collection and its operations. */
    private final TaskList tasks;
    /** The component responsible for console interaction and response formatting. */
    private final Ui ui;
    /** The component responsible for interpreting command text. */
    private final Parser parser;
    /** The component responsible for persistent task data. */
    private final Storage storage;

    /**
     * Creates Alexa with task data stored at the supplied path.
     *
     * @param dataFile The relative location of Alexa's task data file.
     */
    public Alexa(Path dataFile) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(dataFile);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException exception) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Runs Alexa's command loop in the console. */
    public void run() {
        ui.showGreeting();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showResponse(getResponse(command));
            if (command.trim().equals(BYE_COMMAND)) {
                return;
            }
        }
    }

    /**
     * Returns Alexa's greeting for a graphical user interface.
     *
     * @return The greeting message.
     */
    public String getGreeting() {
        return ui.getGreetingMessage();
    }

    /**
     * Processes a command and returns the response without writing to standard output.
     *
     * @param command The command entered by the user.
     * @return The response that should be shown to the user.
     */
    public String getResponse(String command) {
        String trimmedCommand = command.trim();
        if (trimmedCommand.equals(BYE_COMMAND)) {
            return ui.getFarewellMessage();
        }
        try {
            return handleCommand(trimmedCommand);
        } catch (AlexaException exception) {
            return ui.getErrorMessage(exception.getMessage());
        }
    }

    /** Starts Alexa's console interface using the standard relative data-file location. */
    public static void main(String[] args) {
        new Alexa(Path.of("data", "alexa.txt")).run();
    }

    /** Handles a single command and returns its user-facing response. */
    private String handleCommand(String command) throws AlexaException {
        if (command.equals(LIST_COMMAND)) {
            return ui.getTaskListMessage(tasks);
        } else if (parser.isCommand(command, FIND_COMMAND)) {
            String keyword = parser.parseFindKeyword(parser.getArgument(command, FIND_COMMAND));
            return ui.getMatchingTasksMessage(tasks.findTasks(keyword));
        } else if (parser.isCommand(command, TODO_COMMAND)) {
            Task task = addTask(parser.parseTodo(parser.getArgument(command, TODO_COMMAND)));
            return ui.getTaskAddedMessage(task, tasks.size());
        } else if (parser.isCommand(command, DEADLINE_COMMAND)) {
            Task task = addTask(parser.parseDeadline(parser.getArgument(command, DEADLINE_COMMAND)));
            return ui.getTaskAddedMessage(task, tasks.size());
        } else if (parser.isCommand(command, EVENT_COMMAND)) {
            Task task = addTask(parser.parseEvent(parser.getArgument(command, EVENT_COMMAND)));
            return ui.getTaskAddedMessage(task, tasks.size());
        } else if (parser.isCommand(command, MARK_COMMAND)) {
            Task task = updateTaskStatus(parser.getArgument(command, MARK_COMMAND), true);
            return ui.getTaskStatusMessage(task, true);
        } else if (parser.isCommand(command, UNMARK_COMMAND)) {
            Task task = updateTaskStatus(parser.getArgument(command, UNMARK_COMMAND), false);
            return ui.getTaskStatusMessage(task, false);
        } else if (parser.isCommand(command, DELETE_COMMAND)) {
            Task deletedTask = deleteTask(parser.getArgument(command, DELETE_COMMAND));
            return ui.getTaskDeletedMessage(deletedTask, tasks.size());
        } else {
            throw new AlexaException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Stores a task, saves the updated list, and returns the task. */
    private Task addTask(Task task) throws AlexaException {
        OptionalInt duplicateTaskNumber = tasks.findDuplicateTaskNumber(task);
        if (duplicateTaskNumber.isPresent()) {
            throw new AlexaException("This task already exists in task "
                    + duplicateTaskNumber.getAsInt() + ".");
        }
        tasks.add(task);
        saveTasks();
        return task;
    }

    /** Updates the completion status of one task and returns it. */
    private Task updateTaskStatus(String numberText, boolean isDone) throws AlexaException {
        String command = isDone ? MARK_COMMAND : UNMARK_COMMAND;
        int taskNumber = parser.parseTaskNumber(numberText, command, tasks.size());
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task number must identify an existing task.";
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.unmark();
        }
        saveTasks();
        return task;
    }

    /** Removes one task from the list, saves it, and returns the removed task. */
    private Task deleteTask(String numberText) throws AlexaException {
        int taskNumber = parser.parseTaskNumber(numberText, DELETE_COMMAND, tasks.size());
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task number must identify an existing task.";
        Task deletedTask = tasks.remove(taskIndex);
        saveTasks();
        return deletedTask;
    }

    /** Saves the current task list and turns write errors into a user-facing message. */
    private void saveTasks() throws AlexaException {
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            throw new AlexaException("I could not save your tasks: " + exception.getMessage());
        }
    }
}