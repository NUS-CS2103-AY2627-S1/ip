package cooper.parser;

import java.util.Locale;

import cooper.exception.CooperException;
import cooper.note.Note;
import cooper.parser.NoteCommand.Operation;

/** Parses note syntax before storage availability or collection bounds are checked. */
public class NoteParser {
    public static final String USAGE = "I need a valid note command:\n"
            + "note add <text>\nnote list\nnote find <keyword>\n"
            + "note edit <number> <text>\nnote delete <number>";
    public static final String INVALID_NUMBER = "I need a valid positive note number!";
    public static final String EMPTY_KEYWORD = "I need a keyword to find matching notes!";

    /** Parses one complete note command, preserving internal text whitespace. */
    public static NoteCommand parse(String input) {
        if (input.matches("(?s).*\\R.*")) {
            throw new CooperException(Note.LINE_BREAK);
        }
        String[] parts = input.strip().split("[ \\t]+", 3);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("note")) {
            throw new CooperException(USAGE);
        }
        Operation operation;
        try {
            operation = Operation.valueOf(parts[1].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new CooperException(USAGE);
        }
        String arguments = parts.length == 3 ? parts[2].strip() : "";
        return switch (operation) {
            case ADD -> new NoteCommand(operation, 0, new Note(arguments).text());
            case FIND -> new NoteCommand(operation, 0, parseKeyword(arguments));
            case LIST -> parseList(arguments);
            case EDIT, DELETE -> parseNumbered(operation, arguments);
        };
    }

    private static String parseKeyword(String arguments) {
        if (arguments.isEmpty()) {
            throw new CooperException(EMPTY_KEYWORD);
        }
        return arguments;
    }

    private static NoteCommand parseList(String arguments) {
        if (!arguments.isEmpty()) {
            throw new CooperException(USAGE);
        }
        return new NoteCommand(Operation.LIST, 0, "");
    }

    /** Checks structure and required text before validating the numeric token. */
    private static NoteCommand parseNumbered(Operation operation, String arguments) {
        if (arguments.isEmpty()) {
            throw new CooperException(USAGE);
        }
        String[] parts = arguments.split("[ \\t]+", 2);
        if (operation == Operation.DELETE && parts.length != 1) {
            throw new CooperException(USAGE);
        }
        String text = operation == Operation.EDIT
                ? new Note(parts.length == 2 ? parts[1] : "").text() : "";
        return new NoteCommand(operation, parseNumber(parts[0]), text);
    }

    private static int parseNumber(String token) {
        try {
            if (!token.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            int number = Integer.parseInt(token);
            if (number <= 0) {
                throw new NumberFormatException();
            }
            return number;
        } catch (NumberFormatException e) {
            throw new CooperException(INVALID_NUMBER);
        }
    }
}
