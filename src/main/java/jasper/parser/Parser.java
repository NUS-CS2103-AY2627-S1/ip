package jasper.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import jasper.JasperException;
import jasper.command.ByeCommand;
import jasper.command.Command;
import jasper.command.DeadlineCommand;
import jasper.command.DeleteCommand;
import jasper.command.EventCommand;
import jasper.command.FindCommand;
import jasper.command.ListCommand;
import jasper.command.MarkCommand;
import jasper.command.TodoCommand;
import jasper.command.UnmarkCommand;

/**
 * Handles parsing user input into application commands and date-time objects.
 */
public class Parser {
    /**
     * Parses the given user input string and returns the corresponding executable command.
     *
     * @param line Raw user input string to be parsed.
     * @return Command object representing the user's intent.
     * @throws JasperException If the command is unknown or the arguments are invalid.
     */
    public static Command parseCmd(String line) throws JasperException {
        if (line.contains("|")) {
            throw new JasperException("Pipe character '|' not allowed in commands!");
        }
        String[] tokens = line.strip().split("\\s+", 2);
        String arg = (tokens.length > 1) ? tokens[1] : "";
        return switch (tokens[0]) {
            case "bye" -> new ByeCommand(arg);
            case "list" -> new ListCommand(arg);
            case "unmark" -> new UnmarkCommand(arg);
            case "mark" -> new MarkCommand(arg);
            case "delete" -> new DeleteCommand(arg);
            case "todo" -> new TodoCommand(arg);
            case "deadline" -> new DeadlineCommand(arg);
            case "event" -> new EventCommand(arg);
            case "find" -> new FindCommand(arg);
            default -> throw new JasperException("Unknown command: " + tokens[0]);
        };
    }

    /**
     * Parses a date-time string into a LocalDateTime object using a strict format.
     *
     * @param dt Date-time string in the format "uuuu-MM-dd HH:mm".
     * @return Parsed LocalDateTime object.
     * @throws JasperException If the provided date-time string does not match the expected format.
     */
    public static LocalDateTime parseDateTime(String dt) throws JasperException {
        String pattern = "uuuu-MM-dd HH:mm";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern)
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            return LocalDateTime.parse(dt, fmt);
        } catch (DateTimeParseException e) {
            throw new JasperException("Accepted datetime format(s): " + pattern);
        }
    }
}
