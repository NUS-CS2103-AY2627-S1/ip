package tardt.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tardt.Messages;
import tardt.command.Command;
import tardt.exception.TardTException;
import tardt.priority.Priority;
import tardt.storage.Storage;
import tardt.task.Deadline;
import tardt.task.Event;
import tardt.task.Task;
import tardt.task.TaskList;
import tardt.task.ToDo;
import tardt.ui.Ui;

/**
 * Class in charge of parsing user inputs.
 */
public class Parser {
    /** Matches a priority flag at the end of a task command. */
    private static final Pattern PRIORITY_FLAG = Pattern.compile("(?:^|\\s)/priority\\s+(\\S+)\\s*$",
            Pattern.CASE_INSENSITIVE);
    /** Matches the start of a supported task flag anywhere in a task command. */
    private static final Pattern TASK_FLAG_START = Pattern.compile("(?:^|\\s)/(priority|by|from|to)(?=\\s|$)",
            Pattern.CASE_INSENSITIVE);
    private static final String OPTIONAL_PRIORITY_FORMAT = " (optional: /priority low|medium|high)";
    private static final String INVALID_PRIORITY_MESSAGE = "Invalid priority. Priority is optional; when included, use "
            + "/priority low, /priority medium, or /priority high.";
    private static final String REPEATED_FLAG_WARNING = "Repeated flags are not allowed.";

    // ==================== CLI METHOD ====================

    /**
     * Parses and executes one line of input. Returns true if the app should exit.
     * This method is for the CLI version only.
     *
     * @param userInput The user input.
     * @param tasks The list of tasks as a TaskList object.
     * @param ui The Ui object.
     * @param storage The Storage Object.
     * @return True if the response is the goodbye message, False otherwise.
     */
    public static boolean parse(String userInput, TaskList tasks, Ui ui, Storage storage) {
        try {
            String response = parseForResponse(userInput, tasks, storage);
            if (response.equals(Messages.GOODBYE_MESSAGE)) {
                ui.showBye();
                return true;
            }
            ui.showMessage(response);
            return false;
        } catch (TardTException e) {
            ui.showError(e.getMessage());
            return false;
        }
    }

    // ==================== GUI METHOD ====================

    /**
     * Parses user input and returns a response string.
     * This method does NOT use Ui for output — it returns the response instead.
     * Suitable for both GUI and CLI use.
     *
     * @param userInput User input command
     * @param tasks TaskList to operate on
     * @param storage Storage for saving/loading
     * @return Response message as a String
     * @throws TardTException If an error occurs during parsing
     */
    public static String parseForResponse(String userInput, TaskList tasks, Storage storage) throws TardTException {
        assert tasks != null : "The parser must receive the application's task list";
        assert storage != null : "The parser must receive storage for mutating commands";

        if (userInput == null || userInput.isBlank()) {
            throw new TardTException("Please enter a command.");
        }

        // Keep only the first word as the command so that the remaining text can retain its original structure.
        String trimmedInput = userInput.trim();
        int firstWhitespace = findFirstWhitespace(trimmedInput);
        String taskType = firstWhitespace == -1 ? trimmedInput : trimmedInput.substring(0, firstWhitespace);
        String arguments = firstWhitespace == -1 ? "" : trimmedInput.substring(firstWhitespace).trim();
        Command command = Command.fromKeyword(taskType.toLowerCase(Locale.ROOT));

        // Dispatching after normalisation makes command keywords case-insensitive without changing task descriptions.
        switch (command) {
            case BYE:
                requireNoArguments("bye", arguments);
                return Messages.GOODBYE_MESSAGE;
            case LIST:
                requireNoArguments("list", arguments);
                return getTaskListString(tasks);
            case MARK:
                return handleMarkForResponse(tasks, arguments, storage);
            case UNMARK:
                return handleUnmarkForResponse(tasks, arguments, storage);
            case TODO:
                return handleTodoForResponse(tasks, arguments, storage);
            case DEADLINE:
                return handleDeadlineForResponse(tasks, arguments, storage);
            case EVENT:
                return handleEventForResponse(tasks, arguments, storage);
            case DELETE:
                return handleDeleteForResponse(tasks, arguments, storage);
            case FIND:
                return handleFindForResponse(tasks, arguments, storage);
            default:
                throw new TardTException("'" + taskType + "' is not a valid input.\n"
                        + "Valid input formats: \n"
                        + "bye -> exits the interface\n"
                        + "list -> lists all the tasks and their status\n"
                        + "mark [task number] -> marks the task and show their status\n"
                        + "unmark [task number] -> unmarks the task and show their status\n"
                        + "Priority is optional; omit it to use low priority.\n"
                        + "todo [task name]" + OPTIONAL_PRIORITY_FORMAT + " -> adds a todo task to taskList\n"
                        + "deadline [task name] /by [deadline]" + OPTIONAL_PRIORITY_FORMAT
                        + " -> adds a deadline task to taskList\n"
                        + "event [task name] /from [start time] /to [end time]" + OPTIONAL_PRIORITY_FORMAT
                        + " -> adds an event task to taskList\n"
                        + "delete [task number] -> deletes a task from taskList\n"
                        + "find [search string] -> finds a task consisting of the search string");
        }
    }

