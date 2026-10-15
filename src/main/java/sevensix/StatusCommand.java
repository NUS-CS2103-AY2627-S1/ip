package sevensix;

/** Changes completion status using the same workflow for marking and reopening tasks. */
public final class StatusCommand implements Command {
    private final int taskNumber;
    private final boolean isDone;

    /** Receives a parsed task number and its requested status.
     *
     * @param taskNumber the one-based task number.
     * @param isDone whether to complete rather than reopen the task.
     */
    public StatusCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    /** Records the old status and applies the requested status. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) throws SevenSixException {
        Task task = tasks.getByNumber(taskNumber);
        history.save(tasks, getChangeType(), task);
        applyStatus(task);
        return formatResponse(task);
    }

    /** Requires persistence even when the task already has the requested status. */
    @Override
    public boolean isTaskChanging() {
        return true;
    }

    /** Chooses the history action corresponding to the requested status.
     *
     * @return the action to record.
     */
    private ChangeType getChangeType() {
        return isDone ? ChangeType.MARK : ChangeType.UNMARK;
    }

    /** Changes only the completion state of the selected task.
     *
     * @param task the validated task to update.
     */
    private void applyStatus(Task task) {
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /** Selects the shared formatter for the requested status.
     *
     * @param task the updated task.
     * @return the status confirmation.
     */
    private String formatResponse(Task task) {
        return isDone ? Ui.formatMarkedTask(task) : Ui.formatUnmarkedTask(task);
    }
}
