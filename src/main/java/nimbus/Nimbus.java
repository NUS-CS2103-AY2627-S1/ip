package nimbus;

import nimbus.exception.NimbusException;
import nimbus.parser.Command;
import nimbus.parser.Parser;
import nimbus.storage.Storage;
import nimbus.task.Deadline;
import nimbus.task.Event;
import nimbus.task.Task;
import nimbus.task.TaskList;
import nimbus.task.Todo;
import nimbus.ui.Ui;

/**
 * The Nimbus chatbot.
 */
public class Nimbus {

    private static final String TASK_PATH = "data/tasks.txt";
    private final Ui ui;
    private TaskList taskList;
    private final Storage storage;
    private Command commandType;
    private boolean isError;

    /**
     * Creates a Nimbus object and initalizes the Ui and storage.
     * The task list itself is loaded lazily (see {@link #ensureTaskListLoaded()})
     * so that both {@link #run()} and {@link #getResponse(String)} can trigger
     * loading without duplicating the load logic.
     */
    public Nimbus() {
        this.ui = new Ui();
        this.storage = new Storage(TASK_PATH);
        this.commandType = null;
        this.isError = false;
        this.taskList = null;
    }

    /**
     * Runs the chatbot. Command dispatch itself lives in
     * {@link #getResponse(String)}; this loop only handles the CLI-specific
     * concerns of reading input, printing bordered output, and deciding
     * when to stop.
     */
    public void run() {
        ui.showWelcome();
        this.taskList = loadTaskList(true);

        boolean isRunning = true;
        while (isRunning) {
            String input = ui.readCommand();
            String response = getResponse(input);
            ui.printMessage(response);
            // hasError() guards against treating a stale commandType as BYE;
            // see getResponse's javadoc note on why this can't currently
            // happen within this loop, but it's cheap insurance.
            isRunning = !(commandType == Command.BYE && !hasError());
        }

        if (saveTaskList()) {
            ui.showTasksSaved(taskList.getSize());
        }
        ui.showExitMessage();
    }

    /**
     * Returns the response of Nimbus to a given input, for use by front ends
     * (e.g. a GUI) that need the message text without any console output.
     * On the first call, the task list is loaded from storage if it has not
     * been already (see {@link #run()}, which loads it explicitly instead).
     * <p>
     * Note on {@code commandType}: it is only reassigned once
     * {@link Parser#parseCommand(String)} succeeds. If it throws (an
     * unparseable command), {@code commandType} keeps its previous value
     * and {@link #isError} is set instead, so callers checking for
     * {@code Command.BYE} after a call should also check {@link #hasError()}.
     *
     * @param input The user input.
     * @return The response from Nimbus.
     */
    public String getResponse(String input) {
        ensureTaskListLoaded();
        this.isError = false;

        try {
            commandType = Parser.parseCommand(input);
            assert commandType != null : "Parser.parseCommand should never return null";
            String arguments = Parser.parseArguments(input);
            String response;
            switch (commandType) {
                case LIST -> response = ui.formatTaskList(taskList);
                case MARK -> response = handleMarkCommand(arguments);
                case UNMARK -> response = handleUnmarkCommand(arguments);
                case DELETE -> response = handleDeleteCommand(arguments);
                case TODO -> response = handleTodoCommand(arguments);
                case DEADLINE -> response = handleDeadlineCommand(arguments);
                case EVENT -> response = handleEventCommand(arguments);
                case BYE -> {
                    saveTaskList();
                    response = ui.formatExitMessage();
                }
                case FIND -> {
                    TaskList matches = taskList.findTask(arguments);
                    response = ui.formatTaskList(matches);
                }
                case UNKNOWN -> throw new NimbusException("I'm sorry, I don't know what that means.");
                default -> throw new NimbusException("I'm sorry, I don't know what that means.");
            }

            // Persistence for the GUI path (e.g. save-on-close) is not wired up yet.
            return response;
        } catch (NimbusException e) {
            this.isError = true;
            return ui.formatError(e);
        }
    }

    /**
     * Marks the task as completed and returns it.
     *
     * @param arguments The arguments containing the index of the task to mark.
     * @return The formatted message for the marked task.
     * @throws NimbusException If the task is already completed or if there is no
     *                         task at that index.
     */
    public String handleMarkCommand(String arguments) throws NimbusException {
        int taskNumber = Parser.parseIndex(arguments);
        taskList.markTask(taskNumber);
        return ui.formatMarkTask(taskList.getTask(taskNumber));

    }

