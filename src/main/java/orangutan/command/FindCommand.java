package orangutan.command;

import orangutan.OrangutanException;
import orangutan.chatlist.ChatList;

/**
 * Command to find items in list whose descriptions contain a specific string.
 */
class FindCommand implements Command {
    private static final String NAME = "find";
    private final String query;

    /**
     * Creates new command.
     *
     * @param query String to be searched for.
     */
    FindCommand(String query) {
        this.query = query;
    }

    /**
     * Lists items that match the given keyword.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Reply message, plus list of all matching items.
     *      If no items could be found, will return an appropriate reply with no list.
     */
    public String run(Context context) throws OrangutanException {
        assert context != null : "FindCommand says: context should not be null!!!";
        assert context.getList() != null : "FindCommand says: list should not be null!!!";
      
        ErrorChecker.checkEmptyList(context, NAME);

        ChatList res = context.getList().findItem(query);
        if (res.getLength() == 0) {
            return ("The item you requested could not be found. Perchance it is not in our list?");
        } else {
            return ("herein lie the items thou seekest:\n" + res);
        }
    }
}
