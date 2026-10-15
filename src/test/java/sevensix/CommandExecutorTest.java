package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies validation and session recovery when typed commands are executed without text parsing. */
class CommandExecutorTest {
    @TempDir
    private Path temporaryDirectory;

    /** Direct command callers cannot bypass validation or leave an undo entry after rejection. */
    @Test
    void invalidTypedAdditionLeavesNoTasksFileOrHistory() throws SevenSixException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        CommandExecutor executor = new CommandExecutor(new TaskStorage(file));
        Event reversedEvent = new Event("meeting", LocalDate.of(2026, 10, 14), LocalDate.of(2026, 10, 13));

        SevenSixException invalid = assertThrows(SevenSixException.class,
                () -> executor.execute(new AddCommand(reversedEvent)));
        assertEquals(Ui.INVALID_EVENT_RANGE, invalid.getMessage());
        assertEquals(Ui.getEmptyListMessage(), executor.execute(new ListCommand()));
        assertFalse(Files.exists(file));
        SevenSixException empty = assertThrows(SevenSixException.class,
                () -> executor.execute(new UndoCommand()));
        assertEquals(Ui.EMPTY_UNDO_HISTORY, empty.getMessage());

        executor.execute(new AddCommand(new Todo("valid")));
        assertEquals("Undid addition: [T][ ] valid", executor.execute(new UndoCommand()));
    }
}
