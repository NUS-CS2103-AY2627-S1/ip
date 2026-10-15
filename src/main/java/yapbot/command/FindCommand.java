package yapbot.command;

import yapbot.exception.YapBotException;
import yapbot.task.Task;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents the "find" command, which lists tasks whose description
 * contains a given keyword.
 */
public class FindCommand extends Command {

    private static final String COMMAND_WORD_FIND = "find";

    private final String fullCommand;

    /**
     * Creates a find command from the full command text.
     *
     * @param fullCommand the full command text as typed by the user, e.g. "find book".
     */
    public FindCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Shows every task whose description contains the given keyword. Does
     * not modify the task list.
     *
     * @param taskList the task list to search.
     * @param ui the Ui to show the results on.
     * @throws YapBotException if no keyword was given.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) throws YapBotException {
        String keyword = extractKeyword(fullCommand);
        Task[] matches = taskList.findMatching(keyword);
        ui.showMatchingTasks(matches);
    }

    /**
     * Extracts and validates the search keyword out of the full command text.
     *
     * @param fullCommand the full command text as typed by the user.
     * @return the keyword to search for.
     * @throws YapBotException if no keyword was given.
     */
    private static String extractKeyword(String fullCommand) throws YapBotException {
        String keyword = fullCommand.length() > COMMAND_WORD_FIND.length()
                ? fullCommand.substring(COMMAND_WORD_FIND.length()).trim()
                : "";
        if (keyword.isEmpty()) {
            throw new YapBotException("Please specify a keyword to search for, e.g. 'find book'.");
        }
        return keyword;
    }
}
