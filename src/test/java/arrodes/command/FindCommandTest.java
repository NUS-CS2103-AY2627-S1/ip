package arrodes.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import arrodes.exception.ArrodesException;
import arrodes.task.Deadline;
import arrodes.task.TaskList;
import arrodes.task.Todo;
import arrodes.ui.Ui;
import arrodes.ui.cli.Cli;

/** Tests the task-description search performed by {@link FindCommand}. */
class FindCommandTest {

    /** Verifies that matching tasks retain their original one-based numbers. */
    @Test
    void execute_matchingDescriptions_displaysMatchingTasksOnly() throws ArrodesException {
        TaskList taskList = new TaskList(100);
        taskList.insert(new Todo("read book"));
        taskList.insert(new Deadline("return book", LocalDateTime.of(2026, 6, 6, 0, 0)));
        taskList.insert(new Todo("wash dishes"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new CapturingUi(output);

        new FindCommand("book").execute(ui, taskList, null);

        assertEquals("Arrodes reveals the records matching your words:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Jun 06 2026)\n", normalise(output));
    }

    /** Verifies that matching ignores differences in letter case. */
    @Test
    void execute_keywordWithDifferentCase_findsDescription() throws ArrodesException {
        TaskList taskList = new TaskList(100);
        taskList.insert(new Todo("Read Book"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FindCommand("book").execute(new CapturingUi(output), taskList, null);

        assertEquals("Arrodes reveals the records matching your words:\n1.[T][ ] Read Book\n",
                normalise(output));
    }

    /** Verifies that an empty task list produces the no-match message. */
    @Test
    void execute_emptyTaskList_displaysNoMatchMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FindCommand("book").execute(new CapturingUi(output), new TaskList(100), null);

        assertEquals("Arrodes reveals the records matching your words:\n"
                + "The mirror finds no record matching those words.\n", normalise(output));
    }

    /** Converts platform-specific line endings to the format used by assertions. */
    private String normalise(ByteArrayOutputStream output) {
        return output.toString().replace("\r\n", "\n");
    }

    /** UI implementation that captures messages for exact output assertions. */
    private static class CapturingUi extends Cli {
        /** Stream receiving each message shown by the command. */
        private final PrintStream output;

        /** Creates a UI that writes messages to the supplied stream. */
        CapturingUi(ByteArrayOutputStream output) {
            this.output = new PrintStream(output);
        }

        @Override
        public void showMessage(String message) {
            output.println(message);
        }
    }
}
