package benbot;

import java.time.LocalDateTime;
import java.util.Arrays;

/** A task that must be completed by a specified date or time. */
public class Deadline extends Task {
    /** The deadline's date and optional time. */
    private final LocalDateTime deadlineDateTime;

    /** Whether the command supplied a time as well as a date. */
    private final boolean deadlineIncludesTime;

    /**
     * Creates a deadline from words in the form {@code deadline DESCRIPTION /by DATE}.
     * Dates are converted to {@link LocalDateTime} values so they can be validated and compared.
     *
     * @param words the variable-length sequence of words entered in the command.
     * @throws InvalidCommandException if the date text is invalid.
     */
    public Deadline(String... words) throws InvalidCommandException {
        super(getDescription(words), TaskType.DEADLINE);
        DateTimeParser.ParsedDateTime parsedDeadline =
                DateTimeParser.parse(getDeadlineDateText(words));
        deadlineDateTime = parsedDeadline.value();
        deadlineIncludesTime = parsedDeadline.includesTime();
    }

    /** Returns the task description before the {@code /by} marker. */
    private static String getDescription(String[] words) {
        int byIndex = CommandWords.findMarker(words, "/by");
        return String.join(" ", Arrays.copyOfRange(words, 1, byIndex));
    }

    /** Returns the deadline date text after the {@code /by} marker. */
    private static String getDeadlineDateText(String[] words) {
        int byIndex = CommandWords.findMarker(words, "/by");
        return String.join(" ", Arrays.copyOfRange(words, byIndex + 1, words.length));
    }

    /** Returns the deadline in the chatbot's display format. */
    @Override
    public String toString() {
        return super.toString() + " (by: "
                + DateTimeParser.format(deadlineDateTime, deadlineIncludesTime) + ")";
    }

    /** Returns the command used to recreate this deadline from stored data. */
    @Override
    public String toStorageString() {
        return "deadline " + getDescription() + " /by "
                + DateTimeParser.formatForStorage(deadlineDateTime, deadlineIncludesTime);
    }
}
