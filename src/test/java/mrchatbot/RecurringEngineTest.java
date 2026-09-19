package mrchatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.storage.Storage;
import mrchatbot.task.Recurrence;
import mrchatbot.task.RecurringTask;
import mrchatbot.task.TaskList;

public class RecurringEngineTest {
    @TempDir
    private Path tempDir;

    @Test
    public void getResponse_afterMidnight_refreshesBeforeMarkAndPersists() throws Exception {
        MutableClock clock = new MutableClock();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        MrChatbotEngine engine = new MrChatbotEngine(storage, clock);
        engine.getResponse("recurring exercise /every day");
        engine.getResponse("mark 1");
        assertTrue(storage.loadTasks().get(0).isDone());

        clock.instant = Instant.parse("2026-09-22T00:00:00Z");
        assertTrue(engine.getResponse("find exercise").contains("[R][ ]"));
        assertFalse(storage.loadTasks().get(0).isDone());
        engine.getResponse("mark 1");
        assertTrue(storage.loadTasks().get(0).isDone());
        assertTrue(engine.getResponse("list").contains("next reset: 2026-09-23"));
        engine.getResponse("unmark 1");
        assertFalse(storage.loadTasks().get(0).isDone());
        engine.getResponse("delete 1");
        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void constructor_overdueStoredTask_refreshesAndSavesOnStartup() throws Exception {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        RecurringTask task = new RecurringTask("meeting", Recurrence.WEEK, LocalDate.of(2026, 8, 31));
        task.markAsDone();
        storage.saveTasks(new TaskList(task));

        MrChatbotEngine engine = new MrChatbotEngine(storage, new MutableClock());

        assertNull(engine.getStartupError());
        RecurringTask restored = (RecurringTask) storage.loadTasks().get(0);
        assertFalse(restored.isDone());
        assertEquals(LocalDate.of(2026, 9, 28), restored.getNextResetDate());
    }

    @Test
    public void getResponse_resetSaveFails_retriesBeforeNextCommand() throws Exception {
        FailingStorage storage = new FailingStorage(tempDir.resolve("tasks.txt"));
        MutableClock clock = new MutableClock();
        MrChatbotEngine engine = new MrChatbotEngine(storage, clock);
        engine.getResponse("recurring exercise /every day");
        engine.getResponse("mark 1");
        clock.instant = Instant.parse("2026-09-22T00:00:00Z");
        storage.shouldFail = true;

        assertEquals("Save failed", engine.getResponse("list"));
        assertTrue(storage.loadTasks().get(0).isDone());
        storage.shouldFail = false;
        assertTrue(engine.getResponse("list").contains("[R][ ]"));
        assertFalse(storage.loadTasks().get(0).isDone());
    }

    @Test
    public void getResponse_markAtResetBoundary_completesNewOccurrence() throws Exception {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        MutableClock clock = new MutableClock();
        MrChatbotEngine engine = new MrChatbotEngine(storage, clock);
        engine.getResponse("recurring meeting /every week /on monday");
        engine.getResponse("mark 1");
        clock.instant = Instant.parse("2026-10-05T00:00:00Z");

        engine.getResponse("mark 1");

        RecurringTask saved = (RecurringTask) storage.loadTasks().get(0);
        assertTrue(saved.isDone());
        assertEquals(LocalDate.of(2026, 10, 12), saved.getNextResetDate());
        MrChatbotEngine restarted = new MrChatbotEngine(storage, clock);
        assertTrue(restarted.getResponse("list").contains("[R][X]"));
    }

    @Test
    public void constructor_resetSaveFails_retainsLoadedTasksAndRetries() throws Exception {
        FailingStorage storage = new FailingStorage(tempDir.resolve("tasks.txt"));
        RecurringTask task = new RecurringTask("meeting", Recurrence.WEEK, LocalDate.of(2026, 9, 21));
        task.markAsDone();
        storage.saveTasks(new TaskList(task));
        storage.shouldFail = true;

        MrChatbotEngine engine = new MrChatbotEngine(storage, new MutableClock());

        assertEquals("Save failed", engine.getStartupError());
        storage.shouldFail = false;
        assertTrue(engine.getResponse("list").contains("[R][ ] meeting"));
        assertFalse(storage.loadTasks().get(0).isDone());
    }

    /**
     * Advances the calendar without sleeping or changing the machine's clock.
     */
    private static class MutableClock extends Clock {
        private Instant instant = Instant.parse("2026-09-21T00:00:00Z");

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.fixed(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    /**
     * Simulates a temporary save failure while keeping normal disk loading available.
     */
    private static class FailingStorage extends Storage {
        private boolean shouldFail;

        FailingStorage(Path path) {
            super(path.toString());
        }

        @Override
        public void saveTasks(TaskList tasks) throws MrChatbotException {
            if (shouldFail) {
                throw new MrChatbotException("Save failed");
            }
            super.saveTasks(tasks);
        }
    }
}
