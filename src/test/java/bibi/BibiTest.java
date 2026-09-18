package bibi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the entry points the GUI uses, which return what Bibi would have said
 * instead of printing it. The console loop is covered by the scripted UI tests
 * in test/ui-test-plan.md, which can drive standard input; these cannot.
 */
public class BibiTest {

    @Test
    public void getResponse_addTodo_confirmsAndCounts(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        String response = bibi.getResponse("todo borrow book").text();

        assertTrue(response.contains("Added. Do or do not. There is no try:"));
        assertTrue(response.contains("[T][ ] borrow book"));
        // Singular, because "1 tasks" is the kind of wrongness users notice.
        assertTrue(response.contains("Your list holds 1 task."));
    }

    @Test
    public void getResponse_reply_carriesNoSpeakerPrefix(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        // The window shows Bibi's picture beside every reply, so the console's
        // "Bibi: " prefix would only be noise repeated down the transcript.
        assertFalse(bibi.getResponse("list").text().contains("Bibi:"));
    }

    @Test
    public void getResponse_unknownCommand_explainsRatherThanThrowing(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        // The GUI has nowhere useful to send an exception, so a rejected command
        // has to come back as ordinary words.
        assertTrue(bibi.getResponse("blah").text().contains("I don't recognize 'blah'"));
    }

