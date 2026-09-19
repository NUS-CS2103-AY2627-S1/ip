package fox.task;

import fox.exception.FoxException;

/** Dependency-free tests for the encapsulated task collection. */
public class TaskListTest {
    /** Runs task-list tests with assertions enabled. */
    public static void main(String[] args) throws Exception {
        TaskList tasks = new TaskList(2);
        Task first = new Todo("first");
        tasks.add(first);
        tasks.add(new Event("meeting", "10am", "11am"));
        assert tasks.size() == 2;
        assert tasks.markDone(1) == first;
        assert first.isDone();
        assert tasks.markNotDone(1) == first;
        assert !first.isDone();
        assert tasks.delete(1) == first;
        assert tasks.size() == 1;
        try {
            tasks.add(new Todo("third"));
            tasks.add(new Todo("overflow"));
            throw new AssertionError("Capacity should be enforced");
        } catch (FoxException expected) {
            assert expected.getMessage().contains("task list is full");
        }
    }
}
