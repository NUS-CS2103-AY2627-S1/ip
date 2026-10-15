package ace.parser;

import java.time.LocalDate;

import ace.exception.AceException;
import ace.exception.MissingDescriptionException;
import ace.exception.WrongCommandException;
import ace.exception.WrongTaskNumberException;
import ace.task.Deadline;
import ace.task.Event;
import ace.task.Task;
import ace.task.TaskManager;
import ace.task.Todo;
import ace.ui.Ui;

/**
 * Interprets a single line of raw user input and carries out the corresponding
 * action against the task list, reporting the result through {@link Ui}. This is
 * the one place that knows what each command word means.
 */
public class Parser {
    // Shared opening for every "something's wrong with this command" message,
    // so each throw site below only needs to supply the specific reason and
    // usage hint, not repeat this phrase every time.
    private static final String DISCARD_PREFIX = "Oh no! This card has to be discarded! ";

    /**
     * Parses one line of user input and executes the command it represents.
     * Only responsible for recognizing which command input is and dispatching
     * to that command's own handler — the actual argument parsing, validation,
     * and task-list changes for each command live in its handler method.
     *
     * @param input the raw line of user input.
     * @param taskManager the task list to act on.
     * @param ui the UI to report results and errors through.
     * @return true if the command was "bye" and the program should exit, false otherwise.
     * @throws AceException if the input isn't a recognized, well-formed command.
     */
    public static boolean parseCommand(String input, TaskManager taskManager, Ui ui) throws AceException {
        // "list" and "bye" take no argument, so they're checked with plain equality
        // rather than isCommand — isCommand's "starts with X " branch exists
        // specifically to admit a trailing argument, which these commands don't have.
        if (input.equals("list")) {
            return handleList(taskManager);
        }
        if (input.equals("bye")) {
            return handleBye(ui);
        }
        if (isCommand(input, "mark")) {
            return handleMark(input, taskManager);
        }
        if (isCommand(input, "unmark")) {
            return handleUnmark(input, taskManager);
        }
        if (isCommand(input, "todo")) {
            return handleTodo(input, taskManager, ui);
        }
        if (isCommand(input, "deadline")) {
            return handleDeadline(input, taskManager, ui);
        }
        if (isCommand(input, "event")) {
            return handleEvent(input, taskManager, ui);
        }
        if (isCommand(input, "delete")) {
            return handleDelete(input, taskManager, ui);
        }
        if (isCommand(input, "date")) {
            return handleDate(input, taskManager);
        }
        if (isCommand(input, "find")) {
            return handleFind(input, taskManager);
        }

        throw new WrongCommandException(DISCARD_PREFIX + "I do not understand the command \"" + input + "\"!");
    }

    /**
     * Handles the "list" command: prints every task currently in the list.
     *
     * @param taskManager the task list to print.
     * @return false, since "list" never exits the program.
     */
    private static boolean handleList(TaskManager taskManager) {
        taskManager.printTasks();
        return false;
    }

    /**
     * Handles the "bye" command: prints the farewell message.
     *
     * @param ui the UI to print the farewell message through.
     * @return true, so the caller knows to end the program.
     */
    private static boolean handleBye(Ui ui) {
        ui.showGoodbye();
        return true;
    }

    /**
     * Handles the "mark" command: marks the given task number as completed.
     *
     * @param input the raw line of user input, e.g. "mark 2".
     * @param taskManager the task list to act on.
     * @return false, since "mark" never exits the program.
     * @throws AceException if the task number is missing, non-numeric, or out of range.
     */
    private static boolean handleMark(String input, TaskManager taskManager) throws AceException {
        int taskNumber = parseTaskNumber(extractArgument(input, "mark"), "mark", taskManager);
        taskManager.markTask(taskNumber - 1);
        return false;
    }

    /**
     * Handles the "unmark" command: marks the given task number as not completed.
     *
     * @param input the raw line of user input, e.g. "unmark 2".
     * @param taskManager the task list to act on.
     * @return false, since "unmark" never exits the program.
     * @throws AceException if the task number is missing, non-numeric, or out of range.
     */
    private static boolean handleUnmark(String input, TaskManager taskManager) throws AceException {
        int taskNumber = parseTaskNumber(extractArgument(input, "unmark"), "unmark", taskManager);
        taskManager.unmarkTask(taskNumber - 1);
        return false;
    }

    /**
     * Handles the "todo" command: adds a new to-do with the given description.
     *
     * @param input the raw line of user input, e.g. "todo borrow book".
     * @param taskManager the task list to add to.
     * @param ui the UI to report the result through.
     * @return false, since "todo" never exits the program.
     * @throws MissingDescriptionException if no description is given.
     */
    private static boolean handleTodo(String input, TaskManager taskManager, Ui ui)
            throws MissingDescriptionException {
        String todoDescription = extractArgument(input, "todo");
        if (todoDescription.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "A todo needs a description! Try: todo <description>");
        }

