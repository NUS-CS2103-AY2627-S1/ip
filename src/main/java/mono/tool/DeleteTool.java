package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.parser.TaskIdParser;

/**
 * Deletes a task by its one-based list ID.
 */
public class DeleteTool implements Tool {
    /** Creates a tool that deletes tasks. */
    public DeleteTool() {
    }

    /**
     * Parses a task ID and deletes the corresponding task.
     *
     * @param arguments one-based task ID
     * @param bot chatbot from which the task is deleted
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if the ID is malformed or does not exist
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        bot.delete(TaskIdParser.parse(arguments));
        return ToolSignal.CONTINUE;
    }
}
