package niulai.command;

import java.time.LocalDate;
import java.util.Objects;

import niulai.model.TaskList;
import niulai.service.Storage;
import niulai.service.Ui;

/**
 * Displays tasks matching either a date or a keyword in their descriptions.
 */
public class FindCommand extends Command {
    /** The date whose matching tasks should be displayed, when this is a date search. */
    private final LocalDate date;

    /** The keyword whose matching tasks should be displayed, when this is a keyword search. */
    private final String keyword;

    /**
     * Creates a find command for a date.
     *
     * @param date the date to search for
     */
    public FindCommand(LocalDate date) {
        super(Type.FIND);
        this.date = Objects.requireNonNull(date, "date");
        this.keyword = null;
    }

    /**
     * Creates a find command for a keyword in task descriptions.
     *
     * @param keyword the keyword to search for
     */
    public FindCommand(String keyword) {
        super(Type.FIND);
        this.date = null;
        this.keyword = Objects.requireNonNull(keyword, "keyword");
    }

    /**
     * Displays the tasks occurring on the requested date.
     *
     * @param tasks the current task list
     * @param ui the user-interface component
     * @param storage the task storage component
     */
    @Override
    public UndoAction execute(TaskList tasks, Ui ui, Storage storage) {
        // Each constructor initializes exactly one search mode for this command.
        assert (date == null) != (keyword == null)
                : "A find command must have exactly one search criterion.";

        if (date != null) {
            ui.showTasksOnDate(tasks, date);
        } else {
            ui.showTasksContaining(tasks, keyword);
        }
        return null;
    }
}
