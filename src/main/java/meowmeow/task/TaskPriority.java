package meowmeow.task;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * How urgently a {@link Task} needs attention. {@code NONE} means the user
 * has not set a priority, and is the default for every newly created task -
 * it renders no suffix and is never written to the save file. Each other
 * constant carries a label used both when rendering a task (e.g.
 * "(priority: HIGH)") and when saving it, plus a one-letter shorthand a user
 * may type instead of the full word.
 */
public enum TaskPriority {
    /** Needs attention first. */
    HIGH("HIGH", "h"),
    /** Neither urgent nor low-stakes. */
    MEDIUM("MEDIUM", "m"),
    /** Can wait behind everything else. */
    LOW("LOW", "l"),
    /** No priority has been set. */
    NONE("NONE", "n");

    private final String label;
    private final String shortForm;

    TaskPriority(String label, String shortForm) {
        this.label = label;
        this.shortForm = shortForm;
    }

    /** Returns whether this priority has actually been set, i.e. is not {@link #NONE}. */
    public boolean isSet() {
        return this != NONE;
    }

    /**
     * Returns the label shown in a task's rendering and written to the save
     * file, e.g. {@code "HIGH"}.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the priority whose {@link #getLabel() label} or one-letter
     * shorthand matches {@code token}, ignoring case, or {@code null} if none
     * does. Returning {@code null} rather than throwing lets callers such as
     * {@link meowmeow.parser.Parser Parser} and
     * {@link meowmeow.storage.Storage Storage} each decide how to react to an
     * unrecognised token, the same way {@link TaskStatus#fromFileFlag} and
     * {@link TaskType#fromTag} do for their own fields.
     *
     * @param token the text a user typed, or a field read from a save-file line.
     * @return the matching priority, or {@code null} if none matches.
     */
    public static TaskPriority fromInput(String token) {
        for (TaskPriority priority : values()) {
            if (priority.label.equalsIgnoreCase(token) || priority.shortForm.equalsIgnoreCase(token)) {
                return priority;
            }
        }
        return null;
    }

    /**
     * Returns every accepted priority token, in the form
     * {@code "high/h, medium/m, low/l, none/n"}, for use in usage-error
     * hints. Built from {@link #values()} so a hint can never drift out of
     * step with the levels this enum actually accepts.
     */
    public static String acceptedLevels() {
        return Stream.of(values())
                .map(priority -> priority.label.toLowerCase() + "/" + priority.shortForm)
                .collect(Collectors.joining(", "));
    }
}
