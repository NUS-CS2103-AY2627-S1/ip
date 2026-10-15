package orangutan.command;

import java.time.format.DateTimeParseException;

import orangutan.OrangutanException;
import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;


/**
 * Command to create a new event and add it to the list.
 */
class EventCommand implements Command {
    private final String title;
    private final String from;
    private final String to;
    private final boolean isCompleted;

    /**
     * Creates a new command.
     *
     * @param title Title of the list item.
     * @param from Start date and time of the event, of format "yyyymmdd hhmm".
     * @param to End date and time of the event, of format "yyyymmdd hhmm".
     * @param isCompleted Whether the event has passed or not.
     */
    EventCommand(String title, String from, String to, boolean isCompleted) {
        this.title = title;
        this.from = from;
        this.to = to;
        this.isCompleted = isCompleted;
    }

    /**
     * Creates an event and appends it to the list.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Reply message, plus a printout of the new deadline.
     *      If date and time are of the wrong format, will instead return an alert message.
     */
    public String run(Context context) throws OrangutanException {
        assert context != null : "EventCommand says: context should not be null!!!";
        assert context.getList() != null : "EventCommand says: list should not be null!!!";

        try {
            ListItem newEvent = new ListItem(ItemType.EVENT, title, isCompleted, from, to);
            context.getList().addItem(newEvent);

            return ("Hark! An event hath been added to your list:\n " + newEvent);
        } catch (DateTimeParseException e) {
            throw CommandError.dateTimeParseError(ListItem.DATE_TIME_INPUT_FORMAT);
        }
    }
}
