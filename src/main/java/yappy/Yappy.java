package yappy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import yappy.command.Command;
import yappy.command.Parser;
import yappy.exception.YappyException;
import yappy.storage.Storage;
import yappy.task.Task;
import yappy.task.TaskList;
import yappy.ui.Ui;

/**
 * Coordinates Yappy's user interface, command processing, task list, and storage.
 */
public class Yappy {
    /** Style hint for commands that add a task. */
    private static final String ADD_COMMAND = "AddCommand";

    /** Style hint for commands that change whether a task is done. */
    private static final String CHANGE_MARK_COMMAND = "ChangeMarkCommand";

    /** Style hint for commands that delete a task. */
    private static final String DELETE_COMMAND = "DeleteCommand";

    /** Style hint for commands that display reminders. */
    private static final String REMINDER_COMMAND = "ReminderCommand";

    /** Style hint for responses that report invalid input or storage failures. */
    private static final String ERROR_COMMAND = "ErrorCommand";

    /** Style hint for a response after which the GUI should close. */
    private static final String EXIT_COMMAND = "ExitCommand";

    private static final Path DATA_FILE = Paths.get("data", "yappy.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final int skippedRecordCount;
    private final boolean hasLoadFailed;

    private String commandType;

    /**
     * Creates a Yappy chatbot using its default relative data-file location.
     */
    public Yappy() {
        this(DATA_FILE);
    }

    /**
     * Creates a Yappy chatbot backed by the specified data file.
     */
    public Yappy(Path dataFile) {
        this.storage = new Storage(dataFile);
        this.ui = new Ui();

        TaskList loadedTasks;
        int skippedRecords = 0;
        boolean hasLoadingFailed = false;
        try {
            loadedTasks = new TaskList(storage.load());
            skippedRecords = storage.getSkippedRecordCount();
        } catch (IOException e) {
            loadedTasks = new TaskList();
            hasLoadingFailed = true;
        }
        this.tasks = loadedTasks;
        this.skippedRecordCount = skippedRecords;
        this.hasLoadFailed = hasLoadingFailed;
    }

    /**
     * Starts Yappy using its default relative data-file location.
     */
    public static void main(String[] args) {
        new Yappy().run();
    }

    /**
     * Reads and executes commands until the user exits or the input stream ends.
     */
    public void run() {
        ui.showWelcome();
        if (hasLoadFailed) {
            ui.showLoadingError();
        } else if (skippedRecordCount > 0) {
            ui.showSkippedRecords(skippedRecordCount);
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            Command command = Parser.getCommand(input);
            if (command == Command.BYE) {
                try {
                    Parser.parseNoArguments(input, Command.BYE);
                    break;
                } catch (YappyException e) {
                    ui.showLine();
                    ui.showError(e.getMessage());
                    ui.showLine();
                    continue;
                }
            }

            ui.showLine();
            try {
                ui.showMessage(processInput(input));
                if (changesTaskList(command)) {
                    storage.save(tasks);
                }
            } catch (YappyException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showSavingError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.showGoodbye();
    }

    /**
     * Returns Yappy's greeting and any warning raised while loading saved tasks.
     */
    public String getWelcomeMessage() {
        commandType = null;
        if (hasLoadFailed) {
            commandType = ERROR_COMMAND;
            return ui.getWelcome() + "\n" + ui.getLoadingError();
        }
        if (skippedRecordCount > 0) {
            commandType = ERROR_COMMAND;
            return ui.getWelcome() + "\n" + ui.getSkippedRecords(skippedRecordCount);
        }
        return ui.getWelcome();
    }

    /**
     * Executes a GUI command and returns the response Yappy should display.
     */
    public String getResponse(String input) {
        String trimmedInput = input.trim();
        commandType = null;
        try {
            Command command = Parser.getCommand(trimmedInput);
            if (command == Command.BYE) {
                Parser.parseNoArguments(trimmedInput, Command.BYE);
                commandType = EXIT_COMMAND;
                return ui.getGoodbye();
            }

            String response = processInput(trimmedInput);
            if (changesTaskList(command)) {
                storage.save(tasks);
            }
            return response;
        } catch (YappyException e) {
            commandType = ERROR_COMMAND;
            return ui.getError(e.getMessage());
        } catch (IOException e) {
            commandType = ERROR_COMMAND;
            return ui.getSavingError(e.getMessage());
        }
    }

    /**
     * Returns a hint describing the command handled by the latest GUI response.
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns whether the latest GUI response should be followed by closing the application.
     */
    public boolean shouldExit() {
        return EXIT_COMMAND.equals(commandType);
    }

    /**
     * Executes one non-exit command and returns Yappy's response.
     */
    private String processInput(String input) throws YappyException {
        if (input.isEmpty()) {
            throw new YappyException("OOPS!!! Please type a command.");
        }

        Command command = Parser.getCommand(input);
        switch (command) {
            case LIST:
                Parser.parseNoArguments(input, Command.LIST);
                return ui.getTaskList(tasks);
            case MARK:
                return markTask(input);
            case UNMARK:
                return unmarkTask(input);
            case DELETE:
                return deleteTask(input);
            case FIND:
                return findTasks(input);
            case REMIND:
                return showReminders(input);
            case TODO:
                return addTask(Parser.parseTodo(input));
            case DEADLINE:
                return addTask(Parser.parseDeadline(input));
            case EVENT:
                return addTask(Parser.parseEvent(input));
            default:
                throw new YappyException("OOPS!!! I don't know what that means. "
                        + "Try todo, deadline, event, list, find, remind, mark, unmark, or delete.");
        }
    }

    /**
     * Returns whether the command changes task data that must be saved.
     */
    private boolean changesTaskList(Command command) {
        return command == Command.MARK
                || command == Command.UNMARK
                || command == Command.DELETE
                || command == Command.TODO
                || command == Command.DEADLINE
                || command == Command.EVENT;
    }

    /**
     * Adds a task and returns a confirmation message.
     */
    private String addTask(Task task) {
        tasks.add(task);
        commandType = ADD_COMMAND;
        return ui.getTaskAdded(task, tasks.size());
    }

    /**
     * Marks the requested task as done and returns a confirmation message.
     */
    private String markTask(String input) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.MARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        commandType = CHANGE_MARK_COMMAND;
        return ui.getTaskMarked(task);
    }

    /**
     * Marks the requested task as not done yet and returns a confirmation message.
     */
    private String unmarkTask(String input) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.UNMARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        commandType = CHANGE_MARK_COMMAND;
        return ui.getTaskUnmarked(task);
    }

    /**
     * Deletes the requested task and returns a confirmation message.
     */
    private String deleteTask(String input) throws YappyException {
        int index = Parser.parseTaskIndex(input, Command.DELETE, tasks.size());
        Task removedTask = tasks.remove(index);
        commandType = DELETE_COMMAND;
        return ui.getTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Returns the tasks whose descriptions contain the requested keyword.
     */
    private String findTasks(String input) throws YappyException {
        String keyword = Parser.parseFindKeyword(input);
        return ui.getMatchingTasks(tasks.find(keyword));
    }

    /**
     * Returns incomplete deadlines that fall between today and the requested future date.
     */
    private String showReminders(String input) throws YappyException {
        int daysAhead = Parser.parseReminderDays(input);
        commandType = REMINDER_COMMAND;
        return ui.getUpcomingDeadlines(tasks.getUpcomingDeadlines(LocalDate.now(), daysAhead), daysAhead);
    }
}
