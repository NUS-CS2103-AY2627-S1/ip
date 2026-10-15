package orangutan.command;

import java.time.format.DateTimeParseException;

import orangutan.OrangutanException;
import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;


/**
 * Command to create a new deadline and add it to the list.
 */
class DeadlineCommand implements Command {
    private final String title;
    private final String by;
    private final boolean isCompleted;

    /**
     * Creates new command.
     *
     * @param title Description of the list item.
     * @param by Date and time of the deadline, of format "yyyymmdd hhmm".
     * @param isCompleted Whether the deadline has been completed or not.
     */
    DeadlineCommand(String title, String by, boolean isCompleted) {
        this.title = title;
        this.by = by;
        this.isCompleted = isCompleted;
    }

    /**
     * Creates deadline and appends it to the list.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Reply message, plus a printout of the new deadline.
     *      If date and time are of the wrong format, will instead return an alert message.
     */
    public String run(Context context) throws OrangutanException {
        assert context != null : "DeadlineCommand says: context should not be null!!!";
        assert context.getList() != null : "DeadlineCommand says: list should not be null!!!";

        try {
            ListItem newDeadline = new ListItem(ItemType.DEADLINE, title, isCompleted, by);
            context.getList().addItem(newDeadline);

            return ("Hark! A deadline hath been added to your list:\n " + newDeadline);
        } catch (DateTimeParseException e) {
            throw CommandError.dateTimeParseError(ListItem.DATE_TIME_INPUT_FORMAT);
        }
    }
}
