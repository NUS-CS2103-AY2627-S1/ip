package turtley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import turtley.exception.TurtleyException;
import turtley.model.TaskList;
import turtley.model.ToDo;
import turtley.storage.Storage;
import turtley.ui.Ui;

/** Tests case-insensitive substring filtering by tags. */
class FilterCommandTest {

    @Test
    void filterCommand_matchingSubstring_displaysOriginalTaskNumbers() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first", List.of("#Fun")));
        tasks.add(new ToDo("second", List.of("#work")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new FilterCommand("#f").execute(tasks, new Ui(new PrintStream(output)),
                new Storage("build/filter-command-test.txt"));

        assertEquals(String.join("\n",
                "____________________________________________________________",
                " Here are the tasks with tags matching #f:",
                " 1.[T][ ] first [#Fun]",
                "____________________________________________________________",
                ""), output.toString().replace(System.lineSeparator(), "\n"));
    }

    @Test
    void filterCommand_missingHash_throwsFormatError() {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new FilterCommand("fun").execute(new TaskList(), quietUi(),
                        new Storage("build/filter-command-test.txt")));

        assertEquals("Invalid format. Use: filter #tag-substring", exception.getMessage());
    }

    private static Ui quietUi() {
        return new Ui(new PrintStream(OutputStream.nullOutputStream()));
    }
}
