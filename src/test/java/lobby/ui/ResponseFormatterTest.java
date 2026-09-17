package lobby.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lobby.task.Deadline;
import lobby.task.Event;
import lobby.task.TaskList;
import lobby.task.Todo;

/**
 * Tests exact task-list formatting shared by the console and graphical UI.
 */
public class ResponseFormatterTest {
    private final ResponseFormatter formatter = new ResponseFormatter();

    @Test
    public void formatTaskList_emptyList_returnsOnlyIntroduction() {
        assertEquals(" Tasks:", formatter.formatTaskList(" Tasks:", new TaskList()));
    }

    @Test
    public void formatTaskList_mixedTasks_preservesOrderNumberingAndLineBreaks() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(todo,
                new Deadline("return book", LocalDate.of(2026, 9, 30)),
                new Event("meeting", "Monday", "Tuesday"));
        String expected = String.join(System.lineSeparator(),
                " Tasks:",
                " 1.[T][X] read book",
                " 2.[D][ ] return book (by: Sep 30 2026)",
                " 3.[E][ ] meeting (from: Monday to: Tuesday)");

        assertEquals(expected, formatter.formatTaskList(" Tasks:", tasks));
    }
}
