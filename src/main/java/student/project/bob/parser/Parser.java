package student.project.bob.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

import student.project.bob.exception.BobException;
import student.project.bob.model.Deadline;
import student.project.bob.model.Event;
import student.project.bob.model.Task;
import student.project.bob.model.Todo;
import student.project.bob.util.DateTimeParser;

/**
 * Parses user commands into task data and command arguments.
 */
public class Parser {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I do not recognise that command. Try todo, deadline, event, list, mark, unmark, delete, upcoming, on, "
                    + "overdue, find, undo, or bye.";

    /**
     * Creates a parser for Bob commands.
     */
    public Parser() {}

    /**
     * Recognizes the action represented by a user command.
     *
     * @param input the trimmed user command
     * @return a command containing its recognized type and original input
     * @throws BobException if the command is not recognized
     */
    public Command parseCommand(String input) throws BobException {
        if (input.equals("bye")) {
            return new Command(Command.Type.BYE, input);
        }
        if (input.equals("list")) {
            return new Command(Command.Type.LIST, input);
        }
        if (input.equals("upcoming") || input.startsWith("upcoming ")) {
            return new Command(Command.Type.UPCOMING, input);
        }
        if (input.equals("on") || input.startsWith("on ")) {
            return new Command(Command.Type.ON, input);
        }
        if (input.equals("overdue") || input.startsWith("overdue ")) {
            return new Command(Command.Type.OVERDUE, input);
        }
        if (input.equals("find") || input.startsWith("find ")) {
            return new Command(Command.Type.FIND, input);
        }
        if (input.equals("mark") || input.startsWith("mark ")) {
            return new Command(Command.Type.MARK, input);
        }
        if (input.equals("unmark") || input.startsWith("unmark ")) {
            return new Command(Command.Type.UNMARK, input);
        }
        if (input.equals("delete") || input.startsWith("delete ")) {
            return new Command(Command.Type.DELETE, input);
        }
        if (input.equals("undo") || input.startsWith("undo ")) {
            return new Command(Command.Type.UNDO, input);
        }
        if (isTaskCommand(input)) {
            return new Command(Command.Type.TASK, input);
        }
        throw new BobException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     * Date and time details are parsed by the deadline and event task classes.
     *
     * @param input the trimmed user command
     * @return a new task
     * @throws BobException if the command is invalid or unknown
     */
    public Task parseTask(String input) throws BobException {
        if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.length() == "todo".length()
                    ? ""
                    : input.substring("todo ".length()).trim();
            if (description.isEmpty()) {
                throw new BobException("A todo needs a description. Example: \"todo read a book\".");
            }
            return new Todo(description);
        }

        if (input.equals("deadline") || input.startsWith("deadline ")) {
            String content = input.length() == "deadline".length()
                    ? ""
                    : input.substring("deadline ".length()).trim();
            String[] parts = content.split("\\s+/by\\s+", 2);

            if (parts.length != 2
                    || parts[0].trim().isEmpty()
                    || parts[1].trim().isEmpty()) {
                throw new BobException(
                        "A deadline needs a description and a '/by' detail (in yyyy-MM-dd HH:mm / HH:mm:ss). "
                                + "Example: \"deadline submit report /by 2019-10-15\". You may add a time, such as "
                                + "\"2019-10-15 18:00\" or \"2019-10-15 18:00:30\".");
            }

            try {
                return new Deadline(parts[0].trim(), parts[1].trim());
            } catch (DateTimeParseException e) {
                throw new BobException("A deadline's '/by' detail must be valid (in yyyy-MM-dd HH:mm / HH:mm:ss). "
                        + "Example: \"deadline submit report /by 2019-10-15 18:00\".");
            }
        }

        if (input.equals("event") || input.startsWith("event ")) {
            String content = input.length() == "event".length()
                    ? ""
                    : input.substring("event ".length()).trim();
            int fromIndex = content.indexOf("/from");
            int toIndex = fromIndex < 0 ? -1 : content.indexOf("/to", fromIndex + "/from".length());

            if (fromIndex <= 0 || toIndex <= fromIndex + "/from".length()) {
                throw new BobException("An event needs a description, '/from', and '/to' details "
                        + "(in yyyy-MM-dd HH:mm / HH:mm:ss). Example: \"event team meeting /from "
                        + "2019-10-15 09:00 /to 2019-10-15 10:00\".");
            }

            String description = content.substring(0, fromIndex).trim();
            String from =
                    content.substring(fromIndex + "/from".length(), toIndex).trim();
            String to = content.substring(toIndex + "/to".length()).trim();

            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new BobException("Please provide the event description, '/from' date/time, and '/to' date/time "
                        + "(in yyyy-MM-dd HH:mm / HH:mm:ss). Example: \"event team meeting /from "
                        + "2019-10-15 /to 2019-10-16\".");
            }

            try {
                return new Event(description, from, to);
            } catch (DateTimeParseException e) {
                throw new BobException("An event's '/from' and '/to' details must be valid "
                        + "(in yyyy-MM-dd HH:mm / HH:mm:ss). Example: \"event team meeting /from "
                        + "2019-10-15 09:00 /to 2019-10-15 10:00\".");
            }
        }

        throw new BobException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Parses the optional number of days for an upcoming command.
     *
     * @param input the trimmed upcoming command
     * @return the requested number of days, defaulting to seven
     * @throws BobException if the command contains an invalid number of days
     */
    public int parseUpcomingDays(String input) throws BobException {
        String[] parts = input.split("\\s+");
        if (parts.length == 1) {
            return 7;
        }
        if (parts.length != 2) {
            throw new BobException(
                    "Use \"upcoming\" or \"upcoming <number of days>\". Examples: \"upcoming\" or \"upcoming 7\".");
        }

        try {
            int days = Integer.parseInt(parts[1]);
            if (days < 0) {
                throw new BobException("The number of days cannot be negative. Example: \"upcoming 7\".");
            }
            return days;
        } catch (NumberFormatException e) {
            throw new BobException("The number of days must be a whole number. Example: \"upcoming 7\".");
        }
    }

    /**
     * Parses the date supplied to an on command.
     *
     * @param input the trimmed on command
     * @return the requested date
     * @throws BobException if the command does not contain a valid date
     */
    public LocalDate parseOnDate(String input) throws BobException {
        String dateInput = input.length() <= "on".length()
                ? ""
                : input.substring("on".length()).trim();
        if (dateInput.isEmpty()) {
            throw new BobException("Please provide a date. Example: \"on 2019-10-15\".");
        }

        try {
            Temporal temporal = DateTimeParser.parse(dateInput);
            if (temporal instanceof LocalDate date) {
                return date;
            }
        } catch (DateTimeParseException e) {
            // Report the invalid date using Bob's normal input-error flow.
        }
        throw new BobException("Please enter a valid date using yyyy-MM-dd. Example: \"on 2019-10-15\".");
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param input the trimmed find command
     * @return the keyword to search for
     * @throws BobException if the command does not contain a keyword
     */
    public String parseFindKeyword(String input) throws BobException {
        String keyword = input.length() <= "find".length()
                ? ""
                : input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new BobException("Please provide a search keyword. Example: \"find report\".");
        }
        return keyword;
    }

    /**
     * Checks whether the input starts with a supported task command.
     *
     * @param input the trimmed user command
     * @return true if the input is a todo, deadline, or event command
     */
    private boolean isTaskCommand(String input) {
        return input.equals("todo")
                || input.startsWith("todo ")
                || input.equals("deadline")
                || input.startsWith("deadline ")
                || input.equals("event")
                || input.startsWith("event ");
    }
}
