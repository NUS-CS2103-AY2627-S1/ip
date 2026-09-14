package groot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import groot.testutil.IsolatedProcess;

/**
 * Verifies immutable task snapshots and their consistency across commands and failed saves.
 */
public class TaskSummaryTest {
    @TempDir
    public Path directory;

    @Test
    public void summaries_commandsAndRollback_preserveMainListSnapshot() throws Exception {
        assertEquals("passed\n", IsolatedProcess.run(directory, SummaryScenario.class, ""));
    }

    /**
     * Exercises snapshots in a temporary working directory with real persistence.
     */
    public static class SummaryScenario {
        /**
         * Checks every task type, immutable values, numbering, reloads, and rollback.
         */
        public static void main(String[] args) throws Exception {
            Groot groot = new Groot();
            assertEquals(List.of(), groot.getTaskSummaries());
            groot.getResponse("todo first");
            groot.getResponse("deadline report /by 2026-09-18");
            groot.getResponse("event meeting /from Mon /to Tue");
            List<TaskSummary> before = groot.getTaskSummaries();
            assertEquals(List.of(new TaskSummary(1, "Todo", "first", false, ""),
                    new TaskSummary(2, "Deadline", "report", false, "Due Sep 18 2026"),
                    new TaskSummary(3, "Event", "meeting", false, "Mon → Tue")), before);
            assertThrows(UnsupportedOperationException.class, () -> before.clear());
            groot.getResponse("mark 1");
            assertFalse(before.getFirst().isDone());
            assertTrue(groot.getTaskSummaries().getFirst().isDone());
            groot.getResponse("unmark 1");
            for (String command : List.of("find report", "sort", "sort -r", "mark 0", "todo", "unknown")) {
                groot.getResponse(command);
                assertEquals(before, groot.getTaskSummaries());
            }
            assertEquals(before, new Groot().getTaskSummaries());
            Path data = Path.of("data/groot.txt");
            Path backup = Path.of("data/backup.txt");
            Files.move(data, backup);
            Files.createDirectory(data);
            try {
                for (String command : List.of("todo rejected", "mark 1", "delete 2")) {
                    assertTrue(groot.getCommandResponse(command).isError());
                    assertEquals(before, groot.getTaskSummaries());
                }
            } finally {
                Files.delete(data);
                Files.move(backup, data);
            }
            groot.getResponse("delete 1");
            assertEquals("report", groot.getTaskSummaries().getFirst().description());
            assertEquals(1, groot.getTaskSummaries().getFirst().number());
            System.out.println("passed");
        }
    }
}
