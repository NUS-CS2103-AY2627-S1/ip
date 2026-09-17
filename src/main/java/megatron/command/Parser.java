package megatron.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import megatron.exception.EmptyCommandException;
import megatron.exception.EmptyDescriptionException;
import megatron.exception.InvalidTaskFormatException;
import megatron.exception.InvalidTaskNumberException;
import megatron.exception.MegatronException;
import megatron.exception.UnknownCommandException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.Todo;

/**
 * Interprets user commands and creates tasks from add commands.
 */
public final class Parser {
    private static final List<String> AVAILABLE_COMMANDS = new ArrayList<>(List.of(
            "todo", "deadline", "event", "list", "find", "mark", "unmark", "delete", "datetime-help"));

    /**
     * Creates a parser for Megatron commands.
     */
    public Parser() { }

    /**
     * Converts raw input into an executable command.
     *
     * @param rawInput The raw user input.
     * @return The executable command.
     * @throws MegatronException If the input is empty or has an invalid format or task number.
     */
    public Command parse(String rawInput) throws MegatronException {
        Objects.requireNonNull(rawInput);
        String input = rawInput.strip();
        int firstSpace = input.indexOf(' ');
        String commandWord = firstSpace == -1 ? input : input.substring(0, firstSpace);

        switch (commandWord) {
            case "bye":
                if (input.equals("bye")) {
                    return new ExitCommand();
                }
                break;
            case "list":
                if (input.equals("list")) {
                    return new ListCommand();
                }
                break;
            case "find":
                if (input.startsWith("find ")) {
                    String keyword = input.substring(5).trim();
                    if (!keyword.isEmpty()) {
                        return new FindCommand(keyword);
                    }
                }
                throw new InvalidTaskFormatException("find <keyword>.");
            case "datetime-help":
                if (input.equals("datetime-help")) {
                    return new DatetimeHelpCommand();
                }
                break;
            case "mark":
                return new MarkCommand(parseTaskNumber(input, "mark <task number>."));
            case "unmark":
                return new UnmarkCommand(parseTaskNumber(input, "unmark <task number>."));
            case "delete":
                return new DeleteCommand(parseTaskNumber(input, "delete <task number>."));
            default:
                break;
        }

        if (input.isBlank()) {
            throw new EmptyCommandException();
        }
        return new AddCommand(input, this);
    }

    /**
     * Creates the task described by an add command.
     *
     * @param text The add command text.
     * @return The created task.
     * @throws MegatronException If the task description or format is invalid.
     */
    public Task createTask(String text) throws MegatronException {
        Objects.requireNonNull(text);

        if (text.equals("todo") || text.startsWith("todo ")) {
            return createTodo(text);
        }

        if (text.equals("deadline") || text.startsWith("deadline ")) {
            return createDeadline(text);
        }

        if (text.equals("event") || text.startsWith("event ")) {
            return createEvent(text);
        }

        throw new UnknownCommandException(AVAILABLE_COMMANDS);
    }

    /**
     * Creates a todo task from an add command.
     *
     * @param text The complete todo command.
     * @return The created todo task.
     * @throws EmptyDescriptionException If the todo description is empty.
     */
    private static Todo createTodo(String text) throws EmptyDescriptionException {
        String description = text.substring(4).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException();
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline task from an add command.
     *
     * @param text The complete deadline command.
     * @return The created deadline task.
     * @throws InvalidTaskFormatException If the deadline command or date/time is invalid.
     */
    private static Deadline createDeadline(String text) throws InvalidTaskFormatException {
        String[] deadlineParts = text.substring("deadline".length()).split(" /by ", 2);
        if (deadlineParts.length != 2 || deadlineParts[0].trim().isEmpty()
                || deadlineParts[1].trim().isEmpty()) {
            throw new InvalidTaskFormatException("deadline <description> /by <date>.");
        }

        try {
            return new Deadline(deadlineParts[0].trim(), deadlineParts[1].trim());
        } catch (IllegalArgumentException exception) {
            throw new InvalidTaskFormatException("deadline <description> /by <valid date/time>. "
                    + "Use datetime-help to view supported date/time formats.");
        }
    }

    /**
     * Creates an event task from an add command.
     *
     * @param text The complete event command.
     * @return The created event task.
     * @throws InvalidTaskFormatException If the event command or date/time is invalid.
     */
    private static Event createEvent(String text) throws InvalidTaskFormatException {
        String[] eventParts = text.substring("event".length()).split(" /from ", 2);
        if (eventParts.length != 2) {
            throw new InvalidTaskFormatException("event <description> /from <start> /to <end>.");
        }

        String[] eventTimes = eventParts[1].split(" /to ", 2);
        if (eventTimes.length != 2 || eventParts[0].trim().isEmpty()
                || eventTimes[0].trim().isEmpty() || eventTimes[1].trim().isEmpty()) {
            throw new InvalidTaskFormatException("event <description> /from <start> /to <end>.");
        }

        try {
            return new Event(eventParts[0].trim(), eventTimes[0].trim(), eventTimes[1].trim());
        } catch (IllegalArgumentException exception) {
            throw new InvalidTaskFormatException(
                    "event <description> /from <valid start> /to <valid end>. "
                            + "Use datetime-help to view supported date/time formats.");
        }
    }

    /**
     * Extracts a task number from a mark, unmark, or delete command.
     *
     * @param input The complete command text.
     * @param usage The required command format.
     * @return The parsed one-based task number.
     * @throws MegatronException If the command is missing a number or contains an invalid number.
     */
    private static int parseTaskNumber(String input, String usage) throws MegatronException {
        int firstSpace = input.indexOf(' ');
        if (firstSpace == -1 || input.substring(firstSpace + 1).trim().isEmpty()) {
            throw new InvalidTaskFormatException(usage);
        }

        try {
            return Integer.parseInt(input.substring(firstSpace + 1).trim());
        } catch (NumberFormatException exception) {
            throw new InvalidTaskNumberException();
        }
    }
}
