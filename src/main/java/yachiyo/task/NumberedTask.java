package yachiyo.task;

/**
 * Associates a task with its one-based number in the complete task list.
 *
 * @param number one-based task number.
 * @param task numbered task.
 */
public record NumberedTask(int number, Task task) {
    /**
     * Creates a numbered task whose number refers to an entry in the complete task list.
     */
    public NumberedTask {
        assert number >= 1 : "Displayed task number must be one-based";
        assert task != null : "Numbered task must contain a task";
    }
}
