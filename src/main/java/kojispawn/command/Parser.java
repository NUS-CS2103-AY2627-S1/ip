package kojispawn.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import kojispawn.exception.KojisPawnException;

/**
 * Converts raw user input into validated commands.
 */
public class Parser {
    private final TaskParser taskParser = new TaskParser();

    /**
     * Parses one line of user input.
     *
     * @param input Command entered by the user.
     * @return Validated command ready for execution.
     * @throws KojisPawnException If the command or its arguments are invalid.
     */
    public Command parse(String input) throws KojisPawnException {
        String command = input.strip();
        CommandType type = CommandType.getCommandTypeFromString(command);

        return switch (type) {
            case TODO -> new Command(type, taskParser.parseTodo(command), null);
            case DEADLINE -> new Command(type, taskParser.parseDeadline(command), null);
            case EVENT -> new Command(type, taskParser.parseEvent(command), null);
            case MARK -> new Command(type, null, parseTaskNumber(command, "mark"));
            case UNMARK -> new Command(type, null, parseTaskNumber(command, "unmark"));
            case DELETE -> new Command(type, null, parseTaskNumber(command, "delete"));
            case ON -> parseOnCommand(command, type);
            case FIND -> parseFind(command, type);
            case LIST, UNDO, BYE -> parseExactCommand(command, type);
            case UNKNOWN -> throw createUnknownCommandException();
        };
    }

    private int parseTaskNumber(String command, String action) throws KojisPawnException {
        String taskNumberText = command.substring(action.length()).strip();
        if (taskNumberText.isBlank()) {
            throw new KojisPawnException(
                    "Specify which task to " + action + ". Use: " + action + " TASK_NUMBER.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new KojisPawnException(
                    "Task positions are numbers, not guesses. Use: " + action + " TASK_NUMBER.");
        }
        if (taskNumber <= 0) {
            throw new KojisPawnException("The list begins at 1. Choose a positive task number.");
        }
        return taskNumber;
    }

    private Command parseOnCommand(String command, CommandType type) throws KojisPawnException {
        String dateText = command.substring("on".length()).strip();
        if (dateText.isBlank()) {
            throw new KojisPawnException("Specify a date. Use: on yyyy-MM-dd.");
        }

        try {
            return new Command(type, null, null, LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            throw new KojisPawnException(
                    "Query dates must use yyyy-MM-dd and describe a real calendar date.");
        }
    }

    private Command parseFind(String command, CommandType type) throws KojisPawnException {
        String keyword = command.substring("find".length()).strip();
        if (keyword.isBlank()) {
            throw new KojisPawnException("Specify a keyword. Use: find KEYWORD.");
        }
        return new Command(type, keyword);
    }

    private Command parseExactCommand(String command, CommandType type) throws KojisPawnException {
        if (!command.equals(type.name().toLowerCase())) {
            throw createUnknownCommandException();
        }
        return new Command(type, null, null);
    }

    private KojisPawnException createUnknownCommandException() {
        return new KojisPawnException(
                "That command was never part of the plan. "
                        + "Try todo, deadline, event, list, mark, unmark, delete, undo, on, find, or bye.");
    }
}
