package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void update_validDescription_preservesOtherTaskFields() {
        String[] storedTasks = {
            "TODO\t0\toriginal\t",
            "TODO\t1\toriginal\t",
            "DEADLINE\t0\toriginal\t2026-09-10T18:00",
            "DEADLINE\t1\toriginal\t2026-09-10T18:00",
            "EVENT\t0\toriginal\t(from: 2pm to: 4pm)",
            "EVENT\t1\toriginal\t(from: 2pm to: 4pm)"
        };
        for (String stored : storedTasks) {
            Task task = Task.fromStorageString(stored);
            Task neighbour = Task.todo("unchanged");
            TaskList tasks = new TaskList(List.of(neighbour, task));

            assertSame(task, tasks.update(2, "  revised  description  "));
            assertEquals(stored.replace("original", "revised  description"), task.toStorageString());
            assertEquals(List.of(neighbour, task), tasks.getAll());
            assertEquals("TODO\t0\tunchanged\t", neighbour.toStorageString());
        }
    }

    @Test
    public void update_invalidArguments_leavesTaskUnchanged() {
        Task task = Task.todo("original");
        TaskList tasks = new TaskList(List.of(task));
        for (int number : new int[] {0, -1, 2, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> tasks.update(number, "revised"));
        }
        for (String description : new String[] {null, "", "   ", "a\tb", "a\nb", "a\rb"}) {
            assertThrows(IllegalArgumentException.class, () -> tasks.update(1, description));
        }
        assertEquals("TODO\t0\toriginal\t", task.toStorageString());
        assertEquals(1, tasks.size());
    }

    @Test
    public void find_partialKeyword_preservesOrderAndCaseSensitivity() {
        Task firstMatch = Task.todo("read notebook");
        Task differentCase = Task.todo("Book a room");
        Task unrelated = Task.todo("write code");
        Task lastMatch = Task.todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, differentCase, unrelated, lastMatch));

        assertEquals(List.of(firstMatch, lastMatch), tasks.find("book"));
        assertEquals(List.of(differentCase), tasks.find("Book"));
    }

    @Test
    public void find_noMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        assertEquals(List.of(), tasks.find("book"));

        tasks.add(Task.todo("write code"));
        assertEquals(List.of(), tasks.find("book"));
    }

    @Test
    public void find_emptyKeyword_returnsAllTasksIncludingDuplicates() {
        Task task = Task.todo("read book");
        TaskList tasks = new TaskList(List.of(task, task));

        assertEquals(List.of(task, task), tasks.find(""));
    }

    @Test
    public void find_modifyResults_doesNotChangeTaskList() {
        Task task = Task.todo("read book");
        TaskList tasks = new TaskList(List.of(task));

        List<Task> matches = tasks.find("book");
        matches.clear();
        matches.add(Task.todo("another book"));

        assertEquals(List.of(task), tasks.getAll());
        assertEquals(List.of(task), tasks.find("book"));
    }

    @Test
    public void add_validTask_increasesSize() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        assertEquals(
                1,
                tasks.size()
        );
    }

    @Test
    public void delete_validTask_removesCorrectTask() {
        TaskList tasks = new TaskList();

        Task first = Task.todo("read book");
        Task second = Task.todo("write code");

        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.delete(1);

        assertSame(
                first,
                removed
        );

        assertEquals(
                1,
                tasks.size()
        );

        assertSame(
                second,
                tasks.get(1)
        );
    }

    @Test
    public void delete_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        assertThrows(IllegalArgumentException.class, () -> tasks.delete(0));

        assertThrows(IllegalArgumentException.class, () -> tasks.delete(2));
    }

    @Test
    public void markDone_validTask_marksCorrectTask() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        tasks.markDone(1);

        assertEquals(
                "[T][X] read book",
                tasks.get(1).toDisplayString()
        );
    }

    @Test
    public void markDone_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        assertThrows(IllegalArgumentException.class, () -> tasks.markDone(2));
    }

    @Test
    public void get_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> tasks.get(1));
    }
}
