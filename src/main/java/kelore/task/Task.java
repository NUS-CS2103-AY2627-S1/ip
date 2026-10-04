package kelore.task;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import kelore.storage.Storage;

/** Represents a task and whether it has been completed. */
public class Task {
    private static final int SHORT_KEYWORD_MAX_LENGTH = 2;
    private static final int MEDIUM_KEYWORD_MAX_LENGTH = 7;
    private static final int MEDIUM_KEYWORD_ALLOWED_DISTANCE = 1;
    private static final int LONG_KEYWORD_ALLOWED_DISTANCE = 2;

    /** Description of this task. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "A task must have a description";
        assert !description.isBlank() : "A task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing whether this task is complete.
     *
     * @return {@code X} if complete; a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the completion status used by the data file.
     *
     * @return {@code 1} if complete; {@code 0} otherwise.
     */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task in the text format used by the data file.
     *
     * @return Serialized task record.
     */
    public String toStorageString() {
        return Storage.joinFields("T", getStorageStatus(), description);
    }

    /**
     * Returns whether this task occurs on the specified calendar date.
     *
     * @param date Calendar date to check.
     * @return False because a basic task has no associated date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the given keyword.
     *
     * @param keyword Keyword to find.
     * @return True if the description contains the keyword; false otherwise.
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns whether any word in this task's description is a close match for the keyword.
     * A small length-based edit-distance limit avoids overly broad matches for short keywords.
     *
     * @param keyword Keyword to compare with the description's words.
     * @return True if a word is sufficiently close to the keyword; false otherwise.
     */
    public boolean containsCloseKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        int allowedDistance = getAllowedDistance(normalizedKeyword.length());
        return Arrays.stream(description.toLowerCase(Locale.ENGLISH).split("\\s+"))
                .anyMatch(word ->
                        levenshteinDistance(word, normalizedKeyword) <= allowedDistance);
    }

    /**
     * Returns whether another task has the same type and user-provided details.
     * Completion status and insignificant description whitespace are ignored.
     *
     * @param other Task to compare.
     * @return True if both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(description).equals(normalizeDescription(other.description));
    }

    private String normalizeDescription(String value) {
        return value.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ENGLISH);
    }

    private int getAllowedDistance(int keywordLength) {
        if (keywordLength <= SHORT_KEYWORD_MAX_LENGTH) {
            return 0;
        }
        if (keywordLength <= MEDIUM_KEYWORD_MAX_LENGTH) {
            return MEDIUM_KEYWORD_ALLOWED_DISTANCE;
        }
        return LONG_KEYWORD_ALLOWED_DISTANCE;
    }

    /** Calculates the minimum number of single-character edits between two strings. */
    private int levenshteinDistance(String first, String second) {
        int[] previousRow = new int[second.length() + 1];
        for (int secondIndex = 0; secondIndex <= second.length(); secondIndex++) {
            previousRow[secondIndex] = secondIndex;
        }

        for (int firstIndex = 1; firstIndex <= first.length(); firstIndex++) {
            int[] currentRow = new int[second.length() + 1];
            currentRow[0] = firstIndex;
            for (int secondIndex = 1; secondIndex <= second.length(); secondIndex++) {
                int substitutionCost = first.charAt(firstIndex - 1)
                        == second.charAt(secondIndex - 1) ? 0 : 1;
                currentRow[secondIndex] = Math.min(
                        Math.min(currentRow[secondIndex - 1] + 1,
                                previousRow[secondIndex] + 1),
                        previousRow[secondIndex - 1] + substitutionCost);
            }
            previousRow = currentRow;
        }
        return previousRow[second.length()];
    }

    /** Returns an independent copy of this task. */
    Task copy() {
        Task copiedTask = new Task(description);
        if (isDone) {
            copiedTask.markAsDone();
        }
        return copiedTask;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns a displayable description of this task and its completion status. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
