package duke;

import java.time.LocalDate;
import java.util.Scanner;

import duke.exception.DukeException;
import duke.parser.Parser;
import duke.storage.Storage;
import duke.task.Task;
import duke.task.TaskList;
import duke.ui.Response;
import duke.ui.Ui;

/**
 * Entry point for the geen chatbot.
 */
public class Duke {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private String startupError;

    /**
     * Creates a chatbot that saves tasks in the given file.
     *
     * @param filePath Relative or absolute path of the save file.
     */
    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = loadTasks();
    }

    /**
     * Loads tasks saved from earlier runs.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (DukeException e) {
            startupError = e.getMessage();
            return new TaskList();
        }
    }

    /**
     * Greets the user and handles commands until the user enters "bye".
     * Commands are read from standard input and responses are printed to standard output.
     */
    public void run() {
        System.out.println(getGreeting());
        handleCommands();
    }

    /**
     * Starts the chatbot.
     */
    public static void main(String[] args) {
        new Duke("data/duke.txt").run();
    }

    /**
     * Reads user commands and performs the requested action.
     */
    private void handleCommands() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            Response response = getReply(command);

            System.out.println(response.text());
            if (response.isExit()) {
                break;
            }
        }

        scanner.close();
    }

    /**
     * Returns the chatbot's opening message.
     */
    public String getGreeting() {
        if (startupError == null) {
            return ui.formatGreeting();
        }

        return ui.formatError(startupError) + "\n" + ui.formatGreeting();
    }

    /**
     * Returns the chatbot's response to one user command.
     */
    public String getResponse(String command) {
        return getReply(command).text();
    }

    /**
     * Returns a reply with explicit error and exit status for GUI presentation.
     * Leading and trailing whitespace is ignored consistently in both interfaces.
     *
     * @param command User input, which may be blank.
     * @return Reply text and the outcome of the command.
     */
    public Response getReply(String command) {
        String normalizedCommand = command == null ? "" : command.trim();
        try {
            return new Response(handleCommand(normalizedCommand), false, normalizedCommand.equals("bye"));
        } catch (DukeException e) {
            return new Response(ui.formatError(e.getMessage()), true, false);
        }
    }

    /**
     * Returns a startup warning separately so the GUI can highlight it without styling the greeting as an error.
     *
     * @return Warning reply, or null when loading succeeded.
     */
    public Response getStartupWarning() {
        return startupError == null ? null : new Response(ui.formatError(startupError), true, false);
    }

    /**
     * Performs the action requested by one user command.
     */
    private String handleCommand(String command) throws DukeException {
        Parser.CommandType commandType = Parser.parseCommandType(command);
        assert commandType != null : "Parsed command type should not be null";

        if (commandType == Parser.CommandType.EXIT) {
            return ui.formatGoodbye();
        } else if (commandType == Parser.CommandType.LIST) {
            return formatTaskList();
        } else if (commandType == Parser.CommandType.DELETE) {
            return deleteTask(command);
        } else if (commandType == Parser.CommandType.MARK) {
            return markTask(command);
        } else if (commandType == Parser.CommandType.UNMARK) {
            return unmarkTask(command);
        } else if (commandType == Parser.CommandType.FIND) {
            return findTasks(command);
        } else if (commandType == Parser.CommandType.SCHEDULE) {
            return viewSchedule(command);
        } else if (commandType == Parser.CommandType.TODO) {
            return addToDo(command);
        } else if (commandType == Parser.CommandType.DEADLINE) {
            return addDeadline(command);
        } else if (commandType == Parser.CommandType.EVENT) {
            return addEvent(command);
        }

        assert false : "Every command type should be handled";
        throw new DukeException("Sorry, I don't understand that command. Try todo, deadline, event, list, "
                + "mark, unmark, delete, find, schedule, or bye.");
    }

    /**
     * Creates a todo task from the user command.
     */
    private String addToDo(String command) throws DukeException {
        return addTask(Parser.parseToDo(command));
    }

    /**
     * Finds tasks matching the keyword in the user command.
     */
    private String findTasks(String command) throws DukeException {
        String keyword = Parser.parseFindKeyword(command);
        return ui.formatMatchingTasks(tasks.find(keyword));
    }

    /**
     * Returns the deadlines and events scheduled on the requested date.
     */
    private String viewSchedule(String command) throws DukeException {
        LocalDate date = Parser.parseScheduleDate(command);
        return ui.formatSchedule(date, tasks.getScheduledTasks(date));
    }

    /**
     * Creates a deadline task from the user command.
     */
    private String addDeadline(String command) throws DukeException {
        return addTask(Parser.parseDeadline(command));
    }

    /**
     * Creates an event task from the user command.
     */
    private String addEvent(String command) throws DukeException {
        return addTask(Parser.parseEvent(command));
    }

    /**
     * Stores a task and confirms that it was added.
     */
    private String addTask(Task task) throws DukeException {
        tasks.add(task);
        saveTaskList();

        return ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Saves the current task list to persistent storage.
     */
    private void saveTaskList() throws DukeException {
        storage.saveTasks(tasks.getTasks());
    }

    /**
     * Deletes the selected task from the list.
     */
    private String deleteTask(String command) throws DukeException {
        int taskIndex = Parser.parseTaskIndex(command, Parser.CommandType.DELETE, tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        saveTaskList();

        return ui.formatTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Marks the selected task as done.
     */
    private String markTask(String command) throws DukeException {
        int taskIndex = Parser.parseTaskIndex(command, Parser.CommandType.MARK, tasks.size());

        Task task = tasks.mark(taskIndex);
        saveTaskList();

        return ui.formatTaskMarked(task);
    }

    /**
     * Marks the selected task as not done.
     */
    private String unmarkTask(String command) throws DukeException {
        int taskIndex = Parser.parseTaskIndex(command, Parser.CommandType.UNMARK, tasks.size());

        Task task = tasks.unmark(taskIndex);
        saveTaskList();

        return ui.formatTaskUnmarked(task);
    }

    /**
     * Returns all stored tasks in the order they were added.
     */
    private String formatTaskList() {
        return ui.formatTaskList(tasks);
    }
}
