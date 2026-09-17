package orbit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Verifies exact task formatting shared by list and search responses. */
public class UiTest {
    @Test
    public void formatTaskList_emptyList_returnsHeadingWithoutNewline() {
        assertEquals("Tasks:", Ui.formatTaskList("Tasks:", List.of()));
    }

    @Test
    public void formatTaskList_mixedTasks_preservesOrderStatusAndNumbering() {
        Todo done = new Todo("read book");
        done.markAsDone();
        List<Task> tasks = List.of(done, new Event("meeting", "2pm", "3pm"));

        assertEquals("Tasks:\n 1.[T][X] read book\n 2.[E][ ] meeting (from: 2pm to: 3pm)",
                Ui.formatTaskList("Tasks:", tasks));
        assertEquals(List.of(done, tasks.get(1)), tasks);
    }

    @Test
    public void formatTaskList_searchSubset_numbersFromOne() {
        TaskList tasks = new TaskList(List.of(new Todo("buy milk"), new Todo("read book")));

        assertEquals("Matches:\n 1.[T][ ] read book", Ui.formatTaskList("Matches:", tasks.find("book")));
    }
}
