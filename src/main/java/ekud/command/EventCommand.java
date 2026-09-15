package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.Event;
import ekud.task.Task;
import ekud.task.TaskDateTime;

/**
 * Adds an {@link Event} to the task list.
 */
public class EventCommand implements TaskCreatingCommand {
    private static final String FLAG_FROM = "/from";
    private static final String FLAG_TO = "/to";
    private static final String EVENT_USAGE =
            "e.g. event meeting /from 2019-12-02 1400 /to 2019-12-02 1600.";

    private final String description;
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates a command that will add an event.
     *
     * @param description text of the event to add
     * @param from        start date or date-time
     * @param to          end date or date-time
     */
    private EventCommand(String description, TaskDateTime from, TaskDateTime to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Parses {@code event DESCRIPTION /from START /to END}.
     * {@code START} and {@code END} must be supported dates or date-times,
     * for example {@code 2019-12-02 1400}.
     *
     * @param arguments text after the command word
     * @return an event command
     * @throws EkudException if the description, flags, or date/times are missing or invalid
     */
    public static EventCommand parse(String arguments) throws EkudException {
        if (arguments.isBlank()) {
            throw new EkudException("The description of an event cannot be empty.");
        }

        int fromIndex = Parser.indexOfFlag(arguments, FLAG_FROM);
        int toIndex = Parser.indexOfFlag(arguments, FLAG_TO);
        requireFlagsInOrder(fromIndex, toIndex);

        String description = arguments.substring(0, fromIndex).trim();
        String fromText = arguments.substring(fromIndex + FLAG_FROM.length(), toIndex).trim();
        String toText = arguments.substring(toIndex + FLAG_TO.length()).trim();
        requireEventPartsPresent(description, fromText, toText);

        TaskDateTime from = TaskDateTime.parse(fromText);
        TaskDateTime to = TaskDateTime.parse(toText);
        if (isEndBeforeStart(from, to)) {
            throw new EkudException("The event end date/time cannot be before the start date/time.");
        }
        return new EventCommand(description, from, to);
    }

    /**
     * Rejects missing flags or {@code /to} appearing before {@code /from}.
     */
    private static void requireFlagsInOrder(int fromIndex, int toIndex) throws EkudException {
        if (fromIndex < 0 || toIndex < 0) {
            throw new EkudException("Please provide both /from and /to, " + EVENT_USAGE);
        }
        if (fromIndex > toIndex) {
            throw new EkudException("Please put /from before /to, " + EVENT_USAGE);
        }
    }

    /**
     * Rejects a blank description, start, or end after the flags have been split out.
     */
    private static void requireEventPartsPresent(String description, String fromText, String toText)
            throws EkudException {
        if (description.isEmpty()) {
            throw new EkudException("The description of an event cannot be empty.");
        }
        if (fromText.isEmpty()) {
            throw new EkudException("Please provide a start date/time after /from.");
        }
        if (toText.isEmpty()) {
            throw new EkudException("Please provide an end date/time after /to.");
        }
    }

    /**
     * Returns whether {@code to} is earlier than {@code from}.
     * Date-only values are compared by calendar date; times are compared only
     * when both sides include a time on the same day.
     */
    private static boolean isEndBeforeStart(TaskDateTime from, TaskDateTime to) {
        if (from.toLocalDate().isAfter(to.toLocalDate())) {
            return true;
        }
        if (!from.toLocalDate().equals(to.toLocalDate())) {
            return false;
        }
        if (!from.hasTime() || !to.hasTime()) {
            return false;
        }
        return from.toLocalTime().isAfter(to.toLocalTime());
    }

    /**
     * Builds the event without adding it to the list.
     * {@link Storage} uses this when loading a saved {@code event} line.
     *
     * @return a new incomplete event
     */
    @Override
    public Task createTask() {
        return new Event(description, from, to);
    }
}
