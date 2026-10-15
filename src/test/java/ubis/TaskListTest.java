package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests task-list validation at its public operation boundaries.
 */
public class TaskListTest {
    @Test
    public void find_blankKeyword_returnsMissingParameterMessage() {
        TaskList tasks = new TaskList();
        assertEquals("Please provide a keyword to find.\nExample: find book", tasks.find("   \t"));
    }

    @Test
    public void find_noMatches_returnsSpecificMessage() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo().initialise("read book"));
        assertEquals("No matching tasks found.", tasks.find("exercise"));
    }

    @Test
    public void taskNumber_outOfBounds_returnsErrorMessage() {
        TaskList tasks = new TaskList();
        assertEquals("There is no task number 0. Enter a task number shown by \"list\".", tasks.markTask(0));
        assertEquals("There is no task number -1. Enter a task number shown by \"list\".",
                tasks.removeTask(-1));
        assertEquals("There is no task number 1. Enter a task number shown by \"list\".",
                tasks.unmarkTask(1));
    }

    @Test
    public void addTask_mixedTypes_preservesOrderAndIgnoresNull() {
        TaskList tasks = new TaskList();
        assertEquals("", tasks.toString());
        assertEquals("No tasks to show", tasks.listTasks());
        assertEquals("", tasks.addTask(null));
        assertEquals("added: [T][ ] read book", tasks.addTask(new Todo().initialise("read book")));
        tasks.addTask(new Deadline().initialise("report /by 2026-09-30"));
        tasks.addTask(new Event().initialise("trip /from 2026-09-01 /to 2026-09-03"));
        tasks.addTask(null);
        assertEquals("1: [T][ ] read book\n2: [D][ ] report (by: Sep 30 2026)"
                + "\n3: [E][ ] trip (from: Sep 1 2026 to: Sep 3 2026)", tasks.listTasks());
        assertEquals("{T}{0}{read book}\n{D}{0}{report}{2026-09-30}\n"
                + "{E}{0}{trip}{2026-09-01}{2026-09-03}\n", tasks.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void removeTask_validPosition_deletesOnlySelectedTask(int number) {
        TaskList tasks = createThreeTasks();
        assertEquals("Okay, I've deleted [T][ ] task " + number, tasks.removeTask(number));
        StringBuilder expected = new StringBuilder();
        int displayNumber = 1;
        for (int originalNumber = 1; originalNumber <= 3; originalNumber++) {
            if (originalNumber != number) {
                expected.append("\n").append(displayNumber++).append(": [T][ ] task ").append(originalNumber);
            }
        }
        assertEquals(expected.toString().stripLeading(), tasks.listTasks());
        tasks.removeTask(1);
        tasks.removeTask(1);
        assertEquals("No tasks to show", tasks.listTasks());
        assertEquals("", tasks.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 4, Integer.MAX_VALUE})
    void taskNumber_invalidPosition_preservesPopulatedList(int number) {
        TaskList tasks = createThreeTasks();
        String before = tasks.toString();
        String expected = "There is no task number " + number + ". Enter a task number shown by \"list\".";
        assertEquals(expected, tasks.markTask(number));
        assertEquals(expected, tasks.unmarkTask(number));
        assertEquals(expected, tasks.removeTask(number));
        assertEquals(before, tasks.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void markTask_repeatedOperations_changesOnlySelectedTask(int number) {
        TaskList tasks = createThreeTasks();
        String before = tasks.toString();
        String marked = "Nice! I've marked this task as DONE:\n  [T][X] task " + number;
        assertEquals(marked, tasks.markTask(number));
        assertEquals(marked, tasks.markTask(number));
        assertEquals(before.replace("{T}{0}{task " + number + "}", "{T}{1}{task " + number + "}"), tasks.toString());
        String unmarked = "Okay, I've marked this task NOT done yet:\n  [T][ ] task " + number;
        assertEquals(unmarked, tasks.unmarkTask(number));
        assertEquals(unmarked, tasks.unmarkTask(number));
        assertEquals(before, tasks.toString());
    }

    @Test
    public void find_matchingDescriptions_returnsOrderedSubsetWithoutChangingList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo().initialise("read book"));
        tasks.addTask(new Todo().initialise("Book tickets"));
        tasks.addTask(new Todo().initialise("buy bookshelf"));
        tasks.markTask(3);
        String before = tasks.toString();
        assertEquals("1: [T][ ] read book\n3: [T][X] buy bookshelf", tasks.find("  book\t"));
        assertEquals("2: [T][ ] Book tickets", tasks.find("Book"));
        assertEquals("No matching tasks found.", tasks.find("BOOK"));
        assertEquals("Please provide a keyword to find.\nExample: find book", tasks.find(null));
        assertEquals(before, tasks.toString());
    }

    /**
     * Creates a populated list for boundary and state-transition tests.
     */
    private TaskList createThreeTasks() {
        TaskList tasks = new TaskList();
        for (int number = 1; number <= 3; number++) {
            tasks.addTask(new Todo().initialise("task " + number));
        }
        return tasks;
    }
}
