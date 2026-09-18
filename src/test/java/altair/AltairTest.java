package altair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link Altair#getResponse(String)} and the surrounding
 * start-up behavior.
 *
 * <p>{@code getResponse} is now the highest-value method to cover: it parses
 * every command, routes it, mutates the in-memory task list, and persists the
 * result. The task classes and {@code Storage} have their own tests, so these
 * tests focus on the routing and on the parts only visible here &mdash; the
 * error messages for bad input, the {@code bye} exit signal, and that a change
 * made through one {@code Altair} instance is seen by the next one (i.e. it was
 * saved).</p>
 *
 * <p>Each test uses a save file inside a JUnit {@link TempDir}, so the real
 * {@code ./data/} folder is never touched.</p>
 */
public class AltairTest {

    /** A fresh temporary directory per test method. */
    @TempDir
    private Path tempDir;

    /** Creates an {@code Altair} backed by {@code altair.txt} in the temporary directory. */
    private Altair newAltair() {
        return new Altair(tempDir.resolve("altair.txt").toString());
    }

    /** Reads the lines currently saved in the temporary save file. */
    private List<String> savedLines() throws IOException {
        Path file = tempDir.resolve("altair.txt");
        return Files.exists(file) ? Files.readAllLines(file) : List.of();
    }

    // ----- adding tasks -----

    @Test
    public void getResponse_todoCommand_addsTaskConfirmsAndSaves() throws Exception {
        Altair altair = newAltair();

        String response = altair.getResponse("todo buy milk");

        assertEquals("    Alright, I have added it to your list:\n"
                + "      [T][ ] buy milk\n"
                + "    Now you have 1 tasks in the list.", response);
        assertEquals(List.of("T | 0 | buy milk"), savedLines());
    }

    @Test
    public void getResponse_deadlineCommand_parsesAndDisplaysTheDate() {
        Altair altair = newAltair();

        String response = altair.getResponse("deadline return book /by 2019-10-15");

        assertTrue(response.contains("[D][ ] return book (by: Oct 15 2019)"),
                "expected the confirmation to show the formatted date but was: " + response);
    }

    @Test
    public void getResponse_eventCommand_parsesBothDates() {
        Altair altair = newAltair();

        String response = altair.getResponse("event camp /from 2019-10-16 /to 2019-10-17");

        assertTrue(response.contains("[E][ ] camp (from: Oct 16 2019 to: Oct 17 2019)"),
                "expected the confirmation to show both formatted dates but was: " + response);
    }

    @Test
    public void getResponse_eventFromAfterTo_returnsExplanationAndSavesNothing() throws Exception {
        Altair altair = newAltair();

        String response = altair.getResponse("event camp /from 2019-10-17 /to 2019-10-16");

        assertEquals("    I'm afraid that won't do. An event's start date cannot be after its end date.", response);
        assertEquals(List.of(), savedLines());
    }

    @Test
    public void getResponse_emptyTodoDescription_returnsExplanationAndSavesNothing() throws Exception {
        Altair altair = newAltair();

        String response = altair.getResponse("todo");

        assertEquals("    I'm afraid that won't do. The description of a todo cannot be empty.", response);
        assertEquals(List.of(), savedLines());
    }

    @Test
    public void getResponse_deadlineWithoutDate_returnsExplanation() {
        Altair altair = newAltair();
        assertEquals("    I'm afraid that won't do. A deadline needs a date after /by.",
                altair.getResponse("deadline return book"));
    }

    @Test
    public void getResponse_deadlineWithUnparseableDate_returnsFormatHint() {
        Altair altair = newAltair();
        assertEquals("    I'm afraid that won't do. A deadline date must use yyyy-MM-dd format.",
                altair.getResponse("deadline return book /by 15-10-2019"));
    }

    @Test
    public void getResponse_unknownCommand_returnsExplanation() {
        Altair altair = newAltair();
        assertEquals("    I'm afraid that won't do. I do not understand your command. Try again, perhaps?",
                altair.getResponse("blah"));
    }

    @Test
    public void getResponse_descriptionWithPipe_isRejected() {
        Altair altair = newAltair();
        assertEquals("    I'm afraid that won't do. Task details cannot contain the '|' character.",
                altair.getResponse("todo bad | data"));
    }

    // ----- duplicate detection -----

