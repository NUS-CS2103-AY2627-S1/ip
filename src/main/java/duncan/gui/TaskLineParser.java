package duncan.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits one line of Duncan's output into pieces that the GUI colours
 * differently, without touching what the console prints.
 *
 * <p>A task line such as {@code 2.[D][X] return book (by: Jun 6 2026)} is
 * recognised by the shape that {@code Task.toString()} gives it. The
 * bracketed type and status are replaced by a bare letter and a check mark
 * or circle, each padded to the three columns the brackets took, so the rest
 * of the line stays aligned. Any other line comes back as a single plain
 * piece. This class knows nothing about JavaFX, so it can be tested directly.
 */
public final class TaskLineParser {
    /** What a piece of a line is, and so how the GUI should colour it. */
    public enum Kind {
        PLAIN, TODO, DEADLINE, EVENT, DONE, NOT_DONE, DATE
    }

    /**
     * One piece of a line.
     *
     * @param text What to show for this piece.
     * @param kind What the piece is.
     */
    public record Segment(String text, Kind kind) {
    }

    private static final String DATE_PATTERN = "[A-Z][a-z]{2} \\d{1,2} \\d{4}";

    /**
     * A task line: an optional list number or "from:"/"to:" label, then the
     * type and status brackets, then the description and any dates.
     */
    private static final Pattern TASK_LINE = Pattern.compile(
            "^(\\s*(?:\\d+\\.|from:|to:)?\\s*)\\[([TDE])\\]\\[([X ])\\] (.*)$");

    /** The dates a deadline or event appends after its description, in parentheses. */
    private static final Pattern DATE_SUFFIX = Pattern.compile(
            "^(.*) (\\((?:by: " + DATE_PATTERN + "|from: " + DATE_PATTERN + " to: " + DATE_PATTERN + ")\\))$");

    private static final int GROUP_PREFIX = 1;
    private static final int GROUP_TYPE = 2;
    private static final int GROUP_STATUS = 3;
    private static final int GROUP_REST = 4;

    private static final int DATE_SUFFIX_GROUP_DESCRIPTION = 1;
    private static final int DATE_SUFFIX_GROUP_DATES = 2;

    private static final String STATUS_DONE = "X";
    private static final String DISPLAY_DONE = " ✓ ";
    private static final String DISPLAY_NOT_DONE = " ○ ";

    private TaskLineParser() {
    }

    /**
     * Splits a line of output into pieces to be coloured separately.
     *
     * @param line One line of Duncan's output, without a line break.
     * @return The pieces in order; a single plain piece if the line is not a task line.
     */
    public static List<Segment> parse(String line) {
        Matcher taskLine = TASK_LINE.matcher(line);
        if (!taskLine.matches()) {
            return List.of(new Segment(line, Kind.PLAIN));
        }

        String typeLetter = taskLine.group(GROUP_TYPE);
        Kind typeKind = toTypeKind(typeLetter);
        List<Segment> segments = new ArrayList<>();
        addIfNotEmpty(segments, taskLine.group(GROUP_PREFIX), Kind.PLAIN);
        segments.add(new Segment(" " + typeLetter + " ", typeKind));
        segments.add(toStatusSegment(taskLine.group(GROUP_STATUS)));
        if (typeKind == Kind.TODO) {
            // A todo has no dates, so anything that looks like some is part of its description.
            segments.add(new Segment(" " + taskLine.group(GROUP_REST), Kind.PLAIN));
        } else {
            addDescriptionAndDates(segments, taskLine.group(GROUP_REST));
        }
        return segments;
    }

    private static Kind toTypeKind(String typeLetter) {
        switch (typeLetter) {
            case "T":
                return Kind.TODO;
            case "D":
                return Kind.DEADLINE;
            case "E":
                return Kind.EVENT;
            default:
                throw new IllegalArgumentException("Unknown task type: " + typeLetter);
        }
    }

    private static Segment toStatusSegment(String status) {
        if (status.equals(STATUS_DONE)) {
            return new Segment(DISPLAY_DONE, Kind.DONE);
        }
        return new Segment(DISPLAY_NOT_DONE, Kind.NOT_DONE);
    }

    /** Adds the text after the status of a deadline or event, setting off its dates as their own piece. */
    private static void addDescriptionAndDates(List<Segment> segments, String rest) {
        Matcher dateSuffix = DATE_SUFFIX.matcher(rest);
        if (!dateSuffix.matches()) {
            segments.add(new Segment(" " + rest, Kind.PLAIN));
            return;
        }
        segments.add(new Segment(" " + dateSuffix.group(DATE_SUFFIX_GROUP_DESCRIPTION) + " ", Kind.PLAIN));
        segments.add(new Segment(dateSuffix.group(DATE_SUFFIX_GROUP_DATES), Kind.DATE));
    }

    private static void addIfNotEmpty(List<Segment> segments, String text, Kind kind) {
        if (!text.isEmpty()) {
            segments.add(new Segment(text, kind));
        }
    }
}