    /**
     * Finds the first whitespace character in a command without assuming spaces are used as separators.
     *
     * @param input trimmed command text
     * @return the index of the first whitespace character, or -1 when none exists
     */
    private static int findFirstWhitespace(String input) {
        for (int index = 0; index < input.length(); index++) {
            if (Character.isWhitespace(input.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Rejects arguments supplied to a command that does not accept any.
     *
     * @param command command keyword shown in the error message
     * @param arguments text after the command keyword
     * @throws TardTException if arguments were supplied
     */
    private static void requireNoArguments(String command, String arguments) throws TardTException {
        if (!arguments.isEmpty()) {
            throw new TardTException("The '" + command + "' command does not take additional input.");
        }
    }

    /**
     * Gets the task list as a formatted string.
     *
     * @param tasks The TaskList object storing a list of tasks.
     * @return A list of tasks as a String.
     */
    private static String getTaskListString(TaskList tasks) {
        List<Task> taskList = tasks.getTasks();
        if (taskList.isEmpty()) {
            return "Your task list is empty!";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            sb.append(i + 1).append(". ").append(taskList.get(i)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Parses and validates a task index from user input.
     *
     * @param arguments text after the command keyword
     * @param tasks The task list
     * @param prefix The command prefix (e.g., "mark", "unmark")
     * @return The validated task index (0-based)
     * @throws TardTException If the index is invalid
     */
    private static int parseTaskIndex(String arguments, TaskList tasks, String prefix) throws TardTException {
        if (arguments.isEmpty()) {
            throw new TardTException("Missing task number after '" + prefix + "'. Use " + prefix + " [task number].");
        }

        try {
            // User-facing task numbers start at one, while TaskList indices start at zero.
            int idx = Integer.parseInt(arguments) - 1;
            if (idx < 0 || idx >= tasks.size()) {
                throw new TardTException("Task number out of range. There are " + tasks.size() + " tasks.");
            }
            return idx;
        } catch (NumberFormatException e) {
            throw new TardTException("'" + arguments + "' is not a valid integer.");
        }
    }

    /**
     * Handles the MARK command by marking a task as done.
     *
     * @param tasks A list of tasks as a TaskList object.
     * @param userInput The user input.
     * @param storage The Storage object.
     * @return The chatbot's string response.
     * @throws TardTException A unique exception class for TardT.
     */
    private static String handleMarkForResponse(TaskList tasks, String userInput, Storage storage)
            throws TardTException {
        int idx = parseTaskIndex(userInput, tasks, "mark");
        assert idx >= 0 && idx < tasks.size() : "parseTaskIndex must return a valid task index";
        Task task = tasks.get(idx);
        task.markAsDone();
        storage.save(tasks.getTasks());
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /**
     * Handles the UNMARK command by marking a task as undone.
     *
     * @param tasks A list of tasks as a TaskList object.
     * @param userInput The user input.
     * @param storage The Storage object.
     * @return The chatbot's string response.
     * @throws TardTException A unique exception class for TardT.
     */
    private static String handleUnmarkForResponse(TaskList tasks, String userInput, Storage storage)
            throws TardTException {
        int idx = parseTaskIndex(userInput, tasks, "unmark");
        assert idx >= 0 && idx < tasks.size() : "parseTaskIndex must return a valid task index";
        Task task = tasks.get(idx);
        task.markAsNotDone();
        storage.save(tasks.getTasks());
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Parses an optional final /priority flag from task command arguments.
     * Duplicate flags are validated before this method is called.
     *
     * @param input The input string
     * @return A ParsedPriority object encapsulating Task description and Priority value
     * @throws TardTException if the priority is malformed
     */
    private static ParsedPriority parsePriority(String input) throws TardTException {
        // A priority is metadata only when it is the final flag; otherwise the task text stays unambiguous.
        Matcher matcher = PRIORITY_FLAG.matcher(input);
        if (!matcher.find()) {
            if (input.toLowerCase(Locale.ROOT).contains("/priority")) {
                throw new TardTException(INVALID_PRIORITY_MESSAGE);
            }
            return new ParsedPriority(input, Priority.LOW);
        }

        String text = input.substring(0, matcher.start()).trim();
        String keyword = matcher.group(1);
        Priority priority = Priority.fromKeyword(keyword);
        if (text.isEmpty() || priority == null) {
            throw new TardTException(INVALID_PRIORITY_MESSAGE);
        }
        return new ParsedPriority(text, priority);
    }

    /**
     * Rejects input that repeats any supported task flag.
     * Each flag represents one field, so a repeated flag must not become part of a preceding field's text.
     *
     * @param input task command arguments
     * @throws TardTException if a supported task flag appears more than once
     */
    private static void validateNoRepeatedTaskFlags(String input) throws TardTException {
        Matcher matcher = TASK_FLAG_START.matcher(input);
        Set<String> foundFlags = new HashSet<>();
        while (matcher.find()) {
            String flag = matcher.group(1).toLowerCase(Locale.ROOT);
            if (!foundFlags.add(flag)) {
                throw new TardTException(REPEATED_FLAG_WARNING);
            }
        }
    }

    private record ParsedPriority(String text, Priority priority) { }

    /**
     * Handles the TODO command by adding a ToDo Task to taskList.
     *
     * @param tasks A list of tasks as a TaskList object.
     * @param arguments task command arguments
     * @param storage The Storage object.
     * @return The chatbot's string response.
     * @throws TardTException A unique exception class for TardT.
     */
    private static String handleTodoForResponse(TaskList tasks, String arguments, Storage storage)
            throws TardTException {
        validateNoRepeatedTaskFlags(arguments);
        ParsedPriority parsed = parsePriority(arguments);
        String description = parsed.text();

        if (description.isEmpty()) {
            throw new TardTException("Invalid format: Description of todo cannot be empty. Use: todo [task name]"
                    + OPTIONAL_PRIORITY_FORMAT + ".");
        }

        Task newTask = new ToDo(description, parsed.priority());
        int oldSize = tasks.size();
        tasks.add(newTask);
        assert tasks.size() == oldSize + 1 : "A successful todo command must add one task";

        storage.save(tasks.getTasks());
        return "Got it. I've added this task:\n  " + newTask + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Handles the DEADLINE command by adding a Deadline Task to taskList.
     *
     * @param tasks A list of tasks as a TaskList object.
     * @param arguments text after the command keyword
     * @param storage The Storage object.
     * @return The chatbot's string response.
     * @throws TardTException A unique exception class for TardT.
     */
    private static String handleDeadlineForResponse(TaskList tasks, String arguments, Storage storage)
            throws TardTException {
        validateNoRepeatedTaskFlags(arguments);
        ParsedPriority parsed = parsePriority(arguments);
        String rest = parsed.text();

        int byIndex = rest.indexOf(" /by ");
        if (byIndex == -1) {
            throw new TardTException("Invalid format. Use: deadline [task name] /by [deadline]"
                    + OPTIONAL_PRIORITY_FORMAT + ".");
        }

        String description = rest.substring(0, byIndex).trim();
        String by = rest.substring(byIndex + 5).trim();

        if (description.isEmpty()) {
            throw new TardTException("Please provide a task description.");
        }

        if (by.isEmpty()) {
            throw new TardTException("Please provide a deadline.");
        }

        Task newTask = new Deadline(description, by, parsed.priority());
        int oldSize = tasks.size();
        tasks.add(newTask);
        assert tasks.size() == oldSize + 1 : "A successful deadline command must add one task";

        storage.save(tasks.getTasks());
        return "Got it. I've added this task:\n  " + newTask + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Handles the EVENT command by adding an Event Task to taskList.
     *
     * @param tasks A list of tasks as a TaskList object.
     * @param arguments task command arguments
     * @param storage The Storage object.
     * @return The chatbot's string response.
     * @throws TardTException A unique exception class for TardT.
     */
    private static String handleEventForResponse(TaskList tasks, String arguments, Storage storage)
            throws TardTException {
        validateNoRepeatedTaskFlags(arguments);
        ParsedPriority parsed = parsePriority(arguments);
        String rest = parsed.text();

        int fromIndex = rest.indexOf(" /from ");
        if (fromIndex == -1) {
            throw new TardTException("Invalid format. Use: event [task name] /from [start] /to [end]"
                    + OPTIONAL_PRIORITY_FORMAT + ".");
        }

        String description = rest.substring(0, fromIndex).trim();
        String afterDesc = rest.substring(fromIndex + 7).trim();

        int toIndex = afterDesc.indexOf(" /to ");
        if (toIndex == -1) {
            throw new TardTException("Invalid format. Use: event [task name] /from [start] /to [end]"
                    + OPTIONAL_PRIORITY_FORMAT + ".");
        }

        String from = afterDesc.substring(0, toIndex).trim();
        String to = afterDesc.substring(toIndex + 5).trim();

        if (description.isEmpty()) {
            throw new TardTException("Please provide a task description.");
        }

        if (from.isEmpty()) {
            throw new TardTException("Please provide a start time.");
        }

        if (to.isEmpty()) {
            throw new TardTException("Please provide an end time.");
        }

        Task newTask = new Event(description, from, to, parsed.priority());
        int oldSize = tasks.size();
        tasks.add(newTask);
        assert tasks.size() == oldSize + 1 : "A successful event command must add one task";

        storage.save(tasks.getTasks());
        return "Got it. I've added this task:\n  " + newTask + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Handles the DELETE command by removing a Task from taskList.
     *
     * @param tasks A list of tasks as a TaskList object.
     * @param arguments text after the command keyword
     * @param storage The Storage object.
     * @return The chatbot's string response.
     * @throws TardTException A unique exception class for TardT.
     */
    private static String handleDeleteForResponse(TaskList tasks, String arguments, Storage storage)
            throws TardTException {
        int idx = parseTaskIndex(arguments, tasks, "delete");
        assert idx >= 0 && idx < tasks.size() : "parseTaskIndex must return a valid task index";
        int oldSize = tasks.size();
        Task task = tasks.delete(idx);
        assert tasks.size() == oldSize - 1 : "A successful delete command must remove one task";

        storage.save(tasks.getTasks());
        return "Noted. I've removed this task:\n  " + task + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Handles the FIND command by filtering the Tasks matching the input.
     *
     * @param tasks A list of tasks as a TaskList object.
     * @param arguments search text after the command keyword
     * @param storage The Storage object.
     * @return The chatbot's string response.
     * @throws TardTException A unique exception class for TardT.
     */
    private static String handleFindForResponse(TaskList tasks, String arguments, Storage storage)
            throws TardTException {
        if (arguments.isEmpty()) {
            throw new TardTException("Missing search string after 'find'.");
        }

        String keyword = arguments;

        // Search descriptions only, because users enter the task text rather than its generated display fields.
        List<Task> matches = tasks.getTasks().stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();

        if (matches.isEmpty()) {
            return "No tasks found matching: " + keyword;
        }

        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matches.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(matches.get(i)).append("\n");
        }
        return sb.toString();
    }
}
