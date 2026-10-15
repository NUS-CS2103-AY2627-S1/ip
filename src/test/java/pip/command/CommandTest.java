package pip.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pip.exception.ParserException;
import pip.storage.Storage;
import pip.task.Task;
import pip.task.TaskList;
import pip.task.Todo;
import pip.ui.Ui;

/**
 * Tests each {@link Command} subclass's execute() behaviour against a real
 * {@link TaskList} and {@link Storage} backed by a temp file, plus isExit().
 * System.out is captured only where a command's contract is "prints
 * something and does not throw" (ListCommand, ExitCommand); the other
 * commands are verified through their effect on the TaskList instead.
 */
class CommandTest {

    @TempDir
    Path tempDir;

    private Ui ui;
    private Storage storage;
    private final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        ui = new Ui();
        storage = new Storage(tempDir.resolve("pip_command_test.txt").toString());
        originalOut = System.out;
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Nested
    class AddCommandTests {
        @Test
        void execute_appendsTaskToListAndPersistsToStorage() throws Exception {
            TaskList tasks = new TaskList();
            Task todo = new Todo("buy milk");

            new AddCommand(todo).execute(tasks, ui, storage);

            assertEquals(1, tasks.size());
            assertEquals(todo, tasks.get(0));
            // Persistence side effect: a reload should recover the task.
            assertEquals(1, storage.load().size());
        }

        @Test
        void execute_multipleAdds_appendInOrder() throws Exception {
            TaskList tasks = new TaskList();
            new AddCommand(new Todo("first")).execute(tasks, ui, storage);
            new AddCommand(new Todo("second")).execute(tasks, ui, storage);

            assertEquals("first", tasks.get(0).getName());
            assertEquals("second", tasks.get(1).getName());
        }

        @Test
        void isExit_isFalse() {
            assertFalse(new AddCommand(new Todo("x")).isExit());
        }
    }

    @Nested
    class DeleteCommandTests {
        @Test
        void execute_validIndex_removesTaskAndPersists() throws Exception {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("keep"));
            tasks.add(new Todo("remove"));

            new DeleteCommand(1).execute(tasks, ui, storage);

            assertEquals(1, tasks.size());
            assertEquals("keep", tasks.get(0).getName());
            assertEquals(1, storage.load().size());
        }

        @Test
        void execute_indexTooLarge_throwsParserExceptionAndLeavesListUnchanged() {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("only task"));

            assertThrows(ParserException.class, () -> new DeleteCommand(5).execute(tasks, ui, storage));
            assertEquals(1, tasks.size());
        }

        @Test
        void execute_negativeIndex_throwsParserException() {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("only task"));

            assertThrows(ParserException.class, () -> new DeleteCommand(-1).execute(tasks, ui, storage));
        }
    }

    @Nested
    class MarkCommandTests {
        @Test
        void execute_markTrue_marksTaskDoneAndPersists() throws Exception {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("water plants"));

            new MarkCommand(0, true).execute(tasks, ui, storage);

            assertTrue(tasks.get(0).isCompleted());
            assertTrue(storage.load().get(0).isCompleted());
        }

        @Test
        void execute_markFalse_marksTaskUndone() throws Exception {
            TaskList tasks = new TaskList();
            Task task = new Todo("water plants");
            task.markDone();
            tasks.add(task);

            new MarkCommand(0, false).execute(tasks, ui, storage);

            assertFalse(tasks.get(0).isCompleted());
        }

        @Test
        void execute_indexOutOfBounds_throwsParserException() {
            TaskList tasks = new TaskList();
            assertThrows(ParserException.class, () -> new MarkCommand(0, true).execute(tasks, ui, storage));
        }
    }

    @Nested
    class ListCommandTests {
        @Test
        void execute_emptyList_doesNotThrowAndPrintsHeader() {
            TaskList tasks = new TaskList();
            new ListCommand().execute(tasks, ui, storage);
            assertTrue(capturedOut.toString().contains("Here is your task stash"));
        }

        @Test
        void execute_nonEmptyList_printsEachTaskNumbered() {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("buy milk"));

            new ListCommand().execute(tasks, ui, storage);

            String output = capturedOut.toString();
            assertTrue(output.contains("1.[T][ ] buy milk"));
        }

        @Test
        void isExit_isFalse() {
            assertFalse(new ListCommand().isExit());
        }
    }

    @Nested
    class FindCommandTests {
        @Test
        void execute_matchingKeyword_printsOnlyMatchingTasks() {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("read book"));
            tasks.add(new Todo("write report"));

            new FindCommand("book").execute(tasks, ui, storage);

            String output = capturedOut.toString();
            assertTrue(output.contains("Sniffed them out! Here are your matching tasks:"));
            assertTrue(output.contains("1.[T][ ] read book"));
            assertFalse(output.contains("write report"));
        }

        @Test
        void execute_noMatches_printsHeaderOnlyWithNoTaskLines() {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("write report"));

            new FindCommand("book").execute(tasks, ui, storage);

            String output = capturedOut.toString();
            assertTrue(output.contains("Sniffed them out! Here are your matching tasks:"));
            assertFalse(output.contains("write report"));
        }

        @Test
        void execute_doesNotModifyOriginalTaskList() {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("read book"));
            tasks.add(new Todo("write report"));

            new FindCommand("book").execute(tasks, ui, storage);

            assertEquals(2, tasks.size());
        }

        @Test
        void isExit_isFalse() {
            assertFalse(new FindCommand("book").isExit());
        }
    }

    @Nested
    class ExitCommandTests {
        @Test
        void isExit_isTrue() {
            assertTrue(new ExitCommand().isExit());
        }

        @Test
        void execute_printsGoodbyeAndDoesNotThrow() {
            new ExitCommand().execute(new TaskList(), ui, storage);
            assertTrue(capturedOut.toString().contains("Scampering off! See you next time, task buddy."));
        }
    }
}
