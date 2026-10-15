package sevensix;

/** Adds a parsed task of any supported type and records its undo information. */
public final class AddCommand implements Command {
    private final Task task;

    /** Receives a task whose command syntax has already been parsed.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Validates, records, and adds the task before producing its confirmation. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) throws SevenSixException {
        validateAddition(tasks);
        history.save(tasks, ChangeType.ADD, task);
        tasks.add(task);
        return Ui.formatAddedTask(task, tasks.size());
    }

    /** Requires persistence because this action adds a task. */
    @Override
    public boolean isTaskChanging() {
        return true;
    }

    /** Checks task validity and uniqueness as separate validation steps.
     *
     * @param tasks the existing tasks.
     * @throws SevenSixException if the candidate is invalid or duplicated.
     */
    private void validateAddition(TaskList tasks) throws SevenSixException {
        TaskValidation.validate(task);
        rejectDuplicate(tasks);
    }

    /** Rejects equivalent tasks regardless of completion status.
     *
     * @param tasks the existing tasks.
     * @throws SevenSixException if an equivalent task exists.
     */
    private void rejectDuplicate(TaskList tasks) throws SevenSixException {
        if (tasks.hasDuplicate(task)) {
            throw new SevenSixException(Ui.DUPLICATE_TASK);
        }
    }
}
