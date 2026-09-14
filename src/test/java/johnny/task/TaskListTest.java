package johnny.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void constructor_noArgs_emptyList() {
        TaskList list = new TaskList();
        assertEquals(0, list.size());
    }

    @Test
    public void constructor_existingList_retainsTasks() {
        ArrayList<Task> existing = new ArrayList<>();
        existing.add(new Todo("read book"));
        existing.add(new Todo("return book"));
        TaskList list = new TaskList(existing);
        assertEquals(2, list.size());
        assertEquals("[T][ ] read book", list.get(0).toString());
    }

    @Test
    public void add_singleTask_incrementsSize() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        assertEquals(1, list.size());
    }

    @Test
    public void add_multipleTasks_correctOrder() {
        TaskList list = new TaskList();
        list.add(new Todo("first"));
        list.add(new Todo("second"));
        list.add(new Todo("third"));
        assertEquals("[T][ ] first", list.get(0).toString());
        assertEquals("[T][ ] second", list.get(1).toString());
        assertEquals("[T][ ] third", list.get(2).toString());
    }

    @Test
    public void add_atIndex_insertsAtRequestedPosition() {
        TaskList list = new TaskList();
        list.add(new Todo("first"));
        list.add(new Todo("third"));

        list.add(1, new Todo("second"));

        assertEquals("[T][ ] second", list.get(1).toString());
        assertEquals("[T][ ] third", list.get(2).toString());
    }

    @Test
    public void delete_middleTask_removesCorrectTask() {
        TaskList list = new TaskList();
        list.add(new Todo("first"));
        list.add(new Todo("second"));
        list.add(new Todo("third"));
        Task removed = list.delete(1);
        assertEquals("[T][ ] second", removed.toString());
        assertEquals(2, list.size());
        assertEquals("[T][ ] third", list.get(1).toString());
    }

    @Test
    public void delete_firstTask_removesCorrectTask() {
        TaskList list = new TaskList();
        list.add(new Todo("first"));
        list.add(new Todo("second"));
        Task removed = list.delete(0);
        assertEquals("[T][ ] first", removed.toString());
        assertEquals(1, list.size());
        assertEquals("[T][ ] second", list.get(0).toString());
    }

    @Test
    public void delete_lastTask_removesCorrectTask() {
        TaskList list = new TaskList();
        list.add(new Todo("first"));
        list.add(new Todo("second"));
        Task removed = list.delete(1);
        assertEquals("[T][ ] second", removed.toString());
        assertEquals(1, list.size());
    }

    @Test
    public void delete_onlyTask_emptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("only"));
        list.delete(0);
        assertEquals(0, list.size());
    }

    @Test
    public void get_outOfBounds_throwsException() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(5));
    }

    @Test
    public void getAll_returnsUnderlyingList() {
        TaskList list = new TaskList();
        list.add(new Todo("first"));
        list.add(new Todo("second"));
        ArrayList<Task> all = list.getAll();
        assertEquals(2, all.size());
    }

    @Test
    public void find_matchingKeyword_returnsMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("return book"));
        list.add(new Todo("buy groceries"));
        ArrayList<Task> matches = list.find("book");
        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[T][ ] return book", matches.get(1).toString());
    }

    @Test
    public void find_noMatches_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        ArrayList<Task> matches = list.find("xyz");
        assertTrue(matches.isEmpty());
    }

    @Test
    public void find_caseInsensitive_returnsMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("Read Book"));
        ArrayList<Task> matches = list.find("book");
        assertEquals(1, matches.size());
    }

    @Test
    public void find_matchesAcrossTaskTypes_returnsAll() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Deadline("return book", LocalDate.of(2024, 9, 15)));
        list.add(new Event("book fair", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3)));
        ArrayList<Task> matches = list.find("book");
        assertEquals(3, matches.size());
    }
}
