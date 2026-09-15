package luke.tasks;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

/**
 * Built-in task types and the flags required to create each one.
 */
public enum TaskTypes {
    /** A simple task with no required flags. */
    TODO("todo") {
        @Override
        public char getChar() {
            return 'T';
        }
    },

    /** A task that happens over a time range and requires {@code /from} and {@code /to}. */
    EVENT("event", Flag.FROM, Flag.TO) {
        @Override
        public char getChar() {
            return 'E';
        }
    },

    /** A task with a due time and required {@code /by} flag. */
    DEADLINE("deadline", Flag.BY) {
        @Override
        public char getChar() {
            return 'D';
        }
    };

    private final String keyword;
    private final EnumSet<Flag> flags;

    TaskTypes(String keyword, Flag... flags) {
        assert keyword != null && !keyword.isBlank() : "Task type keyword must be present.";
        assert flags != null : "Task type flags must be provided.";
        this.keyword = keyword;
        this.flags = EnumSet.noneOf(Flag.class);
        Collections.addAll(this.flags, flags);
        assert this.flags.size() == flags.length : "Task type flags should not contain duplicates.";
    }

    /**
     * Returns the single-character label used when displaying this task type.
     *
     * @return display character such as {@code T}, {@code D}, or {@code E}
     */
    public abstract char getChar();

    /**
     * Returns the flags required when creating this task type.
     *
     * @return required flags; empty for task types with no required flags
     */
    public EnumSet<Flag> getFlags() {
        return this.flags;
    }

    /**
     * Returns the task type whose keyword matches the given word.
     *
     * @param word the first word typed by the user
     * @return the matching task type, or {@code null} if none matches
     */
    public static TaskTypes findByKeyword(String word) {
        return Arrays.stream(values())
                .filter(taskType -> word.equalsIgnoreCase(taskType.keyword))
                .findFirst()
                .orElse(null);
    }
}
