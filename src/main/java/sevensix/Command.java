package sevensix;

/** Performs one parsed action without owning parsing, storage, or rollback. */
public interface Command {
    /** Runs the action against the session state.
     *
     * @param tasks the current task list.
     * @param history the session's undo history.
     * @return the response to display.
     * @throws SevenSixException if the action cannot be performed.
     */
    String execute(TaskList tasks, UndoHistory history) throws SevenSixException;

    /** Indicates whether the executor must save and protect this command with rollback.
     *
     * @return true for commands that modify task state or consume undo history.
     */
    default boolean isTaskChanging() {
        return false;
    }
}
