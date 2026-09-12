package chin.util;

import chin.task.Deadline;
import chin.task.Event;
import chin.task.Task;
import chin.task.Todo;

/**
 * Parses raw user input strings into concrete {@link Task} instances and
 * validates command indices. Throws {@link ChinException} with a friendly
 * message when input is malformed.
 */
public class Parser {
    /** Builds a new task from the raw input for a {@code todo/deadline/event} command. */
    public static Task parseNewTask(Command cmd, String input) throws ChinException {
        assert cmd != null : "command must not be null";
        assert input != null : "input must not be null";
        switch (cmd) {
        case TODO: {
            String desc = input.length() > 4 ? input.substring(5).trim() : "";
            if (desc.isEmpty()) {
                throw new ChinException("OOPS!!! The description of a todo cannot be empty.");
            }
            return new Todo(desc);
        }
        case DEADLINE: {
            String body = input.length() > 8 ? input.substring(9) : "";
            int byIdx = body.indexOf(" /by ");
            if (byIdx < 0 || body.substring(0, byIdx).trim().isEmpty()
                    || body.substring(byIdx + 5).trim().isEmpty()) {
                throw new ChinException("OOPS!!! A deadline needs a description and ' /by <when>'.");
            }
            return new Deadline(body.substring(0, byIdx).trim(), body.substring(byIdx + 5).trim());
        }
        case EVENT: {
            String body = input.length() > 5 ? input.substring(6) : "";
            int fromIdx = body.indexOf(" /from ");
            int toIdx = body.indexOf(" /to ");
            if (fromIdx < 0 || toIdx < 0 || toIdx < fromIdx
                    || body.substring(0, fromIdx).trim().isEmpty()
                    || body.substring(fromIdx + 7, toIdx).trim().isEmpty()
                    || body.substring(toIdx + 5).trim().isEmpty()) {
                throw new ChinException("OOPS!!! An event needs a description, ' /from <start>' and ' /to <end>'.");
            }
            return new Event(body.substring(0, fromIdx).trim(),
                    body.substring(fromIdx + 7, toIdx).trim(),
                    body.substring(toIdx + 5).trim());
        }
        default:
            throw new ChinException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Parses a 1-based index string and returns the zero-based index if in range. */
    public static int parseIndex(String indexInput, int size) throws ChinException {
        assert indexInput != null : "index input must not be null";
        assert size >= 0 : "task list size must be non-negative";
        try {
            int idx = Integer.parseInt(indexInput.trim()) - 1;
            if (idx < 0 || idx >= size) {
                throw new ChinException("OOPS!!! That task number is out of range.");
            }
            assert idx >= 0 && idx < size : "post-condition: idx must be in range";
            return idx;
        } catch (NumberFormatException e) {
            throw new ChinException("OOPS!!! Task number must be an integer.");
        }
    }
}
