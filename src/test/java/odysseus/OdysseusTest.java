package odysseus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration tests that drive {@link Odysseus#getResponse(String)} end to end.
 *
 * <p>Each test runs against a fresh, temporary save file, so no real user data
 * is touched and tests never share state. Assertions target observable state
 * (task descriptions and before/after list equality) rather than exact response
 * wording, so they survive later changes to the bot's phrasing.
 */
public class OdysseusTest {

    @TempDir
    private Path tempDir;

    /** Creates an Odysseus backed by an empty temporary save file. */
    private Odysseus newBot() {
        return new Odysseus(tempDir.resolve("tasks.txt").toString());
    }

    /** Counts the lines of {@code text} that contain {@code needle}. */
    private long linesContaining(String text, String needle) {
        return text.lines().filter(line -> line.contains(needle)).count();
    }

    @Test
    public void getResponse_addTodo_taskAppearsInList() {
        Odysseus bot = newBot();
        bot.getResponse("todo read book");
        assertEquals(1, linesContaining(bot.getResponse("list"), "read book"));
    }

    @Test
    public void getResponse_addDeadline_taskAppearsInList() {
        Odysseus bot = newBot();
        bot.getResponse("deadline submit report /by 2026-06-06");
        assertTrue(bot.getResponse("list").contains("submit report"));
    }

    @Test
    public void getResponse_addEvent_taskAppearsInList() {
        Odysseus bot = newBot();
        bot.getResponse("event camp /from mon /to wed");
        assertTrue(bot.getResponse("list").contains("camp"));
    }

    @Test
    public void getResponse_deleteValidIndex_removesTask() {
        Odysseus bot = newBot();
        bot.getResponse("todo read book");
        bot.getResponse("delete 1");
        assertEquals(0, linesContaining(bot.getResponse("list"), "read book"));
    }

    @Test
    public void getResponse_markThenUnmark_returnsToOriginalState() {
        Odysseus bot = newBot();
        bot.getResponse("todo read book");
        String before = bot.getResponse("list");
        bot.getResponse("mark 1");
        bot.getResponse("unmark 1");
        assertEquals(before, bot.getResponse("list"));
    }

    @Test
    public void getResponse_updateDescription_changesTask() {
        Odysseus bot = newBot();
        bot.getResponse("todo old name");
        bot.getResponse("update 1 /desc new name");
        String list = bot.getResponse("list");
        assertTrue(list.contains("new name"));
        assertFalse(list.contains("old name"));
    }

    @Test
    public void getResponse_findKeyword_returnsOnlyMatches() {
        Odysseus bot = newBot();
        bot.getResponse("todo alpha task");
        bot.getResponse("todo beta task");
        String found = bot.getResponse("find alpha");
        assertTrue(found.contains("alpha task"));
        assertFalse(found.contains("beta task"));
    }

    @Test
    public void getResponse_onDate_listsTaskOnThatDate() {
        Odysseus bot = newBot();
        bot.getResponse("deadline submit report /by 2026-06-06");
        assertTrue(bot.getResponse("on 2026-06-06").contains("submit report"));
    }

    @Test
    public void getResponse_duplicateTodo_notAddedTwice() {
        Odysseus bot = newBot();
        bot.getResponse("todo read book");
        String before = bot.getResponse("list");
        bot.getResponse("todo read book");
        assertEquals(before, bot.getResponse("list"));
    }

    @Test
    public void getResponse_extraWhitespaceAroundIndex_behavesLikeSingleSpace() {
        Odysseus tidy = newBot();
        tidy.getResponse("todo read book");
        tidy.getResponse("mark 1");

        Odysseus messy = newBot();
        messy.getResponse("todo read book");
        messy.getResponse("mark    1");

        assertEquals(tidy.getResponse("list"), messy.getResponse("list"));
    }

    @Test
    public void getResponse_malformedUpdate_leavesTaskUnchanged() {
        Odysseus bot = newBot();
        bot.getResponse("todo some task");
        String before = bot.getResponse("list");
        bot.getResponse("update 1 /desc"); // missing the new value
        assertEquals(before, bot.getResponse("list"));
    }

    @Test
    public void getResponse_unknownCommand_leavesListUnchanged() {
        Odysseus bot = newBot();
        bot.getResponse("todo read book");
        String before = bot.getResponse("list");
        bot.getResponse("blah blah");
        assertEquals(before, bot.getResponse("list"));
    }

    @Test
    public void getResponse_outOfRangeIndex_leavesListUnchanged() {
        Odysseus bot = newBot();
        bot.getResponse("todo read book");
        String before = bot.getResponse("list");
        bot.getResponse("mark 5");
        assertEquals(before, bot.getResponse("list"));
    }

    @Test
    public void getResponse_blankInput_returnsAMessageAndAddsNothing() {
        Odysseus bot = newBot();
        String response = bot.getResponse("   ");
        assertFalse(response.isBlank());
        assertEquals(0, linesContaining(bot.getResponse("list"), "["));
    }
}
