package mono.tool;

import java.time.format.DateTimeParseException;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;
import mono.task.Event;

/**
 * Creates an event task from a description, start time, and end time.
 */
public class EventTool implements Tool {
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";
    private static final String FORMAT_MESSAGE =
            "Event format: event <description> /from <start-date> /to <end-date>";

    /** Creates a tool that adds event tasks. */
    public EventTool() {
    }

    /**
     * Parses and adds an event task.
     *
     * @param arguments event description, start date, and end date
     * @param bot chatbot to which the event is added
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if the arguments or dates are invalid
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        if (arguments == null) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }
        int fromIndex = arguments.indexOf(FROM_MARKER);
        int toIndex = arguments.indexOf(TO_MARKER);
        if (fromIndex <= 0 || toIndex <= fromIndex + FROM_MARKER.length()) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }

        String description = arguments.substring(0, fromIndex).trim();
        String startDateText = arguments
                .substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String endDateText = arguments.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty()) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }
        TaskDescriptionValidator.validate(description, FORMAT_MESSAGE);

        try {
            bot.addTask(new Event(description, startDateText, endDateText));
        } catch (DateTimeParseException e) {
            throw new WrongFormatException(
                    "Event dates must use the format yyyy-MM-dd (for example, 2019-10-15)");
        }
        return ToolSignal.CONTINUE;
    }
}
