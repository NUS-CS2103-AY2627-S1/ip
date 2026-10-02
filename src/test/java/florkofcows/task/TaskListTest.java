package florkofcows.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import florkofcows.exception.FlorkingExceptions;

public class TaskListTest {
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));
    }

    @Test
    public void mark_validIndex_marksTaskDone() throws FlorkingExceptions {
        Task marked = taskList.mark(1);
        assertTrue(marked.isDone());
    }

    @Test
    public void unmark_validIndex_marksTaskNotDone() throws FlorkingExceptions {
        taskList.mark(1);
        Task unmarked = taskList.unmark(1);
        assertFalse(unmarked.isDone());
    }

    @Test
    public void mark_indexTooLow_exceptionThrown() {
        assertThrows(FlorkingExceptions.class, () -> taskList.mark(0));
    }

    @Test
    public void mark_indexTooHigh_exceptionThrown() {
        assertThrows(FlorkingExceptions.class, () -> taskList.mark(99));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws FlorkingExceptions {
        Task removed = taskList.delete(1);
        assertEquals("read book", removed.getDescription());
        assertEquals(1, taskList.size());
    }

    @Test
    public void delete_indexOutOfRange_exceptionThrown() {
        assertThrows(FlorkingExceptions.class, () -> taskList.delete(5));
    }

    @Test
    public void parseIndex_validNumber_returnsParsedIndex() throws FlorkingExceptions {
        assertEquals(1, taskList.parseIndex("1", "mark"));
    }

    @Test
    public void parseIndex_emptyArgument_exceptionThrown() {
        assertThrows(FlorkingExceptions.class, () -> taskList.parseIndex("", "mark"));
    }

    @Test
    public void parseIndex_nonNumericArgument_exceptionThrown() {
        assertThrows(FlorkingExceptions.class, () -> taskList.parseIndex("abc", "mark"));
    }

    @Test
    public void parseIndex_outOfRangeNumber_exceptionThrown() {
        assertThrows(FlorkingExceptions.class, () -> taskList.parseIndex("99", "mark"));
    }

    @Test
    public void getTasksOn_matchingDate_returnsOnlyMatchingTasks() throws FlorkingExceptions {
        TaskList list = new TaskList();
        list.add(new Deadline("submit", "2019-12-02"));
        list.add(new Todo("unrelated"));
        ArrayList<Task> matches = list.getTasksOn(java.time.LocalDate.of(2019, 12, 2));
        assertEquals(1, matches.size());
        assertEquals("submit", matches.get(0).getDescription());
    }

    @Test
    public void getTasksOn_noMatches_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("unrelated"));
        assertTrue(list.getTasksOn(java.time.LocalDate.of(2019, 12, 2)).isEmpty());
    }

    @Test
    public void findTasks_matchingKeyword_returnsOnlyMatchingTasks() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("return book"));
        list.add(new Todo("walk dog"));

        ArrayList<Task> matches = list.findTasks("book");
        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void findTasks_noMatches_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("walk dog"));
        assertTrue(list.findTasks("book").isEmpty());
    }

    @Test
    public void tag_validTags_addsTagsToTask() throws FlorkingExceptions {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));

        Task tagged = list.tag(1, "#fun", "study");

        assertTrue(tagged.hasTag("#fun"));
        assertTrue(tagged.hasTag("#study"));
        assertEquals(2, tagged.getTags().size());
    }

    @Test
    public void tag_duplicateTag_doesNotDuplicate() throws FlorkingExceptions {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));

        Task tagged = list.tag(1, "#fun", "#fun");

        assertEquals(1, tagged.getTags().size());
    }

    @Test
    public void getAll_returnsUnderlyingList() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));

        assertEquals(1, list.getAll().size());
        assertEquals("read book", list.getAll().get(0).getDescription());
    }

    @Test
    public void findTasks_keywordMatchesSubstring_returnsMatchingTasks() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("book club"));

        ArrayList<Task> matches = list.findTasks("boo");

        assertEquals(2, matches.size());
    }
}
