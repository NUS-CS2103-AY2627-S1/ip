package drax;

/**
 * Stores an independent snapshot of a task list at a particular moment.
 */
public class TaskMemento {
    private final TaskList taskList;

    /**
     * Captures independent copies of the supplied tasks and their state.
     *
     * @param taskList task list to snapshot.
     */
    public TaskMemento(TaskList taskList) {
        this.taskList = new TaskList(taskList.asList());
    }

    /**
     * Returns an independent, mutable copy of the saved task list.
     * Changes to the returned tasks do not affect this snapshot.
     *
     * @return a fresh copy of the saved tasks in their original order
     */
    public TaskList getSavedContent() {
        return new TaskList(this.taskList.asList());
    }
}
