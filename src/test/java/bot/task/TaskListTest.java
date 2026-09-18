package bot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bot.parser.TaskDateTime;

class TaskListTest {

    @Test
    void constructor_default_startsEmpty() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    void constructor_withTasks_copiesGivenTasks() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));
        assertEquals(1, taskList.size());
        assertEquals("[T][ ] read book", taskList.get(0).toString());
    }

    @Test
    void add_appendsToEnd() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));
        assertEquals(2, taskList.size());
        assertEquals("[T][ ] return book", taskList.get(1).toString());
    }

    @Test
    void containsEquivalent_ignoresDoneStatus() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.mark(0);
        assertTrue(taskList.containsEquivalent(new Todo("read book")));
    }

    @Test
    void delete_removesAndReturnsTask() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));
        Task removed = taskList.delete(0);
        assertEquals("[T][ ] read book", removed.toString());
        assertEquals(1, taskList.size());
        assertEquals("[T][ ] return book", taskList.get(0).toString());
    }

    @Test
    void mark_marksTaskAtIndexDone() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.mark(0);
        assertEquals("[T][X] read book", taskList.get(0).toString());
    }

    @Test
    void unmark_marksTaskAtIndexNotDone() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.mark(0);
        taskList.unmark(0);
        assertEquals("[T][ ] read book", taskList.get(0).toString());
    }

    @Test
    void tasksOn_returnsOnlyTasksOccurringOnThatDate() throws Exception {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));
        taskList.add(new Deadline("pay bills", TaskDateTime.parse("2019-10-16")));

        List<Task> onDate = taskList.tasksOn(LocalDate.of(2019, 10, 15));

        assertEquals(1, onDate.size());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", onDate.get(0).toString());
    }

    @Test
    void findByKeyword_matchesCaseInsensitively() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read Book"));
        taskList.add(new Todo("join club"));

        List<Task> matches = taskList.findByKeyword("BOOK");

        assertEquals(1, matches.size());
        assertEquals("[T][ ] read Book", matches.get(0).toString());
    }

    @Test
    void findByKeyword_noMatch_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertTrue(taskList.findByKeyword("xyz").isEmpty());
    }

    @Test
    void sort_putsTodosFirstThenDatedTasksChronologically() throws Exception {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("pay bills", TaskDateTime.parse("2019-10-20")));
        taskList.add(new Todo("read book"));
        taskList.add(new Event("project meeting", TaskDateTime.parse("2019-10-15"), TaskDateTime.parse("2019-10-16")));
        taskList.add(new Todo("join club"));

        taskList.sort();

        assertEquals("[T][ ] read book", taskList.get(0).toString());
        assertEquals("[T][ ] join club", taskList.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)", taskList.get(2).toString());
        assertEquals("[D][ ] pay bills (by: Oct 20 2019)", taskList.get(3).toString());
    }

    @Test
    void sort_sameDate_keepsOriginalRelativeOrder() throws Exception {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));
        taskList.add(new Deadline("pay bills", TaskDateTime.parse("2019-10-15")));

        taskList.sort();

        assertEquals("[D][ ] return book (by: Oct 15 2019)", taskList.get(0).toString());
        assertEquals("[D][ ] pay bills (by: Oct 15 2019)", taskList.get(1).toString());
    }

    @Test
    void asList_returnsTasksInOrder() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));

        List<Task> asList = taskList.asList();

        assertEquals(2, asList.size());
        assertEquals("[T][ ] read book", asList.get(0).toString());
    }

    @Test
    void asList_isUnmodifiable() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        List<Task> asList = taskList.asList();

        assertThrows(UnsupportedOperationException.class, () -> asList.add(new Todo("extra")));
    }
}
