package myriad.task;

import java.util.Optional;

/**
 * Represents the kinds of {@link Task} there are, each with the one-letter
 * code that marks it at the start of a save-format line. An enum rather than
 * a {@code String} constant per subclass, so a type can only ever be one of
 * these three, and a switch over it can be checked by the compiler for a
 * missing case.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    /** The code written as the first field of a save-format line. */
    private final String code;

    TaskType(String code) {
        this.code = code;
    }

    /**
     * Returns the code that marks this type in the data file, e.g. {@code "D"}.
     *
     * @return this type's one-letter code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the type whose code is exactly the given text, matched case
     * sensitively, or an empty {@link Optional} if no type has that code.
     * Empty rather than an exception, so the caller decides how to report an
     * unknown code in its own words.
     *
     * @param code the code to look up, e.g. the first field of a saved line.
     * @return the matching type, or an empty {@code Optional} if there is none.
     */
    public static Optional<TaskType> fromCode(String code) {
        for (TaskType type : values()) {
            if (type.code.equals(code)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