    @Test
    public void getResponse_consecutiveCommands_eachReplyStandsAlone(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));
        bibi.getResponse("todo borrow book");

        String response = bibi.getResponse("list").text();

        // Each call starts a fresh capture, so nothing from the first reply may
        // leak into the second.
        assertFalse(response.contains("Added."));
        assertTrue(response.contains("1. [T][ ] borrow book"));
    }

    @Test
    public void getResponse_changingCommand_writesTheSaveFile(@TempDir Path tempDir)
            throws IOException {
        Path saveFile = tempDir.resolve("bibi.txt");

        new Bibi(saveFile).getResponse("todo borrow book");

        assertEquals("T | 0 | borrow book", Files.readString(saveFile).strip());
    }

    @Test
    public void isExitRequested_beforeAndAfterBye_reportsTheSession(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));
        bibi.getResponse("list");

        assertFalse(bibi.isExitRequested());

        String farewell = bibi.getResponse("bye").text();

        assertTrue(farewell.contains("See you."));
        assertTrue(bibi.isExitRequested());
    }

    @Test
    public void getGreeting_noSaveFile_welcomesWithoutTheBanner(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        String greeting = bibi.getGreeting().text();

        assertTrue(greeting.contains("I'm Bibi."));
        // The banner is drawn out of spaced letters and only lines up in a
        // fixed-width font, so the GUI leaves it to the console.
        assertFalse(greeting.contains("B B B B"));
        assertFalse(greeting.contains("Picked up where we left off"));
    }

    @Test
    public void getGreeting_existingSaveFile_reportsWhatWasRestored(@TempDir Path tempDir)
            throws IOException {
        Path saveFile = tempDir.resolve("bibi.txt");
        Files.writeString(saveFile, "T | 0 | borrow book\nT | 1 | return book\n");

        String greeting = new Bibi(saveFile).getGreeting().text();

        assertTrue(greeting.contains("Picked up where we left off: 2 tasks restored."));
    }

    @Test
    public void getGreeting_damagedSaveFile_warnsAndKeepsGoing(@TempDir Path tempDir)
            throws IOException {
        Path saveFile = tempDir.resolve("bibi.txt");
        Files.writeString(saveFile, "T | 0 | borrow book\nnonsense\n");

        Bibi bibi = new Bibi(saveFile);
        String greeting = bibi.getGreeting().text();

        assertTrue(greeting.contains("I couldn't read these lines in the save file:"));
        assertTrue(bibi.getResponse("list").text().contains("1. [T][ ] borrow book"));
    }

    @Test
    public void getResponse_help_listsEveryCommand(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        String response = bibi.getResponse("help").text();

        // Ui.showDetails takes the whole list as varargs, so one missing entry
        // would be a silently dropped argument rather than a missing call.
        for (String commandWord : new String[] {
            "todo", "deadline", "event", "list", "sort", "find", "on", "mark", "unmark",
            "remove|delete", "undo", "help", "bye", "hi", "hello", "hey", "thanks"}) {
            assertTrue(response.contains(commandWord), "help omitted " + commandWord);
        }
    }

    @Test
    public void getResponse_sort_reordersAndPersists(@TempDir Path tempDir) {
        Path saveFile = tempDir.resolve("bibi.txt");
        Bibi bibi = new Bibi(saveFile);
        bibi.getResponse("todo borrow book");
        bibi.getResponse("deadline submit report /by 2019-12-01");
        bibi.getResponse("deadline pay fees /by 2019-10-15");

        String response = bibi.getResponse("sort").text();

        assertTrue(response.contains("Sorted it, but not your life. Earliest first:"));
        assertTrue(response.contains("1. [D][ ] pay fees"));
        assertTrue(response.contains("2. [D][ ] submit report"));
        // The undated task goes last, not first.
        assertTrue(response.contains("3. [T][ ] borrow book"));

        // A new session proves the order was saved rather than only displayed.
        // getGreeting is what loads the file, which is how the GUI starts up.
        Bibi reopened = new Bibi(saveFile);
        reopened.getGreeting();
        assertTrue(reopened.getResponse("list").text().contains("1. [D][ ] pay fees"));
    }

    @Test
    public void getResponse_sortEmptyList_saysThereIsNothingToSort(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        assertTrue(bibi.getResponse("sort").text().contains("Nothing to sort yet"));
    }

    @Test
    public void getResponse_successfulCommand_notFlaggedAsError(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        assertFalse(bibi.getResponse("todo borrow book").isError());
        assertFalse(bibi.getResponse("list").isError());
    }

    @Test
    public void getResponse_rejectedCommand_flaggedAsError(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        // The GUI styles these apart, so the flag is what makes an error look
        // like one rather than like another confirmation.
        assertTrue(bibi.getResponse("blah").isError());
        assertTrue(bibi.getResponse("mark 0").isError());
        assertTrue(bibi.getResponse("list extra").isError());
    }

    @Test
    public void getResponse_duplicateTask_flaggedAsError(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));
        bibi.getResponse("todo borrow book");

        assertTrue(bibi.getResponse("todo borrow book").isError());
    }

    @Test
    public void getGreeting_cleanStart_notFlaggedAsError(@TempDir Path tempDir) {
        assertFalse(new Bibi(tempDir.resolve("bibi.txt")).getGreeting().isError());
    }

    @Test
    public void getGreeting_damagedSaveFile_flaggedAsError(@TempDir Path tempDir)
            throws IOException {
        Path saveFile = tempDir.resolve("bibi.txt");
        Files.writeString(saveFile, "T | 0 | borrow book\nnonsense\n");

        // A save file that could not be read fully is worth catching the eye.
        assertTrue(new Bibi(saveFile).getGreeting().isError());
    }

    @Test
    public void isLastCommandRejected_rejectedThenSuccessful_resetsForEachCommand(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));

        bibi.getResponse("deadline unfinished /by");

        assertTrue(bibi.isLastCommandRejected());

        bibi.getResponse("list");

        assertFalse(bibi.isLastCommandRejected());
    }

    @Test
    public void isLastCommandRejected_duplicateAndInvalidNumber_keepsInputCorrectable(@TempDir Path tempDir) {
        Bibi bibi = new Bibi(tempDir.resolve("bibi.txt"));
        bibi.getResponse("todo borrow book");

        bibi.getResponse("todo borrow book");

        assertTrue(bibi.isLastCommandRejected());

        bibi.getResponse("remove 2");

        assertTrue(bibi.isLastCommandRejected());
        assertTrue(bibi.getResponse("list").text().contains("1. [T][ ] borrow book"));
    }

    @Test
    public void isLastCommandRejected_failedSave_keepsAppliedChangeDistinct(@TempDir Path tempDir)
            throws IOException {
        // A directory cannot be overwritten as a file, regardless of platform permissions.
        Path saveFile = Files.createDirectory(tempDir.resolve("bibi.txt"));
        Bibi bibi = new Bibi(saveFile);

        Reply reply = bibi.getResponse("todo borrow book");

        assertTrue(reply.isError());
        assertFalse(bibi.isLastCommandRejected());
        assertTrue(bibi.getResponse("list").text().contains("1. [T][ ] borrow book"));
        assertTrue(Files.isDirectory(saveFile));
    }
}