        Task newTodo = new Todo(todoDescription);
        taskManager.addTask(newTodo);
        ui.showTaskAdded(newTodo, "To-Do", taskManager.getCurrentNumberOfTasks());
        return false;
    }

    /**
     * Handles the "deadline" command: adds a new deadline with the given
     * description and due date/time, split on its "/by" marker.
     *
     * @param input the raw line of user input, e.g. "deadline return book /by 2/12/2019 1800".
     * @param taskManager the task list to add to.
     * @param ui the UI to report the result through.
     * @return false, since "deadline" never exits the program.
     * @throws AceException if the description, "/by" marker, or date is missing,
     *     or the date doesn't match an accepted format.
     */
    private static boolean handleDeadline(String input, TaskManager taskManager, Ui ui) throws AceException {
        String deadlineUsage = "Try: deadline <description> /by <date>";
        String card = extractArgument(input, "deadline");
        if (card.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "A deadline needs a description! " + deadlineUsage);
        }

        // Bare marker (no required surrounding spaces), so a missing
        // description or date/time before/after it doesn't hide the
        // marker itself — see the note on handleEvent below for why.
        String byMarker = "/by";
        int byIndex = card.indexOf(byMarker);
        if (byIndex == -1) {
            throw new WrongCommandException(DISCARD_PREFIX + "A deadline needs '/by'! " + deadlineUsage);
        }

        String description = card.substring(0, byIndex).trim();
        String byWhen = card.substring(byIndex + byMarker.length()).trim();

        if (description.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "A deadline needs a description! " + deadlineUsage);
        }
        if (byWhen.isEmpty()) {
            throw new WrongCommandException(
                    DISCARD_PREFIX + "Please give a date after '/by'! " + deadlineUsage);
        }

        Task newDeadline = new Deadline(description, byWhen);
        taskManager.addTask(newDeadline);
        ui.showTaskAdded(newDeadline, "Deadline", taskManager.getCurrentNumberOfTasks());
        return false;
    }

    /**
     * Handles the "event" command: adds a new event with the given
     * description, start time, and end time, split on its "/from" and "/to"
     * markers.
     *
     * @param input the raw line of user input, e.g. "event meeting /from Mon 2pm /to 4pm".
     * @param taskManager the task list to add to.
     * @param ui the UI to report the result through.
     * @return false, since "event" never exits the program.
     * @throws AceException if the description, markers, start time, or end
     *     time are missing, or the markers are out of order.
     */
    private static boolean handleEvent(String input, TaskManager taskManager, Ui ui) throws AceException {
        String eventUsage = "Try: event <description> /from <start> /to <end>";
        String card = extractArgument(input, "event");
        if (card.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "An event needs a description! " + eventUsage);
        }

        // Bare markers (no required surrounding spaces): matching
        // " /from " and " /to " literally meant an empty description,
        // start time, or end time could make the adjacent marker's
        // required space disappear (trimmed away at the edges, or
        // merged into one shared space between two adjacent markers) —
        // the latter previously caused a crash, since the two markers'
        // computed positions could overlap. Explicit emptiness checks
        // below now catch all three cases instead.
        String fromMarker = "/from";
        String toMarker = "/to";
        int fromIndex = card.indexOf(fromMarker);
        int toIndex = card.indexOf(toMarker);
        if (fromIndex == -1 || toIndex == -1 || fromIndex + fromMarker.length() > toIndex) {
            throw new WrongCommandException(
                    DISCARD_PREFIX + "An event needs both '/from' and '/to', with '/from' first! "
                            + eventUsage);
        }

        String description = card.substring(0, fromIndex).trim();
        String startTime = card.substring(fromIndex + fromMarker.length(), toIndex).trim();
        String endTime = card.substring(toIndex + toMarker.length()).trim();

        if (description.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "An event needs a description! " + eventUsage);
        }
        if (startTime.isEmpty() || endTime.isEmpty()) {
            throw new WrongCommandException(
                    DISCARD_PREFIX + "Please give both a start time and an end time! " + eventUsage);
        }

        Task newEvent = new Event(description, startTime, endTime);
        taskManager.addTask(newEvent);
        ui.showTaskAdded(newEvent, "Event", taskManager.getCurrentNumberOfTasks());
        return false;
    }

    /**
     * Handles the "delete" command: removes the given task number from the list.
     *
     * @param input the raw line of user input, e.g. "delete 2".
     * @param taskManager the task list to act on.
     * @param ui the UI to report the result through.
     * @return false, since "delete" never exits the program.
     * @throws AceException if the task number is missing, non-numeric, or out of range.
     */
    private static boolean handleDelete(String input, TaskManager taskManager, Ui ui) throws AceException {
        String card = extractArgument(input, "delete");
        if (card.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "Please give a task number! Try: delete <task number>");
        }

        int taskNumber = parseTaskNumber(card, "delete", taskManager);
        Task deletedTask = taskManager.getTask(taskNumber - 1);
        taskManager.deleteTask(taskNumber - 1);
        ui.showTaskDeleted(deletedTask, taskManager.getCurrentNumberOfTasks());
        return false;
    }

    /**
     * Handles the "date" command: lists every deadline due on the given date.
     *
     * @param input the raw line of user input, e.g. "date 2/12/2019".
     * @param taskManager the task list to search.
     * @return false, since "date" never exits the program.
     * @throws AceException if the date is missing or doesn't match the accepted format.
     */
    private static boolean handleDate(String input, TaskManager taskManager) throws AceException {
        String dateString = extractArgument(input, "date");
        if (dateString.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "Please give a date! Try: date <date>");
        }

        LocalDate targetDate = Deadline.parseDateOnly(dateString);

        taskManager.printTasksByDate(targetDate);
        return false;
    }

    /**
     * Handles the "find" command: lists every task whose description
     * contains the given keyword.
     *
     * @param input the raw line of user input, e.g. "find book".
     * @param taskManager the task list to search.
     * @return false, since "find" never exits the program.
     * @throws MissingDescriptionException if no keyword is given.
     */
    private static boolean handleFind(String input, TaskManager taskManager) throws MissingDescriptionException {
        String keyword = extractArgument(input, "find");
        if (keyword.isEmpty()) {
            throw new MissingDescriptionException(
                    DISCARD_PREFIX + "Please give a keyword to search for! Try: find <keyword>");
        }

        taskManager.printTasksByName(keyword);
        return false;
    }

    /**
     * Returns whether input is the given command, either bare (e.g. "todo") or
     * followed by an argument (e.g. "todo borrow book"). Centralizes the
     * equals-or-starts-with-a-space check every argument-taking command needs,
     * so each one doesn't repeat it (and risk checking only one of the two
     * forms, as mark/unmark/delete previously did).
     *
     * @param input the raw line of user input.
     * @param command the command word to check for, with no trailing space.
     * @return true if input is exactly command, or command followed by a space and more text.
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Returns whatever follows the command word in input, with surrounding
     * whitespace trimmed and any run of internal whitespace (e.g. accidental
     * double spaces) collapsed to a single space. Collapsing internal
     * whitespace also means a marker like " /by " is still found correctly
     * even if the user typed extra spaces around it (e.g. "test  /by  2pm").
     * Assumes {@link #isCommand(String, String)} has already confirmed input
     * actually starts with command.
     *
     * @param input the raw line of user input.
     * @param command the command word to strip off the front.
     * @return the trimmed, whitespace-normalized remainder of input after command.
     */
    private static String extractArgument(String input, String command) {
        return input.substring(command.length()).trim().replaceAll("\\s+", " ");
    }

    /**
     * Parses and validates a task number argument, such as the "5" in "mark 5".
     * Confirms it's a valid integer and refers to a task actually in the list
     * (1-based, matching what the user sees from "list") before handing back a
     * usable 1-based task number.
     *
     * @param input the task number argument, expected to be a plain integer.
     * @param command the command word this argument was given to (e.g.
     *     "mark"), used to build a clear, command-specific error message if
     *     input isn't numeric.
     * @param taskManager the task list, used to check the number is in range.
     * @return the parsed, validated 1-based task number.
     * @throws WrongCommandException if input isn't a valid integer.
     * @throws WrongTaskNumberException if input is a valid integer but out of range.
     */
    private static int parseTaskNumber(String input, String command, TaskManager taskManager)
            throws AceException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new WrongCommandException(
                    DISCARD_PREFIX + "\"" + input + "\" isn't a valid task number! Try: "
                            + command + " <task number>");
        }

        if (taskNumber <= 0 || taskNumber > taskManager.getCurrentNumberOfTasks()) {
            throw new WrongTaskNumberException(taskNumber);
        }

        // Postcondition: every out-of-range case has already been thrown
        // above, so the value about to be returned should always be a valid,
        // in-range task number — this is the guarantee callers (e.g.
        // TaskManager's own bounds assertions) rely on. Asserting it here
        // would catch a bug if a future change to the checks above broke
        // that guarantee.
        assert taskNumber >= 1 && taskNumber <= taskManager.getCurrentNumberOfTasks()
                : "parseTaskNumber is returning an out-of-range value: " + taskNumber;

        return taskNumber;
    }
}
