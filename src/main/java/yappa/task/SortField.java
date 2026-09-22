package yappa.task;

import yappa.exception.YappaException;

/** Identifies the task field used for sorting. */
public enum SortField {
    DESCRIPTION("alpha"),
    DATE_TIME("datetime");

    private final String displayName;

    private SortField(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Resolves a user-typed sort field name into a {@code SortField}.
     *
     * @param input Sort field name as typed by the user.
     * @return Matching sort field.
     * @throws YappaException If the input does not match a known sort field.
     */
    public static SortField fromInput(String input) throws YappaException {
        for (SortField field : values()) {
            if (field.displayName.equalsIgnoreCase(input.trim())) {
                return field;
            }
        }
        throw new YappaException("Invalid sort field. Use 'alpha' or 'datetime'.");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
