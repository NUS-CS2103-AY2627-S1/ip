package melodie.command;

/**
 * Contains the task index, field, and replacement value of an update command.
 */
public class ParsedUpdate {
    /**
     * Fields that can be changed by an update command.
     */
    enum Field {
        DESCRIPTION("/description"),
        DUE_DATE_TIME("/by"),
        START_DATE_TIME("/from"),
        END_DATE_TIME("/to");

        private final String commandToken;

        Field(String commandToken) {
            this.commandToken = commandToken;
        }

        /**
         * Returns the field represented by a command token.
         *
         * @param commandToken Token entered in an update command.
         * @return Matching update field.
         * @throws IllegalArgumentException If the token is not an update field.
         */
        static Field from(String commandToken) throws IllegalArgumentException {
            for (Field field : values()) {
                if (field.commandToken.equalsIgnoreCase(commandToken)) {
                    return field;
                }
            }
            throw new IllegalArgumentException("Unknown update field");
        }

        String getCommandToken() {
            return this.commandToken;
        }
    }

    private final int taskIndex;
    private final Field field;
    private final String value;

    /**
     * Creates parsed update data.
     *
     * @param taskIndex Zero-based index of the task to update.
     * @param field Task field to update.
     * @param value Replacement value for the field.
     */
    ParsedUpdate(int taskIndex, Field field, String value) {
        assert field != null : "Parsed update field must not be null";
        assert value != null && !value.isBlank() : "Parsed update value must not be blank";
        this.taskIndex = taskIndex;
        this.field = field;
        this.value = value;
    }

    public int getTaskIndex() {
        return this.taskIndex;
    }

    Field getField() {
        return this.field;
    }

    String getValue() {
        return this.value;
    }
}
