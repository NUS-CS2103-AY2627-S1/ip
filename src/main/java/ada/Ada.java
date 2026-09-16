package ada;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serves as the main application class for Ada task‑manager.
 * Coordinates user interface, input parsing, task operations and file storage.
 * Initializes core components, loads saved‑tasks, and runs the main interactive loop
 * to process user commands until receiving the "bye" command.
 */
public class Ada {
    private final Ui ui;
    private final Storage storage;
    private final TaskList taskList;
    private final Parser parser;
    private boolean lastResponseWasError;

    /**
     * Constructs an Ada application instance.
     * Initializes UI, storage, parser, and loads existing tasks from disk into TaskList.
     */
    public Ada() {
        ui = new Ui();
        storage = new Storage();
        ArrayList<Task> loaded = storage.load();
        taskList = new TaskList(loaded);
        parser = new Parser();
    }

    /**
     * Starts the main‑event loop of the program.
     * Continuously reads user input, parses and executes commands.
     * Handles runtime‑specific exceptions and terminates on "bye" command.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            try {
                String input = ui.readCommand();
                Parser.CommandInfo cmd = parser.parse(input);

                switch (cmd.getCommand()) {
                    case "bye":
                        ui.showBye();
                        return;
                    case "list":
                        ui.showTaskList(taskList.getAllTasks());
                        break;
                    case "mark":
                        Task markedTask = markTask(cmd.getTaskNum());
                        ui.showMarkDone(markedTask);
                        break;
                    case "unmark":
                        Task unmarkedTask = unmarkTask(cmd.getTaskNum());
                        ui.showMarkUndone(unmarkedTask);
                        break;
                    case "todo":
                        Todo todo = addTodoTask(cmd.getDescription());
                        ui.showAddTask(todo, taskList.size());
                        break;
                    case "deadline":
                        try {
                            Deadline deadline = addDeadlineTask(cmd);
                            ui.showAddTask(deadline, taskList.size());
                        } catch (DateTimeException e) {
                            ui.showDateTimeErrorDeadline();
                        }
                        break;
                    case "event":
                        try {
                            Event event = addEventTask(cmd);
                            ui.showAddTask(event, taskList.size());
                        } catch (DateTimeException
                                 | NumberFormatException
                                 | IndexOutOfBoundsException e) {
                            ui.showDateTimeErrorEvent();
                        }
                        break;
                    case "delete":
                        Task removed = deleteTask(cmd.getTaskNum());
                        ui.showDeleteTask(removed, taskList.size());
                        break;
                    case "find":
                        String keyword = cmd.getKeyword();
                        List<Task> matched = taskList.matchTasksByKeyword(keyword);
                        ui.showFindResult(matched);
                        break;
                    case "sort":
                        taskList.sortTasks();
                        storage.save(taskList.getAllTasks());
                        ui.showSortedTaskList(taskList.getAllTasks());
                        break;
                    default:
                        ui.showUnknownCommand();
                }
            } catch (AdaException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Processes one user command and returns Ada's response for the GUI.
     *
     * @param input command entered by the user
     * @return response to display in the GUI
     */
    public String getResponse(String input) {
        lastResponseWasError = false;

        try {
            Parser.CommandInfo cmd = parser.parse(input);

            assert cmd != null && cmd.getCommand() != null : "The parser must return a command with a command name";

            switch (cmd.getCommand()) {
                case "bye":
                    return ui.getByeText();
                case "list":
                    return ui.getTaskListText(taskList.getAllTasks());
                case "mark":
                    Task markedTask = markTask(cmd.getTaskNum());
                    return ui.getMarkDoneText(markedTask);
                case "unmark":
                    Task unmarkedTask = unmarkTask(cmd.getTaskNum());
                    return ui.getMarkUndoneText(unmarkedTask);
                case "todo":
                    Todo todo = addTodoTask(cmd.getDescription());
                    return ui.getAddTaskText(todo, taskList.size());
                case "deadline":
                    try {
                        Deadline deadline = addDeadlineTask(cmd);
                        return ui.getAddTaskText(deadline, taskList.size());
                    } catch (DateTimeException e) {
                        lastResponseWasError = true;
                        return ui.getDateTimeErrorDeadlineText();
                    }
                case "event":
                    try {
                        Event event = addEventTask(cmd);
                        return ui.getAddTaskText(event, taskList.size());
                    } catch (DateTimeException
                            | NumberFormatException
                            | IndexOutOfBoundsException e) {
                        lastResponseWasError = true;
                        return ui.getDateTimeErrorEventText();
                    }

                case "delete":
                    Task removed = deleteTask(cmd.getTaskNum());
                    return ui.getDeleteTaskText(removed, taskList.size());
                case "find":
                    List<Task> matched = taskList.matchTasksByKeyword(cmd.getKeyword());
                    return ui.getFindResultText(matched);
                case "sort":
                    taskList.sortTasks();
                    storage.save(taskList.getAllTasks());
                    return ui.getSortedTaskListText(taskList.getAllTasks());
                default:
                    lastResponseWasError = true;
                    return ui.getUnknownCommandText();
            }
        } catch (AdaException e) {
            lastResponseWasError = true;
            return e.getMessage();
        }
    }

