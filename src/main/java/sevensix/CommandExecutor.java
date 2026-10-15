package sevensix;

/** Coordinates command execution, persistence, and complete recovery after a failed change. */
public final class CommandExecutor {
    private final TaskStorage storage;
    private final TaskList tasks;
    private UndoHistory history = new UndoHistory();

    /** Loads the task state belonging to one application session.
     *
     * @param storage the storage used by this session.
     */
    public CommandExecutor(TaskStorage storage) {
        this.storage = storage;
        tasks = new TaskList(storage.load());
    }

    /** Runs read-only commands directly and protects changing commands with rollback.
     *
     * @param command the parsed command to execute.
     * @return the successful command response.
     * @throws SevenSixException if validation or saving fails.
     */
    public String execute(Command command) throws SevenSixException {
        if (!command.isTaskChanging()) {
            return command.execute(tasks, history);
        }
        return executeWithRollback(command);
    }

    /** Returns a load warning shared by both application interfaces.
     *
     * @return the warning, or an empty string for healthy storage.
     */
    public String getStartupWarning() {
        return storage.getLoadWarning();
    }

    /** Restores the whole session if a task-changing command fails.
     *
     * @param command the task-changing command.
     * @return its response after a successful save.
     * @throws SevenSixException if execution or saving fails.
     */
    private String executeWithRollback(Command command) throws SevenSixException {
        Checkpoint checkpoint = captureCheckpoint();
        try {
            return executeAndSave(command);
        } catch (SevenSixException exception) {
            restoreCheckpoint(checkpoint);
            throw exception;
        }
    }

    /** Captures the session before any task or history mutation.
     *
     * @return isolated state that can recover a failed change.
     */
    private Checkpoint captureCheckpoint() {
        return new Checkpoint(new TaskSnapshot(tasks), history.copy());
    }

    /** Restores both task state and the complete undo stack.
     *
     * @param checkpoint the session state before the failed change.
     */
    private void restoreCheckpoint(Checkpoint checkpoint) {
        tasks.replaceWith(checkpoint.tasks().restore());
        history = checkpoint.history();
    }

    /** Produces a response only after the updated task list is safely saved.
     *
     * @param command the task-changing command.
     * @return its successful response.
     * @throws SevenSixException if execution or saving fails.
     */
    private String executeAndSave(Command command) throws SevenSixException {
        String response = command.execute(tasks, history);
        saveTasks();
        return response;
    }

    /** Reports storage failure so the executor can recover its checkpoint.
     *
     * @throws SevenSixException if saving is unsuccessful.
     */
    private void saveTasks() throws SevenSixException {
        if (!storage.save(tasks)) {
            throw new SevenSixException(Ui.SAVE_FAILURE);
        }
    }

    /** Keeps task state and its matching history together for failed-command recovery. */
    private record Checkpoint(TaskSnapshot tasks, UndoHistory history) {
    }
}
