package bob.parser;

import bob.exception.BobException;

/**
 * Identifies a task field that can be changed by an edit command.
 */
public enum EditField {
    DESCRIPTION("description"),
    DUE_DATE("by"),
    START_DATE("from"),
    END_DATE("to");

    private static final String EDITABLE_FIELDS_MESSAGE = "Editable fields:\n"
            + "- description (all task types)\n"
            + "- by (deadline tasks only)\n"
            + "- from (event tasks only)\n"
            + "- to (event tasks only)";

    private final String commandWord;

    /**
     * Creates an edit field represented by the specified command word.
     *
     * @param commandWord Word used to select the field in an edit command.
     */
    EditField(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Returns the edit field represented by a command word.
     *
     * @param commandWord Word supplied in an edit command.
     * @return Edit field represented by the command word.
     * @throws BobException If the command word does not identify an editable field.
     */
    public static EditField fromCommandWord(String commandWord) throws BobException {
        for (EditField editField : values()) {
            if (editField.commandWord.equals(commandWord)) {
                return editField;
            }
        }
        throw new BobException(EDITABLE_FIELDS_MESSAGE);
    }

    /**
     * Returns guidance describing every editable field and its applicable task types.
     *
     * @return Guidance for selecting an editable field.
     */
    public static String getEditableFieldsMessage() {
        return EDITABLE_FIELDS_MESSAGE;
    }
}
