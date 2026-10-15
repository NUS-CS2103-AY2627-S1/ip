package dude.task;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /**
     * Text describing the work to be completed.
     */
    private final String description;

    /**
     * Whether this task has been completed.
     */
    private boolean isDone;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        this.description = Objects.requireNonNull(description, "description");
        this.isDone = false;
    }

    /**
     * Returns whether this task occurs on a date.
     *
     * @param targetDate Date to compare with.
     * @return False for tasks without a date.
     */
    public boolean occursOn(LocalDate targetDate) {
        return false;
    }

    /**
     * Splits input into the text before and after a standalone marker.
     *
     * @param input  Input text to split.
     * @param marker Delimiter separating the two text parts.
     * @return Two trimmed text parts, or null when the marker or either part is invalid.
     */
    protected static String[] splitAt(String input, String marker) {
        Matcher matcher = Pattern.compile("^(.*?)\\s+" + Pattern.quote(marker) + "\\s+(.+)$")
                .matcher(input.trim());
        if (!matcher.matches()) {
            return null;
        }

        String before = matcher.group(1).trim();
        String after = matcher.group(2).trim();
        return before.isEmpty() || after.isEmpty() ? null : new String[] {before, after};
    }

    /**
     * Returns whether the input contains the supplied standalone token.
     *
     * @param input Input text to inspect.
     * @param token Token to find.
     * @return True when the token appears as a standalone whitespace-delimited token.
     */
    protected static boolean containsStandaloneToken(String input, String token) {
        return Pattern.compile("(^|\\s)" + Pattern.quote(token) + "(\\s|$)")
                .matcher(input)
                .find();
    }

    /**
     * Counts occurrences of a standalone delimiter token.
     *
     * @param input Input text to inspect.
     * @param token Delimiter token.
     * @return Number of standalone occurrences.
     */
    protected static int countStandaloneTokens(String input, String token) {
        Matcher matcher = Pattern.compile("(?<!\\S)" + Pattern.quote(token) + "(?!\\S)")
                .matcher(input);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Returns the completion marker used when displaying this task.
     *
     * @return X for a completed task, or a blank space otherwise.
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description of this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return True when this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the formatted task representation.
     *
     * @return Task status marker followed by the description.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), description);
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }
}
