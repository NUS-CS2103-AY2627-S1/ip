package sophon.model;

import static java.lang.Math.min;

import java.util.Locale;

/**
 * Represents a task tracked by Sophon.
 */
public class Task {
    /** Details of the task shown to the user and saved to disk. */
    protected String description;

    /** Whether the task has been marked as completed. */
    protected boolean isDone;

    /** Category of task, used to choose the display icon. */
    private final TaskType type;

    /**
     * Creates a task with the given description.
     *
     * @param description details of the task.
     * @param type type of task being tracked.
     */
    public Task(String description, TaskType type) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        assert type != null : "Task type must not be null";

        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the icon shown for this task's completion status.
     *
     * @return X if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Checks whether this task's description matches the given keyword.
     *
     * <p>A match is case-insensitive and may be either a substring match or a
     * fuzzy word match within the permitted edit distance.</p>
     *
     * @param keyword keyword to search for.
     * @return true if the description matches the keyword.
     */
    public boolean containsKeyword(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        if (normalizedDescription.contains(normalizedKeyword)) {
            return true;
        }

        String[] parts = normalizedDescription.split("\\s+");
        for (String word : parts) {
            int editDistance = calculateEditDistance(normalizedKeyword, word);
            if (editDistance <= getMaximumDistance(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the maximum edit distance permitted for a word of the given length.
     *
     * @param word word used to determine the threshold.
     * @return maximum permitted edit distance.
     */
    private int getMaximumDistance(String word) {
        // Require closer matches for short words to reduce false positives.
        int len = word.length();
        if (len <= 2) {
            return 0;
        } else if (len <= 5) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     *
     * @param first first string to compare.
     * @param second second string to compare.
     * @return minimum number of insertions, deletions, and substitutions required.
     */
    private static int calculateEditDistance(String first, String second) {
        int[][] dist = new int[first.length() + 1][second.length() + 1];

        for (int i = 0; i < dist.length; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < dist[0].length; j++) {
            dist[0][j] = j;
        }

        for (int i = 1; i < dist.length; i++) {
            for (int j = 1; j < dist[0].length; j++) {
                int subCost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                int deletion = dist[i - 1][j] + 1;
                int insertion = dist[i][j - 1] + 1;
                int substitution = dist[i - 1][j - 1] + subCost;

                dist[i][j] = min(min(deletion, insertion), substitution);
            }
        }

        return dist[first.length()][second.length()];
    }

    /**
     * Returns this task in the format shown to the user.
     *
     * @return task status and description.
     */
    @Override
    public String toString() {
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task in the format used by the save file.
     *
     * @return save file representation of this task.
     */
    public String toFileString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }
}
