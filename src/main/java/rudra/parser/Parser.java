package rudra.parser;

import rudra.command.Command;
import rudra.command.DeadlineCommand;
import rudra.command.DeleteCommand;
import rudra.command.EventCommand;
import rudra.command.ExitCommand;
import rudra.command.FindCommand;
import rudra.command.ListCommand;
import rudra.command.MarkCommand;
import rudra.command.SortCommand;
import rudra.command.TodoCommand;
import rudra.command.UnmarkCommand;
import rudra.exception.RudraException;
import rudra.task.TaskDateTime;

/**
 * Parses raw user input into executable commands.
 */
public class Parser {
    /**
     * Parses a raw command line into a concrete command.
     *
     * @param fullCommand Raw command line entered by the user.
     * @return Parsed command object.
     * @throws RudraException If the command is invalid.
     */
    public static Command parse(String fullCommand) throws RudraException {
        String[] parts = fullCommand.split(" ", 2);
        CommandWord commandWord = CommandWord.from(parts[0]).orElseThrow(() -> new RudraException(
                "I don't recognize that command yet. Try todo, deadline, event, list, mark, unmark, delete,"
                        + " find, or sort."));

        switch (commandWord) {
            case BYE:
                if (parts.length == 1) {
                    return new ExitCommand();
                }
                break;
            case LIST:
                if (parts.length == 1) {
                    return new ListCommand();
                }
                break;
            case MARK:
                return new MarkCommand(parseTaskNumber(parts));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(parts));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(parts));
            case FIND:
                return new FindCommand(requireKeyword(parts));
            case SORT:
                return parseSortCommand(parts);
            case TODO:
                return new TodoCommand(requireDescription(parts, "todo"));
            case DEADLINE:
                return parseDeadlineCommand(parts);
            case EVENT:
                return parseEventCommand(parts);
            default:
                break;
        }

        throw new RudraException("I don't recognize that command yet. Try todo, deadline, event, list, mark, unmark,"
                + " delete, find, or sort.");
    }

    /**
     * Parses a command that sorts deadlines chronologically.
     *
     * @param parts User input split into command word and remaining text.
     * @return Parsed sort command.
     * @throws RudraException If the sort criterion is missing or unsupported.
     */
    private static Command parseSortCommand(String[] parts) throws RudraException {
        if (parts.length == 2 && parts[1].equals("deadline")) {
            return new SortCommand();
        }

        throw new RudraException("Please use: sort deadline");
    }

    /**
     * Parses the deadline-specific suffix after the main command word.
     *
     * @param parts User input split into command word and remaining text.
     * @return Parsed deadline command.
     * @throws RudraException If the description or deadline format is invalid.
     */
    private static Command parseDeadlineCommand(String[] parts) throws RudraException {
        String descriptionAndBy = requireDescription(parts, "deadline");
        String[] deadlineParts = descriptionAndBy.split(" /by ", 2);
        if (deadlineParts.length < 2 || deadlineParts[0].isBlank() || deadlineParts[1].isBlank()) {
            throw new RudraException("Please use: deadline DESCRIPTION /by WHEN");
        }
        assert deadlineParts.length == 2 && !deadlineParts[0].isBlank() && !deadlineParts[1].isBlank()
                : "A validated deadline must have a description and due date";

        return new DeadlineCommand(deadlineParts[0], TaskDateTime.parse(deadlineParts[1]));
    }

    /**
     * Parses the event-specific suffix after the main command word.
     *
     * @param parts User input split into command word and remaining text.
     * @return Parsed event command.
     * @throws RudraException If the description or event time range is invalid.
     */
    private static Command parseEventCommand(String[] parts) throws RudraException {
        String descriptionAndTime = requireDescription(parts, "event");
        String[] eventParts = descriptionAndTime.split(" /from | /to ", 3);
        if (eventParts.length < 3 || eventParts[0].isBlank() || eventParts[1].isBlank() || eventParts[2].isBlank()) {
            throw new RudraException("Please use: event DESCRIPTION /from START /to END");
        }
        assert eventParts.length == 3 && !eventParts[0].isBlank()
                && !eventParts[1].isBlank() && !eventParts[2].isBlank()
                : "A validated event must have a description, start date, and end date";

        return new EventCommand(eventParts[0], TaskDateTime.parse(eventParts[1]), TaskDateTime.parse(eventParts[2]));
    }

    /**
     * Parses a one-based task number from a command that targets an existing task.
     *
     * @param parts User input split into command word and remaining text.
     * @return Zero-based task index.
     * @throws RudraException If the task number is missing or malformed.
     */
    private static int parseTaskNumber(String[] parts) throws RudraException {
        if (parts.length <= 1 || parts[1].isBlank()) {
            throw new RudraException("Please include a task number.");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1) {
                throw new RudraException("That task number is out of range.");
            }
            assert taskNumber >= 1 : "A validated user-facing task number must be positive";
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new RudraException("Task numbers should be whole numbers.");
        }
    }

    /**
     * Ensures that a command includes a non-empty description segment.
     *
     * @param parts User input split into command word and remaining text.
     * @param taskType Task type name used in the validation message.
     * @return Non-empty description text.
     * @throws RudraException If the description is missing.
     */
    private static String requireDescription(String[] parts, String taskType) throws RudraException {
        if (parts.length <= 1 || parts[1].isBlank()) {
            throw new RudraException("The description of a " + taskType + " cannot be empty.");
        }
        return parts[1];
    }

    private static String requireKeyword(String[] parts) throws RudraException {
        if (parts.length <= 1 || parts[1].isBlank()) {
            throw new RudraException("The keyword for find cannot be empty.");
        }
        return parts[1];
    }
}
