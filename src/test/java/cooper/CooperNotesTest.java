package cooper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cooper.parser.NoteParser;
import cooper.storage.NoteStorage;

/** Exercises exact note responses, task compatibility, and transaction failure behavior. */
public class CooperNotesTest {
    @TempDir
    private Path directory;

    @Test
    public void commands_fullLifecycle_returnsExactResponsesAndPersists() {
        String path = directory.resolve("cooper.txt").toString();
        Cooper cooper = new Cooper(path);
        assertReply(cooper, "note list", "Your mission notebook is empty. Start with: note add <text>");
        assertReply(cooper, "note add Waist size: 32", "Logged in the mission notebook. I've added this note:\n"
                + "1.[N] Waist size: 32\nNow you have 1 note in the list.");
        assertReply(cooper, "note add Watch Arrival", "Logged in the mission notebook. I've added this note:\n"
                + "2.[N] Watch Arrival\nNow you have 2 notes in the list.");
        assertReply(cooper, "note list", "Here are your notes:\n1.[N] Waist size: 32\n2.[N] Watch Arrival");
        assertReply(cooper, "note find ARRIVAL", "Here are the matching notes:\n2.[N] Watch Arrival");
        assertReply(cooper, "note find missing", "No matching notes found!");
        assertReply(cooper, "note edit 02 Watch Spirited Away", "Got it. I've updated this note:\n"
                + "2.[N] Watch Spirited Away");
        assertReply(cooper, "note edit 2 Watch Spirited Away", "Got it. I've updated this note:\n"
                + "2.[N] Watch Spirited Away");
        assertReply(cooper, "note delete 1", "Noted. I've removed this note:\n"
                + "1.[N] Waist size: 32\nNow you have 1 note in the list.");
        Cooper restarted = new Cooper(path);
        assertReply(restarted, "note list", "Here are your notes:\n1.[N] Watch Spirited Away");
        assertReply(restarted, "note delete 1", "Noted. I've removed this note:\n"
                + "1.[N] Watch Spirited Away\nNow you have 0 notes in the list.");
        assertReply(new Cooper(path), "note list", "Your mission notebook is empty. Start with: note add <text>");
        assertTrue(restarted.getResponse("bye").shouldExit());
    }

    @Test
    public void notesAndTasks_stayIndependentAcrossRestarts() throws IOException {
        Path taskFile = directory.resolve("custom.txt");
        String original = "T | 0 | original\nD | 1 | due | 2026-08-30T23:59\n"
                + "E | 0 | event | 2026-08-30T14:00 | 2026-08-30T16:00\n";
        Files.writeString(taskFile, original);
        Cooper cooper = new Cooper(taskFile.toString());
        assertEquals("Cooper here, your task co-pilot. What's our next mission?", cooper.getStartupMessage());
        cooper.getResponse("note add original");
        cooper.getResponse("note add original");
        assertEquals(original, Files.readString(taskFile));
        assertFalse(cooper.getResponse("list").message().contains("[N]"));
        assertFalse(cooper.getResponse("find original").message().contains("[N]"));
        String notesData = Files.readString(directory.resolve("notes.txt"));
        cooper.getResponse("mark 1");
        cooper.getResponse("unmark 1");
        cooper.getResponse("delete 1");
        assertEquals(notesData, Files.readString(directory.resolve("notes.txt")));
        assertReply(new Cooper(taskFile.toString()), "note list",
                "Here are your notes:\n1.[N] original\n2.[N] original");
    }

