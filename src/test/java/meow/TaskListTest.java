package meow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    void findTasks_matchingKeyword_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Todo("return BOOK"));

        TaskList matches = tasks.findTasks("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.getTask(0).getDescription());
        assertEquals("return BOOK", matches.getTask(1).getDescription());
    }

    @Test
    void sortChronologically_mixedTasks_sortsDatedTasksAndPlacesTodosLast() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first todo"));
        tasks.add(new Deadline(
                "later deadline",
                LocalDate.of(2026, 9, 20)));
        tasks.add(new Event(
                "early event",
                LocalDate.of(2026, 9, 5),
                LocalDate.of(2026, 9, 6)));
        tasks.add(new Deadline(
                "middle deadline",
                LocalDate.of(2026, 9, 10)));
        tasks.add(new Todo("second todo"));

        tasks.sortChronologically();

        assertEquals("early event", tasks.getTask(0).getDescription());
        assertEquals("middle deadline", tasks.getTask(1).getDescription());
        assertEquals("later deadline", tasks.getTask(2).getDescription());
        assertEquals("first todo", tasks.getTask(3).getDescription());
        assertEquals("second todo", tasks.getTask(4).getDescription());
    }

    @Test
    void sortChronologically_sameDate_preservesRelativeOrder() {
        TaskList tasks = new TaskList();

        tasks.add(new Deadline(
                "first dated task",
                LocalDate.of(2026, 9, 10)));
        tasks.add(new Event(
                "second dated task",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 11)));

        tasks.sortChronologically();

        assertEquals("first dated task", tasks.getTask(0).getDescription());
        assertEquals("second dated task", tasks.getTask(1).getDescription());
    }

    @Test
    void sortChronologically_onlyTodos_preservesOrder() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first todo"));
        tasks.add(new Todo("second todo"));

        tasks.sortChronologically();

        assertEquals("first todo", tasks.getTask(0).getDescription());
        assertEquals("second todo", tasks.getTask(1).getDescription());
    }
}

