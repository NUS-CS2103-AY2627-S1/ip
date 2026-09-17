package jonathan.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jonathan.JonathanException;
import jonathan.command.AddCommand;
import jonathan.command.CheckCommand;
import jonathan.command.Command;
import jonathan.command.DeleteCommand;
import jonathan.command.ExitCommand;
import jonathan.command.FilterPriorityCommand;
import jonathan.command.FindCommand;
import jonathan.command.ListCommand;
import jonathan.command.MarkCommand;
import jonathan.command.PriorityCommand;
import jonathan.command.SortCommand;
import jonathan.task.Deadlines;
import jonathan.task.Event;
import jonathan.task.Priority;
import jonathan.task.ToDo;
/** Parses user input into executable chatbot commands. */
public class Parser {

    /**
     * Parses the raw command string entered by the user and returns the corresponding {@code Command} object.
     *
     * @param fullCommand The raw input string entered by the user.
     * @return The specific {@code Command} object representing the user's intent.
     * @throws JonathanException If the command format is invalid, missing required fields, or unrecognized.
     */
    public static Command parse(String fullCommand) throws JonathanException {
        String command = fullCommand.trim();

        if (command.equals("bye")) {
            return new ExitCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (command.startsWith("mark ") || command.equals("mark")) {
            int index = extractIndex(command, "mark");
            return new MarkCommand(index, true);
        } else if (command.startsWith("unmark ") || command.equals("unmark")) {
            int index = extractIndex(command, "unmark");
            return new MarkCommand(index, false);
        } else if (command.startsWith("delete ") || command.equals("delete")) {
            int index = extractIndex(command, "delete");
            return new DeleteCommand(index);
        } else if (command.startsWith("check ") || command.equals("check")) {
            String dateString = command.substring("check".length()).trim();
            require(!dateString.isEmpty(),
                    "Please provide a date to check (e.g., check 2026-08-27).");
            return new CheckCommand(dateString);
        } else if (command.startsWith("find ") || command.equals("find")) {
            String keyword = command.substring("find".length()).trim();
            require(!keyword.isEmpty(),
                    "Please provide a keyword to search for (e.g., find book).");
            return new FindCommand(keyword);
        } else if (command.startsWith("todo ") || command.equals("todo")) {
            String description = command.substring("todo".length()).trim();
            require(!description.isEmpty(), "A todo needs a description after `todo`.");
            return new AddCommand(new ToDo(description));
        } else if (command.startsWith("deadline ") || command.equals("deadline")) {
            String details = command.substring("deadline".length()).trim();
            int byIndex = details.indexOf(" /by ");
            require(byIndex > 0 && byIndex + 5 < details.length(),
                    "A deadline needs a description and a `/by` time.");

            String description = details.substring(0, byIndex);
            String by = details.substring(byIndex + 5).trim();
            require(isValidDate(by), "The deadline date must be in yyyy-mm-dd format.");
            return new AddCommand(new Deadlines(description, by));
        } else if (command.startsWith("event ") || command.equals("event")) {
            String details = command.substring("event".length()).trim();
            int fromIndex = details.indexOf(" /from ");
            int toIndex = details.indexOf(" /to ");
            require(fromIndex > 0 && toIndex > fromIndex + 7 && toIndex + 5 < details.length(),
                    "An event needs a description, a `/from` time, and a `/to` time.");

            String description = details.substring(0, fromIndex);
            String from = details.substring(fromIndex + 7, toIndex).trim();
            String to = details.substring(toIndex + 5).trim();
            require(isValidDate(from) && isValidDate(to),
                    "jonathan.task.Event dates must be in yyyy-mm-dd format.");
            return new AddCommand(new Event(description, from, to));
        } else if (command.startsWith("priority") || command.equals("priority")) {
            return parsePriorityCommand(command);
        } else if (command.startsWith("filter ") || command.equals("filter")) {
            return parseFilterCommand(command);
        } else if (command.startsWith("sort ") || command.equals("sort")) {
            return parseSortCommand(command);
        } else {
            throw new JonathanException(
                    "I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.");
        }
    }

    /**
     * Extracts and parses the zero-based task index from a command string.
     *
     * @param command     The full command string containing the index.
     * @param commandWord The specific command prefix (e.g., "mark", "delete") to be removed.
     * @return The parsed zero-based index of the target task.
     * @throws JonathanException If the index is missing or is not a valid integer.
     */
    private static int extractIndex(String command, String commandWord) throws JonathanException {
        String numberText = command.substring(commandWord.length()).trim();
        require(!numberText.isEmpty(), "`" + commandWord + "` needs a task number.");
        try {
            return Integer.parseInt(numberText) - 1; // Zero-based index
        } catch (NumberFormatException exception) {
            throw new JonathanException("`" + commandWord + "` needs a valid integer task number.");
        }
    }
    /**
     * Parses a priority command.
     *
     * @param command complete priority command
     * @return parsed priority command
     * @throws JonathanException if the syntax or priority is invalid
     */
    private static Command parsePriorityCommand(String command) throws JonathanException {
        String details = command.substring("priority".length()).trim();
        String[] parts = details.split("\\s+");

        require(parts.length == 2,
                "Use priority <task number> < high | medium | low >.");
        int taskIndex;
        try {
            taskIndex = Integer.parseInt(parts[0]) - 1;
        } catch (NumberFormatException exception) {
            throw new JonathanException(
                    "Priority needs a valid task number"
            );
        }

        try {
            Priority priority = Priority.fromInput(parts[1]);
            return new PriorityCommand(taskIndex, priority);

        } catch (IllegalArgumentException exception) {
            throw new JonathanException(
                    "Priority must be high, medium, low, or none; "
                            + "e.g. `priority 1 high`."
            );
        }
    }

    /**
     * Parses a command that filters tasks by priority.
     *
     * @param command complete filter command
     * @return parsed filter command
     * @throws JonathanException if the filter syntax or priority is invalid
     */
    private static Command parseFilterCommand(String command) throws JonathanException {
        String details = command.substring("filter".length()).trim();
        String[] parts = details.split("\\s+");

        require(parts.length == 2 && parts[0].equals("priority"),
                "Use filter priority <high | medium | low | none>.");

        try {
            return new FilterPriorityCommand(Priority.fromInput(parts[1]));
        } catch (IllegalArgumentException exception) {
            throw new JonathanException(
                    "Priority must be high, medium, low or none.");
        }
    }

    /**
     * Parses a command that sorts tasks by priority.
     *
     * @param command complete sort command
     * @return parsed sort command
     * @throws JonathanException if the sort syntax is invalid
     */
    private static Command parseSortCommand(String command) throws JonathanException {
        require(command.equals("sort priority"), "Use sort priority.");
        return new SortCommand();
    }


    /**
     * Evaluates a condition and throws an exception with the specified message if it is false.
     *
     * @param condition The required condition to evaluate.
     * @param message   The error message to display if the condition fails.
     * @throws JonathanException If the condition evaluates to {@code false}.
     */
    public static void require(boolean condition, String message) throws JonathanException {
        if (!condition) {
            throw new JonathanException(message);
        }
    }

    /**
     * Checks if a provided string matches the {@code yyyy-mm-dd} date format.
     *
     * @param dateString The date string to validate.
     * @return {@code true} if the string can be parsed into a {@code LocalDate}; {@code false} otherwise
     */
    public static boolean isValidDate(String dateString) {
        try {
            LocalDate.parse(dateString);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }
}
