package james.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import james.command.Command;
import james.exception.UserInputException;
import james.task.Deadline;
import james.task.Event;
import james.task.Task;
import james.task.ToDo;

/**
 * Parses raw user input strings into commands, arguments, task objects, and dates.
 */
public class Parser {

    /**
     * Parses and validates a date string argument into a LocalDate object.
     *
     * @param arguments User-supplied date string.
     * @return Parsed LocalDate object.
     * @throws UserInputException If the date argument is empty or formatted incorrectly.
     */
    public static LocalDate parseDate(String arguments) throws UserInputException {
        String noDateMessage = "Please provide a date in the format: yyyy-mm-dd";
        String invalidDateFormatMessage = "The date format provided is incorrect! Please use the format: yyyy-mm-dd";

        boolean isDateMissing = arguments == null || arguments.trim().isEmpty();
        if (isDateMissing) {
            throw new UserInputException(noDateMessage);
        }
        try {
            return LocalDate.parse(arguments.trim());
        } catch (DateTimeParseException e) {
            throw new UserInputException(invalidDateFormatMessage);
        }
    }

    /**
     * Parses and validates a keyword for the find command.
     *
     * @param arguments User-supplied search keyword.
     * @return Trimmed search keyword.
     * @throws UserInputException If the search keyword is empty.
     */
    public static String parseFindKeyword(String arguments) throws UserInputException {
        String noFindKeywordMessage = "Please provide a keyword to search for.\n" +
                "Try: find <keyword>";

        boolean isFindKeywordMissing = arguments == null || arguments.trim().isEmpty();
        if (isFindKeywordMissing) {
            throw new UserInputException(noFindKeywordMessage);
        }
        return arguments.trim();
    }

