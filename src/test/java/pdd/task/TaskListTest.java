package pdd.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pdd.PDDException;

public class TaskListTest {
    @Test
    public void toIndex_validNumber_returnsZeroBasedIndex() throws PDDException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("a"), new Todo("b"))));
        assertEquals(0, tasks.toIndex("1"));
        assertEquals(1, tasks.toIndex("2"));
    }

    @Test
    public void toIndex_nonNumeric_throwsExceptionWithHint() {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("a"))));
        PDDException e = assertThrows(PDDException.class, () -> tasks.toIndex("abc"));
        assertEquals("OOPS!!! Please provide a valid task number, e.g. mark 2", e.getMessage());
    }

    @Test
    public void toIndex_zero_throwsOutOfRangeException() {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("a"))));
        PDDException e = assertThrows(PDDException.class, () -> tasks.toIndex("0"));
        assertEquals("OOPS!!! Task number 0 does not exist. You have 1 task(s) in the list.", e.getMessage());
    }

    @Test
    public void toIndex_greaterThanSize_throwsOutOfRangeException() {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("a"))));
        PDDException e = assertThrows(PDDException.class, () -> tasks.toIndex("5"));
        assertEquals("OOPS!!! Task number 5 does not exist. You have 1 task(s) in the list.", e.getMessage());
    }

    @Test
    public void add_task_increasesSizeAndIsRetrievable() {
        TaskList tasks = new TaskList(new ArrayList<>());
        Todo todo = new Todo("read book");
        tasks.add(todo);
        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() {
        Todo first = new Todo("a");
        Todo second = new Todo("b");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(first, second)));
        Todo removed = (Todo) tasks.delete(0);
        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    public void findMatching_keywordInSomeDescriptions_returnsOnlyMatches() {
        Todo readBook = new Todo("read book");
        Todo returnBook = new Todo("return book");
        Todo joinClub = new Todo("join sports club");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(readBook, returnBook, joinClub)));
        assertEquals(List.of(readBook, returnBook), tasks.findMatching("book"));
    }

    @Test
    public void findMatching_differentCase_stillMatches() {
        Todo readBook = new Todo("read book");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(readBook)));
        assertEquals(List.of(readBook), tasks.findMatching("BOOK"));
    }

    @Test
    public void findMatching_noDescriptionContainsKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("read book"))));
        assertEquals(List.of(), tasks.findMatching("xyz"));
    }

    @Test
    public void sortTasks_mixedTaskTypes_datedTasksFirstChronologicallyThenUndatedAlphabetically() {
        Todo zTodo = new Todo("z todo");
        Todo aTodo = new Todo("a todo");
        Deadline laterDeadline = new Deadline("later deadline", LocalDate.of(2019, 12, 15));
        Event earlierEvent = new Event("earlier event", LocalDate.of(2019, 12, 1), "4pm");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(zTodo, laterDeadline, aTodo, earlierEvent)));

        tasks.sortTasks();

        assertEquals(List.of(earlierEvent, laterDeadline, aTodo, zTodo), tasks.getTasks());
    }
}
