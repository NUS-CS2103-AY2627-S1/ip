package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Regression tests for input that would corrupt saved records and failed save replacement.
 */
public class PersistenceSafetyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void parseTask_pipeInDescription_rejectsBeforeSaving() {
        Parser parser = new Parser();
        assertThrows(YapperException.class, () -> parser.parseTodo("todo read | book"));
        assertThrows(YapperException.class, () -> parser.parseDeadline("deadline a|b /by 2026-10-14"));
        assertThrows(YapperException.class, () ->
                parser.parseEvent("event a | b /from 2026-10-14 /to 2026-10-15"));
    }

    @Test
    public void save_replacementFails_preservesExistingContentsAndRemovesTemporaryFile() throws IOException {
        Path destination = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        Path existing = destination.resolve("keep.txt");
        Files.writeString(existing, "original");
        assertThrows(IOException.class, () -> new Storage(destination).save(List.of(new Todo("book"))));
        assertEquals("original", Files.readString(existing));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(destination), files.toList());
        }
    }
}
