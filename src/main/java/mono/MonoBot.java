package mono;

import java.util.Objects;

import mono.exception.NonExistentException;
import mono.task.Task;
import mono.task.TaskList;
import mono.ui.ConversationUi;

/**
 * Coordinates task operations with Mono's conversation user interface.
 */
public class MonoBot {
    private final TaskList taskList;
    private final ConversationUi ui;

    /** Creates Mono with its persisted task list and console UI. */
    public MonoBot() {
        this(new ConversationUi());
    }

    /**
     * Creates Mono with a caller-supplied conversation output destination.
     *
     * @param ui conversation UI used to display responses
     */
    public MonoBot(ConversationUi ui) {
        this.taskList = new TaskList();
        this.ui = Objects.requireNonNull(ui, "ui");
    }

    /**
     * Adds a task and displays the result.
     *
     * @param task task to add
     */
    public void addTask(Task task) {
        Task addedTask = this.taskList.addTask(task);
        assert addedTask == task : "TaskList.addTask must return the task it stores";
        this.ui.showTaskAdded(addedTask, this.taskList.getTasks().size());
    }

    /**
     * Deletes a task and displays the result.
     *
     * @param id one-based task ID
     * @throws NonExistentException if no task has the specified ID
     */
    public void delete(int id) throws NonExistentException {
        Task deletedTask = this.taskList.delete(id);
        this.ui.showTaskDeleted(deletedTask, this.taskList.getTasks().size());
    }

    /** Displays all tasks. */
    public void listTasks() {
        this.ui.showTaskList(this.taskList.getTasks());
    }

    /**
     * Finds tasks containing a keyword and displays the matches.
     *
     * @param keyword text to find in task names
     */
    public void findTasks(String keyword) {
        this.ui.showTaskList(this.taskList.findTasks(keyword));
    }

    /**
     * Marks a task done and displays the result.
     *
     * @param id one-based task ID
     * @throws NonExistentException if no task has the specified ID
     */
    public void markTaskDone(int id) throws NonExistentException {
        this.ui.showTaskMarkedDone(this.taskList.markTaskDone(id));
    }

    /**
     * Marks a task undone and displays the result.
     *
     * @param id one-based task ID
     * @throws NonExistentException if no task has the specified ID
     */
    public void unmarkTaskDone(int id) throws NonExistentException {
        this.ui.showTaskUnmarked(this.taskList.unmarkTaskDone(id));
    }

    /** Displays Mono's greeting. */
    public void greet() {
        this.ui.showGreeting();
    }

    /** Displays Mono's farewell. */
    public void exit() {
        this.ui.showExit();
    }
}
