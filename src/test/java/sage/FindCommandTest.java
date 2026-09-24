package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import sage.command.FindCommand;
import sage.exception.SageException;
import sage.model.TaskList;
import sage.model.Todo;
import sage.ui.Ui;

class FindCommandTest {
    @Test
    void findCommand_displaysMatchingTasksInOriginalListFormat() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy milk"));
        tasks.get(1).markAsDone();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));
        try {
            new FindCommand("book").execute(tasks, new Ui(), null);
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][X] return book\n", output.toString());
    }

    @Test
    void findCommand_reportsWhenThereAreNoMatches() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));
        try {
            new FindCommand("holiday").execute(new TaskList(), new Ui(), null);
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("There are no tasks matching \"holiday\".\n", output.toString());
    }

    @Test
    void findCommand_rejectsMissingKeyword() {
        assertThrows(SageException.class,
                () -> new FindCommand("").execute(new TaskList(), new Ui(), null));
    }
}
