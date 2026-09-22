package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;

/**
 * Finds tasks whose names contain a keyword.
 */
public class FindTasksTool implements Tool {
    /** Creates a tool that searches task names. */
    public FindTasksTool() {
    }

    /**
     * Displays tasks whose names contain the supplied keyword.
     *
     * @param arguments keyword to find
     * @param bot chatbot whose tasks are searched
     * @return {@link ToolSignal#CONTINUE}
     * @throws MonoException if the keyword is empty
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        if (arguments == null) {
            throw new WrongFormatException("Find format: find <keyword>");
        }
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new WrongFormatException("Find format: find <keyword>");
        }
        bot.findTasks(keyword);
        return ToolSignal.CONTINUE;
    }
}
