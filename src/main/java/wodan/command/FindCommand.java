package wodan.command;

import java.util.ArrayList;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Shows tasks whose description contains a keyword.
 */
public class FindCommand extends Command {
    private final String arguments;

    /**
     * Creates a find command from the text after {@code find}.
     *
     * @param arguments Expected to be a keyword.
     */
    public FindCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Shows matching tasks, numbered from 1 in the order they appear in {@code tasks}.
     *
     * @param tasks The task list to search.
     * @param ui The user interface for the reply.
     * @param storage Unused.
     * @throws WodanException If the keyword is missing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        String keyword = Parser.parseFindKeyword(arguments);
        ArrayList<Integer> numbers = tasks.taskNumbersMatching(keyword);
        if (numbers.isEmpty()) {
            ui.show("    The ravens found no matching quests.");
        } else {
            ui.show("     Here are the matching tasks in your list:");
            for (int i = 0; i < numbers.size(); i++) {
                int listNumber = numbers.get(i);
                ui.showNumberedTask(i + 1, tasks.get(listNumber - 1));
            }
        }
    }
}
