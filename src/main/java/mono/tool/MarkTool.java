package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.parser.TaskIdParser;

/**
 * Marks a task as completed.
 */
public class MarkTool implements Tool {
    /** Creates a tool that marks tasks as completed. */
    public MarkTool() {
    }

    /**
     * Parses a task ID and marks the corresponding task as completed.
     *
     * @param arguments one-based task ID
     * @param bot chatbot whose task is marked
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if the ID is malformed or does not exist
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        bot.markTaskDone(TaskIdParser.parse(arguments));
        return ToolSignal.CONTINUE;
    }
}