    /**
     * Returns Ada's welcome message for the GUI.
     *
     * @return welcome message
     */
    public String getWelcomeText() {
        return ui.getWelcomeText();
    }

    private void validateTaskNumber(int taskNum) throws AdaException {
        if (taskNum < 1 || taskNum > taskList.size()) {
            throw new AdaException(
                    "This task does not exist. Please enter a valid task number.");
        }

        assert taskNum >= 1 && taskNum <= taskList.size() : "A validated task number must refer to an existing task";
    }

    private Deadline addDeadlineTask(Parser.CommandInfo cmd) {
        LocalDateTime byDateTime = parseDateTime(
                cmd.getBy(), DateTimeFormats.DEFAULT_TIME);
        Deadline deadline = new Deadline(cmd.getDescription(), byDateTime);
        taskList.addTask(deadline);
        storage.save(taskList.getAllTasks());
        return deadline;
    }

    private Event addEventTask(Parser.CommandInfo cmd) {
        LocalDateTime start = parseDateTime(
                cmd.getFrom(), DateTimeFormats.DEFAULT_TIME);
        LocalDateTime end = parseEventEnd(cmd.getTo(), start);
        Event event = new Event(cmd.getDescription(), start, end);
        taskList.addTask(event);
        storage.save(taskList.getAllTasks());
        return event;
    }

    private LocalDateTime parseDateTime(String value, LocalTime defaultTime) {
        assert value != null && defaultTime != null : "Date-time parsing requires a value and a fallback time";
        if (value.contains(" ")) {
            return LocalDateTime.parse(
                    value, DateTimeFormats.INPUT_DATE_TIME);
        }

        LocalDate date = LocalDate.parse(
                value, DateTimeFormats.INPUT_DATE);
        return LocalDateTime.of(date, defaultTime);
    }

    private LocalDateTime parseEventEnd(
            String value, LocalDateTime start) {
        assert value != null && start != null : "Event-end parsing requires an end value and event start";

        String trimmedValue = value.trim();

        if (trimmedValue.contains("-")) {
            if (trimmedValue.contains(" ")) {
                return LocalDateTime.parse(
                        trimmedValue, DateTimeFormats.INPUT_DATE_TIME);
            }
            return LocalDateTime.parse(trimmedValue);
        }

        int hour = Integer.parseInt(trimmedValue.substring(0, 2));
        int minute = Integer.parseInt(trimmedValue.substring(2, 4));
        LocalTime time = LocalTime.of(hour, minute);
        return LocalDateTime.of(start.toLocalDate(), time);
    }
    private Task markTask(int taskNum) throws AdaException {
        validateTaskNumber(taskNum);
        taskList.markDone(taskNum - 1);
        Task task = taskList.getTask(taskNum - 1);
        storage.save(taskList.getAllTasks());
        return task;
    }

    private Task unmarkTask(int taskNum) throws AdaException {
        validateTaskNumber(taskNum);
        taskList.markUndone(taskNum - 1);
        Task task = taskList.getTask(taskNum - 1);
        storage.save(taskList.getAllTasks());
        return task;
    }

    private Todo addTodoTask(String description) {
        Todo todo = new Todo(description);
        taskList.addTask(todo);
        storage.save(taskList.getAllTasks());
        return todo;
    }

    private Task deleteTask(int taskNum) throws AdaException {
        validateTaskNumber(taskNum);
        Task removed = taskList.removeTask(taskNum - 1);
        storage.save(taskList.getAllTasks());
        return removed;
    }

    public boolean wasLastResponseError() {
        return this.lastResponseWasError;
    }

    /**
     * Application entry‑point.
     * Creates an Ada instance and launches the interactive program.
     *
     * @param args command‑line arguments (unused)
     */
    public static void main(String[] args) {
        new Ada().run();
    }
}
