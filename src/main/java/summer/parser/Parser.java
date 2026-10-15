package summer.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import summer.SummerException;
import summer.command.AddCommand;
import summer.command.Command;
import summer.command.DeleteCommand;
import summer.command.ExitCommand;
import summer.command.FindCommand;
import summer.command.ListCommand;
import summer.command.MarkCommand;
import summer.command.OnDateCommand;
import summer.command.SortCommand;
import summer.command.UnmarkCommand;
import summer.task.Deadline;
import summer.task.Event;
import summer.task.Task;
import summer.task.ToDo;

/**
 * Makes sense of the raw command text typed by the user: decides which
 * {@link Command} it represents, and turns command text into the tasks,
 * dates, and indices those commands need.
 */
public class Parser {
    /**
     * Parses one full line of user input into the command it represents.
     *
     * @param fullCommand the raw command line typed by the user
     * @return the command to execute
     * @throws SummerException if the command names an invalid date or omits a required keyword
     */
    public static Command parse(String fullCommand) throws SummerException {
        // Summer never passes null here: the text loop and GUI both hand over
        // a real (possibly empty) line.
        assert fullCommand != null : "command text must not be null";

        if (fullCommand.equals("bye")) {
            return new ExitCommand();
        }

        if (fullCommand.equals("list")) {
            return new ListCommand();
        }

        if (fullCommand.equals("sort")) {
            return new SortCommand();
        }

        if (fullCommand.startsWith("on ")) {
            LocalDate date = parseDate(fullCommand.substring("on ".length()).trim());
            return new OnDateCommand(date);
        }

        if (fullCommand.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(fullCommand));
        }

        if (fullCommand.startsWith("unmark ")) {
            return new UnmarkCommand(parseTaskNumber(fullCommand));
        }

        if (fullCommand.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(fullCommand));
        }

        if (fullCommand.equals("find") || fullCommand.startsWith("find ")) {
            String keyword = fullCommand.substring("find".length()).trim();
            if (keyword.isEmpty()) {
                throw new SummerException("Gimme a keyword to search for, bud.");
            }
            return new FindCommand(keyword);
        }

        return new AddCommand(fullCommand);
    }

    /**
     * Creates the task type represented by a user command.
     *
     * @param command user command describing a todo, deadline, or event
     * @return task represented by the command
     * @throws SummerException if the command is unknown or missing required details
     */
    public static Task createTask(String command) throws SummerException {
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new SummerException("A todo needs a description, what's the plan?");
            }

            return new ToDo(description, false);
        }

        if (command.equals("deadline") || command.startsWith("deadline ")) {
            String details = command.substring("deadline".length()).trim();
            int byIndex = details.indexOf(" /by ");
            if (byIndex == -1) {
                throw new SummerException("Deadlines go like this: deadline DESCRIPTION /by WHEN");
            }

            // indexOf found " /by " within details, so the text after it is a
            // valid (possibly empty) substring range.
            int byValueStart = byIndex + " /by ".length();
            assert byValueStart <= details.length() : "/by offset ran past the command text";

            String description = details.substring(0, byIndex).trim();
            String by = details.substring(byValueStart).trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new SummerException("Need both a description and a /by value for that deadline.");
            }

            return new Deadline(description, parseDate(by), false);
        }

        if (command.equals("event") || command.startsWith("event ")) {
            String details = command.substring("event".length()).trim();
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ");
            if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                throw new SummerException(
                        "Events go like this: event DESCRIPTION /from START /to END");
            }

            // fromIndex < toIndex was just checked, so " /from " ends before
            // " /to " begins and both substring ranges below are valid.
            assert fromIndex + " /from ".length() <= toIndex : "/from section overlaps /to";

            String description = details.substring(0, fromIndex).trim();
            String from = details.substring(fromIndex + " /from ".length(), toIndex).trim();
            String to = details.substring(toIndex + " /to ".length()).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new SummerException("An event needs a description, a /from, and a /to.");
            }

            return new Event(description, parseDate(from), parseDate(to), false);
        }

        throw new SummerException("Never heard of that one, try again.");
    }

    /**
     * Parses a date given by the user in yyyy-mm-dd format.
     *
     * @param text date text, expected as yyyy-mm-dd (e.g. 2019-10-15)
     * @return parsed date
     * @throws SummerException if the text is not a valid yyyy-mm-dd date
     */
    private static LocalDate parseDate(String text) throws SummerException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new SummerException("Dates go yyyy-mm-dd out here, e.g. 2019-10-15.");
        }
    }

    /**
     * Parses the task number that follows a command word (e.g. the {@code 2} in
     * {@code delete 2}) and converts it to a zero-based index.
     *
     * @param command user command with a task number after the first space
     * @return zero-based index of the requested task
     * @throws SummerException if the text after the command word is not a number
     */
    private static int parseTaskNumber(String command) throws SummerException {
        String argument = command.substring(command.indexOf(" ") + 1).trim();
        try {
            return Integer.parseInt(argument) - 1;
        } catch (NumberFormatException e) {
            throw new SummerException("Need a task number for that, e.g. delete 2.");
        }
    }
}
