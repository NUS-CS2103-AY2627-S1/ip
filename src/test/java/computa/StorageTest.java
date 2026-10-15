package computa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StorageTest {
    @TempDir Path directory;

    @Test void savesAndLoadsAllTaskTypes() throws Exception {
        Storage storage = new Storage(directory.resolve("tasks.txt").toString());
        TaskList original = new TaskList();
        original.addTask(new Todo("todo"));
        original.addTask(new Deadline("deadline", "2026-12-01 1159"));
        original.addTask(new Event("event", "2026-12-01 1200", "2026-12-01 1300"));
        original.addTask(new RecurringTask("weekly", DayOfWeek.MONDAY));
        original.addTask(new RecurringTask("weekly", DayOfWeek.MONDAY,
                LocalDate.of(2026, 9, 14), false));
        storage.saveTasks(original);
        assertEquals(5, storage.load().size());
        assertTrue(storage.load().get(4) instanceof RecurringTask);
    }

    @Test void missingFileLoadsAsEmptyList() throws Exception {
        assertTrue(new Storage(directory.resolve("missing.txt").toString()).load().isEmpty());
    }
}
