package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests exact response formatting independently of command parsing and persistence.
 */
public class UiTest {

    @ParameterizedTest
    @CsvSource({"0, missions", "1, mission", "2, missions"})
    public void formatTaskAddedAndDeleted_taskCounts_useCorrectGrammar(int count, String noun) {
        Ui ui = new Ui();
        Task task = new Todo("read book");
        String countMessage = "\nFlight plan now holds " + count + " " + noun + ".";

        assertEquals("Mission logged:\n\t[T][ ] read book" + countMessage,
                ui.formatTaskAdded(task, count));
        assertEquals("Mission scrubbed from the flight plan:\n\t[T][ ] read book" + countMessage,
                ui.formatTaskDeleted(task, count));
    }

    @Test
    public void formatTaskList_mixedTasks_preservesOrderAndNumbersFromOne() {
        Ui ui = new Ui();
        Task todo = new Todo("read book");
        todo.markAsDone();
        List<Task> tasks = List.of(todo, new Deadline("return book", "2024-02-29"),
                new Event("meeting", "2024-12-31", "2025-01-01"));
        String rows = "\n\t1.[T][X] read book\n\t2.[D][ ] return book (by: Feb 29 2024)"
                + "\n\t3.[E][ ] meeting (from: Dec 31 2024 to: Jan 1 2025)";

        assertEquals("Flight plan, coming right up:" + rows, ui.formatTaskList(tasks));
        assertEquals("Scanner results—matching missions:" + rows, ui.formatMatchingTasks(tasks));
        assertEquals("Flight plan, coming right up:", ui.formatTaskList(List.of()));
        assertEquals("Scanner results—matching missions:", ui.formatMatchingTasks(List.of()));
    }

    @Test
    public void formatTaskStatusUpdated_bothStatuses_formatsHeadingAndTask() {
        Ui ui = new Ui();
        Task task = new Todo("read book");
        task.markAsDone();

        assertEquals("Touchdown! Mission complete:\n\t[T][X] read book",
                ui.formatTaskStatusUpdated(task, true));
        task.markAsUndone();
        assertEquals("Course corrected. Mission active again:\n\t[T][ ] read book",
                ui.formatTaskStatusUpdated(task, false));
    }
}
