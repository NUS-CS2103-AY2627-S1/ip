package nutrisoy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Task;
import nutrisoy.task.Todo;

/** Exercises storage compatibility and failures using isolated temporary files. */
public class StorageBoundaryTest {
    @TempDir
    private Path directory;

    @Test
    public void load_missingFile_returnsEmptyWithoutCreatingFile() throws DukeException {
        Path file = directory.resolve("missing.txt");
        assertTrue(new Storage(file.toString()).loadTasks().isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    public void partialLoad_saveBlocked_preservesOriginalFile() throws Exception {
        Path file = directory.resolve("partial.txt");
        String original = "T | 0 | read\nbroken\n";
        Files.writeString(file, original);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> recovered = storage.loadTasks();
        assertEquals(1, recovered.size());
        assertTrue(storage.getLoadWarning().contains("line 2"));
        assertThrows(DukeException.class, () -> storage.saveTasks(recovered));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void save_replacement_leavesNoTemporaryFiles() throws Exception {
        Path file = directory.resolve("replace.txt");
        Files.writeString(file, "T | 0 | old\n");
        Storage storage = new Storage(file.toString());
        storage.loadTasks();
        storage.saveTasks(new ArrayList<>(List.of(new Todo("new"))));
        assertEquals(List.of("T | 0 | new"), Files.readAllLines(file));
        try (Stream<Path> paths = Files.list(directory)) {
            assertEquals(List.of(file), paths.toList());
        }
    }

    @Test
    public void load_emptyTrailingTagsAndPaddedFields_preservesTasks() throws Exception {
        Path file = directory.resolve("spaces.txt");
        Files.writeString(file, "T | 0 | read | \nD | 1 | report | 2028-02-29 | \n");
        Storage storage = new Storage(file.toString());
        assertEquals(List.of("T | 0 | read", "D | 1 | report | 2028-02-29"),
                storage.loadTasks().stream().map(Task::toFileFormat).toList());
        assertEquals("", storage.getLoadWarning());
    }

    @Test
    public void save_nestedDirectoryAndEmptyList_createsThenClearsFile() throws Exception {
        Path file = directory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.saveTasks(new ArrayList<>(List.of(new Todo("read"))));
        assertEquals(List.of("T | 0 | read"), Files.readAllLines(file));
        storage.saveTasks(new ArrayList<>());
        assertEquals("", Files.readString(file));
        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void save_parentIsFile_preservesExistingContents() throws IOException {
        Path parent = directory.resolve("blocked");
        Files.writeString(parent, "keep this");
        Storage direct = new Storage(parent.resolve("tasks.txt").toString());
        Storage nested = new Storage(parent.resolve("nested/tasks.txt").toString());
        assertThrows(DukeException.class, () -> direct.saveTasks(new ArrayList<>()));
        assertThrows(DukeException.class, () -> nested.saveTasks(new ArrayList<>()));
        assertEquals("keep this", Files.readString(parent));
    }

    @Test
    public void load_blankLinesAndLegacyDatedRecords_preservesDetails() throws Exception {
        Path file = directory.resolve("legacy.txt");
        Files.writeString(file, "\n  \nD | 1 | report | 2028-02-29\n"
                + "E | 0 | trip | 2026-12-31 | 2027-01-01\n");
        List<Task> tasks = new Storage(file.toString()).loadTasks();
        assertEquals(List.of("D | 1 | report | 2028-02-29",
                "E | 0 | trip | 2026-12-31 | 2027-01-01"),
                tasks.stream().map(Task::toFileFormat).toList());
    }

    @Test
    public void load_oldEmptyTagsBeforeDates_recoversAndRewritesCanonicalFormat() throws Exception {
        Path file = directory.resolve("old.txt");
        Files.writeString(file, "D | 0 | report |  | 2026-09-20\n"
                + "E | 1 | trip |  | 2026-09-20 | 2026-09-21\n");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.loadTasks();
        storage.saveTasks(tasks);
        assertEquals(List.of("D | 0 | report | 2026-09-20",
                "E | 1 | trip | 2026-09-20 | 2026-09-21"), Files.readAllLines(file));
    }

    @TestFactory
    public Stream<DynamicTest> load_malformedRecord_preservesSurroundingValidTasks() {
        return Stream.of("broken", "X | 0 | unknown", "T | 9 | bad status", "T | 0 |  | work",
                "T | 0 | too many | work | extra", "D | 0 | missing date",
                "D | 0 | extra | 2026-09-20 | work | extra", "D | 0 | bad date | 2026-02-30",
                "D | 0 | bad legacy | work | nope", "E | 0 | missing dates",
                "E | 0 | extra | 2026-09-20 | 2026-09-21 | work | extra",
                "E | 0 | bad dates | nope | nope", "E | 0 | bad legacy | work | nope | nope",
                "E | 0 | equal dates | 2026-09-20 | 2026-09-20",
                "E | 0 | reversed | 2026-09-21 | 2026-09-20",
                "T | 0 | bad tag | #work", "T | 0 | duplicate tags | work,WORK",
                "T | 0 | empty tag | work,,school")
                .map(record -> dynamicTest(record, () -> {
                    Path file = Files.createTempFile(directory, "invalid-", ".txt");
                    String contents = "T | 0 | before\n" + record + "\nT | 1 | after\n";
                    Files.writeString(file, contents);
                    List<Task> loaded = new Storage(file.toString()).loadTasks();
                    assertEquals(List.of("T | 0 | before", "T | 1 | after"),
                            loaded.stream().map(Task::toFileFormat).toList());
                    assertEquals(contents, Files.readString(file));
                }));
    }
}
