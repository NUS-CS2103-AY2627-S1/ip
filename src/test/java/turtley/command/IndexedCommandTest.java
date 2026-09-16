package turtley.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import turtley.exception.TurtleyException;
import turtley.model.TaskList;
import turtley.model.ToDo;

/** Tests task-number parsing and range validation shared by indexed commands. */
class IndexedCommandTest {

    private final ExposedIndexedCommand command = new ExposedIndexedCommand();

    @Test
    void parseTaskIndex_validOneBasedNumbers_returnsZeroBasedIndex() {
        assertAll(
                () -> assertEquals(0, command.parse("1")),
                () -> assertEquals(1, command.parse("+2")),
                () -> assertEquals(99, command.parse("100")));
    }

    @Test
    void parseTaskIndex_invalidOrOverflowingInput_throwsTurtleyException() {
        assertAll(
                () -> assertInvalidNumber(null),
                () -> assertInvalidNumber(""),
                () -> assertInvalidNumber("+"),
                () -> assertInvalidNumber("-"),
                () -> assertInvalidNumber("1x"),
                () -> assertInvalidNumber("2147483648"),
                () -> assertInvalidNumber("-2147483649"));
    }

    @Test
    void requireTaskIndex_validatesOneBasedRange() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));

        assertEquals(0, command.require("1", tasks));
        assertEquals(1, command.require("2", tasks));
        assertAll(
                () -> assertOutOfRange("0", tasks),
                () -> assertOutOfRange("-1", tasks),
                () -> assertOutOfRange("3", tasks),
                () -> assertOutOfRange("1", new TaskList()));
    }

    private void assertInvalidNumber(String input) {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> command.parse(input));
        assertEquals("Please provide a valid task number.", exception.getMessage());
    }

    private void assertOutOfRange(String input, TaskList tasks) {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> command.require(input, tasks));
        assertEquals("Task number is not in your list.", exception.getMessage());
    }

    private static final class ExposedIndexedCommand extends IndexedCommand {

        private int parse(String input) {
            return parseTaskIndex(input);
        }

        private int require(String input, TaskList tasks) {
            return requireTaskIndex(input, tasks);
        }

        @Override
        public void execute(TaskList tasks, turtley.ui.Ui ui, turtley.storage.Storage storage) {
            // This test helper only exposes the protected parsing methods.
        }
    }
}
