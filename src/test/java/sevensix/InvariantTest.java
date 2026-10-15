package sevensix;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Verifies assertion contracts that protect model and parser invariants from programmer misuse. */
class InvariantTest {
    /** Task construction must reject missing descriptions and type information. */
    @Test
    void taskConstructorsRejectMissingRequiredFields() {
        assertThrows(AssertionError.class, () -> new Task(null));
        assertThrows(AssertionError.class, () -> new Task(" "));
        assertThrows(AssertionError.class, () -> new Task("description", null));
        assertThrows(AssertionError.class, () -> new Deadline("description", (LocalDate) null));
        assertThrows(AssertionError.class, () -> new Event("description", null, LocalDate.of(2024, 1, 1)));
        assertThrows(AssertionError.class, () -> new Event("description", LocalDate.of(2024, 1, 1), null));
    }

    /** Invalid list operations must fail at their documented preconditions. */
    @Test
    void taskListRejectsInvalidArgumentsAndIndices() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
        TaskList tasks = new TaskList(List.of(new Todo("first")));

        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.get(-1));
        assertThrows(AssertionError.class, () -> tasks.get(1));
        assertThrows(AssertionError.class, () -> tasks.remove(-1));
        assertThrows(AssertionError.class, () -> tasks.remove(1));
        assertThrows(AssertionError.class, () -> tasks.find(null));
        assertThrows(AssertionError.class, () -> tasks.find(" "));
    }

    /** Specialized parsers must not accept commands routed to the wrong handler. */
    @Test
    void parserRejectsIncorrectDispatch() {
        assertThrows(AssertionError.class, () -> Parser.parseTodoDescription("find book"));
        assertThrows(AssertionError.class, () -> Parser.parseFindKeyword("todo book"));
        assertThrows(AssertionError.class, () -> Parser.parseDeadline("todo book"));
        assertThrows(AssertionError.class, () -> Parser.parseEvent("todo book"));
        assertThrows(AssertionError.class, () -> Parser.parseTaskNumber("delete 1", Parser.COMMAND_MARK));
    }

    /** Formatting and undo require a real date, a valid task count, and a saved snapshot. */
    @Test
    void formattingAndUndoRejectInvalidInternalState() {
        assertThrows(AssertionError.class, () -> DateTimeParser.formatForDisplay(null, null));
        assertThrows(AssertionError.class, () -> DateTimeParser.formatForStorage(null, null));
        assertThrows(AssertionError.class, () -> Ui.formatAddedTask(new Todo("task"), -1));
        assertThrows(AssertionError.class, () -> new UndoHistory().takeEntry().restoreTasks());
    }
}
