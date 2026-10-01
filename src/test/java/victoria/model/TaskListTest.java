package victoria.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskListTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void captureOutput() {
        originalOut = System.out;
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void constructor_negativeCapacity_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(-1));
    }

    @Test
    void add_validTask_addsTaskAndReportsSuccess() {
        TaskList tasks = new TaskList(1);

        assertTrue(tasks.add("Buy milk"));
        assertEquals(1, tasks.size());
        assertEquals("Buy milk", tasks.getTask(1).getDescription());
    }

    @Test
    void add_nullBlankOrWhenFull_rejectsTask() {
        TaskList tasks = new TaskList(1);

        assertFalse(tasks.add((Task) null));
        assertFalse(tasks.add("   "));
        assertTrue(tasks.add("First"));
        assertFalse(tasks.add("Second"));
        assertEquals(1, tasks.size());
    }

    @Test
    void getTask_invalidNumbers_returnsNull() {
        TaskList tasks = new TaskList(2);
        tasks.add("First");

        assertNull(tasks.getTask(0));
        assertNull(tasks.getTask(-1));
        assertNull(tasks.getTask(2));
    }

    @Test
    void markDoneAndNotDone_validAndInvalidNumbers_updateExpectedTask() {
        TaskList tasks = new TaskList(2);
        tasks.add("First");
        Task task = tasks.getTask(1);

        assertSame(task, tasks.markDone(1));
        assertTrue(task.isDone());
        assertSame(task, tasks.markNotDone(1));
        assertFalse(task.isDone());
        assertNull(tasks.markDone(2));
    }

    @Test
    void delete_validAndInvalidNumbers_removeOnlyValidTask() {
        TaskList tasks = new TaskList(2);
        tasks.add("First");
        tasks.add("Second");

        Task deleted = tasks.delete(1);

        assertEquals("First", deleted.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("Second", tasks.getTask(1).getDescription());
        assertNull(tasks.delete(0));
        assertNull(tasks.delete(2));
    }

    @Test
    void printTasks_emptyAndNonEmptyList_printsExpectedOutput() {
        TaskList tasks = new TaskList(2);
        tasks.printTasks();
        assertTrue(output.toString().contains("Your list is clear"));

        output.reset();
        tasks.add("First");
        tasks.printTasks();
        assertTrue(output.toString().contains("1.[T][ ] First"));
    }

    @Test
    void printTasksOn_deadlineAndEventDate_printsMatchingTasksOnly() {
        TaskList tasks = new TaskList(3);
        tasks.add("ordinary");
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 8, 23)));
        tasks.add(new Event("conference", "2026-08-22", "2026-08-24"));

        tasks.printTasksOn(LocalDate.of(2026, 8, 23));

        String printed = output.toString();
        assertTrue(printed.contains("2.[D][ ] submit report"));
        assertTrue(printed.contains("3.[E][ ] conference"));
        assertFalse(printed.contains("1.[T][ ] ordinary"));
    }

    @Test
    void printTasksOn_dateWithNoMatches_reportsNoMatchingTasks() {
        TaskList tasks = new TaskList(1);
        tasks.add(new Deadline("submit report", "2026-08-23"));

        tasks.printTasksOn(LocalDate.of(2026, 8, 24));

        assertTrue(output.toString().contains("Nothing is scheduled for 2026-08-24"));
    }

    @Test
    void printTasksContaining_keywordIsCaseInsensitiveAndKeepsOriginalNumbers() {
        TaskList tasks = new TaskList(3);
        tasks.add("Read book");
        tasks.add("Buy milk");
        tasks.add("Return BOOK");

        tasks.printTasksContaining("book");

        String printed = output.toString();
        assertTrue(printed.contains("I found these matching tasks:"));
        assertTrue(printed.contains("1.[T][ ] Read book"));
        assertTrue(printed.contains("3.[T][ ] Return BOOK"));
        assertFalse(printed.contains("2.[T][ ] Buy milk"));
    }

    @Test
    void printTasksContaining_noMatches_reportsNoMatchingTasks() {
        TaskList tasks = new TaskList(1);
        tasks.add("Buy milk");

        tasks.printTasksContaining("book");

        assertTrue(output.toString().contains("No matching tasks yet"));
    }
}
