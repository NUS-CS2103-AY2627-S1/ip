package bot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add((Task) null));
    }

    @Test
    void getTask_indexOutsideList_throwsAssertionError() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(AssertionError.class, () -> tasks.getTask(1));
    }

    @Test
    void add_multipleTasks_addsAllTasksInOrder() {
        TaskList tasks = new TaskList();

        tasks.add(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 6, 6)),
                new Todo("buy milk"));

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTask(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", tasks.getTask(1).toString());
        assertEquals("[T][ ] buy milk", tasks.getTask(2).toString());
    }

    @Test
    void find_caseInsensitivePartialKeyword_returnsMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 6, 6)));
        tasks.add(new Todo("buy milk"));

        TaskList matchingTasks = tasks.find("BoO");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] Read Book", matchingTasks.getTask(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", matchingTasks.getTask(1).toString());
        assertEquals(3, tasks.size());
    }

    @Test
    void find_keywordAppearsOnlyInTaskDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDate.of(2019, 12, 2)));

        TaskList matchingTasks = tasks.find("2019");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    void find_noMatchingDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    void sortChronologically_mixedTasks_ordersDatedTasksBeforeTodos() {
        TaskList tasks = new TaskList();
        tasks.add(
                new Todo("first todo"),
                new Event("later event", LocalDateTime.of(2019, 12, 3, 14, 0),
                        LocalDateTime.of(2019, 12, 3, 16, 0)),
                new Deadline("middle deadline", LocalDate.of(2019, 12, 2)),
                new Event("earliest event", LocalDateTime.of(2019, 12, 1, 9, 0),
                        LocalDateTime.of(2019, 12, 1, 10, 0)),
                new Todo("second todo"));

        tasks.sortChronologically();

        assertEquals("[E][ ] earliest event (from: Dec 01 2019, 9:00AM to: Dec 01 2019, 10:00AM)",
                tasks.getTask(0).toString());
        assertEquals("[D][ ] middle deadline (by: Dec 02 2019)", tasks.getTask(1).toString());
        assertEquals("[E][ ] later event (from: Dec 03 2019, 2:00PM to: Dec 03 2019, 4:00PM)",
                tasks.getTask(2).toString());
        assertEquals("[T][ ] first todo", tasks.getTask(3).toString());
        assertEquals("[T][ ] second todo", tasks.getTask(4).toString());
    }

    @Test
    void sortChronologically_equalKeys_preservesOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(
                new Todo("first todo"),
                new Deadline("first deadline", LocalDate.of(2019, 12, 2)),
                new Todo("second todo"),
                new Deadline("second deadline", LocalDate.of(2019, 12, 2)));

        tasks.sortChronologically();

        assertEquals("[D][ ] first deadline (by: Dec 02 2019)", tasks.getTask(0).toString());
        assertEquals("[D][ ] second deadline (by: Dec 02 2019)", tasks.getTask(1).toString());
        assertEquals("[T][ ] first todo", tasks.getTask(2).toString());
        assertEquals("[T][ ] second todo", tasks.getTask(3).toString());
    }

    @Test
    void containsSameTask_matchingTodoIgnoresCaseAndCompletionStatus_returnsTrue() {
        TaskList tasks = new TaskList();
        Todo existingTask = new Todo("Read Book");
        existingTask.mark();
        tasks.add(existingTask);

        assertTrue(tasks.containsSameTask(new Todo("read book")));
    }

    @Test
    void containsSameTask_sameDescriptionWithDifferentDates_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDate.of(2019, 12, 2)));

        assertFalse(tasks.containsSameTask(
                new Deadline("submit report", LocalDate.of(2019, 12, 3))));
    }

    @Test
    void containsSameTask_matchingEventDetails_returnsTrue() {
        TaskList tasks = new TaskList();
        LocalDateTime start = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime end = LocalDateTime.of(2019, 12, 2, 16, 0);
        tasks.add(new Event("meeting", start, end));

        assertTrue(tasks.containsSameTask(new Event("MEETING", start, end)));
    }
}
