package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import duke.task.TaskList;
import duke.ui.CapturingUi;

public class FindCommandTest {

    @Test
    public void execute_keywordMatchesMultipleTasks_showsMatchesInOrder() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.addDeadline("return book", LocalDate.of(2026, 6, 6));
        tasks.addTodo("write notes");
        tasks.mark(0, true);
        tasks.mark(1, true);

        CapturingUi ui = new CapturingUi();

        new FindCommand("book").execute(tasks, ui);

        assertNull(ui.getLastErrorMessage());
        assertEquals(2, ui.getLastShownMatchingTasks().size());
        assertEquals("[T] [X] read book", ui.getLastShownMatchingTasks().get(0).toString());
        assertEquals("[D] [X] return book (by: 2026-06-06)", ui.getLastShownMatchingTasks().get(1).toString());
    }

    @Test
    public void execute_keywordIsCaseInsensitive_showsMatches() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        CapturingUi ui = new CapturingUi();

        new FindCommand("BOOK").execute(tasks, ui);

        assertEquals(1, ui.getLastShownMatchingTasks().size());
        assertEquals("[T] [ ] read book", ui.getLastShownMatchingTasks().get(0).toString());
    }

    @Test
    public void execute_keywordWithRepeatedWhitespace_normalizesAndShowsMatches() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read the book");
        CapturingUi ui = new CapturingUi();

        new FindCommand("  the   book  ").execute(tasks, ui);

        assertEquals(1, ui.getLastShownMatchingTasks().size());
    }

    @Test
    public void execute_keywordWithNoMatches_showsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        CapturingUi ui = new CapturingUi();

        new FindCommand("xyz").execute(tasks, ui);

        assertNull(ui.getLastErrorMessage());
        assertEquals(0, ui.getLastShownMatchingTasks().size());
    }

    @Test
    public void execute_emptyKeyword_showsError() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");

        CapturingUi ui = new CapturingUi();

        new FindCommand("   ").execute(tasks, ui);

        assertEquals("The find keyword cannot be empty!", ui.getLastErrorMessage());
        assertNull(ui.getLastShownMatchingTasks());
    }
}
