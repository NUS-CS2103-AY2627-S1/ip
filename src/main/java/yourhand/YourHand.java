package yourhand;

import yourhand.commands.AddTaskCommand;
import yourhand.commands.Command;
import yourhand.commands.DeleteCommand;
import yourhand.commands.ExitCommand;
import yourhand.commands.FindCommand;
import yourhand.commands.HelpCommand;
import yourhand.commands.ListCommand;
import yourhand.commands.TaskStatusCommand;
import yourhand.commands.ViewScheduleCommand;
import yourhand.exceptions.CorruptFileException;
import yourhand.exceptions.InvalidCommandException;
import yourhand.exceptions.InvalidDateException;
import yourhand.exceptions.InvalidTimeException;
import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.Deadline;
import yourhand.tasks.Event;
import yourhand.tasks.Task;
import yourhand.tasks.TaskDateTime;
import yourhand.tasks.TaskList;
import yourhand.tasks.Todo;
import yourhand.ui.Ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A command-line chatbot that manages to-dos, deadlines, and events.
 */
public class YourHand {
    private static final Pattern TODO_PATTERN = Pattern.compile("^todo(?:\\s+(.*))?$");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile("^deadline\\s+(.+?)\\s+/by\\s+(.+)$");
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^event\\s+(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^(mark|unmark)(?:\\s+(.+))?$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^delete(?:\\s+(.+))?$");
    private static final Pattern FIND_PATTERN = Pattern.compile("^find(?:\\s+(.*))?$");
    private static final Pattern VIEW_SCHEDULE_PATTERN = Pattern.compile("^view schedule(?:\\s+(.+))?$");
    private static final Pattern ISO_DATE_ONLY_PATTERN = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$");
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile(
            "^(?:\\d{4}-\\d{1,2}-\\d{1,2}|\\d{1,2}/\\d{1,2}/\\d{4})\\s+.+$");
    private static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-M-d")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_COMPACT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-M-d HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_COLON_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-M-d HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Starts the YourHand command-line application.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcomeMessage();
        Storage storage = new Storage();
        TaskList taskList = loadTasks(storage, ui);
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();
            try {
                Command parsedCommand = parseCommand(command);
                parsedCommand.execute(taskList, ui, storage);
                if (parsedCommand.isExit()) {
                    ui.showSeparator();
                    break;
                }
            } catch (YourHandException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

    /**
     * Converts a user-entered command into an executable command object.
     *
     * @param command user-entered command
     * @return The executable command represented by the input.
     * @throws YourHandException if the command is invalid
     */
    static Command parseCommand(String command) throws YourHandException {
        validateCommand(command);
        if (command.equals("help")) {
            return new HelpCommand();
        }
        if (command.equals("bye")) {
            return new ExitCommand();
        }
        if (command.equals("list")) {
            return new ListCommand();
        }
        Matcher findMatcher = FIND_PATTERN.matcher(command);
        if (findMatcher.matches()) {
            String keyword = findMatcher.group(1);
            if (keyword == null || keyword.isBlank()) {
                throw new YourHandException("Tell me what to find, e.g. find book.");
            }
            return new FindCommand(keyword.trim());
        }
        Matcher scheduleMatcher = VIEW_SCHEDULE_PATTERN.matcher(command);
        if (scheduleMatcher.matches()) {
            String dateText = scheduleMatcher.group(1);
            if (dateText == null || dateText.isBlank()) {
                throw new YourHandException(
                        "Tell me which date to view, e.g. view schedule 2026-09-10.");
            }
            return new ViewScheduleCommand(
                    parseTaskDateTime(dateText.trim()).getValue().toLocalDate());
        }
        Matcher statusMatcher = STATUS_PATTERN.matcher(command);
        if (statusMatcher.matches()) {
            return createTaskStatusCommand(statusMatcher);
        }

        Matcher deleteMatcher = DELETE_PATTERN.matcher(command);
        if (deleteMatcher.matches()) {
            return createDeleteCommand(deleteMatcher);
        }

        return new AddTaskCommand(createTask(command));
    }

    /** Rejects command text that cannot be parsed reliably or stored safely. */
    private static void validateCommand(String command) throws YourHandException {
        if (command == null || command.isBlank()) {
            throw new InvalidCommandException("Please enter a command. Try help to see what I can do.");
        }
        if (!command.equals(command.strip())) {
            throw new InvalidCommandException("Please remove spaces before or after your command.");
        }
        if (command.matches(".*\\s{2,}.*")) {
            throw new InvalidCommandException("Please use only one space between command parts.");
        }
        if (command.chars().anyMatch(Character::isISOControl)) {
            throw new InvalidCommandException("Please remove control characters from your command.");
        }
        if (command.indexOf('|') >= 0) {
            throw new InvalidCommandException("Please don't use | in a command. It is reserved for saved data.");
        }
    }

    /** Updates a task's completion status from a validated mark or unmark command. */
    private static Command createTaskStatusCommand(Matcher matcher) throws YourHandException {
        String taskNumberText = matcher.group(2);
        if (taskNumberText == null || taskNumberText.isBlank()) {
            throw new YourHandException("Don't make me guess — give me a task number, e.g. "
                    + matcher.group(1) + " 2.");
        }

        int taskNumber = parseNumber(
                taskNumberText,
                "Task numbers are whole numbers, not creative writing.");

        boolean isMarkCommand = matcher.group(1).equals("mark");
        return new TaskStatusCommand(taskNumber, isMarkCommand);
    }

    /** Removes the task specified by a validated delete command. */
    private static Command createDeleteCommand(Matcher matcher) throws YourHandException {
        String taskNumberText = matcher.group(1);
        if (taskNumberText == null || taskNumberText.isBlank()) {
            throw new YourHandException("Don't make me guess — give me a task number, e.g. delete 2.");
        }

        int taskNumber = parseNumber(
                taskNumberText,
                "Task numbers are whole numbers, not creative writing.");

        return new DeleteCommand(taskNumber);
    }

    /** Creates a task from a task-creation command. */
    private static Task createTask(String command) throws YourHandException {
        Matcher todoMatcher = TODO_PATTERN.matcher(command);
        if (todoMatcher.matches()) {
            String description = todoMatcher.group(1);
            if (description == null || description.isBlank()) {
                throw new YourHandException("You handed me an empty to-do. Try: todo borrow book");
            }
            return new Todo(
                    validateTaskText(
                            description,
                            "You handed me an empty to-do. Try: todo borrow book"));
        }

        Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(command);
        if (deadlineMatcher.matches()) {
            return new Deadline(
                    validateTaskText(deadlineMatcher.group(1), "Your deadline needs a description."),
                    parseTaskDateTime(validateTaskText(deadlineMatcher.group(2),
                            "Your deadline needs a due date.")));
        }
        if (isCommand(command, "deadline")) {
            throw new YourHandException("Bro due when please. Try: deadline DESCRIPTION /by DATE_OR_TIME");
        }

        Matcher eventMatcher = EVENT_PATTERN.matcher(command);
        if (eventMatcher.matches()) {
            String description = validateTaskText(
                    eventMatcher.group(1), "Your event needs a description.");
            TaskDateTime from = parseTaskDateTime(
                    validateTaskText(
                            eventMatcher.group(2),
                            "Your event needs a start date."));
            TaskDateTime to = parseTaskDateTime(
                    validateTaskText(
                            eventMatcher.group(3),
                            "Your event needs an end date."));
            if (!to.getValue().isAfter(from.getValue())) {
                throw new YourHandException("Your event must end after it starts.");
            }
            return new Event(description, from, to);
        }
        if (isCommand(command, "event")) {
            throw new YourHandException("Walao when the even happening. Try: event DESCRIPTION /from START /to END");
        }

        throw new YourHandException("Hmm, I don't speak that yet. Try help to see what I can do.");
    }

    /** Returns whether a command begins with a command word. */
    private static boolean isCommand(String command, String commandWord) {
        return command.equals(commandWord) || command.startsWith(commandWord + " ");
    }

    /** Converts user-entered text to a whole number or reports a command-specific error. */
    private static int parseNumber(String numberText, String errorMessage) throws YourHandException {
        try {
            return Integer.parseInt(numberText.trim());
        } catch (NumberFormatException exception) {
            throw new YourHandException(errorMessage);
        }
    }

    /** Validates and trims text that will be stored in the pipe-delimited data file. */
    private static String validateTaskText(String text, String emptyMessage) throws YourHandException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new YourHandException(emptyMessage);
        }
        if (trimmedText.contains("|")) {
            throw new YourHandException("Please don't use | in a task. I need it to save your data safely.");
        }
        if (trimmedText.chars().anyMatch(Character::isISOControl)) {
            throw new YourHandException("Please don't use control characters in a task.");
        }
        return trimmedText;
    }

