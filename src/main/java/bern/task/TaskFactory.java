package bern.task;

import java.text.ParseException;
import java.time.format.DateTimeParseException;

import bern.datetime.DateTime;
import bern.datetime.DateTimeFactory;
import bern.parser.ParseFactory;

/**
 * Creates task objects from command input or saved task data.
 */
public class TaskFactory extends ParseFactory {
    /**
     * Creates a task factory with no instance state.
     */
    public TaskFactory() {
    }

    /**
     * Returns a task reconstructed from a saved task record.
     * Fields contain the type symbol, completion flag ({@code 0} or {@code 1}), and name,
     * followed by a deadline for {@code D}, or start and end values for {@code E}.
     * Stored events are accepted without checking that the end follows the start.
     *
     * @param data The fields parsed from a saved task record.
     * @return The reconstructed task.
     * @throws ParseException If the task type is invalid.
     * @throws IllegalArgumentException If the record has invalid fields or structure.
     * @throws DateTimeParseException If a stored date or time cannot be parsed.
     */
    public static Task makeTaskFromData(String[] data) throws ParseException, IllegalArgumentException {
        Task task = null;
        if (data.length < 3) {
            throw new IllegalArgumentException("Data has insufficient arguments for type, completeness and name");
        }

        switch (data[0]) {
            case "T":
                if (data.length != 3) {
                    throw new IllegalArgumentException("Stored todo has incorrect number of arguments");
                }
                task = new Todo(data[2]);
                break;
            case "D":
                if (data.length != 4) {
                    throw new IllegalArgumentException("Stored deadline has incorrect number of arguments");
                }
                task = new Deadline(data[2], DateTimeFactory.makeDateFromDataString(data[3]));
                break;
            case "E":
                if (data.length != 5) {
                    throw new IllegalArgumentException("Stored event has incorrect number of arguments");
                }
                task = new Event(data[2],
                        DateTimeFactory.makeDateFromDataString(data[3]),
                        DateTimeFactory.makeDateFromDataString(data[4])
                );
                break;
            default:
                throw new ParseException("Invalid task type", -1);
        }
        assert task != null;
        if (data[1].equals("1")) {
            task.setDone(true);
        } else if (!data[1].equals("0")) {
            throw new IllegalArgumentException("Stored task has invalid completeness marking");
        }

        return task;
    }

    /**
     * Returns a todo task created from command input.
     *
     * @param inputTokens The command name followed by the todo description tokens.
     * @return The constructed todo task.
     * @throws ParseException If the task description is missing.
     */
    public static Todo makeTodo(String[] inputTokens) throws ParseException {
        String[] data = parseData(inputTokens, new String[]{});
        return new Todo(data[0]);
    }

    /**
     * Returns a deadline task created from command input.
     *
     * @param inputTokens The command name, description, {@code /by}, and deadline tokens.
     * @return The constructed deadline task.
     * @throws ParseException If the {@code /by} keyword, description, or deadline argument is missing.
     * @throws DateTimeParseException If the deadline date or time is invalid.
     */
    public static Deadline makeDeadline(String[] inputTokens) throws ParseException, DateTimeParseException {
        String[] data = parseData(inputTokens, new String[]{"/by"});
        return new Deadline(data[0], DateTimeFactory.parseDateTime(data[1]));
    }

    /**
     * Returns an event task created from command input.
     * Requires the start to sort before the end according to {@link DateTime#compareTo(DateTime)}.
     *
     * @param inputTokens The command name, description, {@code /from}, start, {@code /to}, and end tokens.
     * @return The constructed event task.
     * @throws ParseException If a required argument is missing or the boundary keywords are missing or out of order.
     * @throws DateTimeParseException If either event date or time is invalid.
     * @throws IllegalArgumentException If the event start is not before its end.
     */
    public static Event makeEvent(String[] inputTokens)
            throws ParseException, DateTimeParseException, IllegalArgumentException {
        String[] data = parseData(inputTokens, new String[]{"/from", "/to"});

        DateTime startDateTime = DateTimeFactory.parseDateTime(data[1]);
        DateTime endDateTime = DateTimeFactory.parseDateTime(data[2]);

        // Date is wrong
        if (startDateTime.compareTo(endDateTime) >= 0) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return new Event(data[0], startDateTime, endDateTime);
    }
}