    @Test
    public void getResponse_duplicateTask_warnsAndDoesNotAddUntilConfirmed() throws Exception {
        Altair altair = newAltair();
        altair.getResponse("todo buy milk");

        String response = altair.getResponse("todo buy milk");

        assertEquals("    I believe this already appears on the books:\n"
                + "      [T][ ] buy milk\n"
                + "    Add it anyway? (y/n)", response);
        assertEquals(List.of("T | 0 | buy milk"), savedLines());
    }

    @Test
    public void getResponse_duplicateConfirmedWithY_addsAndSavesTheSecondTask() throws Exception {
        Altair altair = newAltair();
        altair.getResponse("todo buy milk");
        altair.getResponse("todo buy milk");

        String response = altair.getResponse("Y");

        assertEquals("    Alright, I have added it to your list:\n"
                + "      [T][ ] buy milk\n"
                + "    Now you have 2 tasks in the list.", response);
        assertEquals(List.of("T | 0 | buy milk", "T | 0 | buy milk"), savedLines());
    }

    @Test
    public void getResponse_duplicateDeclinedWithNo_doesNotAddTheSecondTask() {
        Altair altair = newAltair();
        altair.getResponse("todo buy milk");
        altair.getResponse("todo buy milk");

        assertEquals("    Very well. I shall leave it be.", altair.getResponse("no"));
        assertEquals("     The following are your tasks\n"
                + "     1.[T][ ] buy milk", altair.getResponse("list"));
    }

    @Test
    public void getResponse_duplicateThenUnrelatedCommand_cancelsPromptAndRunsThatCommand() {
        Altair altair = newAltair();
        altair.getResponse("todo buy milk");
        altair.getResponse("todo buy milk");

        // "list" is neither y/n, so the pending duplicate is dropped and this
        // is processed as an ordinary command instead of a bad answer.
        assertEquals("     The following are your tasks\n"
                + "     1.[T][ ] buy milk", altair.getResponse("list"));
    }

    // ----- listing and finding -----

    @Test
    public void getResponse_listWhenEmpty_showsOnlyTheHeader() {
        Altair altair = newAltair();
        assertEquals("     The following are your tasks", altair.getResponse("list"));
    }

    @Test
    public void getResponse_listWithTasks_numbersThemFromOne() {
        Altair altair = newAltair();
        altair.getResponse("todo read book");
        altair.getResponse("todo buy milk");

        assertEquals("     The following are your tasks\n"
                + "     1.[T][ ] read book\n"
                + "     2.[T][ ] buy milk", altair.getResponse("list"));
    }

    @Test
    public void getResponse_listWithArgument_isRejected() {
        Altair altair = newAltair();
        assertEquals("    I'm afraid that won't do. Please use: list.", altair.getResponse("list now"));
    }

    @Test
    public void getResponse_find_listsOnlyMatchingTasks() {
        Altair altair = newAltair();
        altair.getResponse("todo read book");
        altair.getResponse("todo borrow book");
        altair.getResponse("todo buy milk");

        assertEquals("     Here are the matching tasks in your list:\n"
                + "     1.[T][ ] read book\n"
                + "     2.[T][ ] borrow book", altair.getResponse("find book"));
    }

    @Test
    public void getResponse_findWithNoMatches_saysSo() {
        Altair altair = newAltair();
        altair.getResponse("todo read book");
        assertEquals("     Nothing of that description turns up, I'm afraid.", altair.getResponse("find xyzzy"));
    }

    @Test
    public void getResponse_findWithoutKeyword_returnsUsageHint() {
        Altair altair = newAltair();
        assertEquals("    I'm afraid that won't do. Please use: find <keyword>.", altair.getResponse("find"));
    }

    // ----- mark, unmark, delete -----

    @Test
    public void getResponse_mark_setsTheStatusIconAndSaves() throws Exception {
        Altair altair = newAltair();
        altair.getResponse("todo read book");

        String response = altair.getResponse("mark 1");

        assertEquals("     Duly noted. Marked as complete:\n"
                + "       [T][X] read book", response);
        assertEquals(List.of("T | 1 | read book"), savedLines());
    }

