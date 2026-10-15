package cooper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cooper.storage.NoteStorage;
import cooper.storage.Storage;

/** Covers successful persistence, recovery, and queries through the public command interface. */
public class CooperAdditionalTest {
    @TempDir
    private Path directory;

    @Test
    public void taskLifecycle_restarts_preserveStateAndRenumbering() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Cooper cooper = new Cooper(file.toString());
        assertSuccess(cooper, "todo read book");
        assertSuccess(cooper, "deadline report /by 2026-10-01 09:00");
        assertSuccess(cooper, "event meeting /from 2026-10-02 14:00 /to 2026-10-02 16:00");
        assertSuccess(cooper, "mark 2");

        Cooper restarted = new Cooper(file.toString());
        assertEquals("Here's your flight plan:\n1.[T][ ] read book\n"
                + "2.[D][X] report (by: Oct 01 2026 09:00)\n"
                + "3.[E][ ] meeting (from: Oct 02 2026 14:00 to: Oct 02 2026 16:00)",
                assertSuccess(restarted, "list").message());
        assertSuccess(restarted, "unmark 2");
        assertSuccess(restarted, "delete 1");

        Cooper finalRestart = new Cooper(file.toString());
        assertEquals("Here's your flight plan:\n1.[D][ ] report (by: Oct 01 2026 09:00)\n"
                + "2.[E][ ] meeting (from: Oct 02 2026 14:00 to: Oct 02 2026 16:00)",
                assertSuccess(finalRestart, "list").message());
        assertEquals(List.of("D | 0 | report | 2026-10-01T09:00",
                "E | 0 | meeting | 2026-10-02T14:00 | 2026-10-02T16:00"), Files.readAllLines(file));
    }

    @Test
    public void failedSave_retryOnSameInstance_savesExactlyOnce() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | original\n");
        Storage storage = new Storage(file.toString()) {
            private boolean isFailNext = true;

            @Override
            protected void replaceFile(Path temporary, Path destination) throws IOException {
                if (isFailNext) {
                    isFailNext = false;
                    throw new IOException("Fail only the first replacement");
                }

                super.replaceFile(temporary, destination);
            }
        };
        Cooper cooper = new Cooper(storage, new NoteStorage(file.toString()));
        byte[] before = Files.readAllBytes(file);

        CommandResult failed = cooper.getResponse("todo retry me");
        assertTrue(failed.isError());
        assertFalse(failed.shouldExit());
        assertEquals(Storage.SAVE_FAILED, failed.message());
        assertEquals("Here's your flight plan:\n1.[T][ ] original", assertSuccess(cooper, "list").message());
        assertArrayEquals(before, Files.readAllBytes(file));

        assertSuccess(cooper, "todo retry me");
        String expected = "Here's your flight plan:\n1.[T][ ] original\n2.[T][ ] retry me";
        assertEquals(expected, assertSuccess(cooper, "list").message());
        assertEquals(expected, assertSuccess(new Cooper(file.toString()), "list").message());
        assertEquals(List.of("T | 0 | original", "T | 0 | retry me"), Files.readAllLines(file));
    }

    @Test
    public void bothFilesCorrupted_reportsBothFailuresAndStillExits() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Path notes = directory.resolve("notes.txt");
        Files.writeString(file, "broken tasks\n");
        Files.writeString(notes, "broken notes\n");
        byte[] taskBytes = Files.readAllBytes(file);
        byte[] noteBytes = Files.readAllBytes(notes);
        Cooper cooper = new Cooper(file.toString());

        assertTrue(cooper.getStartupMessage().contains("I couldn't load your tasks."));
        assertTrue(cooper.getStartupMessage().contains("I couldn't load your notes."));
        for (String command : new String[] {"list", "todo overwrite", "note list", "note add overwrite"}) {
            CommandResult result = cooper.getResponse(command);
            assertTrue(result.isError(), command);
            assertFalse(result.shouldExit(), command);
            assertEquals(command.startsWith("note") ? NoteStorage.UNAVAILABLE : Storage.UNAVAILABLE,
                    result.message());
        }

        CommandResult goodbye = cooper.getResponse("bye");
        assertTrue(goodbye.shouldExit());
        assertFalse(goodbye.isError());
        assertArrayEquals(taskBytes, Files.readAllBytes(file));
        assertArrayEquals(noteBytes, Files.readAllBytes(notes));
    }

    @Test
    public void readOnlyCommands_preserveBothFilesAndReturnExpectedResults() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Path notes = directory.resolve("notes.txt");
        Files.writeString(file, "T | 0 | read book\nT | 1 | buy milk\n");
        Files.writeString(notes, "COOPER_NOTES_V1\nN | reading list\nN | shopping list\n");
        byte[] taskBytes = Files.readAllBytes(file);
        byte[] noteBytes = Files.readAllBytes(notes);
        Cooper cooper = new Cooper(file.toString());
        String[][] cases = {
            {"list", "Here's your flight plan:\n1.[T][ ] read book\n2.[T][X] buy milk"},
            {"find book", "Here are the matching tasks in your list:\n1.[T][ ] read book"},
            {"find absent", "No matching tasks found!"},
            {"note list", "Here are your notes:\n1.[N] reading list\n2.[N] shopping list"},
            {"note find SHOPPING", "Here are the matching notes:\n2.[N] shopping list"},
            {"note find absent", "No matching notes found!"}
        };

        for (String[] example : cases) {
            assertEquals(example[1], assertSuccess(cooper, example[0]).message());
            assertArrayEquals(taskBytes, Files.readAllBytes(file), example[0]);
            assertArrayEquals(noteBytes, Files.readAllBytes(notes), example[0]);
        }

        assertEquals(cases[0][1], assertSuccess(cooper, "list").message());
        assertEquals(cases[3][1], assertSuccess(cooper, "note list").message());
    }

    /** Checks the shared success contract without relying on private implementation details. */
    private CommandResult assertSuccess(Cooper cooper, String command) {
        CommandResult result = cooper.getResponse(command);
        assertFalse(result.isError(), command + ": " + result.message());
        assertFalse(result.shouldExit(), command);
        return result;
    }
}
