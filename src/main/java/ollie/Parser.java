package ollie;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user input and converts it into commands and tasks.
 */
public class Parser {
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("[1-9]\\d*");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final String BY_PARAMETER = "/by";
    private static final String FROM_PARAMETER = "/from";
    private static final String TO_PARAMETER = "/to";

    /**
     * Creates a parser for Ollie commands.
     */
    public Parser() {
    }

    /**
     * Identifies the type of command represented by the input.
     *
     * @param input Input entered by the user.
     * @return Type of the command, or {@link CommandType#UNKNOWN} if it is not recognized.
     */
    public CommandType parseCommand(String input) {
        String normalizedInput = normalizeWhitespace(input);
        if (normalizedInput.isEmpty()) {
            return CommandType.UNKNOWN;
        }

        String commandWord = normalizedInput.split(" ", 2)[0];
        switch (commandWord) {
            case "help":
                return CommandType.HELP;
            case "list":
                return CommandType.LIST;
            case "find":
                return CommandType.FIND;
            case "todo":
                return CommandType.TODO;
            case "deadline":
                return CommandType.DEADLINE;
            case "event":
                return CommandType.EVENT;
            case "mark":
                return CommandType.MARK;
            case "unmark":
                return CommandType.UNMARK;
            case "delete":
                return CommandType.DELETE;
            case "bye":
                return CommandType.BYE;
            default:
                return CommandType.UNKNOWN;
        }
    }

    /**
     * Parses the keyword from a find command.
     *
     * @param command Find command entered by the user.
     * @return Keyword to search for.
     * @throws OllieException If the keyword is empty.
     */
    public String parseFindKeyword(String command) throws OllieException {
        String normalizedCommand = normalizeWhitespace(command);
        if (!normalizedCommand.equals("find") && !normalizedCommand.startsWith("find ")) {
            throw new OllieException("Use find <keyword> to search for tasks.");
        }

        String keyword = normalizedCommand.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new OllieException("Tell me what to find. Try: find <keyword>.");
        }