    @Test
    public void getResponse_unmark_clearsTheStatusIcon() {
        Altair altair = newAltair();
        altair.getResponse("todo read book");
        altair.getResponse("mark 1");

        assertEquals("     As you wish. Returned to the unfinished pile:\n"
                + "       [T][ ] read book", altair.getResponse("unmark 1"));
    }

    @Test
    public void getResponse_deleteMiddleTask_removesItAndRenumbers() {
        Altair altair = newAltair();
        altair.getResponse("todo a");
        altair.getResponse("todo b");
        altair.getResponse("todo c");

        String response = altair.getResponse("delete 2");

        assertEquals("    Consider it done. I have removed the following:\n"
                + "      [T][ ] b\n"
                + "    Now you have 2 tasks in the list.", response);
        assertEquals("     The following are your tasks\n"
                + "     1.[T][ ] a\n"
                + "     2.[T][ ] c", altair.getResponse("list"));
    }

    @Test
    public void getResponse_markOutOfRange_isRejectedAndListUnchanged() {
        Altair altair = newAltair();
        altair.getResponse("todo read book");

        assertEquals("    I'm afraid that won't do. That task number is not in your list.",
                altair.getResponse("mark 0"));
        assertEquals("     The following are your tasks\n"
                + "     1.[T][ ] read book", altair.getResponse("list"));
    }

    @Test
    public void getResponse_deleteWithNonNumericArgument_isRejected() {
        Altair altair = newAltair();
        altair.getResponse("todo read book");
        assertEquals("    I'm afraid that won't do. Please use a valid task number.",
                altair.getResponse("delete second"));
    }

    @Test
    public void getResponse_markWithoutNumber_returnsUsageHint() {
        Altair altair = newAltair();
        assertEquals("    I'm afraid that won't do. Please use: mark <task number>.", altair.getResponse("mark"));
    }

    // ----- bye / exit signalling -----

    @Test
    public void getResponse_bye_returnsFarewellAndSetsExit() {
        Altair altair = newAltair();

        assertFalse(altair.isExit());
        assertEquals("    Very well. I shall be here when next you require me.",
                altair.getResponse("bye"));
        assertTrue(altair.isExit());
    }

    @Test
    public void getResponse_byeWithArgument_isRejectedAndDoesNotExit() {
        Altair altair = newAltair();

        assertEquals("    I'm afraid that won't do. Please use: bye.", altair.getResponse("bye now"));
        assertFalse(altair.isExit());
    }

    // ----- error flag for the GUI -----

    @Test
    public void hasError_afterUnknownCommand_isTrue() {
        Altair altair = newAltair();

        altair.getResponse("blah");

        assertTrue(altair.hasError());
    }

    @Test
    public void hasError_afterSuccessfulTodo_isFalse() {
        Altair altair = newAltair();

        altair.getResponse("todo buy milk");

        assertFalse(altair.hasError());
    }

    @Test
    public void hasError_resetsOnNextValidCommand_isFalse() {
        Altair altair = newAltair();
        altair.getResponse("blah");
        assertTrue(altair.hasError());

        altair.getResponse("todo buy milk");

        assertFalse(altair.hasError());
    }

    // ----- persistence across instances -----

    @Test
    public void getResponse_changesArePersisted_soANewInstanceSeesThem() {
        Altair first = newAltair();
        first.getResponse("todo read book");
        first.getResponse("deadline return book /by 2019-10-15");

        Altair second = newAltair();

        assertEquals("     The following are your tasks\n"
                + "     1.[T][ ] read book\n"
                + "     2.[D][ ] return book (by: Oct 15 2019)", second.getResponse("list"));
    }

    // ----- start-up load failure -----

    @Test
    public void construct_corruptedSaveFile_reportsLoadErrorAndStartsEmpty() throws Exception {
        Files.writeString(tempDir.resolve("altair.txt"), "not a valid task line\n");

        Altair altair = newAltair();

        assertTrue(altair.getLoadError() != null && altair.getLoadError().contains("line 1"),
                "expected a line-specific load error but was: " + altair.getLoadError());
        assertTrue(altair.getGreeting().contains(altair.getLoadError()),
                "expected the GUI greeting to include the load error");
        assertEquals("     The following are your tasks", altair.getResponse("list"));
    }

    @Test
    public void getLoadError_cleanSaveFile_isNull() {
        assertTrue(newAltair().getLoadError() == null);
    }
}
