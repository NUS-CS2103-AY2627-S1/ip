package ari;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ari.storage.Storage;

/** Regression checks for rejected commands and recoverable storage failures. */
public class ReliabilityTest {
    @TempDir
    private Path directory;

    @Test
    public void processCommand_failedReplacement_preservesMemoryAndFile() throws IOException {
        Path file = this.directory.resolve("ari.txt");
        FailingStorage storage = new FailingStorage(file);
        Ari ari = new Ari(storage);
        assertFalse(ari.processCommand("todo original").isError());
        String originalFile = Files.readString(file);
        String originalList = ari.getResponse("list");
        storage.shouldFail = true;
        for (String command : new String[] {"mark 1", "delete 1", "sort", "todo rejected"}) {
            CommandResult result = ari.processCommand(command);
            assertTrue(result.isError(), command);
            assertTrue(result.message().contains("Nothing was changed."));
            assertEquals(originalFile, Files.readString(file), command);
            assertEquals(originalList, ari.getResponse("list"), command);
        }
        storage.shouldFail = false;
        assertFalse(ari.processCommand("mark 1").isError());
        assertTrue(Files.readString(file).contains("T | 1 | original"));
        try (var entries = Files.list(this.directory)) {
            assertEquals(1, entries.count());
        }
    }

    @Test
    public void start_corruptFile_protectsOriginalThroughMutationAndExit() throws IOException {
        Path file = this.directory.resolve("ari.txt");
        String original = "T | 0 | valid\nD | 2 | corrupt | Sunday\n";
        Files.writeString(file, original);
        Ari ari = new Ari(file.toString());
        assertTrue(ari.start().contains("Storage is protected"));
        assertTrue(ari.processCommand("todo replacement").isError());
        assertTrue(ari.getResponse("list").contains("tasks were not loaded"));
        assertTrue(ari.processCommand("bye").shouldExit());
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void processCommand_invalidInput_rejectsWithoutEndingSession() {
        Ari ari = new Ari(this.directory.resolve("ari.txt").toString());
        String[] invalid = {"add", "mark 1 extra", "list extra", "bye extra", "exit extra",
            "todo a|b", "todo a\nb", "deadline x /by 2026-02-30",
            "deadline x /by Monday /by Tuesday", "event x /to Tuesday /from Monday",
            "event x /from 2026-09-15 /to 2026-09-15", "event x /from 2026-09-16 /to 2026-09-15"};
        for (String command : invalid) {
            CommandResult result = ari.processCommand(command);
            assertTrue(result.isError(), command);
            assertFalse(result.shouldExit(), command);
        }
        assertFalse(ari.processCommand("deadline x /by whenever I can").isError());
        assertFalse(ari.processCommand("event x /from 2026-09-15 /to 2026-09-16").isError());
    }

    /** Fails before the atomic move, without relying on platform-specific permission behavior. */
    private static class FailingStorage extends Storage {
        private boolean shouldFail;

        FailingStorage(Path file) {
            super(file.toString());
        }

        @Override
        protected void replaceFile(Path temporaryFile) throws IOException {
            if (this.shouldFail) {
                throw new IOException("simulated replacement failure");
            }
            super.replaceFile(temporaryFile);
        }
    }
}
