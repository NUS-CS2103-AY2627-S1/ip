package sevensix;

/** Displays all tasks in their insertion order without changing session state. */
public final class ListCommand implements Command {
    /** Selects the empty-list response or the numbered pipeline. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) {
        return tasks.size() == 0 ? Ui.getEmptyListMessage() : Ui.formatTaskList(tasks);
    }
}
