package discitrack.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import discitrack.command.AddCommand;
import discitrack.command.ByeCommand;
import discitrack.command.CheckDateCommand;
import discitrack.command.Command;
import discitrack.command.DeleteCommand;
import discitrack.command.FindCommand;
import discitrack.command.HelpCommand;
import discitrack.command.ListCommand;
import discitrack.command.MarkCommand;
import discitrack.command.TagCommand;
import discitrack.command.UnmarkCommand;
import discitrack.command.UntagCommand;
import discitrack.exception.DisciTrackException;
import discitrack.task.Deadline;
import discitrack.task.Event;
import discitrack.task.Task;
import discitrack.task.Todo;

/**
 * Parses user input into executable commands.
 */
public class Parser {
    private static final Pattern TAG_MARKER = Pattern.compile("(?<!\\S)/tag(?=\\s|$)");
    private static final String TAG_ORDER_ERROR = "UHOH! Put tags at the end using /tag TAG for each tag.";
    /**
     * Parses the full user command into the corresponding command object.
     *
     * @param fullCommand the full command entered by the user.
     * @return the command represented by the user input.
     * @throws DisciTrackException if the command is unknown or has an invalid format.
     */
    public static Command parse(String fullCommand) throws DisciTrackException {
        assert fullCommand != null
                : "A command must be provided before parsing.";

        String command = fullCommand.trim();
        String[] commandParts = command.split("\\s+", 2);
        String commandWord = commandParts[0];
        String arguments = commandParts.length > 1 ? commandParts[1].trim() : "";

        if (List.of("bye", "help", "list").contains(commandWord) && !arguments.isEmpty()) {
            throw new DisciTrackException("UHOH! " + commandWord + " takes no arguments. Use: " + commandWord);
        }
        if (List.of("mark", "unmark", "delete").contains(commandWord)
                && arguments.split("\\s+").length > 1) {
            throw new DisciTrackException("UHOH! Change one task at a time. No tasks were changed. Use: "
                    + commandWord + " NUMBER");
        }

        if (commandWord.equals("bye")) {
            return new ByeCommand();
        } else if (commandWord.equals("help")) {
            return new HelpCommand();
        } else if (commandWord.equals("list")) {
            return new ListCommand();
        } else if (commandWord.equals("mark")) {
            return new MarkCommand(parseTaskNumber(arguments));
        } else if (commandWord.equals("unmark")) {
            return new UnmarkCommand(parseTaskNumber(arguments));
        } else if (commandWord.equals("checkdate")) {
            return new CheckDateCommand(parseCheckDate(arguments));
        } else if (commandWord.equals("find")) {
            return new FindCommand(parseFindKeyword(arguments));
        } else if (commandWord.equals("tag") || commandWord.equals("untag")) {
            return parseTagCommand(commandWord, arguments);
        } else if (commandWord.equals("todo") || commandWord.equals("deadline") || commandWord.equals("event")) {
            return parseAddCommand(commandWord, arguments);
        } else if (commandWord.equals("delete")) {
            return new DeleteCommand(parseTaskNumber(arguments));
        } else {
            throw new DisciTrackException("UHOH! I don't recognise that command. "
                    + "Type help or click Commands to see what you can use.");
        }
    }

    private static Command parseTagCommand(String commandWord, String arguments) throws DisciTrackException {
        String[] parts = arguments.split("\\s+");
        if (parts.length != 2) {
            throw new DisciTrackException("UHOH! Use: " + commandWord + " NUMBER TAG");
        }
        int number = parseTaskNumber(parts[0]);
        // Tag validation occurs after resolving the task number during execution.
        return commandWord.equals("tag") ? new TagCommand(number, parts[1]) : new UntagCommand(number, parts[1]);
    }

    private static AddCommand parseAddCommand(String commandWord, String arguments) throws DisciTrackException {
        Matcher marker = TAG_MARKER.matcher(arguments);
        List<String> tags = new ArrayList<>();
        String details = arguments;
        if (marker.find()) {
            details = arguments.substring(0, marker.start()).trim();
            tags = parseCreationTags(arguments.substring(marker.start()));
        }
        Task task;
        try {
            if (commandWord.equals("todo")) {
                task = parseTodo(details);
            } else if (commandWord.equals("deadline")) {
                task = parseDeadline(details);
            } else {
                task = parseEvent(details);
            }
        } catch (IllegalArgumentException e) {
            throw new DisciTrackException(e.getMessage());
        }
        task.replaceTags(tags);
        return new AddCommand(task);
    }

