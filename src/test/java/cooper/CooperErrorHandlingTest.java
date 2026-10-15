package cooper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cooper.storage.NoteStorage;
import cooper.storage.Storage;

/** Verifies error responses preserve task data and allow subsequent commands. */
public class CooperErrorHandlingTest {
    @TempDir
    private Path directory;

    @Test
    public void invalidCommands_leaveMemoryAndDiskUnchanged() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Cooper cooper = new Cooper(file.toString());
        assertFalse(cooper.getResponse("todo original").isError());
        String before = Files.readString(file);
        String listed = cooper.getResponse("list").message();
        for (String input : new String[] {
            "event meeting /from 2026-10-01", "event meeting /from 2026-10-01 /to 2026-10-01",
            "deadline report /by", "mark 2147483648", "mark 2", "delete 0", "unmark 1 extra",
            "todo  ", "find", "list extra", "bye extra", "dance", "todo first\nsecond"
        }) {
            CommandResult result = cooper.getResponse(input);
            assertTrue(result.isError(), input);
            assertFalse(result.shouldExit(), input);
            assertEquals(listed, cooper.getResponse("list").message(), input);
            assertEquals(before, Files.readString(file), input);
        }
        assertFalse(cooper.getResponse("  MARK\t 1 ").isError());
        assertTrue(cooper.getResponse("bye").shouldExit());
    }

    @Test
    public void failedMutations_preserveMemoryAndFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Cooper initial = new Cooper(file.toString());
        initial.getResponse("todo pending");
        initial.getResponse("todo done");
        initial.getResponse("mark 2");
        Storage failing = new Storage(file.toString()) {
            @Override
            protected void replaceFile(Path temporary, Path destination) throws IOException {
                throw new IOException("Injected replacement failure");
            }
        };
        Cooper cooper = new Cooper(failing, new NoteStorage(file.toString()));
        String before = Files.readString(file);
        String listed = cooper.getResponse("list").message();
        for (String input : new String[] {"todo new", "delete 1", "mark 1", "unmark 2", "mark 2", "unmark 1"}) {
            CommandResult result = cooper.getResponse(input);
            assertTrue(result.isError());
            assertFalse(result.shouldExit());
            assertEquals(Storage.SAVE_FAILED, result.message());
            assertEquals(listed, cooper.getResponse("list").message());
            assertEquals(before, Files.readString(file));
        }
        try (var files = Files.list(directory)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void malformedTaskFile_blocksTasksButAllowsNotesAndExit() throws IOException {
        Path file = directory.resolve("tasks.txt");
        for (String contents : new String[] {
            "T | 0 | original\nD | 0 | bad | nonsense\n", "T | yes | invalid\n",
            "E | 0 | bad | 2026-10-02T00:00 | 2026-10-01T00:00\n", "T | 0 |   \n"
        }) {
            Files.writeString(file, contents);
            Cooper cooper = new Cooper(file.toString());
            assertTrue(cooper.getStartupMessage().contains("I couldn't load your tasks"));
            for (String input : new String[] {"todo overwrite", "mark 1", "delete 1", "list", "find original"}) {
                assertEquals(Storage.UNAVAILABLE, cooper.getResponse(input).message());
                assertTrue(cooper.getResponse(input).isError());
            }
            assertFalse(cooper.getResponse("note add safe").isError());
            assertTrue(cooper.getResponse("bye").shouldExit());
            assertEquals(contents, Files.readString(file));
        }
        Files.writeString(file, "T | 0 | repaired\n");
        assertFalse(new Cooper(file.toString()).getResponse("todo available").isError());
    }

    @Test
    public void descriptionsWithPunctuationAndDuplicates_surviveRestart() {
        String file = directory.resolve("tasks.txt").toString();
        Cooper cooper = new Cooper(file);
        for (String input : new String[] {
            "todo caf? | \\ path  ?", "todo caf? | \\ path  ?",
            "DEADLINE report | draft /by 2000-01-01",
            "Event meeting | team /from 2000-01-01 /to 2000-01-02"
        }) {
            assertFalse(cooper.getResponse(input).isError(), input);
        }
        assertEquals(cooper.getResponse("list").message(), new Cooper(file).getResponse("list").message());
    }
}
