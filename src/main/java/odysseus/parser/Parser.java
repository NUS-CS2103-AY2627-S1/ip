package odysseus.parser;

import odysseus.OdysseusException;
import odysseus.task.Deadline;
import odysseus.task.Event;
import odysseus.task.Todo;

/** Handles parsing of input provided by user. */
public class Parser {

    private Parser() {
    }

    /**
     * Validates and converts a user-entered task number into a 0-based array index.
     *
     * @param parts the whitespace-split user input; index 1 holds the number
     * @param count the current number of tasks (upper bound for the number)
     * @return the 0-based index into the task array
     * @throws OdysseusException if the number is missing, non-numeric, or out of range
     */
    public static int parseIndex(String[] parts, int count) throws OdysseusException {
        if (parts.length < 2) {
            throw new OdysseusException("Which one? Give me a number.");
        }
        int n;
        try {
            n = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new OdysseusException("That's not a number.");
        }
        if (n < 1 || n > count) {
            throw new OdysseusException("There's no task " + n + ".");
        }
        return n - 1;
    }

    /**
     * Builds a todo from the argument text.
     *
     * @param rest description of Todo
     * @return todo task
     * @throws OdysseusException if description is empty
     */
    public static Todo parseTodo(String rest) throws OdysseusException {
        if (rest.isEmpty()) {
            throw new OdysseusException("It needs a name.");
        }
        return new Todo(rest);
    }

    /**
     * Builds a deadline from argument text.
     *
     * @param rest string with description and deadline
     * @return deadline task
     * @throws OdysseusException if description or /by is empty
     */
    public static Deadline parseDeadline(String rest) throws OdysseusException {
        String[] parts = rest.split(" /by ");
        if (parts.length < 2) {
            throw new OdysseusException("A deadline needs a /by time.");
        }
        if (parts[0].isEmpty()) {
            throw new OdysseusException("It needs a name.");
        }
        if (parts[1].isEmpty()) {
            throw new OdysseusException("The time can't be empty.");
        }
        return new Deadline(parts[0], parts[1]);
    }

    /**
     * Builds an event from argument text.
     *
     * @param rest string with description, /from and /to
     * @return event task
     * @throws OdysseusException if description, /from or /to is empty
     */
    public static Event parseEvent(String rest) throws OdysseusException {
        String[] fromParts = rest.split(" /from ");
        if (fromParts.length < 2) {
            throw new OdysseusException("An event needs a /from. When does it start?");
        }
        String[] toParts = fromParts[1].split(" /to ");
        if (fromParts[0].isEmpty()) {
            throw new OdysseusException("It needs a name.");
        }

        if (toParts.length < 2) {
            throw new OdysseusException("An event needs a /to. When does it end?");
        }

        if (toParts[0].isEmpty()) {
            throw new OdysseusException("The start can't be empty.");
        }
        if (toParts[1].isEmpty()) {
            throw new OdysseusException("The end can't be empty.");
        }
        return new Event(fromParts[0], toParts[0], toParts[1]);
    }
}