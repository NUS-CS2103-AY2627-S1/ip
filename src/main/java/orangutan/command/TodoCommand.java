package orangutan.command;

import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;

/**
 * Command to create a new to-do and add it to the list.
 */
class TodoCommand implements Command {
    private final String title;
    private final boolean isCompleted;

    /**
     * Creates new command.
     *
     * @param title Description of the list item.
     * @param isCompleted Whether the to-do has been completed or not.
     */
    TodoCommand(String title, boolean isCompleted) {
        this.title = title;
        this.isCompleted = isCompleted;
    }

    /**
     * Creates to-do and appends it to the list.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Reply message, plus a printout of the new to-do.
     */
    public String run(Context context) {
        assert context != null : "TodoCommand says: context should not be null!!!";
        assert context.getList() != null : "TodoCommand says: list should not be null!!!";

        ListItem newTodo = new ListItem(ItemType.TODO, title, isCompleted);
        context.getList().addItem(newTodo);

        return ("Hark! A task hath been added to your list:\n " + newTodo);
    }
}
