package amigobot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for AmigoBot's getResponse method, covering valid commands,
 * error handling, and edge cases.
 */
public class AmigoBotTest {

    @TempDir
    Path tempDir;

    private AmigoBot bot;

    @BeforeEach
    public void setUp() {
        bot = new AmigoBot(tempDir.resolve("tasks.txt").toString());
    }

    // --- Todo command ---

    @Test
    public void todo_validInput_addsTask() {
        String response = bot.getResponse("todo read book");
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("1 tasks"));
    }

    @Test
    public void todo_emptyDescription_showsError() {
        String response = bot.getResponse("todo");
        assertTrue(response.contains("Ay caramba!"));
        assertTrue(response.contains("description"));
    }

    // --- Deadline command ---

    @Test
    public void deadline_validWithDate_addsTask() {
        String response = bot.getResponse("deadline return book /by 2025-12-02");
        assertTrue(response.contains("[D][ ] return book (by: Dec 2 2025)"));
    }

    @Test
    public void deadline_validWithString_addsTask() {
        String response = bot.getResponse("deadline return book /by Sunday");
        assertTrue(response.contains("[D][ ] return book (by: Sunday)"));
    }

    @Test
    public void deadline_missingBy_showsError() {
        String response = bot.getResponse("deadline return book");
        assertTrue(response.contains("Ay caramba!"));
        assertTrue(response.contains("/by"));
    }

    @Test
    public void deadline_emptyDescription_showsError() {
        String response = bot.getResponse("deadline");
        assertTrue(response.contains("Ay caramba!"));
    }

    // --- Event command ---

    @Test
    public void event_validWithStrings_addsTask() {
        String response = bot.getResponse("event meeting /from Mon 2pm /to 4pm");
        assertTrue(response.contains("[E][ ] meeting (from: Mon 2pm to: 4pm)"));
    }

    @Test
    public void event_missingFrom_showsError() {
        String response = bot.getResponse("event meeting /to 4pm");
        assertTrue(response.contains("Ay caramba!"));
        assertTrue(response.contains("/from"));
    }

    @Test
    public void event_missingTo_showsError() {
        String response = bot.getResponse("event meeting /from Mon 2pm");
        assertTrue(response.contains("Ay caramba!"));
        assertTrue(response.contains("/to"));
    }

    @Test
    public void event_emptyDescription_showsError() {
        String response = bot.getResponse("event");
        assertTrue(response.contains("Ay caramba!"));
    }

    // --- List command ---

    @Test
    public void list_emptyList_showsHeader() {
        String response = bot.getResponse("list");
        assertTrue(response.contains("Here are the tasks in your list:"));
    }

    @Test
    public void list_withTasks_showsNumberedTasks() {
        bot.getResponse("todo first task");
        bot.getResponse("todo second task");
        String response = bot.getResponse("list");
        assertTrue(response.contains("1.[T][ ] first task"));
        assertTrue(response.contains("2.[T][ ] second task"));
    }

    // --- Mark / Unmark commands ---

    @Test
    public void mark_validIndex_marksTask() {
        bot.getResponse("todo read book");
        String response = bot.getResponse("mark 1");
        assertTrue(response.contains("[T][X] read book"));
    }

    @Test
    public void unmark_validIndex_unmarksTask() {
        bot.getResponse("todo read book");
        bot.getResponse("mark 1");
        String response = bot.getResponse("unmark 1");
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void mark_invalidIndex_showsError() {
        bot.getResponse("todo read book");
        String response = bot.getResponse("mark 5");
        assertTrue(response.contains("Ay caramba!"));
        assertTrue(response.contains("does not exist"));
    }

    @Test
    public void mark_noIndex_showsError() {
        String response = bot.getResponse("mark");
        assertTrue(response.contains("Ay caramba!"));
    }

    // --- Delete command ---

    @Test
    public void delete_validIndex_removesTask() {
        bot.getResponse("todo read book");
        String response = bot.getResponse("delete 1");
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("0 tasks"));
    }

    @Test
    public void delete_invalidIndex_showsError() {
        String response = bot.getResponse("delete 1");
        assertTrue(response.contains("Ay caramba!"));
    }

    // --- Mass operations ---

    @Test
    public void delete_multipleIndices_removesAll() {
        bot.getResponse("todo task one");
        bot.getResponse("todo task two");
        bot.getResponse("todo task three");
        String response = bot.getResponse("delete 1 3");
        assertTrue(response.contains("task one"));
        assertTrue(response.contains("task three"));
        assertTrue(response.contains("1 tasks"));
    }

    @Test
    public void mark_range_marksAll() {
        bot.getResponse("todo task one");
        bot.getResponse("todo task two");
        bot.getResponse("todo task three");
        String response = bot.getResponse("mark 1-3");
        assertTrue(response.contains("[T][X] task one"));
        assertTrue(response.contains("[T][X] task two"));
        assertTrue(response.contains("[T][X] task three"));
    }

    @Test
    public void delete_range_removesAll() {
        bot.getResponse("todo task one");
        bot.getResponse("todo task two");
        bot.getResponse("todo task three");
        String response = bot.getResponse("delete 1-3");
        assertTrue(response.contains("0 tasks"));
    }

    // --- Find command ---

    @Test
    public void find_matchingKeyword_showsResults() {
        bot.getResponse("todo read book");
        bot.getResponse("todo return book");
        bot.getResponse("todo buy groceries");
        String response = bot.getResponse("find book");
        assertTrue(response.contains("read book"));
        assertTrue(response.contains("return book"));
    }

    @Test
    public void find_noMatch_showsNoResults() {
        bot.getResponse("todo read book");
        String response = bot.getResponse("find xyz");
        assertTrue(response.contains("No matching tasks found"));
    }

    @Test
    public void find_emptyKeyword_showsError() {
        String response = bot.getResponse("find");
        assertTrue(response.contains("Ay caramba!"));
    }

    // --- On command ---

    @Test
    public void on_validDate_showsMatchingTasks() {
        bot.getResponse("deadline return book /by 2025-12-02");
        String response = bot.getResponse("on 2025-12-02");
        assertTrue(response.contains("return book"));
    }

    @Test
    public void on_invalidDate_showsError() {
        String response = bot.getResponse("on not-a-date");
        assertTrue(response.contains("Ay caramba!"));
        assertTrue(response.contains("Invalid date"));
    }

    @Test
    public void on_emptyDate_showsError() {
        String response = bot.getResponse("on");
        assertTrue(response.contains("Ay caramba!"));
    }

    // --- Bye command ---

    @Test
    public void bye_returnsGoodbye() {
        String response = bot.getResponse("bye");
        assertTrue(response.contains("amigo") || response.contains("compadre"));
    }

    // --- Unknown command ---

    @Test
    public void unknownCommand_showsError() {
        String response = bot.getResponse("blah");
        assertTrue(response.contains("Ay caramba!"));
        assertTrue(response.contains("don't know"));
    }

    // --- Edge cases ---

    @Test
    public void caseInsensitive_commandRecognized() {
        String response = bot.getResponse("TODO read book");
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void extraSpaces_commandStillWorks() {
        String response = bot.getResponse("  todo   read book  ");
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void emptyInput_showsError() {
        String response = bot.getResponse("   ");
        assertTrue(response.contains("Ay caramba!"));
    }
}
