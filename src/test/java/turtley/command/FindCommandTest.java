package turtley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import turtley.exception.TurtleyException;
import turtley.model.Deadline;
import turtley.model.TaskList;
import turtley.model.ToDo;
import turtley.storage.Storage;
import turtley.ui.Ui;

/** Tests keyword matching and output for the find command. */
class FindCommandTest {

    @Test
    void execute_matchingKeyword_displaysCaseInsensitiveMatchesWithOriginalNumbers() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", "2026-06-06"));
        tasks.add(new ToDo("join sports club"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FindCommand("BOOK").execute(tasks, new Ui(new PrintStream(output)), quietStorage());

        assertEquals("____________________________________________________________\n"
                + " Here are the matching tasks in your list:\n"
                + " 1.[T][ ] read book\n"
                + " 2.[D][ ] return book (by: 2026-06-06)\n"
                + "____________________________________________________________\n",
                output.toString().replace(System.lineSeparator(), "\n"));
    }

    @Test
    void execute_nonMatchingKeyword_displaysNoMatchingTasksMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FindCommand("movie").execute(tasks, new Ui(new PrintStream(output)), quietStorage());

        assertEquals("____________________________________________________________\n"
                + " Here are the matching tasks in your list:\n"
                + "None! o/T\\>\n"
                + "____________________________________________________________\n",
                output.toString().replace(System.lineSeparator(), "\n"));
    }

    @Test
    void execute_tagKeyword_displaysTaskMatchingTag() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book", List.of("#personal")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FindCommand("#PER").execute(tasks, new Ui(new PrintStream(output)), quietStorage());

        assertTrue(output.toString().contains("1.[T][ ] read book [#personal]"));
    }

    @Test
    void execute_missingKeyword_throwsFormatError() {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new FindCommand(" ").execute(new TaskList(), quietUi(), quietStorage()));

        assertEquals("Invalid format. Use: find <keyword>", exception.getMessage());
    }

    private static Ui quietUi() {
        return new Ui(new PrintStream(OutputStream.nullOutputStream()));
    }

    private static Storage quietStorage() {
        return new Storage("build/find-command-test.txt");
    }
}
