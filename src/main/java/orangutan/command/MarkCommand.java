package orangutan.command;

import orangutan.OrangutanException;

/**
 * Command to mark an item as completed.
 */
class MarkCommand implements Command {
    private static final String NAME = "mark";
    private final String index;

    /**
     * Creates a new command.
     *
     * @param index Index of the item to be marked.
     */
    MarkCommand(String index) {
        this.index = index;
    }

    /**
     * Marks item at the given index of the list.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Reply message, plus a printout of the marked item.
     *      If index is not a number, will instead return an alert message.
     * @throws OrangutanException If list is empty, or if index is out of range.
     */
    public String run(Context context) throws OrangutanException {
        assert context != null : "MarkCommand says: context should not be null!!!";
        assert context.getList() != null : "MarkCommand says: list should not be null!!!";

        ErrorChecker.checkEmptyList(context, NAME);

        try {
            int markIndex = Integer.parseInt(index);
            ErrorChecker.checkIndexOutOfBounds(context, markIndex);

            String markItem = context.getList().markItem(markIndex);
            return ("My compliments, thou hast completed a task:\n " + markItem);
        } catch (NumberFormatException e) {
            throw CommandError.numberParseError(context.getList().getLength());
        }
    }
}
