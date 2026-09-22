package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;
import mono.task.ToDo;

/**
 * Creates a to-do task from a description.
 */
public class TodoTool implements Tool {
    private static final String FORMAT_MESSAGE = "Todo format: todo <description>";

    /** Creates a tool that adds to-do tasks. */
    public TodoTool() {
    }

    /**
     * Parses and adds a to-do task.
     *
     * @param arguments to-do description
     * @param bot chatbot to which the task is added
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if the description is empty
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        if (arguments == null) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new WrongFormatException(FORMAT_MESSAGE);
        }
        TaskDescriptionValidator.validate(description, FORMAT_MESSAGE);
        bot.addTask(new ToDo(description));
        return ToolSignal.CONTINUE;
    }
}
