package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies recovery, portable file failures, and preservation of existing data. */
class StorageRecoveryTest {
    @TempDir
    Path directory;

    @Test
    void save_emptyList_clearsPreviouslySavedTasks() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Todo("学习 📚")));
        assertEquals("学习 📚", storage.load().getFirst().getDescription());
        storage.save(List.of());
        assertEquals("", Files.readString(file));
        assertTrue(storage.load().isEmpty());
        assertEquals("", storage.getLoadWarning());
        try (var children = Files.list(directory)) {
            assertEquals(List.of(file), children.toList());
        }
    }

    @Test
    void load_invalidFieldCountsAndBooleans_reportsEveryBadLine() throws IOException {
        Path file = directory.resolve("tasks.txt");
        List<String> lines = List.of("TODO|TRUE|bad", "TODO||bad", "DEADLINE|false|missing date",
                "EVENT|false|missing date", "DEADLINE|false|extra|01-01-2027 1200|extra",
                "EVENT|false|extra|01-01-2027 1200|extra", "EVENT|false|empty date|",
                "DEADLINE|false||01-01-2027 1200", "TODO|false|valid", "   ");
        Files.write(file, lines);
        Storage storage = new Storage(file.toString());
        assertEquals(1, storage.load().size());
        assertTrue(storage.getLoadWarning().startsWith(
                "Some tasks could not be loaded because lines 1, 2, 3, 4, 5, 6, 7, 8"));
        assertThrows(IllegalStateException.class, () -> storage.save(List.of()));
        assertEquals(lines, Files.readAllLines(file));
    }

    @Test
    void load_repairedFile_clearsPreviousWarningAndAllowsSave() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken");
        Storage storage = new Storage(file.toString());
        storage.load();
        assertFalse(storage.getLoadWarning().isEmpty());
        Files.writeString(file, "TODO|true|repaired\n");
        assertEquals("x", storage.load().getFirst().getStatusIcon());
        assertEquals("", storage.getLoadWarning());
        storage.save(List.of(new Todo("new task")));
        assertEquals("new task", storage.load().getFirst().getDescription());
    }

    @Test
    void load_brokenSymbolicLink_blocksSaveWithoutReplacingLink() throws IOException {
        Path link = directory.resolve("tasks.txt");
        try {
            Files.createSymbolicLink(link, directory.resolve("missing.txt"));
        } catch (UnsupportedOperationException | IOException | SecurityException exception) {
            assumeTrue(false, "Symbolic links unavailable: " + exception.getMessage());
        }
        Storage storage = new Storage(link.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarning().contains("symbolic link points to a missing file"));
        assertTrue(storage.getLoadWarning().contains("restore the linked file"));
        assertThrows(IllegalStateException.class, () -> storage.save(List.of()));
        assertTrue(Files.isSymbolicLink(link));
    }

    @Test
    void save_destinationIsNonemptyDirectory_cleansTemporaryFileAndPreservesContents() throws IOException {
        Path destination = Files.createDirectory(directory.resolve("tasks.txt"));
        Path original = destination.resolve("original.txt");
        Files.writeString(original, "keep me");
        Storage storage = new Storage(destination.toString());
        UncheckedIOException error = assertThrows(UncheckedIOException.class, () -> {
            storage.save(List.of(new Todo("new task")));
        });
        assertTrue(error.getMessage().startsWith("could not save tasks:"));
        assertEquals("keep me", Files.readString(original));
        try (var children = Files.list(directory)) {
            assertEquals(List.of(destination), children.toList());
        }
    }

    @Test
    void chat_partialLoad_mutationStaysInMemoryAndOriginalFileSurvives() throws IOException {
        Path file = directory.resolve("tasks.txt");
        String original = "TODO|false|valid\nbroken\n";
        Files.writeString(file, original);
        Doe doe = new Doe(file.toString());
        assertTrue(doe.getStartupWarning().startsWith(
                "Some tasks could not be loaded because lines 2"));
        assertTrue(doe.getWelcomeMessage().startsWith("hello! i'm doe"));
        doe.getResponse("todo");
        doe.getResponse("mark");
        String response = doe.getResponse("1");
        assertTrue(response.startsWith("Some tasks could not be loaded because lines 2"));
        assertTrue(response.contains("How to fix:"));
        assertTrue(doe.wasLastResponseWarning());
        assertEquals(original, Files.readString(file));
        doe.getResponse("todo");
        assertTrue(doe.getResponse("view").contains("1. [t][x] valid"));
    }
}
