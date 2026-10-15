package orangutan.command;

/**
 * Command to print all list items.
 */
class ListCommand implements Command {
    /**
     * Prints out all list items in a numbered list.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return The printout of list items, or a message if list is empty.
     */
    public String run(Context context) {
        assert context != null : "ListCommand says: context should not be null!!!";
        assert context.getList() != null : "ListCommand says: list should not be null!!!";

        if (context.getList().getLength() == 0) {
            return ("The list contains no undertakings.");
        } else {
            return ("Herein lie the undertakings in your list:\n" + context.getList());
        }
    }
}
