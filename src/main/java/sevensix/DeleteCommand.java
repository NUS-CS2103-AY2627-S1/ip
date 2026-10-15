package sevensix;

/** Removes one numbered task while preserving its position and values for undo. */
public final class DeleteCommand implements Command {
    private final int taskNumber;

    /** Receives the parsed task number to remove.
     *
     * @param taskNumber the one-based task number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Records and removes the task before reporting the remaining count. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) throws SevenSixException {
        Task task = tasks.getByNumber(taskNumber);
        history.save(tasks, ChangeType.DELETE, task);
        tasks.remove(taskNumber - 1);
        return Ui.formatDeletedTask(task, tasks.size());
    }

    /** Requires persistence because this action removes a task. */
    @Override
    public boolean isTaskChanging() {
        return true;
    }
}
