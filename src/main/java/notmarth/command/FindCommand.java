package notmarth.command;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that searches task descriptions for a keyword. */
public final class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command for a keyword.
     *
     * @param keyword the keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param tasks the task list to search.
     * @param ui the console interaction handler.
     * @param storage the task archive handler, which is unused.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Integer> matchingIndexes = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .boxed()
                .toList();
        if (matchingIndexes.isEmpty()) {
            ui.showNoFindTasks(keyword);
            return;
        }
        ui.showFindTasksHeader();
        matchingIndexes.forEach(index -> ui.showNumberedTask(index + 1, tasks.get(index)));
    }
}
