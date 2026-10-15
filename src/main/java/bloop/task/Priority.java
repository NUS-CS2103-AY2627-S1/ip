package bloop.task;

import java.util.Locale;

/** Represents a task's urgency level. */
public enum Priority {
    LOW,
    MED,
    HIGH;

    /** Returns the priority represented by the supplied input, or {@code null} when it is invalid. */
    public static Priority fromInput(String input) {
        try {
            return valueOf(input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /** Returns the lowercase label used in commands, storage, and display. */
    public String getLabel() {
        return name().toLowerCase(Locale.ROOT);
    }
}
