package allmind.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import allmind.parser.InvalidCommandException;

/** Tests task collection operations and event conflict detection. */
public class TaskListTest {
    private static final LocalDate START = LocalDate.of(2026, 10, 20);

    @Test
    public void addAndAsList_tasksAreStoredInOrderAndViewIsReadOnly() {
        TaskList list = new TaskList();
        Task task = new ToDo("read notes");
        list.add(task);

        assertEquals(1, list.size());
        assertEquals(List.of(task), list.asList());
        assertThrows(UnsupportedOperationException.class, () -> list.asList().clear());
    }

    @Test
    public void findEventConflicts_overlappingUnfinishedEvents_returnsOverlapDetails() {
        TaskList list = new TaskList();
        list.add(new Event("existing", START, START.plusDays(3)));

        List<TaskList.EventConflict> conflicts = list.findEventConflicts(
                new Event("new", START.plusDays(2), START.plusDays(5)));

        assertEquals(List.of(new TaskList.EventConflict(1, "existing", START.plusDays(2),
                START.plusDays(3))), conflicts);
    }

    @Test
    public void findEventConflicts_completedOrSeparateEvents_areIgnored() {
        TaskList list = new TaskList();
        Event completed = new Event("completed", START, START.plusDays(2));
        completed.markAsDone();
        list.add(completed);
        list.add(new Event("separate", START.plusDays(5), START.plusDays(6)));

        assertTrue(list.findEventConflicts(new Event("new", START, START.plusDays(2))).isEmpty());
    }

    @Test
    public void markAndUnmarkTask_validNumber_updatesTaskStatus() throws InvalidCommandException {
        Task task = new ToDo("task");
        TaskList list = new TaskList();
        list.add(task);

        list.markTask("mark 1");
        assertTrue(task.isDone());
        list.unmarkTask("unmark 1");
        assertFalse(task.isDone());
    }

    @Test
    public void taskOperations_invalidNumber_throwInvalidCommandException() {
        TaskList list = new TaskList();

        assertThrows(InvalidCommandException.class, () -> list.markTask("mark abc"));
        assertThrows(InvalidCommandException.class, () -> list.unmarkTask("unmark abc"));
        assertThrows(InvalidCommandException.class, () -> list.deleteTask("delete abc"));
    }

    @Test
    public void deleteTask_validNumber_removesSelectedTask() throws InvalidCommandException {
        TaskList list = new TaskList();
        list.add(new ToDo("first"));
        list.add(new ToDo("second"));

        list.deleteTask("delete 1");

        assertEquals(1, list.size());
        assertEquals("second", list.asList().get(0).getDescription());
    }

    @Test
    public void findTasks_emptyKeyword_rejected() {
        TaskList list = new TaskList();

        assertThrows(InvalidCommandException.class, () -> list.findTasks("find "));
    }

    @Test
    public void printTasksAndFindTasks_matchingTasks_arePrinted() throws InvalidCommandException {
        TaskList list = new TaskList();
        list.add(new ToDo("Read Notes"));
        list.add(new ToDo("buy milk"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));
        try {
            list.printTasks();
            list.findTasks("find notes");
        } finally {
            System.setOut(original);
        }

        String text = output.toString();
        assertTrue(text.contains("1.[T][ ] Read Notes"));
        assertTrue(text.contains("Here are the matching tasks"));
        assertTrue(text.contains("1.[T][ ] Read Notes"));
        assertTrue(text.contains("2.[T][ ] buy milk"));
    }
}
