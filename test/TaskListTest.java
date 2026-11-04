import java.time.LocalDate;
import java.util.List;

import soar.task.Deadline;
import soar.task.Event;
import soar.task.Task;
import soar.task.TaskList;
import soar.task.ToDo;

/**
 * Checks the collection operations owned by {@link TaskList}.
 */
public class TaskListTest {
    /**
     * Exercises construction, mutation, rollback, snapshots, and date matching.
     *
     * @param args command-line arguments; they are not used
     */
    public static void main(String[] args) {
        ToDo todo = new ToDo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2019, 10, 15));
        Event event = new Event("conference", "2019-10-14", "2019-10-16");
        TaskList tasks = new TaskList(List.of(todo, deadline, event));

        require(tasks.size() == 3, "Initial tasks were not copied");
        verifySnapshotCannotChangeStructure(tasks);
        verifyAddDeleteAndRestore(tasks);
        verifyReplace(tasks);
        verifyCompletionAndRestore(tasks);
        require(tasks.findIndicesOn(LocalDate.of(2019, 10, 15)).equals(List.of(1, 2)),
                "Date matching did not preserve original task indices");
        require(tasks.findIndicesOn(LocalDate.of(2019, 10, 17)).isEmpty(),
                "Date matching included tasks outside the requested date");

        System.out.println("[PASS] TaskList handled snapshots, mutations, rollback, and date matching");
    }

    /** Verifies that callers cannot bypass TaskList's structural operations. */
    private static void verifySnapshotCannotChangeStructure(TaskList tasks) {
        try {
            tasks.asList().add(new ToDo("bypass list operations"));
            throw new AssertionError("Task snapshots should not allow structural changes");
        } catch (UnsupportedOperationException expected) {
            require(tasks.size() == 3, "A snapshot changed the task list");
        }
    }

    /** Verifies add, delete, and restoration at the original position. */
    private static void verifyAddDeleteAndRestore(TaskList tasks) {
        ToDo added = new ToDo("new task");
        tasks.add(added);
        require(tasks.size() == 4 && tasks.get(3) == added, "Adding did not append the task");

        Task removed = tasks.delete(1);
        require(tasks.size() == 3 && removed instanceof Deadline, "Deleting removed the wrong task");
        tasks.restoreDeletedTask(1, removed);
        require(tasks.size() == 4 && tasks.get(1) == removed,
                "A deleted task was not restored at its original position");

        tasks.delete(3);
    }

    /** Verifies replacement retains list size and returns the previous task. */
    private static void verifyReplace(TaskList tasks) {
        Task original = tasks.get(0);
        ToDo replacement = new ToDo("replacement");

        require(tasks.replace(0, replacement) == original, "Replacement did not return the original task");
        require(tasks.size() == 3 && tasks.get(0) == replacement,
                "Replacement changed the list size or wrong position");
        tasks.replace(0, original);
    }

    /** Verifies mark, unmark, and restoration of an earlier completion state. */
    private static void verifyCompletionAndRestore(TaskList tasks) {
        Task marked = tasks.mark(0);
        require(marked.isDone(), "Marking did not complete the task");
        tasks.restoreCompletion(0, false);
        require(!marked.isDone(), "Completion rollback did not restore an incomplete task");

        Task unmarked = tasks.unmark(1);
        require(!unmarked.isDone(), "Unmarking did not make the task incomplete");
        tasks.restoreCompletion(1, true);
        require(unmarked.isDone(), "Completion rollback did not restore a completed task");
        tasks.unmark(1);
    }

    /** Fails with a useful message when a condition is false. */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
