package trackie.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import trackie.exception.TrackieException;
import trackie.task.Deadline;
import trackie.task.Event;
import trackie.task.Task;
import trackie.task.Todo;
import trackie.task.WithinPeriod;

/** Tests task persistence without writing to Trackie's runtime data file. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws TrackieException {
        Storage storage = new Storage(this.temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(List.of(), storage.loadTasks());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesData() throws TrackieException {
        Task todo = new Todo("read book");
        Task deadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));
        Task event = new Event("project meeting", LocalDate.of(2026, 9, 21),
                LocalDate.of(2026, 9, 22));
        Task withinPeriod = new WithinPeriod("collect certificate", LocalDate.of(2026, 9, 23),
                LocalDate.of(2026, 9, 30));
        deadline.markAsDone();
        List<Task> originalTasks = List.of(todo, deadline, event, withinPeriod);
        Storage storage = new Storage(this.temporaryDirectory.resolve("nested/trackie.txt").toString());

        storage.saveTasks(originalTasks);
        List<String> loadedTaskData = storage.loadTasks().stream()
                .map(Task::toDataString)
                .toList();

        assertEquals(originalTasks.stream().map(Task::toDataString).toList(), loadedTaskData);
    }

    @Test
    public void loadTasks_corruptedStatus_throwsTrackieException() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("trackie.txt");
        Files.writeString(dataFile, "T | complete | read book");
        Storage storage = new Storage(dataFile.toString());

        TrackieException exception = assertThrows(TrackieException.class, storage::loadTasks);

        assertEquals("Oops! Saved task data is corrupted at line 1.", exception.getMessage());
    }

    @Test
    public void loadTasks_invalidStoredDate_throwsTrackieException() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("trackie.txt");
        Files.writeString(dataFile, "D | 0 | submit report | 2026-02-30");
        Storage storage = new Storage(dataFile.toString());

        TrackieException exception = assertThrows(TrackieException.class, storage::loadTasks);

        assertEquals("Oops! Saved task data is corrupted at line 1.", exception.getMessage());
    }
}