    /** Parses a supported task date or date-time and presents parse errors as chatbot errors. */
    static TaskDateTime parseTaskDateTime(String dateText) throws YourHandException {
        if (dateText == null || dateText.isBlank()) {
            throw new YourHandException("A date or time is required.");
        }
        String trimmedDateText = dateText.trim();
        try {
            return new TaskDateTime(LocalDate.parse(trimmedDateText, ISO_DATE_FORMAT));
        } catch (DateTimeParseException exception) {
            if (ISO_DATE_ONLY_PATTERN.matcher(trimmedDateText).matches()) {
                throw new InvalidDateException(trimmedDateText);
            }
            return parseTaskDateTimeWithTime(trimmedDateText);
        }
    }

    /** Parses one of the supported date-time formats. */
    private static TaskDateTime parseTaskDateTimeWithTime(String dateText) throws YourHandException {
        for (DateTimeFormatter formatter : List.of(
                ISO_COMPACT_DATE_TIME_FORMAT,
                ISO_COLON_DATE_TIME_FORMAT,
                SLASH_DATE_TIME_FORMAT)) {
            try {
                return new TaskDateTime(LocalDateTime.parse(dateText, formatter));
            } catch (DateTimeParseException exception) {
                // Try the next documented format.
            }
        }
        if (DATE_TIME_PATTERN.matcher(dateText).matches()) {
            String timeText = dateText.substring(dateText.indexOf(' ') + 1);
            throw new InvalidTimeException(timeText);
        }
        throw new InvalidCommandException("Date/time [" + dateText + "] is not in a format I understand. "
                + "Use yyyy-M-d, yyyy-M-d HHmm, yyyy-M-d HH:mm, or d/M/yyyy HHmm.");
    }

    /** Loads saved tasks and starts with an empty list if the data file cannot be read. */
    static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            return storage.load();
        } catch (CorruptFileException exception) {
            ui.showCorruptFileWarning();
            return new TaskList();
        } catch (IOException | SecurityException exception) {
            ui.showLoadingError();
            return new TaskList();
        }
    }
}