    private static List<String> parseCreationTags(String suffix) throws DisciTrackException {
        String[] tokens = suffix.split("\\s+");
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < tokens.length; i += 2) {
            if (!tokens[i].equals("/tag")) {
                throw new DisciTrackException(TAG_ORDER_ERROR);
            }
            if (i + 1 == tokens.length || tokens[i + 1].equals("/tag")) {
                throw new DisciTrackException("UHOH! Each /tag must be followed by one tag name.");
            }
            if (tokens[i + 1].equals("/by") || tokens[i + 1].equals("/from") || tokens[i + 1].equals("/to")) {
                throw new DisciTrackException(TAG_ORDER_ERROR);
            }
            tags.add(tokens[i + 1]);
        }
        try {
            tags.forEach(Task::validateTag);
        } catch (IllegalArgumentException e) {
            throw new DisciTrackException(e.getMessage());
        }
        return tags;
    }

    /**
     * Parses the task number after a command word.
     *
     * @param taskNumberString the task number entered by the user.
     * @return the one-based task number entered by the user.
     * @throws DisciTrackException if the task number is missing or invalid.
     */
    private static int parseTaskNumber(String taskNumberString) throws DisciTrackException {
        if (taskNumberString.isEmpty()) {
            throw new DisciTrackException("OOPSIE! I need a task number after the command. "
                    + "Use list to check task numbers.");
        }

        try {
            return Integer.parseInt(taskNumberString);
        } catch (NumberFormatException e) {
            throw new DisciTrackException("UHOH! Use a whole-number task number from your list. Type list to check.");
        }
    }

    /**
     * Parses the date supplied to a checkdate command.
     *
     * @param date the date entered by the user.
     * @return the date to check.
     * @throws DisciTrackException if the date is missing or not in yyyy-MM-dd format.
     */
    private static LocalDate parseCheckDate(String date) throws DisciTrackException {
        if (date.isEmpty()) {
            throw new DisciTrackException("OOPSIE! I need a date. Try: checkdate 2026-09-30");
        }

        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new DisciTrackException("UHOH! Use a valid date in yyyy-MM-dd format, for example 2026-09-30.");
        }
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param keyword the keyword entered by the user.
     * @return the keyword to search for.
     * @throws DisciTrackException if the keyword is empty.
     */
    private static String parseFindKeyword(String keyword) throws DisciTrackException {
        if (keyword.isEmpty()) {
            throw new DisciTrackException("OOPSIE! I need a keyword. Try: find exercise");
        }

        return keyword;
    }

    /**
     * Parses a todo command into a todo task.
     *
     * @param activity the todo activity entered by the user.
     * @return the todo task described by the command.
     * @throws DisciTrackException if the todo activity is empty.
     */
    private static Todo parseTodo(String activity) throws DisciTrackException {
        if (activity.isEmpty()) {
            throw new DisciTrackException("OOPSIE! Add a description after todo. Example: todo exercise.");
        }

        return new Todo(activity);
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param input the deadline details entered by the user.
     * @return the deadline task described by the command.
     * @throws DisciTrackException if the activity, /by keyword, or date is invalid.
     */
    private static Deadline parseDeadline(String input) throws DisciTrackException {
        if (input.isEmpty()) {
            throw new DisciTrackException("OOPSIE! I need a task description. Try: deadline homework /by 2026-09-30");
        }

        String[] parts = splitDateField(input, "/by");

        String activity = parts[0].trim();
        String time = parts[1].trim();

        if (activity.isEmpty()) {
            throw new DisciTrackException("OOPSIE! I need a task description. Try: deadline homework /by 2026-09-30");
        }

        if (time.isEmpty()) {
            throw new DisciTrackException("OOPSIE! Add a date after /by, for example /by 2026-09-30.");
        }

        try {
            return new Deadline(activity, LocalDate.parse(time));
        } catch (DateTimeParseException e) {
            throw new DisciTrackException("UHOH! Use a valid deadline date in yyyy-MM-dd format, "
                    + "for example 2026-09-30.");
        }
    }

    /**
     * Parses an event command into an event task.
     *
     * @param input the event details entered by the user.
     * @return the event task described by the command.
     * @throws DisciTrackException if the activity, /from keyword, /to keyword, or dates are invalid.
     */
    private static Event parseEvent(String input) throws DisciTrackException {
        if (input.isEmpty()) {
            throw new DisciTrackException("OOPSIE! I need an event description before /from.");
        }

        String[] eventAndDates = splitDateField(input, "/from");
        if (Pattern.compile("(?<!\\S)/to(?=\\s|$)").matcher(eventAndDates[0]).find()) {
            throw new DisciTrackException("UHOH! Put /from before /to, followed by their dates.");
        }
        String[] startAndEndDates = splitDateField(eventAndDates[1], "/to");
        String activity = eventAndDates[0].trim();
        String startDate = startAndEndDates[0].trim();
        String endDate = startAndEndDates[1].trim();

        validateEventDetails(activity, startDate, endDate);
        return createEvent(activity, startDate, endDate);
    }

    private static String[] splitDateField(String input, String separator) throws DisciTrackException {
        Matcher marker = Pattern.compile("(?<!\\S)" + Pattern.quote(separator) + "(?=\\s|$)").matcher(input);
        if (!marker.find()) {
            throw new DisciTrackException("OOPSIE! Include " + separator + " followed by a date in yyyy-MM-dd format.");
        }
        int start = marker.start();
        int end = marker.end();
        if (marker.find()) {
            throw new DisciTrackException("UHOH! Use " + separator + " only once. No tasks were changed.");
        }
        return new String[] {input.substring(0, start).trim(), input.substring(end).trim()};
    }

    private static void validateEventDetails(String activity, String startDate, String endDate)
            throws DisciTrackException {
        if (activity.isEmpty()) {
            throw new DisciTrackException("OOPSIE! I need an event description before /from.");
        }

        if (startDate.isEmpty()) {
            throw new DisciTrackException("OOPSIE! Add a date after /from, for example /from 2026-09-30.");
        }

        if (endDate.isEmpty()) {
            throw new DisciTrackException("OOPSIE! Add a date after /to, for example /to 2026-10-01.");
        }
    }

    private static Event createEvent(String activity, String startDate, String endDate)
            throws DisciTrackException {
        return new Event(activity, parseEventDate(startDate, "start"), parseEventDate(endDate, "end"));
    }

    private static LocalDate parseEventDate(String value, String field) throws DisciTrackException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new DisciTrackException("UHOH! The event's " + field
                    + " date is invalid. Use a real date in yyyy-MM-dd format, for example 2026-09-30.");
        }
    }
}