        return keyword;
    }

    /**
     * Parses and validates a one-based task number from a command.
     *
     * @param command Full command entered by the user.
     * @param action Command word that precedes the task number.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based task index.
     * @throws OllieException If the task number is missing, invalid, or out of range.
     */
    public int parseTaskIndex(String command, String action, int taskCount) throws OllieException {
        String normalizedCommand = normalizeWhitespace(command);
        if (!normalizedCommand.equals(action) && !normalizedCommand.startsWith(action + " ")) {
            throw new OllieException("Use " + action + " <task number>.");
        }

        String taskNumberText = normalizedCommand.substring(action.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new OllieException("Tell me which task to " + action
                    + ". Try: " + action + " <task number>.");
        }

        if (!POSITIVE_INTEGER_PATTERN.matcher(taskNumberText).matches()) {
            throw new OllieException("The task number must be a positive whole number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new OllieException("That task number is too large.");
        }

        if (taskCount == 0) {
            throw new OllieException("There are no tasks to " + action + ".");
        } else if (taskNumber < 1 || taskNumber > taskCount) {
            throw new OllieException("Choose a task number between 1 and " + taskCount + ".");
        }

        return taskNumber - 1;
    }

    /**
     * Parses a task-creation command into the corresponding task type.
     *
     * @param command Task-creation command entered by the user.
     * @return Parsed task.
     * @throws OllieException If the command is unknown or incomplete.
     */
    public Task parseTask(String command) throws OllieException {
        String normalizedCommand = normalizeWhitespace(command);
        CommandType commandType = parseCommand(normalizedCommand);
        if (commandType == CommandType.TODO) {
            return createTodo(normalizedCommand);
        } else if (commandType == CommandType.DEADLINE) {
            return createDeadline(normalizedCommand);
        } else if (commandType == CommandType.EVENT) {
            return createEvent(normalizedCommand);
        } else if (normalizedCommand.isEmpty()) {
            throw new OllieException("Please enter a command. Type help to see the available commands.");
        }

        throw new OllieException("I don't recognize that command. Type help to see what I understand.");
    }

    /**
     * Creates a todo from a validated todo command.
     *
     * @param command Todo command entered by the user.
     * @return Parsed todo.
     * @throws OllieException If the description is empty.
     */
    private Todo createTodo(String command) throws OllieException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new OllieException("A todo needs a description. Try: todo <description>.");
        }

        validateDescription(description);
        return new Todo(description);
    }

    /**
     * Creates a deadline from a validated deadline command.
     *
     * @param command Deadline command entered by the user.
     * @return Parsed deadline.
     * @throws OllieException If required details are missing or invalid.
     */
    private Deadline createDeadline(String command) throws OllieException {
        String details = command.substring("deadline".length()).trim();
        int byParameterCount = countParameter(details, BY_PARAMETER);

        if (containsParameter(details, FROM_PARAMETER) || containsParameter(details, TO_PARAMETER)) {
            throw new OllieException("A deadline only accepts the /by parameter.");
        } else if (byParameterCount == 0) {
            throw new OllieException("A deadline needs /by followed by a date.");
        } else if (byParameterCount > 1) {
            throw new OllieException("Specify /by only once for a deadline.");
        }

        int dueDateDelimiterIndex = findParameterIndex(details, BY_PARAMETER);
        String description = details.substring(0, dueDateDelimiterIndex).trim();
        String dueDateText = details.substring(dueDateDelimiterIndex + BY_PARAMETER.length()).trim();
        if (description.isEmpty()) {
            throw new OllieException("A deadline needs a description before /by.");
        } else if (dueDateText.isEmpty()) {
            throw new OllieException("A deadline needs a date after /by.");
        }

        validateDescription(description);
        LocalDate dueDate = parseDate(dueDateText, "deadline date");
        return new Deadline(description, dueDate);
    }

    /**
     * Creates an event from a validated event command.
     *
     * @param command Event command entered by the user.
     * @return Parsed event.
     * @throws OllieException If required details are missing or invalid.
     */
    private Event createEvent(String command) throws OllieException {
        String details = command.substring("event".length()).trim();
        int fromParameterCount = countParameter(details, FROM_PARAMETER);
        int toParameterCount = countParameter(details, TO_PARAMETER);

        if (containsParameter(details, BY_PARAMETER)) {
            throw new OllieException("An event only accepts /from and /to parameters.");
        } else if (fromParameterCount == 0) {
            throw new OllieException("An event needs /from followed by a start date.");
        } else if (fromParameterCount > 1) {
            throw new OllieException("Specify /from only once for an event.");
        } else if (toParameterCount == 0) {
            throw new OllieException("An event needs /to followed by an end date.");
        } else if (toParameterCount > 1) {
            throw new OllieException("Specify /to only once for an event.");
        }

        int startDateDelimiterIndex = findParameterIndex(details, FROM_PARAMETER);
        int endDateDelimiterIndex = findParameterIndex(details, TO_PARAMETER);
        if (endDateDelimiterIndex < startDateDelimiterIndex) {
            throw new OllieException("Place /from before /to in an event.");
        }

        String description = details.substring(0, startDateDelimiterIndex).trim();
        String startDateText = details.substring(
                startDateDelimiterIndex + FROM_PARAMETER.length(), endDateDelimiterIndex).trim();
        String endDateText = details.substring(endDateDelimiterIndex + TO_PARAMETER.length()).trim();
        if (description.isEmpty()) {
            throw new OllieException("An event needs a description before /from.");
        } else if (startDateText.isEmpty()) {
            throw new OllieException("An event needs a start date after /from.");
        } else if (endDateText.isEmpty()) {
            throw new OllieException("An event needs an end date after /to.");
        }

        validateDescription(description);
        LocalDate startDate = parseDate(startDateText, "event start date");
        LocalDate endDate = parseDate(endDateText, "event end date");
        if (!startDate.isBefore(endDate)) {
            throw new OllieException("The event start date must be before its end date.");
        }

        return new Event(description, startDate, endDate);
    }

    /**
     * Ensures a command that takes no arguments contains only its command word.
     *
     * @param command Full command entered by the user.
     * @param commandWord Command word that should appear alone.
     * @throws OllieException If unexpected arguments follow the command word.
     */
    public void validateNoArguments(String command, String commandWord) throws OllieException {
        if (!normalizeWhitespace(command).equals(commandWord)) {
            throw new OllieException("The " + commandWord + " command does not take any arguments.");
        }
    }

    /**
     * Parses an ISO date and reports a user-friendly error if it is invalid.
     *
     * @param dateText Date text to parse.
     * @param dateDescription Name of the date used in an error message.
     * @return Parsed date.
     * @throws OllieException If the date is not a valid ISO date.
     */
    private LocalDate parseDate(String dateText, String dateDescription) throws OllieException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new OllieException("The " + dateDescription + " must be a real date in "
                    + "yyyy-MM-dd format, e.g., 2019-12-02.");
        }
    }

    private void validateDescription(String description) throws OllieException {
        if (description.contains("|")) {
            throw new OllieException("Task descriptions cannot contain the | character.");
        }
    }

    private boolean containsParameter(String details, String parameter) {
        return countParameter(details, parameter) > 0;
    }

    private int countParameter(String details, String parameter) {
        int count = 0;
        Matcher matcher = createParameterPattern(parameter).matcher(details);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private int findParameterIndex(String details, String parameter) {
        Matcher matcher = createParameterPattern(parameter).matcher(details);
        boolean isParameterFound = matcher.find();
        assert isParameterFound : "Required parameter should be present before locating it";
        return matcher.start();
    }

    private Pattern createParameterPattern(String parameter) {
        return Pattern.compile("(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)");
    }

    private String normalizeWhitespace(String input) {
        if (input == null) {
            return "";
        }
        return WHITESPACE_PATTERN.matcher(input.trim()).replaceAll(" ");
    }
}