    /**
     * Parses a command keyword string into its corresponding Command enum.
     *
     * @param commandString Command word entered by user.
     * @return The matching Command enum constant.
     * @throws UserInputException If the command word is unrecognized.
     */
    public static Command parseCommandType(String commandString) throws UserInputException {
        String unknownCommandMessage = "I don't know that recipe! Try list or todo <description>.";

        try {
            return Command.valueOf(commandString.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new UserInputException(unknownCommandMessage);
        }
    }

    /**
     * Parses and validates the task number supplied to a mark-related or delete command.
     *
     * @param arguments The text after the command.
     * @param command The command being processed.
     * @param taskListSize The current number of tasks in the list.
     * @return The zero-based index of the selected task.
     * @throws UserInputException If no valid task number is supplied or index is out of bounds.
     */
    public static int parseTaskNumber(String arguments, String command, int taskListSize) throws UserInputException {
        int taskNumber = getTaskNumber(arguments, command);

        boolean isTaskNumberBelowMinimum = taskNumber < 1;
        boolean isTaskNumberAboveMaximum = taskNumber > taskListSize;
        String taskNumberDoesntExistMessage = "I can't find task number " + taskNumber + ".\n" +
                "Your list currently has " + taskListSize + " tasks.";

        if (isTaskNumberBelowMinimum || isTaskNumberAboveMaximum) {
            throw new UserInputException(taskNumberDoesntExistMessage);
        }
        return taskNumber - 1;
    }

    /**
     * Parses the required whole-number argument for a task command.
     *
     * @param arguments The text after the command.
     * @param command The command used in error guidance.
     * @return Parsed task number.
     * @throws UserInputException If the argument is missing or is not a whole number.
     */
    private static int getTaskNumber(String arguments, String command) throws UserInputException {
        String noTaskNumberMessage = "Which task shall I fetch? Please provide a task number.\n" +
                "Try: " + command + " <task number>";
        String taskNumberNotIntMessage = "No half-slices here! The task number must be a whole number.\n" +
                "Try: " + command + " <task number>";

        boolean isTaskNumberMissing = arguments == null || arguments.trim().isEmpty();
        if (isTaskNumberMissing) {
            throw new UserInputException(noTaskNumberMessage);
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new UserInputException(taskNumberNotIntMessage);
        }
        return taskNumber;
    }

    /**
     * Parses arguments for a deadline command into a Deadline task instance.
     *
     * @param arguments Description and /by date arguments.
     * @return Constructed Deadline task.
     * @throws UserInputException If description or date are missing or malformed.
     */
    public static Task parseDeadline(String arguments) throws UserInputException {
        String noDeadlineDescriptionMessage = "The description of a deadline cannot be empty.\n" +
                "Try: deadline <description> /by <end-date>";
        String noDeadlineDateMessage = "A deadline needs a by date.\n" + "Try: deadline <description> /by <date>";
        String invalidDateFormatMessage = "Formatting of the date is incorrect, try: yyyy-mm-dd";
        String deadlineDateSeparator = "\\s+/by(?:\\s+|$)";

        boolean isDeadlineDescriptionMissing = arguments == null || arguments.trim().isEmpty();
        if (isDeadlineDescriptionMissing) {
            throw new UserInputException(noDeadlineDescriptionMessage);
        }
        validateOptions(arguments, "/by");
        String[] deadlineParts = arguments.split(deadlineDateSeparator, 2);
        boolean isDeadlineIncomplete = deadlineParts.length < 2 ||
                deadlineParts[0].trim().isEmpty() || deadlineParts[1].trim().isEmpty();
        if (isDeadlineIncomplete) {
            throw new UserInputException(noDeadlineDateMessage);
        }
        try {
            LocalDate by = LocalDate.parse(deadlineParts[1].trim());
            Task.validateDescription(deadlineParts[0]);
            return new Deadline(deadlineParts[0].trim(), by);
        } catch (DateTimeParseException e) {
            throw new UserInputException(invalidDateFormatMessage);
        }
    }

    /**
     * Parses arguments for a todo command into a ToDo task instance.
     *
     * @param arguments Description of the todo task.
     * @return Constructed ToDo task.
     * @throws UserInputException If description is empty.
     */
    public static Task parseTodo(String arguments) throws UserInputException {
        String noTodoDescriptionMessage = "The description of a todo cannot be empty.\n" +
                "Try: todo <description>";

        boolean isTodoDescriptionMissing = arguments == null || arguments.trim().isEmpty();
        if (isTodoDescriptionMissing) {
            throw new UserInputException(noTodoDescriptionMessage);
        }
        Task.validateDescription(arguments);
        return new ToDo(arguments.trim());
    }

    /**
     * Parses arguments for an event command into an Event task instance.
     *
     * @param arguments Description, /from start date, and /to end date.
     * @return Constructed Event task.
     * @throws UserInputException If description or dates are missing or malformed.
     */
    public static Task parseEvent(String arguments) throws UserInputException {
        String noEventDescriptionMessage = "The description of a event cannot be empty.\n" +
                "Try: event <description> /from <start> /to <end>";
        String noEventFromMessage = "An event needs a description followed by /from.\n" +
                "Try: event <description> /from <start> /to <end>";
        String noEventTimesMessage = "An event needs both a start and end time.\n" +
                "Try: event <description> /from <start> /to <end>";
        String invalidDateFormatMessage = "Formatting of the date is incorrect, try: yyyy-mm-dd";
        String eventStartSeparator = "\\s+/from(?:\\s+|$)";
        String eventEndSeparator = "\\s+/to(?:\\s+|$)";

        boolean isEventDescriptionMissing = arguments == null || arguments.trim().isEmpty();
        if (isEventDescriptionMissing) {
            throw new UserInputException(noEventDescriptionMessage);
        }
        validateOptions(arguments, "/from", "/to");
        String[] eventParts = arguments.split(eventStartSeparator, 2);
        boolean isEventDescriptionOrStartSeparatorMissing = eventParts.length < 2 || eventParts[0].trim().isEmpty();
        if (isEventDescriptionOrStartSeparatorMissing) {
            throw new UserInputException(noEventFromMessage);
        }
        String[] timeParts = eventParts[1].split(eventEndSeparator, 2);
        boolean isEventTimeRangeIncomplete = timeParts.length < 2 ||
                timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty();
        if (isEventTimeRangeIncomplete) {
            throw new UserInputException(noEventTimesMessage);
        }
        try {
            LocalDate from = LocalDate.parse(timeParts[0].trim());
            LocalDate to = LocalDate.parse(timeParts[1].trim());
            Task.validateDescription(eventParts[0]);
            if (!from.isBefore(to)) {
                throw new UserInputException("An event must end after its start date.");
            }
            return new Event(eventParts[0].trim(), from, to);
        } catch (DateTimeParseException e) {
            throw new UserInputException(invalidDateFormatMessage);
        }
    }

    /**
     * Splits a raw user input line into the command keyword and remaining arguments.
     *
     * @param rawInput Raw input line from the user.
     * @return An array of strings where index 0 is the command and index 1 (if present) is the arguments.
     * @throws UserInputException If the input is null or blank.
     */
    public static String[] parseCommand(String rawInput) throws UserInputException {
        String noCommandMessage = "No command specified\n" + "Try: <command> <arguments:optional>";
        String commandArgumentSeparator = "\\s+";

        boolean isCommandMissing = rawInput == null || rawInput.trim().isEmpty();
        if (isCommandMissing) {
            throw new UserInputException(noCommandMessage);
        }

        return rawInput.trim().split(commandArgumentSeparator, 2);
    }

    /**
     * Rejects repeated, incompatible, or out-of-order date options while allowing paths in descriptions.
     */
    private static void validateOptions(String arguments, String... expected) throws UserInputException {
        int optionIndex = 0;
        for (String token : arguments.trim().split("\\s+")) {
            if (token.equals("/by") || token.equals("/from") || token.equals("/to")) {
                if (optionIndex >= expected.length || !token.equals(expected[optionIndex])) {
                    throw new UserInputException("Date options must appear once and in order: " +
                            String.join(" ", expected));
                }
                optionIndex++;
            }
        }
    }

    /**
     * Extracts the argument text from a split command.
     *
     * @param parts Command parts returned by parseCommand.
     * @return Argument text, or null if absent.
     */
    public static String extractArguments(String[] parts) {
        boolean isMoreThanOneArgument = parts.length > 1;
        return isMoreThanOneArgument ? parts[1] : null;
    }
}