    @Test
    public void invalidCommands_doNotMutateSavedOrActiveNotes() throws IOException {
        Cooper cooper = new Cooper(directory.resolve("cooper.txt").toString());
        cooper.getResponse("note add original");
        String data = Files.readString(directory.resolve("notes.txt"));
        assertReply(cooper, "note delete 2", "I couldn't find a note with that number :(");
        assertReply(cooper, "note edit 2 replacement", "I couldn't find a note with that number :(");
        assertReply(cooper, "note list extra", NoteParser.USAGE);
        assertReply(cooper, "note delete 2147483648", NoteParser.INVALID_NUMBER);
        assertReply(cooper, "note add", "I need some text for your note!");
        assertReply(cooper, "note edit 1", "I need some text for your note!");
        assertReply(cooper, "note find", "I need a keyword to find matching notes!");
        assertReply(cooper, "note add first\nsecond", "I need each note command on a single line!");
        assertReply(cooper, "note delete -1", "I need a valid positive note number!");
        assertEquals(data, Files.readString(directory.resolve("notes.txt")));
        assertReply(cooper, "note list", "Here are your notes:\n1.[N] original");
    }

    @Test
    public void corruptedNotes_disableNotesButAllowTaskCommandsAndSyntaxErrors() throws IOException {
        Path notesFile = directory.resolve("notes.txt");
        String damaged = "COOPER_NOTES_V1\nN | valid\ninvalid\n";
        Files.writeString(notesFile, damaged);
        Cooper cooper = new Cooper(directory.resolve("cooper.txt").toString());
        assertEquals("Cooper here, your task co-pilot. What's our next mission?\n\n"
                + "I couldn't load your notes. Notes are unavailable until you fix the notes file "
                + "and restart me. Your tasks are still available.", cooper.getStartupMessage());
        for (String input : new String[] {"note list", "note find valid", "note add new",
            "note edit 1 replacement", "note delete 1"}) {
            assertReply(cooper, input, NoteStorage.UNAVAILABLE);
        }
        assertReply(cooper, "note", NoteParser.USAGE);
        assertReply(cooper, "note delete 0", NoteParser.INVALID_NUMBER);
        assertTrue(cooper.getResponse("todo task still works").message().contains("I've added this task"));
        assertEquals(damaged, Files.readString(notesFile));
        Files.writeString(notesFile, "COOPER_NOTES_V1\nN | repaired\n");
        assertReply(cooper, "note list", NoteStorage.UNAVAILABLE);
        assertReply(new Cooper(directory.resolve("cooper.txt").toString()), "note list",
                "Here are your notes:\n1.[N] repaired");
    }

    @Test
    public void failedMutations_preserveMemoryAndSavedCollection() throws IOException {
        String taskPath = directory.resolve("cooper.txt").toString();
        new Cooper(taskPath).getResponse("note add original");
        String original = Files.readString(directory.resolve("notes.txt"));
        NoteStorage failing = new NoteStorage(taskPath) {
            @Override
            protected void replaceFile(Path temporary, Path destination) throws IOException {
                throw new IOException("Simulated replacement failure");
            }
        };
        Cooper cooper = new Cooper(taskPath, failing);
        for (String input : new String[] {"note add new", "note edit 1 replacement", "note delete 1"}) {
            assertReply(cooper, input, NoteStorage.SAVE_FAILED);
            assertReply(cooper, "note list", "Here are your notes:\n1.[N] original");
            assertEquals(original, Files.readString(directory.resolve("notes.txt")));
        }
    }

    private void assertReply(Cooper cooper, String input, String expected) {
        CommandResult response = cooper.getResponse(input);
        assertEquals(expected, response.message(), input);
        assertFalse(response.shouldExit(), input);
    }

    @Test
    public void failedFirstSave_doesNotPublishCandidate() throws IOException {
        String taskPath = directory.resolve("cooper.txt").toString();
        Cooper cooper = new Cooper(taskPath);
        // A directory at the destination deterministically prevents replacement by the candidate file.
        Path destination = directory.resolve("notes.txt");
        Files.createDirectory(destination);
        Files.writeString(destination.resolve("keep.txt"), "keep");
        assertReply(cooper, "note add new", NoteStorage.SAVE_FAILED);
        assertReply(cooper, "note list", "Your mission notebook is empty. Start with: note add <text>");
        assertEquals("keep", Files.readString(destination.resolve("keep.txt")));
    }
}
