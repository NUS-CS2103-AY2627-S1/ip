package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;

/**
 * Displays all tasks currently stored by Mono.
 */
public class ListTool implements Tool {
    /** Creates a tool that lists all tasks. */
    public ListTool() {
    }

    /**
     * Displays every task when no arguments are supplied.
     *
     * @param arguments text following the tool name
     * @param bot chatbot whose tasks are displayed
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if arguments are supplied
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        if (arguments == null || !arguments.isEmpty()) {
            throw new WrongFormatException("list does not accept arguments.");
        }
        bot.listTasks();
        return ToolSignal.CONTINUE;
    }
}
