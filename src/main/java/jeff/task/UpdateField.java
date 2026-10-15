package jeff.task;

/**
 * Identifies a task detail that can be updated.
 */
public enum UpdateField {
    /**
     * Task description.
     */
    DESCRIPTION("/description"),

    /**
     * Deadline due date.
     */
    DUE_DATE("/by"),

    /**
     * Event starting time.
     */
    START_TIME("/from"),

    /**
     * Event ending time.
     */
    END_TIME("/to");

    private final String commandFlag;

    UpdateField(String commandFlag) {
        this.commandFlag = commandFlag;
    }

    /**
     * Returns the update field represented by the specified command flag.
     *
     * @param commandFlag Command flag to convert.
     * @return Corresponding update field.
     * @throws IllegalArgumentException If the command flag is unsupported.
     */
    public static UpdateField fromCommandFlag(String commandFlag) {
        for (UpdateField field : values()) {
            if (field.commandFlag.equals(commandFlag)) {
                return field;
            }
        }

        throw new IllegalArgumentException(
                "Valid update fields are: /description, /by, /from, /to.");
    }

    /**
     * Returns whether the specified text is a supported command flag.
     *
     * @param text Text to check.
     * @return True if the text is a supported command flag.
     */
    public static boolean isCommandFlag(String text) {
        for (UpdateField field : values()) {
            if (field.commandFlag.equals(text)) {
                return true;
            }
        }

        return false;
    }
}
