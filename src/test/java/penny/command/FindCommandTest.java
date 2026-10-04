package penny.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import penny.common.PennyException;
import penny.storage.Storage;
import penny.task.Deadline;
import penny.task.TaskList;
import penny.task.Todo;
import penny.ui.Ui;

/**
 * Unit test suite for {@link FindCommand}.
 * Verifies execution of keyword searches and assertion contracts.
 */
public class FindCommandTest {

    @TempDir
    private Path tempDir;

    private TaskList taskList;
    private Ui ui;
    private Storage storage;
    private Todo sampleTodo;
    private Deadline sampleDeadline;

    @BeforeEach
    public void setUp() throws PennyException {
        taskList = new TaskList();
        sampleTodo = new Todo("read biology textbook");
        sampleDeadline = new Deadline("submit math homework", "2026-10-15");
        taskList.add(sampleTodo);
        taskList.add(sampleDeadline);
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test_tasks.txt"));
    }

    @Test
    public void execute_matchingPartialKeywords_returnsUiFormattedMatches() {
        FindCommand command = new FindCommand("bio text");
        String result = command.execute(taskList, ui, storage);

        assertNotNull(result);
        assertTrue(result.contains("read biology textbook"));
        assertFalse(result.contains("submit math homework"));
    }

    @Test
    public void execute_reversedKeywordOrder_returnsUiFormattedMatches() {
        FindCommand command = new FindCommand("textbook bio");
        String result = command.execute(taskList, ui, storage);

        assertNotNull(result);
        assertTrue(result.contains("read biology textbook"));
    }

    @Test
    public void execute_nonMatchingKeyword_returnsZeroMatchesMessage() {
        FindCommand command = new FindCommand("chemistry");
        String result = command.execute(taskList, ui, storage);

        assertNotNull(result);
        assertTrue(result.contains("chemistry"));
    }

    @Test
    public void constructor_emptyKeyword_assertionError() {
        assertThrows(AssertionError.class, () -> {
            new FindCommand("");
        });
    }

    @Test
    public void constructor_nullKeyword_assertionError() {
        assertThrows(AssertionError.class, () -> {
            new FindCommand(null);
        });
    }
}
