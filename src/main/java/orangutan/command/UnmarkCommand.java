package orangutan.command;

import orangutan.OrangutanException;

/**
 * Command to unmark an item on the list as to be completed.
 */
class UnmarkCommand implements Command {
    private static final String NAME = "unmark";
    private final String index;

    /**
     * Creates a new command.
     *
     * @param index Index of the item to be unmarked.
     */
    UnmarkCommand(String index) {
        this.index = index;
    }

    /**
     * Unmarks item at the given index from the list.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Reply message, plus a printout of the unmarked item.
     *      If index is not a number, will instead return an alert message.
     * @throws OrangutanException If list is empty, or if index is out of range.
     */
    public String run(Context context) throws OrangutanException {
        assert context != null : "UnmarkCommand says: context should not be null!!!";
        assert context.getList() != null : "UnmarkCommand says: list should not be null!!!";

        ErrorChecker.checkEmptyList(context, NAME);

        try {
            int unmarkIndex = Integer.parseInt(index);
            ErrorChecker.checkIndexOutOfBounds(context, unmarkIndex);

            String unmarkedItem = context.getList().unmarkItem(unmarkIndex);
            return ("Take heed, this task hath not yet been completed:\n " + unmarkedItem);
        } catch (NumberFormatException e) {
            throw CommandError.numberParseError(context.getList().getLength());
        }
    }
}
