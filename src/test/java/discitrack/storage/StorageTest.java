package discitrack.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import discitrack.task.Task;
import discitrack.task.Todo;

/**
 * Checks the optional tag field and compatibility with existing task files.
 */
public class StorageTest {
    @TempDir
    public Path tempDir;

    @Test
    public void loadMixedRecords_roundTrip_preservesLegacyAndTaggedData() throws IOException {
        Path path = tempDir.resolve("tasks.txt");
        List<String> lines = List.of(
                "T | 0 | explain /tag syntax",
                "D | 1 | report | 2026-10-01",
                "E | 0 | meeting | 2026-10-01 | 2026-10-02",
                "T | 0 | slides | tags=School,CS2103T",
                "D | 0 | pay | 2026-10-01 | tags=personal",
                "E | 1 | camp | 2026-10-01 | 2026-10-02 | tags=school");
        Files.write(path, lines);
        String original = Files.readString(path);
        Storage storage = new Storage(path.toString());
        List<Task> tasks = storage.load();
        assertEquals(original, Files.readString(path));
        assertEquals("explain /tag syntax", tasks.get(0).getActivity());
        assertTrue(tasks.get(0).getTags().isEmpty());
        assertEquals(List.of("School", "CS2103T"), tasks.get(3).getTags());
        storage.save(tasks);
        assertEquals(lines, Files.readAllLines(path));
    }

    @Test
    public void loadDuplicates_keepsFirstSpellingAndOrder() throws IOException {
        Path path = tempDir.resolve("tasks.txt");
        Files.writeString(path, "T | 0 | slides | tags=School,CS2103T,SCHOOL");
        Storage storage = new Storage(path.toString());
        List<Task> tasks = storage.load();
        assertEquals(List.of("School", "CS2103T"), tasks.get(0).getTags());
        tasks.get(0).removeTag("school");
        tasks.get(0).removeTag("cs2103t");
        storage.save(tasks);
        assertEquals(List.of("T | 0 | slides"), Files.readAllLines(path));
    }

    @Test
    public void malformedTagFields_rejectWholeLoadAndBlockSaving() throws IOException {
        Path path = tempDir.resolve("tasks.txt");
        for (String suffix : List.of("tags=", "tags=school,", "tags=,school", "tags=school,,urgent",
                "tags=#school", "tags=nus teacher", "tags=" + "a".repeat(31), "", "school",
                "tags=school | tags=urgent")) {
            String original = "T | 0 | valid\nT | 0 | broken | " + suffix;
            Files.writeString(path, original);
            Storage storage = new Storage(path.toString());
            IOException error = assertThrows(IOException.class, storage::load, suffix);
            assertEquals("invalid tag data on line 2", error.getMessage(), suffix);
            assertThrows(IOException.class, () -> storage.save(List.of(new Todo("overwrite"))));
            assertEquals(original, Files.readString(path));
        }
    }

    @Test
    public void missingFile_loadAndSave_createsOnlyWhenSaving() throws IOException {
        Path path = tempDir.resolve("nested/tasks.txt");
        Storage storage = new Storage(path.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(Files.notExists(path));
        Task task = new Todo("sleep");
        task.addTag("personal");
        storage.save(List.of(task));
        assertEquals(List.of("T | 0 | sleep | tags=personal"), Files.readAllLines(path));
    }
}
