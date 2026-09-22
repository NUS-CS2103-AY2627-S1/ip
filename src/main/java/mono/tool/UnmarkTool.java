package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.parser.TaskIdParser;

/**
 * Marks a task as not completed.
 */
public class UnmarkTool implements Tool {
    /** Creates a tool that marks tasks as incomplete. */
    public UnmarkTool() {
    }

    /**
     * Parses a task ID and marks the corresponding task as incomplete.
     *
     * @param arguments one-based task ID
     * @param bot chatbot whose task is unmarked
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if the ID is malformed or does not exist
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        bot.unmarkTaskDone(TaskIdParser.parse(arguments));
        return ToolSignal.CONTINUE;
    }
}
