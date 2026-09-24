package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests legacy compatibility, missing paths, and non-destructive storage errors.
 */
class StorageTest {
    @TempDir
    Path directory;

    @Test
    void loadAndSave_legacyRecords_preserveTypesStateAndOrder() throws IOException {
        Path file = directory.resolve("aravii.txt");
        String original = "TODO\tfalse\tread\t\nDEADLINE\ttrue\treport\t(by: 2026-09-20)\n"
                + "EVENT\tfalse\tmeeting\t(from: 2026-09-21 14:00 to: 2026-09-21 15:00)\n";
        Files.writeString(file, original);
        Storage storage = new Storage(file);
        TaskList tasks = storage.load();
        assertInstanceOf(Todo.class, tasks.get("1"));
        assertInstanceOf(Deadline.class, tasks.get("2"));
        assertInstanceOf(Event.class, tasks.get("3"));
        assertTrue(tasks.get("2").isCompleted());
        storage.save(tasks);
        assertEquals(original, Files.readString(file).replace("\r\n", "\n"));
        assertEquals(tasks.formatAll(), storage.load().formatAll());
    }

    @Test
    void save_missingDirectories_createsPortablePath() throws IOException {
        Storage storage = new Storage(directory.resolve("nested/data/aravii.txt"));
        TaskList tasks = storage.load();
        assertEquals("", tasks.formatAll());
        tasks.add(new Todo("日本語 café"));
        storage.save(tasks);
        assertEquals(tasks.formatAll(), storage.load().formatAll());
    }

    @ParameterizedTest
    @ValueSource(strings = {"broken", "TODO\tmaybe\twork\t", "UNKNOWN\tfalse\twork\t",
        "TODO\tfalse\tone\ttwo\t", "TODO\tfalse\twork\tunexpected",
        "DEADLINE\tfalse\twork\t(by: 2026-02-30)", "EVENT\tfalse\twork\t(from: bad)",
        "EVENT\tfalse\twork\t(from: 2026-09-21 15:00 to: 2026-09-21 14:00)"})
    void load_corruptRecord_preservesWholeOriginal(String badLine) throws IOException {
        Path file = directory.resolve("aravii.txt");
        String original = "TODO\tfalse\tvalid task\t\n" + badLine + "\n";
        Files.writeString(file, original);
        IOException error = assertThrows(IOException.class, () -> new Storage(file).load());
        assertTrue(error.getMessage().contains("line 2"));
        assertEquals(original, Files.readString(file));
    }

    @Test
    void save_replacementFails_preservesTargetAndCleansTemporaryFile() throws IOException {
        Path target = Files.createDirectory(directory.resolve("aravii.txt"));
        Path existing = target.resolve("keep.txt");
        Files.writeString(existing, "keep me");
        assertThrows(IOException.class, () -> new Storage(target).save(new TaskList()));
        assertEquals("keep me", Files.readString(existing));
        try (var children = Files.list(directory)) {
            assertEquals(1, children.count());
        }
    }

    @Test
    void publicMethods_rejectNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Storage(null));
        assertThrows(IllegalArgumentException.class, () -> new Storage(directory.resolve("tasks.txt"))
                .save(null));
    }
}
