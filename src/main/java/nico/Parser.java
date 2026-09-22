package nico;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Interprets user input and creates command objects when possible.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Splits user input into a command word and its optional argument.
     *
     * @param fullCommand Complete command entered by the user.
     * @return Command corresponding to the user input.
     * @throws NicoException If the command is not recognized or has invalid arguments.
     */
    public static Command parseCommand(String fullCommand) throws NicoException {
        String[] commandParts = fullCommand.trim().split("\\s+", 2);
        String argument = commandParts.length > 1 ? commandParts[1] : null;
        switch (commandParts[0]) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "urgent":
                if (argument != null) {
                    throw NicoException.invalidInput("`urgent` does not take extra text.", "urgent");
                }
                return new UrgentCommand();
            case "mark":
                return new MarkCommand(argument);
            case "unmark":
                return new UnmarkCommand(argument);
            case "todo":
                return new TodoCommand(argument);
            case "deadline":
                return new DeadlineCommand(argument);
            case "event":
                return new EventCommand(argument);
            case "delete":
                return new DeleteCommand(argument);
            case "find":
                return new FindCommand(argument);
            case "addpriority":
                return new AddPriorityCommand(argument);
            case "showpriority":
                return new ShowPriorityCommand(argument);
            default:
                throw NicoException.invalidInput("I don't recognise that command.",
                        "todo DESCRIPTION, list, mark TASK_NUMBER, or bye");
        }
    }

    /**
     * Converts user-entered date-time text to a LocalDateTime value.
     *
     * @param input Date-time text to parse.
     * @param format Expected date-time format.
     * @return Parsed date and time.
     * @throws NicoException If the input does not match the expected format.
     */
    public static LocalDateTime parseDateTime(String input, String format) throws NicoException {
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException exception) {
            throw new NicoException("Aiyoh! The date and time is invalid." + System.lineSeparator()
                    + "Try this lah: " + format, exception);
        }
    }
}
