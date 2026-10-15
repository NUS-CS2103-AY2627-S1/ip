package benbot;

import java.time.LocalDateTime;
import java.util.Arrays;

/** A task that has a starting time and an ending time. */
public class Event extends Task {
    /** The event's start date and optional time. */
    private final LocalDateTime startDateTime;

    /** Whether the start date included a time. */
    private final boolean startIncludesTime;

    /** The event's end date and optional time. */
    private final LocalDateTime endDateTime;

    /** Whether the end date included a time. */
    private final boolean endIncludesTime;

    /**
     * Creates an event from words in the form
     * {@code event DESCRIPTION /from START /to END}. Dates are stored as date-time values.
     *
     * @param words the variable-length sequence of words entered in the command.
     * @throws InvalidCommandException if either date text is invalid or the end is not after the start.
     */
    public Event(String... words) throws InvalidCommandException {
        super(getDescription(words), TaskType.EVENT);
        DateTimeParser.ParsedDateTime parsedStart = DateTimeParser.parse(getStartDateText(words));
        DateTimeParser.ParsedDateTime parsedEnd = DateTimeParser.parse(getEndDateText(words));
        startDateTime = parsedStart.value();
        startIncludesTime = parsedStart.includesTime();
        endDateTime = parsedEnd.value();
        endIncludesTime = parsedEnd.includesTime();
        if (!endDateTime.isAfter(startDateTime)) {
            throw new InvalidCommandException("An event's end must be after its start.");
        }
    }

    /** Returns the event description before the {@code /from} marker. */
    private static String getDescription(String[] words) {
        int fromIndex = CommandWords.findMarker(words, "/from");
        return String.join(" ", Arrays.copyOfRange(words, 1, fromIndex));
    }

    /** Returns the start-date text between the {@code /from} and {@code /to} markers. */
    private static String getStartDateText(String[] words) {
        int fromIndex = CommandWords.findMarker(words, "/from");
        int toIndex = CommandWords.findMarker(words, "/to");
        return String.join(" ", Arrays.copyOfRange(words, fromIndex + 1, toIndex));
    }

    /** Returns the end-date text after the {@code /to} marker. */
    private static String getEndDateText(String[] words) {
        int toIndex = CommandWords.findMarker(words, "/to");
        return String.join(" ", Arrays.copyOfRange(words, toIndex + 1, words.length));
    }

    /** Returns the event in the chatbot's display format. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.format(startDateTime, startIncludesTime)
                + " to: " + DateTimeParser.format(endDateTime, endIncludesTime) + ")";
    }

    /** Returns the command used to recreate this event from stored data. */
    @Override
    public String toStorageString() {
        return "event " + getDescription() + " /from "
                + DateTimeParser.formatForStorage(startDateTime, startIncludesTime) + " /to "
                + DateTimeParser.formatForStorage(endDateTime, endIncludesTime);
    }
}