    /**
     * Unmarks the task as not completed and returns it.
     *
     * @param arguments The arguments containing the index of the task to unmark.
     * @return The formatted message for the unmarked task.
     * @throws NimbusException If there is no task at that index.
     */
    public String handleUnmarkCommand(String arguments) throws NimbusException {
        int taskNumber = Parser.parseIndex(arguments);
        taskList.unmarkTask(taskNumber);
        return ui.formatUnmarkTask(taskList.getTask(taskNumber));
    }

    /**
     * Deletes the task at the given index and returns it.
     *
     * @param arguments The arguments containing the index of the task to delete.
     * @return The formatted message for the deleted task.
     * @throws NimbusException If there is no task at that index.
     */
    public String handleDeleteCommand(String arguments) throws NimbusException {
        int taskNumber = Parser.parseIndex(arguments);
        Task deletedTask = taskList.removeTask(taskNumber);
        return ui.formatDeleteTask(deletedTask, taskList);
    }

    /**
     * Handles the "todo" command by parsing the arguments, creating a Todo task,
     * adding it to the task list, and returning the formatted message.
     *
     * @param arguments The arguments for the "todo" command.
     * @return The formatted message for the added Todo task.
     * @throws NimbusException If there is an error in parsing or adding the task.
     */
    public String handleTodoCommand(String arguments) throws NimbusException {
        Todo todo = Parser.parseTodo(arguments);
        taskList.addTask(todo);
        return ui.formatAddTask(todo, taskList);
    }

    /**
     * Handles the "deadline" command by parsing the arguments, creating a Deadline
     * task,
     * adding it to the task list, and returning the formatted message.
     *
     * @param arguments The arguments for the "deadline" command.
     * @return The formatted message for the added Deadline task.
     * @throws NimbusException If there is an error in parsing or adding the task.
     */
    public String handleDeadlineCommand(String arguments) throws NimbusException {
        Deadline deadline = Parser.parseDeadline(arguments);
        taskList.addTask(deadline);
        return ui.formatAddTask(deadline, taskList);
    }

    /**
     * Handles the "event" command by parsing the arguments, creating an Event
     * task,
     * adding it to the task list, and returning the formatted message.
     *
     * @param arguments The arguments for the "event" command.
     * @return The formatted message for the added Event task.
     * @throws NimbusException If there is an error in parsing or adding the task.
     */
    public String handleEventCommand(String arguments) throws NimbusException {
        Event event = Parser.parseEvent(arguments);
        taskList.addTask(event);
        return ui.formatAddTask(event, taskList);
    }

    /**
     * Returns the welcome message shown at startup, for front ends
     * that don't call run() (e.g. the GUI).
     *
     * @return The welcome message text.
     */
    public String getWelcomeMessage() {
        return ui.formatWelcome();
    }

    /**
     * Loads the task list from storage if it has not been loaded yet.
     * Used by {@link #getResponse(String)} so that a GUI front end can call
     * it directly without first calling {@link #run()}.
     */
    private void ensureTaskListLoaded() {
        if (taskList == null) {
            taskList = loadTaskList(false);
        }
    }

    /**
     * Loads the task list from storage.
     *
     * @param announce Whether to print the outcome via {@link Ui} (the CLI
     *                 path wants this; the GUI path loads silently since
     *                 there is no console for the user to see it).
     * @return The loaded task list, or an empty one if loading failed.
     */
    private TaskList loadTaskList(boolean announce) {
        try {
            TaskList loaded = new TaskList(storage.readFromFile());
            assert loaded != null : "Storage.readFromFile should never return null";
            if (announce) {
                ui.showTasksLoaded(loaded.getSize());
            }
            return loaded;
        } catch (NimbusException e) {
            if (announce) {
                ui.printError(e);
            }
            return new TaskList();
        }
    }

    /**
     * Persists the current task list to storage.
     *
     * @return True if the save succeeded, false otherwise.
     */
    public boolean saveTaskList() {
        try {
            storage.writeToFile(taskList.getTasks());
            return true;
        } catch (NimbusException e) {
            ui.printError(e);
            return false;
        }
    }

    public boolean hasError() {
        return this.isError;
    }

    public Command getCommandType() {
        return this.commandType;
    }

    /**
     * Entry point of the Nimbus application.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Nimbus().run();
    }
}
