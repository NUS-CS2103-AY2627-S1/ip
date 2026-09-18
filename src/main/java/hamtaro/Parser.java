package hamtaro;

import hamtaro.command.Command;
import hamtaro.exception.HamtaroException;
import hamtaro.task.Deadline;
import hamtaro.task.Event;
import hamtaro.task.Todo;
import hamtaro.util.Utils;

/** Converts user input into command objects. */
public class Parser {

    /** Converts one line of input into the matching command object. */
    public Command parseCommand(String userInput) throws HamtaroException {
        String input = userInput.trim();
        if (input.isEmpty()) {
            throw new HamtaroException("Command doesn't exist!");
        }

        String[] parts = input.split("\\s+", 2);
        return switch (parts[0]) {
            case "tag" -> Command.tag(parseTagTaskNumber(parts), parseTag(parts));
            case "untag" -> Command.untag(parseTagTaskNumber(parts), parseTag(parts));
            case "mark" -> Command.mark(parseCommandTaskNumber(parts, "mark"), true);
            case "unmark" -> Command.mark(parseCommandTaskNumber(parts, "unmark"), false);
            case "delete" -> Command.delete(parseCommandTaskNumber(parts, "delete"));
            case "todo" -> Command.add(createTodo(parts));
            case "deadline" -> Command.add(createDeadline(parts));
            case "event" -> Command.add(createEvent(parts));
            case "list" -> createListCommand(parts);
            case "find" -> Command.find(parseFindKeyword(parts));
            case "bye" -> createExitCommand(parts);
            default -> throw new HamtaroException("Command doesn't exist!");
        };
    }

    private int parseCommandTaskNumber(String[] parts, String command) throws HamtaroException {
        if (parts.length == 1 || parts[1].contains(" ")) {
            throw new HamtaroException("Invalid Argument Length! Usage: " + command + " [task number]");
        }
        if (!Utils.isInteger(parts[1])) {
            throw new HamtaroException(parts[1] + " is not a number! Usage: " + command + " [task number]");
        }
        return Integer.parseInt(parts[1]);
    }

    private int parseTagTaskNumber(String[] parts) throws HamtaroException {
        if (parts.length != 2 || parts[1].split("\\s+").length != 2) {
            throw new HamtaroException("Invalid Argument Format! Usage: tag [task number] [#tag]");
        }
        String taskNumber = parts[1].split("\\s+")[0];
        if (!Utils.isInteger(taskNumber)) {
            throw new HamtaroException(taskNumber + " is not a number! Usage: tag [task number] [#tag]");
        }
        return Integer.parseInt(taskNumber);
    }

    private String parseTag(String[] parts) throws HamtaroException {
        String[] arguments = parts.length == 2 ? parts[1].split("\\s+") : new String[0];
        if (arguments.length != 2 || !arguments[1].startsWith("#")) {
            throw new HamtaroException("Invalid Argument Format! Usage: tag [task number] [#tag]");
        }
        try {
            return hamtaro.task.Task.normalizeTag(arguments[1]);
        } catch (IllegalArgumentException e) {
            throw new HamtaroException(e.getMessage());
        }
    }

    private Todo createTodo(String[] parts) throws HamtaroException {
        if (parts.length == 1 || parts[1].isBlank()) {
            throw new HamtaroException("Invalid Argument Format! Usage: todo [task description]");
        }
        return new Todo(parts[1]);
    }

    private Deadline createDeadline(String[] parts) throws HamtaroException {
        if (parts.length == 1) {
            throw new HamtaroException("Invalid Argument Format! Usage: deadline [task description] /by [deadline]");
        }
        String[] details = parts[1].split(" /by ", -1);
        if (details.length != 2 || details[0].isBlank() || details[1].isBlank()) {
            throw new HamtaroException("Invalid Argument Format! Usage: deadline [task description] /by [deadline]");
        }
        // The format check above guarantees exactly one non-blank description/date pair.
        assert details.length == 2 && !details[0].isBlank() && !details[1].isBlank();
        try {
            return new Deadline(details[0], Utils.parseDate(details[1].trim()));
        } catch (IllegalArgumentException e) {
            throw new HamtaroException(e.getMessage());
        }
    }

    private Event createEvent(String[] parts) throws HamtaroException {
        if (parts.length == 1) {
            throw new HamtaroException("Invalid Argument Format! Usage: event [task description] /from [start] /to [end]");
        }
        String details = parts[1];
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex < 1 || toIndex < fromIndex || details.indexOf(" /from ", fromIndex + 1) != -1
                || details.indexOf(" /to ", toIndex + 1) != -1) {
            throw new HamtaroException("Invalid Argument Format! Usage: event [task description] /from [start] /to [end]");
        }
        // These positions are valid because the structural checks above succeeded.
        assert fromIndex >= 1 && toIndex >= fromIndex;
        try {
            String description = details.substring(0, fromIndex);
            String from = details.substring(fromIndex + 7, toIndex).trim();
            String to = details.substring(toIndex + 5).trim();
            return new Event(description, Utils.parseDateTime(from), Utils.parseDateTime(to));
        } catch (IllegalArgumentException e) {
            throw new HamtaroException(e.getMessage());
        }
    }

    private Command createListCommand(String[] parts) throws HamtaroException {
        if (parts.length == 2 && parts[1].startsWith("#")) {
            try {
                return Command.findTag(hamtaro.task.Task.normalizeTag(parts[1]));
            } catch (IllegalArgumentException e) {
                throw new HamtaroException(e.getMessage());
            }
        }
        if (parts.length > 1) {
            throw new HamtaroException("Invalid Argument Length! Usage: list");
        }
        return Command.list();
    }

    /** Extracts the keyword supplied to a find command. */
    private String parseFindKeyword(String[] parts) throws HamtaroException {
        if (parts.length == 1 || parts[1].isBlank()) {
            throw new HamtaroException("Invalid Argument Format! Usage: find [keyword]");
        }
        return parts[1];
    }

    private Command createExitCommand(String[] parts) throws HamtaroException {
        if (parts.length > 1) {
            throw new HamtaroException("Invalid Argument Length! Usage: bye");
        }
        return Command.exit();
    }

}

