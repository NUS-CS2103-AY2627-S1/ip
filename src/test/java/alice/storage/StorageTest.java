package alice.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alice.exception.AliceException;
import alice.task.Deadline;
import alice.task.Recurrence;
import alice.task.Task;

/** Tests persistence of recurring scheduled tasks. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that a saved recurring deadline retains its recurrence after loading. */
    @Test
    void saveAndLoad_recurringDeadline_preservesRecurrence() throws AliceException {
        Storage storage = new Storage(temporaryDirectory.resolve("Alice.txt"));
        Deadline deadline = new Deadline("pay rent", LocalDate.of(2026, 10, 1));
        deadline.setRecurrence(Recurrence.MONTHLY);

        storage.save(List.of(deadline));
        Task loadedTask = storage.load().getFirst();

        assertEquals("[D][ ] pay rent (by: Oct 01 2026) (repeats: monthly)", loadedTask.toString());
    }
}
