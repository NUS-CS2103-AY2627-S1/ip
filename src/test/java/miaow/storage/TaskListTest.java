package miaow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import miaow.task.Deadline;
import miaow.task.Event;
import miaow.task.Task;
import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void addTaskRejectsNullAndDuplicateDetails() {
        TaskList list = new TaskList();
        list.addTask(new Task("same task"));

        assertThrows(
                IllegalArgumentException.class,
                () -> list.addTask(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> list.addTask(new Task("same task"))
        );

        assertEquals(1, list.size());
    }

    @Test
    void invalidIndexesAreRejected() {
        TaskList list = new TaskList();

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> list.getTask(0)
        );

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> list.deleteTask(-1)
        );

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> list.markTask(1)
        );
    }

    @Test
    void findIsCaseInsensitiveAndHandlesBlankKeyword() {
        TaskList list = new TaskList();
        list.addTask(new Task("Finish Homework"));

        assertEquals(
                1,
                list.findTasksByKeyword(" homework ").size()
        );

        assertTrue(list.findTasksByKeyword(null).isEmpty());
        assertTrue(list.findTasksByKeyword("   ").isEmpty());
    }

    @Test
    void sortPutsUndatedTasksAfterDatedTasks() {
        Deadline later = new Deadline("later");
        later.by("2026-12-01");

        Deadline earlier = new Deadline("earlier");
        earlier.by("2026-01-01");

        TaskList list = new TaskList(new ArrayList<>());
        list.addTask(new Task("undated"));
        list.addTask(later);
        list.addTask(earlier);

        list.sortChronologically();

        assertEquals("earlier", list.getTask(0).getDescription());
        assertEquals("later", list.getTask(1).getDescription());
        assertEquals("undated", list.getTask(2).getDescription());
    }

    @Test
    void eventWithInvalidRangeIsRejectedByModel() {
        Event event = new Event("meeting");
        event.from("2026-01-02");

        assertThrows(
                IllegalArgumentException.class,
                () -> event.to("2026-01-01")
        );
    }

    @Test
    void eventWithEqualStartAndEndDatesIsRejected() {
        Event event = new Event("meeting");
        event.from("2026-01-02");

        assertThrows(
                IllegalArgumentException.class,
                () -> event.to("2026-01-02")
        );
    }
}
