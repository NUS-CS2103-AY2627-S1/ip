package sioet.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import sioet.SioetException;

/**
 * Parses user commands entered into sioet.ui.Sioet.
 */
public class Parser {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Converts a user command into a command object.
     *
     * @param command the complete command entered by the user
     * @return the command represented by the input
     * @throws SioetException if the command is not recognised
     */
    public static Command parse(String command) throws SioetException {
        command = command.trim();

        if (command.equals("bye")) {
            return new ExitCommand();
        }

        String commandType = parseCommand(command);
        String arguments = getArguments(command);

        switch (commandType) {
            case "list":
                return new ListCommand();
            case "mark":
                return new MarkCommand(arguments, true);
            case "unmark":
                return new MarkCommand(arguments, false);
            case "todo":
                return new TodoCommand(arguments);
            case "deadline":
                return new DeadlineCommand(arguments);
            case "event":
                return new EventCommand(arguments);
            case "delete":
                return new DeleteCommand(arguments);
            case "find":
                return new FindCommand(arguments);
            case "repeat":
                return new RecurringTaskCommand(arguments);
            default:
                throw new SioetException(
                        "I don't recognise that command. Try list, todo, deadline, event, "
                        + "mark, unmark, repeat or find.");
        }
    }

    /**
     * Identifies the type of a user command.
     *
     * @param command the command entered by the user
     * @return the command type
     * @throws SioetException if the command is not recognised
     */
    private static String parseCommand(String command) throws SioetException {
        if (command.equals("list")) {
            return "list";
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            return "mark";
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            return "unmark";
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return "todo";
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return "deadline";
        } else if (command.equals("event") || command.startsWith("event ")) {
            return "event";
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            return "delete";
        } else if (command.equals("find") || command.startsWith("find ")) {
            return "find";
        } else if (command.equals("repeat") || command.startsWith("repeat ")) {
            return "repeat";
        } else {
            throw new SioetException(
                    "I don't recognise that command. Try list, todo, deadline, event, mark, "
                    + "unmark, repeat or find.");
        }
    }

    /**
     * Extracts the argument portion of a command.
     *
     * @param command the complete command
     * @return the text after the command word
     */
    private static String getArguments(String command) {
        int spaceIndex = command.indexOf(" ");

        if (spaceIndex == -1) {
            return "";
        }

        return command.substring(spaceIndex + 1).trim();
    }

    /**
     * Parses a date and time from the sioet.ui.Sioet date format.
     *
     * @param text the date and time text
     * @return the parsed date and time
     * @throws SioetException if the date format is invalid
     */
    public static LocalDateTime parseDateTime(String text) throws SioetException {
        try {
            return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new SioetException(
                    "Please use the date format d/M/yyyy HHmm. "
                            + "Example: 2/12/2019 1800");
        }
    }
}
