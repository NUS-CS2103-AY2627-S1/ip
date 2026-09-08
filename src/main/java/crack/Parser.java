package crack;

import crack.task.Deadline;
import crack.task.Event;
import crack.task.Task;
import crack.task.TaskDate;
import crack.task.Todo;

/**
 * Makes sense of what the user typed, turning raw lines into commands and tasks.
 */
public class Parser {
    private static final String BY_MARKER = " /by ";
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";

    private static final String EVENT_FORMAT_HINT = "Yo, an event needs a '/from' and a '/to'. "
            + "Like: event project meeting /from 2/12/2020 1400 /to 2/12/2020 1600";

    /**
     * One line of input, split into the command word and everything after it.
     *
     * @param command The command the user asked for.
     * @param arguments The rest of the line, trimmed, or blank if there was none.
     */
    public record Parsed(Command command, String arguments) {
    }

    private Parser() {
    }

    /**
     * Splits a line of input into its command and its arguments.
     *
     * @param input The whole line the user typed.
     * @return The command, paired with whatever followed it.
     * @throws CrackException If the first word is not a command we know.
     */
    public static Parsed parse(String input) throws CrackException {
        // Split on a run of whitespace so stray spaces or a tab do not hide the command.
        String[] parts = input.split("\\s+", 2);
        assert parts.length >= 1 : "String.split always yields at least one part";
        Command command = Command.fromKeyword(parts[0]);
        String arguments = parts.length > 1 ? parts[1].trim() : "";
        return new Parsed(command, arguments);
    }

    /**
     * Builds a todo from a plain description.
     *
     * @param arguments The description the user typed.
     * @return The new todo.
     * @throws CrackException If the description is blank.
     */
    public static Task parseTodo(String arguments) throws CrackException {
        return new Todo(requireDescription(arguments, "todo"));
    }

    /**
     * Builds a deadline from "description /by when".
     *
     * @param arguments Everything the user typed after the command word.
     * @return The new deadline.
     * @throws CrackException If the "/by" is missing, or either half is unusable.
     */
    public static Task parseDeadline(String arguments) throws CrackException {
        String[] parts = arguments.split(BY_MARKER, 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new CrackException("Yo, a deadline needs a '/by'. Like: deadline return book /by 2/12/2020 1500");
        }
        return Deadline.of(requireDescription(parts[0].trim(), "deadline"), parts[1].trim());
    }

    /**
     * Builds an event from "description /from start /to end".
     *
     * @param arguments Everything the user typed after the command word.
     * @return The new event.
     * @throws CrackException If "/from" or "/to" is missing, or any part is unusable.
     */
    public static Task parseEvent(String arguments) throws CrackException {
        String[] parts = arguments.split(FROM_MARKER, 2);
        if (parts.length < 2) {
            throw new CrackException(EVENT_FORMAT_HINT);
        }
        String[] periodParts = parts[1].split(TO_MARKER, 2);
        if (periodParts.length < 2 || periodParts[0].isBlank() || periodParts[1].isBlank()) {
            throw new CrackException(EVENT_FORMAT_HINT);
        }
        return Event.of(requireDescription(parts[0].trim(), "event"), periodParts[0].trim(), periodParts[1].trim());
    }

    /**
     * Turns a task number the user typed into a list index.
     *
     * @param arguments The number as typed, counting from one.
     * @param listSize How many tasks there are to point at.
     * @return The matching zero based index.
     * @throws CrackException If it is blank, not a number, or points outside the list.
     */
    public static int parseIndex(String arguments, int listSize) throws CrackException {
        if (arguments.isEmpty()) {
            throw new CrackException("Which one tho? Gimme a task number.");
        }
        int position;
        try {
            position = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new CrackException("'" + arguments + "' ain't a number gng.");
        }
        if (listSize == 0) {
            throw new CrackException("Your list is empty, ain't nothing to point at.");
        }
        if (position < 1 || position > listSize) {
            throw new CrackException("You only got " + listSize + (listSize == 1 ? " task" : " tasks")
                    + ", so " + position + " ain't it.");
        }
        int index = position - 1;
        assert index >= 0 && index < listSize : "a checked position must land inside the list";
        return index;
    }

    /**
     * Reads the keyword the user wants to search for.
     *
     * @param arguments Everything the user typed after the command word.
     * @return The keyword to look for.
     * @throws CrackException If no keyword was given.
     */
    public static String parseKeyword(String arguments) throws CrackException {
        if (arguments.isEmpty()) {
            throw new CrackException("Find what tho? Gimme a word to look for.");
        }
        return arguments;
    }

    /**
     * Reads the day the user asked about.
     *
     * @param arguments The date as the user typed it.
     * @return The day they meant.
     * @throws CrackException If no date was given, or it cannot be read.
     */
    public static TaskDate parseDate(String arguments) throws CrackException {
        if (arguments.isEmpty()) {
            throw new CrackException("On what day tho? Try: on 2/12/2020");
        }
        return TaskDate.parse(arguments);
    }

    /**
     * Returns the description unchanged, as long as the user actually gave one.
     *
     * @param description The description as typed.
     * @param taskType The kind of task, used to word the complaint.
     * @return The same description.
     * @throws CrackException If the description is blank.
     */
    private static String requireDescription(String description, String taskType) throws CrackException {
        if (description.isEmpty()) {
            throw new CrackException("Nah, a " + taskType + " needs an actual description gng.");
        }
        return description;
    }
}
