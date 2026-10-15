package orangutan.command;

import orangutan.OrangutanException;

/**
 * Command to delete an item from the list.
 */
class DeleteCommand implements Command {
    private static final String NAME = "delete";
    private final String index;

    /**
     * Creates a new command.
     *
     * @param index Index of the item to be deleted.
     */
    DeleteCommand(String index) {
        this.index = index;
    }

    /**
     * Deletes item at the given index from the list.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Reply message, plus a printout of the deleted item.
     *      If index is not a number, will instead return an alert message.
     * @throws OrangutanException If list is empty, or if index is out of range.
     */
    public String run(Context context) throws OrangutanException {
        assert context != null : "DeleteCommand says: context should not be null!!!";
        assert context.getList() != null : "DeleteCommand says: list should not be null!!!";

        ErrorChecker.checkEmptyList(context, NAME);

        try {
            int deleteIndex = Integer.parseInt(index);
            ErrorChecker.checkIndexOutOfBounds(context, deleteIndex);

            String deleteItem = context.getList().deleteItem(deleteIndex);
            return ("The following task hath been purged from our records:\n " + deleteItem);
        } catch (NumberFormatException e) {
            throw CommandError.numberParseError(context.getList().getLength());
        }
    }
}
