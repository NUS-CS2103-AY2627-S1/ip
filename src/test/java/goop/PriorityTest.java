package goop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import goop.storage.Storage;
import goop.task.Priority;
import goop.task.Task;

/**
 * Tests priority commands through the same facade used by the graphical interface.
 */
class PriorityTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void priority_allTaskTypes_displaysAndPersistsAfterRestart() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Goop goop = new Goop(file);
        goop.getResponse("todo read book");
        goop.getResponse("deadline return book /by 2/12/2019 1800");
        goop.getResponse("event book club /from Mon /to Tue");
        goop.getResponse("priority 1 high");
        goop.getResponse("priority 2 medium");
        goop.getResponse("priority 3 low");
        goop.getResponse("mark 1");

        String expectedTasks = "\n1.[T][X] [high] read book"
                + "\n2.[D][ ] [medium] return book (by: Dec 2 2019, 6:00 PM)"
                + "\n3.[E][ ] [low] book club (from: Mon to: Tue)";
        Goop reloaded = new Goop(file);
        assertEquals("Here are the tasks in your list:" + expectedTasks,
                reloaded.getResponse("list").getResponse());
        assertEquals("Here are the matching tasks in your list:" + expectedTasks,
                reloaded.getResponse("find book").getResponse());
    }

    @Test
    void priority_aliasesAndCase_assignsExpectedLevels() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Goop goop = new Goop(file);
        goop.getResponse("todo task");
        String[] labels = {"1", "2", "3", "HIGH", "Medium", "LoW", "NONE"};
        Priority[] levels = {
            Priority.HIGH, Priority.MEDIUM, Priority.LOW,
            Priority.HIGH, Priority.MEDIUM, Priority.LOW, Priority.NONE
        };
        for (int i = 0; i < labels.length; i++) {
            CommandResult result = goop.getResponse("priority 1 " + labels[i]);
            assertFalse(result.getResponse().startsWith("ERROR:"));
            assertFalse(result.isExit());
            assertEquals(levels[i], new Storage(file).loadTasks().get(0).getPriority());
        }
    }

    @Test
    void priority_changeAndClear_preservesCompletionAndDescription() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Goop goop = new Goop(file);
        goop.getResponse("todo task");
        goop.getResponse("mark 1");
        goop.getResponse("priority 1 high");
        assertEquals("OK, I've set this task's priority to low:\n  [T][X] [low] task",
                goop.getResponse("priority 1 low").getResponse());
        assertEquals("OK, I've cleared this task's priority:\n  [T][X] task",
                goop.getResponse("priority 1 none").getResponse());
        assertEquals("Here are the tasks in your list:\n1.[T][X] task",
                new Goop(file).getResponse("list").getResponse());
        assertEquals(Priority.NONE, new Storage(file).loadTasks().get(0).getPriority());
    }

    @Test
    void priority_invalidArguments_doesNotChangeSavedTask() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Goop goop = new Goop(file);
        goop.getResponse("todo task");
        goop.getResponse("priority 1 high");
        for (String input : List.of("priority", "priority 1", "priority 1 high extra",
                "priority 0 low", "priority -1 low", "priority two low", "priority 2 low",
                "priority 99999999999999999 low", "priority 1 urgent", "priority 1 4")) {
            CommandResult result = goop.getResponse(input);
            assertTrue(result.getResponse().startsWith("ERROR:"), input);
            assertFalse(result.isExit());
            List<Task> tasks = new Storage(file).loadTasks();
            assertEquals(1, tasks.size());
            assertEquals(Priority.HIGH, tasks.get(0).getPriority());
        }
    }

    @Test
    void priority_emptyList_reportsHowToRecover() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("ERROR: There are no tasks to prioritize. Add a task first.",
                goop.getResponse("priority 1 high").getResponse());
    }
}
