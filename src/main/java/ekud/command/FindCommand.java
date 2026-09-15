package ekud.command;

import java.util.List;

import ekud.EkudException;
import ekud.storage.Storage;
import ekud.task.Task;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Lists tasks whose descriptions contain a given keyword.
 * Matching ignores case. This command does not change the save file.
 */
public class FindCommand implements Command {
    private final String keyword;

    /**
     * Creates a command that will search descriptions for {@code keyword}.
     *
     * @param keyword the text to look for
     */
    private FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Parses {@code find KEYWORD}. Extra words are treated as part of the
     * search text, so {@code find read book} looks for {@code read book}.
     *
     * @param arguments text after the command word
     * @return a find command for that keyword
     * @throws EkudException if the keyword is missing
     */
    public static FindCommand parse(String arguments) throws EkudException {
        if (arguments.isBlank()) {
            throw new EkudException("Please provide a keyword, e.g. find book.");
        }
        return new FindCommand(arguments.trim());
    }

    /**
     * Shows tasks whose descriptions contain this keyword.
     *
     * @param tasks   the list to search
     * @param ui      used to show the matches
     * @param storage unused; this command does not change the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.findByKeyword(keyword);
        ui.showFound(keyword, matches);
    }
}
