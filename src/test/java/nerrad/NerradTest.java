package nerrad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests shared command processing used by Nerrad's console and graphical interfaces.
 */
class NerradTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskCommands_updatesTasksAndFormatsReplies() {
        Nerrad nerrad = createNerrad();

        assertEquals("  Placed on your path:\n"
                        + "    [T][ ] read book\n"
                        + "  Now you have 1 tasks in the list.",
                nerrad.getResponse("todo read book"));
        assertEquals("  A bright step forward - this task is complete:\n    [T][X] read book",
                nerrad.getResponse("mark 1"));
        assertEquals("  Here is the path ahead:\n  1.[T][X] read book",
                nerrad.getResponse("list"));
    }

    @Test
    void getResponse_invalidCommandAndBye_returnsAppropriateReplies() {
        Nerrad nerrad = createNerrad();

        assertEquals("  OOPS!!! I'm sorry, but I don't know what that means :-(",
                nerrad.getResponse("unknown"));
        assertTrue(nerrad.isExitCommand("bye"));
        assertFalse(nerrad.isExitCommand("list"));
        assertEquals("  Safe travels. May your path stay clear.", nerrad.getResponse("bye"));
    }

    @Test
    void getResponse_loanCommands_persistAndSettleLoan() {
        Nerrad nerrad = createNerrad();

        assertEquals("  Kept safely in your ledger:\n"
                        + "    [LENT][OUTSTANDING] Alex: S$12.50 (lunch)\n"
                        + "  You now have 1 loan records.",
                nerrad.getResponse("loan lend Alex 12.50 /for lunch"));
        assertEquals("  Your ledger is balanced for this loan:\n"
                        + "    [LENT][SETTLED] Alex: S$12.50 (lunch)",
                nerrad.getResponse("settle-loan 1"));

        Nerrad restartedNerrad = createNerrad();
        assertEquals("  Here is your loan ledger:\n"
                        + "  1.[LENT][SETTLED] Alex: S$12.50 (lunch)",
                restartedNerrad.getResponse("loans"));
    }

    @Test
    void getDashboardSummary_taskAndLoanChanges_updatesCounts() {
        Nerrad nerrad = createNerrad();

        assertEquals("0 tasks  ·  0 complete  ·  0 loan records", nerrad.getDashboardSummary());
        nerrad.getResponse("todo read book");
        nerrad.getResponse("loan lend Alex 10");
        nerrad.getResponse("mark 1");

        assertEquals("1 tasks  ·  1 complete  ·  1 loan records", nerrad.getDashboardSummary());
    }

    @Test
    void constructor_corruptedLoanData_reportsLoadingError() throws IOException {
        Path loanFile = temporaryDirectory.resolve("data/loans.txt");
        Files.createDirectories(loanFile.getParent());
        Files.writeString(loanFile, "L | LENT | invalid status | Alex | 10 | lunch");

        Nerrad nerrad = createNerrad();

        assertTrue(nerrad.hasLoadingError());
        assertEquals("  OOPS!!! I could not load your saved tasks.", nerrad.getWelcomeMessage());
    }

    /**
     * Creates a chatbot that saves data inside the test's temporary directory.
     *
     * @return Chatbot with isolated storage.
     */
    private Nerrad createNerrad() {
        return new Nerrad(temporaryDirectory.resolve("data/nerrad.txt").toString());
    }
}
