package mono.tool;

import java.time.format.DateTimeParseException;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;
import mono.task.Deadline;

/**
 * Creates a deadline task from a description and due date.
 */
public class DeadlineTool implements Tool {
    private static final String BY_MARKER = " /by ";
    private static final String FORMAT_MESSAGE =
            "Deadline format: deadline <description> /by <date>";

    /** Creates a tool that adds deadline tasks. */
    public DeadlineTool() {
    }

    /**
     * Parses and adds a deadline task.
     *
     * @param arguments deadline description and date
     * @param bot chatbot to which the deadline is added
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if the arguments or date are invalid
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        if (arguments == null) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }
        int byIndex = arguments.lastIndexOf(BY_MARKER);
        if (byIndex <= 0) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }

        String description = arguments.substring(0, byIndex).trim();
        String dateText = arguments.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty() || dateText.isEmpty()) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }
        TaskDescriptionValidator.validate(description, FORMAT_MESSAGE);

        try {
            bot.addTask(new Deadline(description, dateText));
        } catch (DateTimeParseException e) {
            throw new WrongFormatException(
                    "Deadline date must use the format yyyy-MM-dd (for example, 2019-10-15)");
        }
        return ToolSignal.CONTINUE;
    }
}
