package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the methods a window holds its conversation through —
 * {@link Bob#getGreeting()}, {@link Bob#getResponse}, {@link Bob#isExit()},
 * {@link Bob#isLastResponseError()}, and {@link Bob#hasUnsavedChanges()} and
 * {@link Bob#getResponseToClosing()} for when the window is closed.
 *
 * <p>The rest of this class is tested through the text UI test plan, which runs
 * the console chatbot end to end and checks everything it prints. These three
 * cannot be: they print nothing at all. What they do instead — collect the lines
 * of one answer, hand them back, and start again empty for the next — is the whole
 * of what the window depends on, and none of it is reached when the program is run
 * at a console.
 *
 * <p>The mistake worth guarding against here is a chatbot that never forgets: if
 * an answer were not cleared once it had been handed over, every reply would
 * arrive with the whole conversation so far stuck to the front of it. That is what
 * {@link #getResponse_severalCommands_eachAnswerHeldOnItsOwn()} is for.
 *
 * <p>Every test gets a save file in a folder of its own, made and thrown away by
 * JUnit, so none of them touches the real one.
 */
public class BobTest {

    /** A folder to keep each test's save file in, thrown away after the test. */
    @TempDir
    private Path tempDirectory;

    @Test
    public void getGreeting_noSaveFile_greetsWithoutMentioningTasks() {
        String greeting = bobWithSavedLines().getGreeting();

        // No banner: it is drawn in columns, which a window's font would not keep.
        assertEquals("Bello! Me Bob!\nWat yu want Bob do? Banana?", greeting);
    }

    @Test
    public void getGreeting_savedTasks_reportsWhatWasPickedUp() {
        String greeting = bobWithSavedLines("T | 0 | read book", "T | 1 | return book").getGreeting();

        assertTrue(greeting.startsWith("Bello! Me Bob!"));
        assertTrue(greeting.contains("Bob found 2 tasks from before."));
    }

    @Test
    public void getResponse_addThenList_taskAddedAndListedBack() {
        Bob bob = bobWithSavedLines();

        String added = bob.getResponse("todo read book");
        String listed = bob.getResponse("list");

        assertEquals("Okay! Bob add dis:\n  [T][ ] read book\n"
                + "Now yu have 1 task in da list.", added);
        assertEquals("Luk at tu! Here da tasks:\n1.[T][ ] read book", listed);
    }

    @Test
    public void getResponse_severalCommands_eachAnswerHeldOnItsOwn() {
        Bob bob = bobWithSavedLines();
        bob.getGreeting();
        bob.getResponse("todo read book");

        String second = bob.getResponse("todo return book");

        // Nothing said earlier — not the greeting, not the first confirmation —
        // may still be hanging around in front of this answer.
        assertTrue(second.startsWith("Okay!"));
        assertFalse(second.contains("Bello!"));
        assertFalse(second.contains("read book"));
    }

    @Test
    public void getResponse_commandThatCannotBeCarriedOut_explanationReturnedNotThrown() {
        Bob bob = bobWithSavedLines();

        String response = bob.getResponse("sing me a song");

        // A window has nowhere to put an exception, so the complaint comes back as
        // the answer and the conversation carries on.
        assertTrue(response.contains("sing"));
        assertFalse(bob.isExit());
    }

    @Test
    public void isLastResponseError_commandThatCannotBeCarriedOut_reportedAsAnError() {
        Bob bob = bobWithSavedLines();

        bob.getResponse("sing me a song");

        // The window colors a complaint differently from an answer, and this is the
        // only thing telling it which it has been given: the text of the two is
        // just as much a line of English either way.
        assertTrue(bob.isLastResponseError());
    }

    @Test
    public void isLastResponseError_commandThatWorked_notReportedAsAnError() {
        Bob bob = bobWithSavedLines();

        bob.getResponse("todo read book");

        assertFalse(bob.isLastResponseError());
    }

    @Test
    public void isLastResponseError_errorThenGoodCommand_forgottenOnTheNextAnswer() {
        Bob bob = bobWithSavedLines();
        bob.getResponse("sing me a song");

        bob.getResponse("list");

        // The flag describes the answer just given, not the worst answer so far.
        // Were it never cleared, every bubble after the user's first typing mistake
        // would be colored as a complaint for the rest of the conversation.
        assertFalse(bob.isLastResponseError());
    }

    @Test
    public void isLastResponseError_beforeAnyCommand_notAnError() {
        Bob bob = bobWithSavedLines();

        // The greeting is not an answer to anything, so there is nothing to
        // complain about yet.
        bob.getGreeting();

        assertFalse(bob.isLastResponseError());
    }

    @Test
    public void isLastResponseError_taskNumberThatNamesNoTask_reportedAsAnError() {
        Bob bob = bobWithSavedLines("T | 0 | read book");

        // Not every error comes from the parser: this one is only an error because
        // of how long the list happens to be, so it is thrown further in. It has to
        // reach the window colored the same as any other.
        bob.getResponse("mark 7");

        assertTrue(bob.isLastResponseError());
    }

    @Test
    public void getResponse_emptyLine_answeredRatherThanIgnored() {
        // The window declines to send an empty box, but a line of only spaces
        // reaches here as an empty one, and is answered like any other bad input.
        String response = bobWithSavedLines().getResponse("");

        // Every complaint opens with the same line, so the explanation comes second.
        assertTrue(response.startsWith("MINION EMERGENCY!\nYou didn't type anything."));
    }

    @Test
    public void getResponse_spacesAroundLine_readAsTheCommandInside() {
        Bob bob = bobWithSavedLines();

        // getResponse is public, so its line need not come from the window, which
        // trims its own; the line is trimmed where it is parsed, for every caller.
        String response = bob.getResponse("  list  ");

        assertEquals("No tasks yet! Bob bored...", response);
        assertFalse(bob.isLastResponseError());
    }

    @Test
    public void getResponse_changedList_writtenToTheSaveFile() throws IOException {
        Bob bob = bobWithSavedLines();

        bob.getResponse("todo read book");

        // The window's chatbot saves as the console's does; a task added in one
        // session has to be there in the next.
        assertEquals("T | 0 | read book", Files.readString(saveFile(), StandardCharsets.UTF_8).strip());
    }

    @Test
    public void getResponse_taskAlreadyInTheList_refusedAsAnErrorAndNotSaved() throws IOException {
        Bob bob = bobWithSavedLines("T | 0 | read book");

        String response = bob.getResponse("todo Read  Book");

        // The task already there is shown as it is, not as the new line was typed.
        assertEquals("MINION EMERGENCY!\nYu already have dis as task 1: [T][ ] read book", response);
        assertTrue(bob.isLastResponseError());
        assertEquals("T | 0 | read book", Files.readString(saveFile(), StandardCharsets.UTF_8).strip());
    }

    @Test
    public void getResponse_markTaskAlreadyDone_answeredNotAsAnErrorAndNothingSaved() throws IOException {
        // Written without the spaces a save puts around each bar, so a file still
        // written this way afterwards shows that nothing was saved.
        Bob bob = bobWithSavedLines("T|1|read book");

        String response = bob.getResponse("mark 1");

        assertEquals("Dis one already finish! Nothing to change:\n  [T][X] read book", response);
        assertFalse(bob.isLastResponseError());
        assertEquals("T|1|read book", Files.readString(saveFile(), StandardCharsets.UTF_8).strip());
    }

    @Test
    public void getResponse_unmarkTaskNotDone_answeredNotAsAnErrorAndNothingSaved() throws IOException {
        Bob bob = bobWithSavedLines("T|0|read book");

        String response = bob.getResponse("unmark 1");

        assertEquals("Dis one not finish yet! Nothing to change:\n  [T][ ] read book", response);
        assertFalse(bob.isLastResponseError());
        assertEquals("T|0|read book", Files.readString(saveFile(), StandardCharsets.UTF_8).strip());
    }

    @Test
    public void isExit_onlyAfterGoodbye() {
        Bob bob = bobWithSavedLines();

        assertFalse(bob.isExit());
        bob.getResponse("list");
        assertFalse(bob.isExit());

        String farewell = bob.getResponse("bye");

        assertEquals("Poopaye! Bob go find banana, see yu soon!", farewell);
        assertTrue(bob.isExit());
    }

    @Test
    public void getResponse_byeWithChangeThatStillCannotBeSaved_heldBackOnceThenQuits() throws IOException {
        Bob bob = bobWithFolderForSaveFile();
        bob.getResponse("todo read book");

        String firstBye = bob.getResponse("bye");

        String expectedWarning = "MINION EMERGENCY!\nI couldn't save your tasks to " + saveFile()
                + " because it is a folder, not a file."
                + "\nThe change is in this session's list, but it won't survive quitting."
                + "\nFix that and type bye to try again."
                + " If it still can't be saved, bye will quit without saving.";
        assertEquals(expectedWarning, firstBye);
        assertTrue(bob.isLastResponseError());
        // The window stays open, so the user can still fix the problem.
        assertFalse(bob.isExit());

        String secondBye = bob.getResponse("bye");

        assertEquals("Bob quit without saving yur changes. Dey gone!\n"
                + "Poopaye! Bob go find banana, see yu soon!", secondBye);
        assertFalse(bob.isLastResponseError());
        assertTrue(bob.isExit());
    }

    @Test
    public void getResponse_byeAfterProblemFixed_changeSavedAndQuits() throws IOException {
        Bob bob = bobWithFolderForSaveFile();
        bob.getResponse("todo read book");
        bob.getResponse("bye");
        Files.delete(saveFile());

        String response = bob.getResponse("bye");

        assertEquals("Your changes are saved now.\nPoopaye! Bob go find banana, see yu soon!", response);
        assertTrue(bob.isExit());
        assertEquals("T | 0 | read book", Files.readString(saveFile(), StandardCharsets.UTF_8).strip());
    }

    @Test
    public void getResponse_byeWhenProblemFixedBeforeIt_savedWithoutHoldingBack() throws IOException {
        Bob bob = bobWithFolderForSaveFile();
        bob.getResponse("todo read book");
        Files.delete(saveFile());

        String response = bob.getResponse("bye");

        assertEquals("Your changes are saved now.\nPoopaye! Bob go find banana, see yu soon!", response);
        assertTrue(bob.isExit());
        assertEquals("T | 0 | read book", Files.readString(saveFile(), StandardCharsets.UTF_8).strip());
    }

    @Test
    public void hasUnsavedChanges_saveFailed_trueUntilASaveWorks() throws IOException {
        Bob bob = bobWithFolderForSaveFile();
        assertFalse(bob.hasUnsavedChanges());

        bob.getResponse("todo read book");
        assertTrue(bob.hasUnsavedChanges());

        Files.delete(saveFile());
        bob.getResponse("todo return book");
        assertFalse(bob.hasUnsavedChanges());
    }

    @Test
    public void getResponseToClosing_changeThatStillCannotBeSaved_answeredAsByeIs() throws IOException {
        Bob bob = bobWithFolderForSaveFile();
        bob.getResponse("todo read book");

        String firstClose = bob.getResponseToClosing();

        // Closing the window is a goodbye, so the first try is held back just as a
        // first bye is, and the window stays open to show why.
        assertTrue(firstClose.startsWith("MINION EMERGENCY!\nI couldn't save your tasks to "));
        assertTrue(bob.isLastResponseError());
        assertFalse(bob.isExit());

        String secondClose = bob.getResponseToClosing();

        assertEquals("Bob quit without saving yur changes. Dey gone!\n"
                + "Poopaye! Bob go find banana, see yu soon!", secondClose);
        assertTrue(bob.isExit());
    }

    @Test
    public void getResponseToClosing_problemFixedBeforeClosing_changeSavedAndQuits() throws IOException {
        Bob bob = bobWithFolderForSaveFile();
        bob.getResponse("todo read book");
        Files.delete(saveFile());

        String response = bob.getResponseToClosing();

        assertEquals("Your changes are saved now.\nPoopaye! Bob go find banana, see yu soon!", response);
        assertFalse(bob.hasUnsavedChanges());
        assertTrue(bob.isExit());
        assertEquals("T | 0 | read book", Files.readString(saveFile(), StandardCharsets.UTF_8).strip());
    }

    /** Returns the save file this test's chatbots use. */
    private Path saveFile() {
        return tempDirectory.resolve("bob.txt");
    }

    /**
     * Returns a chatbot for a window whose save file's place is taken by a folder, so
     * that every save fails until the folder is deleted.
     */
    private Bob bobWithFolderForSaveFile() throws IOException {
        Files.createDirectory(saveFile());
        return Bob.forGui(saveFile());
    }

    /**
     * Returns a chatbot for a window, started on a save file holding the given
     * lines — or on no save file at all when given none, which is the first run.
     */
    private Bob bobWithSavedLines(String... lines) {
        try {
            if (lines.length > 0) {
                Files.write(saveFile(), List.of(lines), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new AssertionError("could not write the test's save file", e);
        }
        return Bob.forGui(saveFile());
    }
}
