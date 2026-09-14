package morgan.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import morgan.command.AddCommand;
import morgan.command.Command;
import morgan.command.DeleteCommand;
import morgan.command.ExitCommand;
import morgan.command.FindDateCommand;
import morgan.command.FindKeywordCommand;
import morgan.command.ListCommand;
import morgan.command.MarkCommand;
import morgan.command.SortCommand;
import morgan.exception.MorganException;
import morgan.storage.Storage;
import morgan.task.Deadline;
import morgan.task.Event;
import morgan.task.TaskList;
import morgan.task.ToDo;
import morgan.ui.Ui;

/**
 * Parses user input into executable commands for Morgan application using HashMap lookup.
 */
public class Parser {

    @FunctionalInterface
    private interface CommandFunction {
        Command parse(String arguments) throws MorganException;
    }

    private static final Map<String, CommandFunction> COMMAND_MAP = new HashMap<>();

    static {
        COMMAND_MAP.put("bye", args -> parseNoArgument(args, new ExitCommand()));
        COMMAND_MAP.put("list", args -> parseNoArgument(args, new ListCommand()));
        COMMAND_MAP.put("mark", args -> new MarkCommand(parseIndex(args), true));
        COMMAND_MAP.put("unmark", args -> new MarkCommand(parseIndex(args), false));
        COMMAND_MAP.put("delete", args -> new DeleteCommand(parseIndex(args)));
        COMMAND_MAP.put("todo", Parser::parseTodo);
        COMMAND_MAP.put("deadline", Parser::parseDeadline);
        COMMAND_MAP.put("event", Parser::parseEvent);
        COMMAND_MAP.put("dates", Parser::parseDates);
        COMMAND_MAP.put("find", Parser::parseFind);
        COMMAND_MAP.put("sort", args -> parseNoArgument(args, new SortCommand()));
    }

    /**
     * Parses user input and returns the corresponding Command object.
     *
     * @param fullCommand Full line typed by user.
     * @return Abstract Command object to execute.
     * @throws MorganException If input is invalid.
     */
    public static Command parse(String fullCommand) throws MorganException {
        String trimmedInput = fullCommand.trim();
        if (trimmedInput.isEmpty()) {
            throw new MorganException("Meow? Is that a fish?");
        }

        String[] parts = trimmedInput.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1].trim() : "";

        CommandFunction fn = COMMAND_MAP.get(commandWord);
        if (fn == null) {
            throw new MorganException("Meow? Is that a fish? Try list, todo, deadline, event, find, sort, or bye.");
        }

        return fn.parse(arguments);
    }

    /**
     * Parses the input and executes the resulting command.
     */
    public static boolean parseAndExecute(String input, TaskList tasks, Ui ui, Storage storage) throws MorganException {
        Command command = parse(input);
        return command.execute(tasks, ui, storage);
    }

    private static int parseIndex(String args) throws MorganException {
        if (args.isEmpty()) {
            throw new MorganException("Meow~ Please state which fish number!");
        }
        try {
            return Integer.parseInt(args) - 1;
        } catch (NumberFormatException e) {
            throw new MorganException("Meow~ That index is not a valid number!");
        }
    }

    /**
     * Validates that a command which takes no arguments is used on its own.
     *
     * @param args Arguments entered after the command word.
     * @param command Command to return when no arguments are present.
     * @return The validated command.
     * @throws MorganException If unexpected arguments are present.
     */
    private static Command parseNoArgument(String args, Command command) throws MorganException {
        if (!args.isEmpty()) {
            throw new MorganException(
                    "Meow? This command needs no extra treats. Please use it on its own.");
        }
        return command;
    }

    private static Command parseTodo(String args) throws MorganException {
        if (args.isEmpty()) {
            throw new MorganException("Meow? Tell me what to add, e.g., todo have a nice sleep.");
        }
        return new AddCommand(new ToDo(args));
    }

    private static Command parseDeadline(String args) throws MorganException {
        String[] parts = args.split(" /by ");
        if (parts.length < 2 || parts[0].trim().isEmpty()) {
            throw new MorganException(
                    "Meow? Tell me what to add, e.g., deadline catch Jerry the mouse /by 2026-09-18.");
        }
        try {
            return new AddCommand(new Deadline(parts[0].trim(), parts[1].trim()));
        } catch (DateTimeParseException e) {
            throw new MorganException("Meow! Please use date format: yyyy-MM-dd (e.g., 2026-08-31)");
        }
    }

    private static Command parseEvent(String args) throws MorganException {
        if (args.isEmpty() || args.startsWith("/from ") || args.startsWith("/to ")) {
            throw new MorganException(
                    "Meow? Tell me what event to add, e.g., event fish party "
                            + "/from 2026-08-31 1400 /to 2026-08-31 1600.");
        }

        int fromIndex = args.indexOf(" /from ");
        int toIndex = args.indexOf(" /to ");
        boolean hasValidStructure = fromIndex > 0
                && toIndex > fromIndex + " /from ".length()
                && toIndex + " /to ".length() < args.length()
                && args.indexOf(" /from ", fromIndex + 1) == -1
                && args.indexOf(" /to ", toIndex + 1) == -1;

        if (!hasValidStructure) {
            throw new MorganException(
                    "Meow? An event needs one /from and one /to, in that order.");
        }

        String name = args.substring(0, fromIndex).trim();
        String start = args.substring(fromIndex + " /from ".length(), toIndex).trim();
        String end = args.substring(toIndex + " /to ".length()).trim();
        try {
            Event event = new Event(name, start, end);
            if (!event.getEnd().isAfter(event.getStart())) {
                throw new MorganException("Meow? Morgan doesn't think you can do time traveling.");
            }
            return new AddCommand(event);
        } catch (DateTimeParseException e) {
            throw new MorganException("Meow! Please use date format: yyyy-MM-dd HHmm (e.g., 2026-08-31 1400)");
        }
    }

    private static Command parseDates(String args) throws MorganException {
        if (args.isEmpty()) {
            throw new MorganException("Meow! Please use format: dates yyyy-MM-dd (e.g., dates 2026-09-09)");
        }
        try {
            LocalDate targetDate = LocalDate.parse(args, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
            return new FindDateCommand(targetDate);
        } catch (DateTimeParseException e) {
            throw new MorganException("Meow! Please use format: dates yyyy-MM-dd (e.g., dates 2026-09-09)");
        }
    }

    private static Command parseFind(String args) throws MorganException {
        if (args.isEmpty()) {
            throw new MorganException("Meow~ Please state the keyword to search for!");
        }
        return new FindKeywordCommand(args);
    }
}
