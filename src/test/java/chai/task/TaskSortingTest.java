package chai.task;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests chronological ordering across different task types. */
public class TaskSortingTest {

    @Test
    public void getSortDate_mixedTasks_ordersDatedTasksBeforeTodos() {
        Todo firstTodo = new Todo("borrow book");
        Todo secondTodo = new Todo("read book");
        Event laterEvent = new Event(
                "conference", LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 3));
        Deadline earlierDeadline = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        Event sameDayEvent = new Event(
                "presentation", LocalDate.of(2026, 9, 30), LocalDate.of(2026, 9, 30));
        List<Task> tasks = new ArrayList<>(List.of(
                firstTodo, laterEvent, earlierDeadline, secondTodo, sameDayEvent));

        tasks.sort(Comparator.comparing(Task::getSortDate));

        assertIterableEquals(
                List.of(earlierDeadline, sameDayEvent, laterEvent, firstTodo, secondTodo), tasks);
    }
}
