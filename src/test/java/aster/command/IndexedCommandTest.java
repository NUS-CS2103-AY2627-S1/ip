package aster.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.TaskList;
import aster.task.Todo;
import aster.ui.Ui;

/**
 * Tests the exact messages a command naming a task by its number gives when that
 * number is missing, is not a number, or names no task, and the order those checks run in.
 *
 * <p>The messages are compared in full, and through all three commands, so that
 * restructuring how the usage example in them is built cannot change what the user
 * reads, or which keyword it names, without a test noticing.
 *
 * <p>Only plain ASCII digits count as a task number. Signs, decimals, spaces and digits
 * from other scripts are refused as not a number, even where Java's own integer parsing
 * would accept them, while a digit string too long to fit an {@code int} is simply a
 * number too large for the list.
 *
 * <p>Each command is handed a store pointed at a temporary directory, so the real
 * {@code data/aster.txt} is never at risk.
 */
class IndexedCommandTest {
    @TempDir
    Path tempDir;

    // ---------- missing number ----------

    @Test
    void execute_markWithoutNumber_throwsMissingNumberMessage() {
        AsterException thrown = assertThrows(AsterException.class, () -> execute(new MarkCommand("")));

        assertEquals("Tell me which task to mark. Try: mark 2", thrown.getMessage());
    }

    @Test
    void execute_markWithoutNumberOnEmptyList_throwsMissingNumberMessage() {
        // A missing number is reported before an empty list.
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new MarkCommand(""), 0));

        assertEquals("Tell me which task to mark. Try: mark 2", thrown.getMessage());
    }

    // ---------- not a number ----------

    @Test
    void execute_deleteWithNonNumber_throwsNotANumberMessage() {
        AsterException thrown = assertThrows(AsterException.class, () -> execute(new DeleteCommand("x")));

        assertEquals("\"x\" is not a task number. Try: delete 2", thrown.getMessage());
    }

    @Test
    void execute_markWithNonNumberOnEmptyList_throwsEmptyListMessage() {
        // An empty list is reported before wording that is not a number.
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new MarkCommand("x"), 0));

        assertEquals("Your list is empty, so there is nothing to mark yet.", thrown.getMessage());
    }

    @Test
    void execute_markWithPlusSign_throwsNotANumberMessage() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new MarkCommand("+2"), 2));

        assertEquals("\"+2\" is not a task number. Try: mark 2", thrown.getMessage());
    }

    @Test
    void execute_deleteWithMinusSign_throwsNotANumberMessage() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new DeleteCommand("-1"), 2));

        assertEquals("\"-1\" is not a task number. Try: delete 2", thrown.getMessage());
    }

    @Test
    void execute_unmarkWithFullWidthDigit_throwsNotANumberMessage() {
        // U+FF12 is FULLWIDTH DIGIT TWO, which Integer.parseInt would read as 2.
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new UnmarkCommand("\uFF12"), 2));

        assertEquals("\"\uFF12\" is not a task number. Try: unmark 2", thrown.getMessage());
    }

    @Test
    void execute_markWithDecimal_throwsNotANumberMessage() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new MarkCommand("2.0"), 2));

        assertEquals("\"2.0\" is not a task number. Try: mark 2", thrown.getMessage());
    }

    @Test
    void execute_markWithTwoNumbers_throwsNotANumberMessage() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new MarkCommand("2 3"), 2));

        assertEquals("\"2 3\" is not a task number. Try: mark 2", thrown.getMessage());
    }

    // ---------- out of range ----------

    @Test
    void execute_markWithZero_throwsOutOfRangeMessage() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new MarkCommand("0"), 2));

        assertEquals("You have 2 tasks, so 0 is out of range. Pick a number from 1 to 2.",
                thrown.getMessage());
    }

    @Test
    void execute_markAboveListSize_throwsOutOfRangeMessage() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new MarkCommand("3"), 2));

        assertEquals("You have 2 tasks, so 3 is out of range. Pick a number from 1 to 2.",
                thrown.getMessage());
    }

    @Test
    void execute_deleteWithNumberTooLargeForInt_throwsOutOfRangeMessage() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> execute(new DeleteCommand("99999999999"), 1));

        assertEquals("You have 1 task, so 99999999999 is out of range. Pick a number from 1 to 1.",
                thrown.getMessage());
    }

    // ---------- accepted ----------

    @Test
    void execute_markWithLeadingZero_marksThatTask() throws AsterException {
        TaskList tasks = execute(new MarkCommand("02"), 2);

        assertFalse(tasks.get(0).isDone(), "task 1 must be left as it was");
        assertTrue(tasks.get(1).isDone(), "02 must name task 2");
    }

    // ---------- helpers ----------

    /**
     * Carries out the command on a list holding one task, so that the empty-list check
     * does not answer first, with its output collected and its store pointed at this
     * test's temporary directory.
     */
    private void execute(Command command) throws AsterException {
        execute(command, 1);
    }

    /**
     * Carries out the command on a list holding the given number of tasks, with its
     * output collected and its store pointed at this test's temporary directory.
     *
     * @param command the command to carry out.
     * @param taskCount how many tasks the list holds.
     * @return the list after the command has run.
     */
    private TaskList execute(Command command, int taskCount) throws AsterException {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= taskCount; i++) {
            tasks.add(new Todo("task " + i));
        }
        List<String> lines = new ArrayList<>();
        command.execute(tasks, new Ui(lines::add), new Storage(tempDir.resolve("aster.txt")));
        return tasks;
    }
}
