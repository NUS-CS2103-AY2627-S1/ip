package sevensix;

/** Restores the latest remembered task state and reports the reversed action. */
public final class UndoCommand implements Command {
    /** Restores one entry, leaving earlier history available for subsequent undo commands. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) throws SevenSixException {
        UndoEntry entry = takeLatestEntry(history);
        tasks.replaceWith(entry.restoreTasks());
        return Ui.formatUndoMessage(entry);
    }

    /** Requires persistence and recovery if the undo cannot be saved. */
    @Override
    public boolean isTaskChanging() {
        return true;
    }

    /** Rejects empty history before consuming its latest entry.
     *
     * @param history the history to consume.
     * @return the most recent change.
     * @throws SevenSixException if there is no change to undo.
     */
    private UndoEntry takeLatestEntry(UndoHistory history) throws SevenSixException {
        if (!history.hasSnapshot()) {
            throw new SevenSixException(Ui.EMPTY_UNDO_HISTORY);
        }
        return history.takeEntry();
    }
}
