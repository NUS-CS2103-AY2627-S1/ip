package gunna.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Deadline;
import gunna.task.Todo;

/**
 * JUnit test class for testing the SearchCommand class.
 * Tests cover search functionality and validation of keywords.
 */
public class SearchCommandTest {
    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage("data/test_command_tasks.txt");
    }

    @Test
    public void execute_validKeyword_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2024, 6, 15)));
        tasks.add(new Todo("buy groceries"));
        SearchCommand command = new SearchCommand("book");

        // Should not throw exception
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_emptyKeyword_throwsException() {
        SearchCommand command = new SearchCommand("");

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("Search keyword required"),
                "Exception should indicate that a search keyword is required");
    }

    @Test
    public void execute_keywordNotFound_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        SearchCommand command = new SearchCommand("xyz");

        // Should not throw exception even when no matches found
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_caseInsensitiveSearch_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("Read Book"));
        tasks.add(new Todo("buy groceries"));
        SearchCommand command = new SearchCommand("book");

        // Should find "Read Book" with lowercase "book" search
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_partialMatch_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("reading books"));
        tasks.add(new Todo("bookstore visit"));
        tasks.add(new Todo("buy groceries"));
        SearchCommand command = new SearchCommand("book");

        // Should find both tasks containing "book"
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_emptyTaskList_executesSuccessfully() throws DukeException {
        SearchCommand command = new SearchCommand("book");

        // Should not throw exception even with empty task list
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_multipleMatches_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2024, 6, 15)));
        tasks.add(new Todo("buy book"));
        SearchCommand command = new SearchCommand("book");

        // Should find all three matching tasks
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_whitespaceKeyword_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("read book"));
        SearchCommand command = new SearchCommand("   ");

        // Should execute (though may not find matches)
        command.execute(tasks, ui, storage);
    }
}
