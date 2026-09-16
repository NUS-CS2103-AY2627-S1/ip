package discitrack.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import discitrack.DisciTrack;
import discitrack.task.Todo;

/**
 * Checks malformed records and repair without risking real saved tasks.
 */
public class StorageCorruptionTest {
    @TempDir
    public Path directory;

    @TestFactory
    public Stream<DynamicTest> malformedRecord_blocksLoadAndWrites_preservesFile() {
        return Stream.of(
                new String[] {"unknown type", "X | 0 | report"},
                new String[] {"invalid status", "T | 2 | report"},
                new String[] {"missing status", "T |  | report"},
                new String[] {"missing description", "T | 0"},
                new String[] {"missing deadline date", "D | 0 | report"},
                new String[] {"missing event end", "E | 0 | camp | 2026-09-30"},
                new String[] {"blank line", ""},
                new String[] {"whitespace line", "   "})
                .map(entry -> DynamicTest.dynamicTest(entry[0], () -> {
                    Path file = Files.createTempDirectory(directory, "record-").resolve("tasks.txt");
                    String original = "T | 0 | before\n" + entry[1] + "\nT | 0 | after\n";
                    Files.writeString(file, original);
                    Storage storage = new Storage(file.toString());
                    IOException error = assertThrows(IOException.class, storage::load);
                    assertTrue(error.getMessage().contains("line 2"));
                    assertThrows(IOException.class, () -> storage.save(List.of(new Todo("overwrite"))));

                    DisciTrack app = new DisciTrack(file.toString());
                    assertTrue(app.hasLoadError());
                    assertTrue(app.getTasks().isEmpty());
                    app.getResponse("todo overwrite");
                    assertTrue(app.hasResponseError());
                    assertEquals(original, Files.readString(file));
                }));
    }

    @Test
    public void repairedFile_restart_restoresNormalCommands() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 2 | report");
        DisciTrack blocked = new DisciTrack(file.toString());
        assertTrue(blocked.hasLoadError());
        blocked.getResponse("help");
        assertFalse(blocked.hasResponseError());
        blocked.getResponse("bye");
        assertTrue(blocked.shouldExit());

        Files.writeString(file, "T | 0 | report");
        DisciTrack repaired = new DisciTrack(file.toString());
        assertFalse(repaired.hasLoadError());
        repaired.getResponse("mark 1");
        assertFalse(repaired.hasResponseError());
        assertTrue(new Storage(file.toString()).load().getFirst().isDone());
    }

    @Test
    public void emptyFile_load_returnsEmptyListWithoutRewriting() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "");
        assertTrue(new Storage(file.toString()).load().isEmpty());
        assertEquals("", Files.readString(file));
    }
}
