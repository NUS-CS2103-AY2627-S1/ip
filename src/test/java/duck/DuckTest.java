package duck;

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
 * Tests Duck's graphical-interface response boundary.
 */
public class DuckTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getWelcomeMessage_missingDataFile_returnsExistingGreeting() {
        Duck duck = createDuck();

        assertEquals("Quack! I'm Duck. What shall we get done today? "
                + "Try: todo read a book, list, or find book. "
                + "Type help for every command.",
                duck.getWelcomeMessage());
    }

    @Test
    public void getWelcomeMessage_invalidDataFile_includesLoadingError() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Files.writeString(dataFile, "D | 0 | impossible | 2026-02-30\n",
                StandardCharsets.UTF_8);

        Duck duck = new Duck(dataFile.toString());

        assertEquals("Quack! I'm Duck. What shall we get done today? "
                + "Try: todo read a book, list, or find book. "
                + "Type help for every command.\n"
                + "Quack? Unable to load tasks from line 1: "
                + "the deadline date must be a valid yyyy-MM-dd date.",
                duck.getWelcomeMessage());
    }

    @Test
    public void getResponse_addAfterLoadFailure_preservesDataFile() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        String originalContents = "T | 0 | valid task\ninvalid record\n";
        Files.writeString(dataFile, originalContents, StandardCharsets.UTF_8);
        Duck duck = new Duck(dataFile.toString());

        String response = duck.getResponse("todo replacement task");
        boolean isResponseError = duck.isLastResponseError();
        String listResponse = duck.getResponse("list");

        assertEquals("Quack? Tasks cannot be changed because Duck could not load "
                + "the saved task file. Repair or move " + dataFile
                + ", then restart Duck.", response);
        assertTrue(isResponseError);
        assertEquals("The pond is clear—there are no tasks yet.", listResponse);
        assertEquals(originalContents, Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void getResponse_existingSavedTasks_loadsAndListsTasks() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Files.writeString(dataFile,
                "T | 1 | read book\nD | 0 | submit report | 2026-10-15\n",
                StandardCharsets.UTF_8);
        Duck duck = new Duck(dataFile.toString());

        String response = duck.getResponse("list");

        assertEquals("Here are the tasks in your pond:\n"
                + "1.[T][X] read book\n"
                + "2.[D][ ] submit report (by: Oct 15 2026)", response);
        assertFalse(duck.isLastResponseError());
    }

    @Test
    public void getResponse_findMatches_preservesFullListNumbers() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Files.writeString(dataFile,
                "T | 0 | read book\nT | 0 | submit report\nT | 0 | return book\n",
                StandardCharsets.UTF_8);
        Duck duck = new Duck(dataFile.toString());

        String response = duck.getResponse("find book");

        assertEquals("Here are the matching tasks in your pond:\n"
                + "1.[T][ ] read book\n"
                + "3.[T][ ] return book", response);
        assertFalse(duck.isLastResponseError());
    }

    @Test
    public void getResponse_validCommands_preservesTaskStateBetweenCommands() {
        Duck duck = createDuck();

        String addResponse = duck.getResponse("todo read book");
        boolean isAddResponseError = duck.isLastResponseError();
        String listResponse = duck.getResponse("list");

        assertEquals("✓ Got it—this task is now under my wing:\n"
                + "[T][ ] read book\n"
                + "You now have 1 task in your pond.", addResponse);
        assertFalse(isAddResponseError);
        assertEquals("Here are the tasks in your pond:\n"
                + "1.[T][ ] read book", listResponse);
        assertFalse(duck.isLastResponseError());
    }

    @Test
    public void getResponse_eventDateMissingYearSeparator_normalizesDisplayedAndSavedRange()
            throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Duck duck = new Duck(dataFile.toString());

        String response = duck.getResponse(
                "event todo /from 10-05-2026 /to 12-062026");

        assertEquals("✓ Got it—this task is now under my wing:\n"
                + "[E][ ] todo (from: 10-05-2026 to: 12-06-2026)\n"
                + "You now have 1 task in your pond.", response);
        assertFalse(duck.isLastResponseError());
        assertEquals(List.of("E | 0 | todo | 10-05-2026 | 12-06-2026"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void getResponse_duplicateTask_reportsErrorWithoutChangingList() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Duck duck = new Duck(dataFile.toString());
        duck.getResponse("todo read book");

        String duplicateResponse = duck.getResponse("todo read book");
        boolean isDuplicateResponseError = duck.isLastResponseError();
        String listResponse = duck.getResponse("list");

        assertEquals("Quack? That task already exists as task 1.", duplicateResponse);
        assertTrue(isDuplicateResponseError);
        assertEquals("Here are the tasks in your pond:\n"
                + "1.[T][ ] read book", listResponse);
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void getResponse_addMarkDeleteSequence_persistsChanges() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Duck duck = new Duck(dataFile.toString());

        duck.getResponse("todo read book");
        duck.getResponse("deadline submit report /by 2026-10-15");
        duck.getResponse("mark 1");
        duck.getResponse("delete 2");
        Duck reloadedDuck = new Duck(dataFile.toString());

        assertEquals("Here are the tasks in your pond:\n"
                + "1.[T][X] read book", reloadedDuck.getResponse("list"));
        assertEquals(List.of("T | 1 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void getResponse_unknownOrEmptyCommand_returnsUserFacingError() {
        Duck duck = createDuck();
        String expectedMessage =
                "Quack? I didn't understand that command. Try help to see what I can do.";

        assertEquals(expectedMessage, duck.getResponse("unknown"));
        assertTrue(duck.isLastResponseError());
        assertEquals(expectedMessage, duck.getResponse(""));
        assertTrue(duck.isLastResponseError());
        assertFalse(duck.isExitRequested());
    }

    @Test
    public void isLastResponseError_errorFollowedByValidCommand_tracksLatestCommand() {
        Duck duck = createDuck();

        duck.getResponse("unknown");
        assertTrue(duck.isLastResponseError());

        duck.getResponse("list");
        assertFalse(duck.isLastResponseError());
    }

    @Test
    public void getResponse_bye_returnsFarewellAndRequestsExit() {
        Duck duck = createDuck();

        assertEquals("Goodbye! Keep your ducks in a row!", duck.getResponse("bye"));
        assertTrue(duck.isExitRequested());
    }

    @Test
    public void getResponse_errorFollowedByExit_resetsResponseFlags() {
        Duck duck = createDuck();

        duck.getResponse("unknown");
        assertTrue(duck.isLastResponseError());
        assertFalse(duck.isExitRequested());

        assertEquals("Goodbye! Keep your ducks in a row!", duck.getResponse("bye"));
        assertFalse(duck.isLastResponseError());
        assertTrue(duck.isExitRequested());
    }

    @Test
    public void getResponse_help_returnsCommandGuide() {
        Duck duck = createDuck();

        assertEquals("Here are the commands I can help with:\n"
                + "  help\n"
                + "  todo DESCRIPTION\n"
                + "  deadline DESCRIPTION /by DATE\n"
                + "  event DESCRIPTION /from START /to END\n"
                + "  list\n"
                + "  find KEYWORD\n"
                + "  mark NUMBER\n"
                + "  unmark NUMBER\n"
                + "  delete NUMBER\n"
                + "  bye", duck.getResponse("help"));
        assertFalse(duck.isLastResponseError());
    }

    /** Creates a Duck using a fresh task file for each test. */
    private Duck createDuck() {
        return new Duck(this.temporaryDirectory.resolve("duck.txt").toString());
    }
}
