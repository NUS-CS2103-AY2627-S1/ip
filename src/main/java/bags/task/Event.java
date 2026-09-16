package bags.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import bags.exception.BagsException;

/**
 * Represents a task that spans a time range.
 */
public class Event extends Task {

    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy h:mma", Locale.ENGLISH);

    private final LocalDateTime from;
    private final String formattedFrom;
    private final LocalDateTime to;
    private final String formattedTo;

    /**
     * Creates an event task.
     *
     * @param description text describing the task
     * @param from start date-time string in {@code yyyy-MM-dd HH:mm} format
     * @param to end date-time string in {@code yyyy-MM-dd HH:mm} format
     * @throws BagsException if either date-time format is invalid or end date is before start date
     */
    public Event(String description, String from, String to) throws BagsException {
        super(description, TaskType.EVENT);
        assert description != null : "Task description must not be null";
        assert from != null : "Event start time must not be null";
        assert to != null : "Event end time must not be null";

        try {
            this.from = LocalDateTime.parse(from, INPUT_FORMATTER);
            this.to = LocalDateTime.parse(to, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BagsException(
                    "Oops! Invalid date. Use YYYY-MM-DD HH:MM in 24-hour time.");
        }

        if (this.from.isAfter(this.to)) {
            throw new BagsException("Hmm, the event can't end before it starts. Check the dates and try again.");
        }

        this.formattedFrom = this.from.format(OUTPUT_FORMATTER);
        this.formattedTo = this.to.format(OUTPUT_FORMATTER);
    }

    /**
     * Creates an event task from an add-task command string.
     *
     * @param command user input after {@code event}
     * @return the created event task
     * @throws BagsException if the description or time range is missing,
     *     or the format is invalid
     */
    public static Event createTask(String command) throws BagsException {
        assert command != null : "Command string must not be null";

        String[] words = command.split(" ");
        if (words.length < 2) {
            throw new BagsException(
                    "Oops! Missing task description. Add some details after event.");
        }

        int fromIndex = findIndex(words, "/from");
        int toIndex = findIndex(words, "/to");

        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new BagsException(
                    "Hmm, Missing /from or /to. Add both after the event name.");
        }

        String description = buildText(words, 1, fromIndex);
        if (description.isEmpty()) {
            throw new BagsException(
                    "Oops! Missing task description. Add some details after event.");
        }

        String fromString = buildText(words, fromIndex + 1, toIndex);
        String toString = buildText(words, toIndex + 1, words.length);

        if (fromString.isEmpty() || toString.isEmpty()) {
            throw new BagsException(
                    "Hmm, Missing timeframe after /from or /to. Maybe the dates slipped out of the bag?");
        }

        return new Event(description, fromString, toString);
    }

    /**
     * Finds the position of {@code /from} and {@code /to}in the command.
     *
     * @param words the referenced word list
     * @param keyword the keyword to find
     * @return the keyword index, or {@code -1} if it is absent
     */
    private static int findIndex(String[] words, String keyword) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(keyword)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Builds a string given a specific range from a referenced
     * list of words.
     *
     * @param words the referenced word list
     * @param startIndex the inclusive starting index
     * @param endIndex the exclusive ending index
     * @return the combined text
     */
    private static String buildText(String[] words, int startIndex, int endIndex) {
        StringBuilder text = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            text.append(words[i]).append(" ");
        }
        return text.toString().trim();
    }

    public LocalDateTime getFrom() {
        return this.from;
    }

    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Returns the event task in a user-readable format.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description
                + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }

    /**
     * Converts the event task into the format used for saving to storage.
     *
     * @return the event task in its stored format.
     */
    @Override
    public String parseEvent() {
        return "E | " + "[" + getStatusIcon() + "] | " + description + " | "
                + from.format(INPUT_FORMATTER) + " | " + to.format(INPUT_FORMATTER);
    }
}
