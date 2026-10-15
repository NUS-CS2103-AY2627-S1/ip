package ralph.command;

import ralph.exception.RalphException;
import ralph.parser.Parser;

/**
 * Parses raw user input and returns the corresponding command object.
 */
public class CommandHandler {
    private final Parser parser;

    public CommandHandler(Parser parser) {
        this.parser = parser;
    }

    /**
     * Parses a raw input line into the corresponding Command object.
     *
     * @param line raw user input
     * @return Command instance representing the requested action
     * @throws RalphException on invalid or malformed input
     */
    public Command parse(String line) throws RalphException {
        Parser.Parsed parsed = parser.parseCommand(line);
        String command = parsed.command();
        String rest = parsed.args();

        switch (command) {
            case "bye":
                return new Command.ByeCommand();
            case "list":
                if (rest == null || rest.isEmpty()) {
                    return new Command.ListCommand();
                }
                // Expect syntax: /by <key>
                String low = rest.toLowerCase();
                if (!low.startsWith("/by")) {
                    throw new RalphException("I can sort by deadline or status only.");
                }
                String key = rest.length() > 3 ? rest.substring(3).trim() : "";
                if (key.isEmpty()) {
                    throw new RalphException("I can sort by deadline or status only.");
                }
                return new Command.ListCommand(key);
            case "find":
                return new Command.FindCommand(Parser.getRequiredDescription(rest, "find"));
            case "mark":
                return new Command.MarkCommand(Parser.parseTaskIndex(rest, "mark"));
            case "unmark":
                return new Command.UnmarkCommand(Parser.parseTaskIndex(rest, "unmark"));
            case "delete":
                return new Command.DeleteCommand(Parser.parseTaskIndex(rest, "delete"));
            case "todo":
                return new Command.AddTodoCommand(Parser.getRequiredDescription(rest, "todo"));
            case "deadline":
                return parseDeadlineCommand(rest);
            case "event":
                return parseEventCommand(rest);
            default:
                throw new RalphException(
                    "I don't recognise that command. Try: list, find, todo, deadline, event, "
                        + "mark, unmark, delete, bye."
                );
        }
    }

    private static Command parseDeadlineCommand(String rest) throws RalphException {
        String input = Parser.getRequiredDescription(rest, "deadline");
        String[] parts = input.split(" /by ", 2);
        String description = parts[0].trim();
        String dueBy = parts.length > 1 ? parts[1].trim() : "";
        return new Command.AddDeadlineCommand(description, dueBy);
    }

    private static Command parseEventCommand(String rest) throws RalphException {
        String input = Parser.getRequiredDescription(rest, "event");
        String[] parts = input.split(" /from ", 2);
        String description = parts[0].trim();
        if (parts.length < 2) {
            throw new RalphException(
                "Events need '/from' and '/to' times "
                    + "(e.g. 'event meeting /from 2019-10-10 /to 2019-10-11')."
            );
        }

        String[] times = parts[1].split(" /to ", 2);
        String from = times[0].trim();
        String to = times.length > 1 ? times[1].trim() : "";
        return new Command.AddEventCommand(description, from, to);
    }
}
