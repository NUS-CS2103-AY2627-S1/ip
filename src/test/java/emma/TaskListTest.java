package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks the task list itself: how it numbers tasks from one, which numbers it refuses,
 * and that the view it hands out cannot be used to change it behind its back.
 */
public class TaskListTest {

    private static final LocalDate OCT_15 = LocalDate.of(2019, 10, 15);
    private static final LocalDate OCT_16 = LocalDate.of(2019, 10, 16);

    /** A list holding a todo, a deadline and an event, in that order. */
    private TaskList threeTasks() {
        return new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", OCT_15),
                new Event("meeting", OCT_15, OCT_16)));
    }

    @Test
    public void constructor_emptyList_startsEmpty() {
        TaskList tasks = new TaskList(List.of());
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
        assertEquals("", tasks.format());
    }

    @Test
    public void constructor_givenTasks_keepsThemInOrder() {
        assertEquals("1. [T][ ] read book\n"
                + "2. [D][ ] return book (by: Oct 15 2019)\n"
                + "3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", threeTasks().format());
    }

    @Test
    public void constructor_callerChangesTheListAfterwards_theTaskListIsUnaffected() {
        List<Task> initial = new ArrayList<>(List.of(new Todo("read book")));
        TaskList tasks = new TaskList(initial);
        initial.add(new Todo("sneaked in"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_task_appendsItToTheEnd() {
        TaskList tasks = new TaskList(List.of(new Todo("first")));
        tasks.add(new Todo("second"));
        assertEquals(2, tasks.size());
        assertEquals("1. [T][ ] first\n2. [T][ ] second", tasks.format());
    }

    @Test
    public void get_firstAndLastNumbers_returnThoseTasks() {
        TaskList tasks = threeTasks();
        assertEquals("[T][ ] read book", tasks.get(1).toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", tasks.get(3).toString());
    }

    @Test
    public void get_numberOutsideTheList_isRejected() {
        TaskList tasks = threeTasks();
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(4));
    }

    @Test
    public void get_fromAnEmptyList_isRejected() {
        TaskList tasks = new TaskList(List.of());
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
    }

    @Test
    public void applyMark_task_marksItAndReturnsIt() {
        TaskList tasks = threeTasks();
        Task marked = tasks.applyMark(2, true);
        assertTrue(marked.isDone());
        assertSame(tasks.get(2), marked);
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void applyMark_taskThatIsAlreadyDone_leavesItDone() {
        TaskList tasks = threeTasks();
        tasks.applyMark(1, true);
        tasks.applyMark(1, true);
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void applyMark_numberOutsideTheList_isRejected() {
        TaskList tasks = threeTasks();
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.applyMark(4, true));
    }

    @Test
    public void delete_task_removesItAndRenumbersTheRest() {
        TaskList tasks = threeTasks();
        Task removed = tasks.delete(1);
        assertEquals("[T][ ] read book", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("1. [D][ ] return book (by: Oct 15 2019)\n"
                + "2. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", tasks.format());
    }

    @Test
    public void delete_numberOutsideTheList_isRejected() {
        TaskList tasks = threeTasks();
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(4));
        assertEquals(3, tasks.size());
    }

    @Test
    public void insert_atAPositionInTheMiddle_pushesTheRestDown() {
        TaskList tasks = threeTasks();
        tasks.insert(2, new Todo("squeezed in"));
        assertEquals("1. [T][ ] read book\n"
                + "2. [T][ ] squeezed in\n"
                + "3. [D][ ] return book (by: Oct 15 2019)\n"
                + "4. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", tasks.format());
    }

    @Test
    public void insert_onePastTheEnd_isAllowedAndAppends() {
        TaskList tasks = threeTasks();
        tasks.insert(4, new Todo("last"));
        assertEquals(4, tasks.size());
        assertEquals("[T][ ] last", tasks.get(4).toString());
    }

    @Test
    public void insert_intoAnEmptyList_isAllowedAtPositionOne() {
        TaskList tasks = new TaskList(List.of());
        tasks.insert(1, new Todo("only"));
        assertEquals("1. [T][ ] only", tasks.format());
    }

    @Test
    public void insert_furtherPastTheEndOrBeforeTheStart_isRejected() {
        TaskList tasks = threeTasks();
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.insert(5, new Todo("too far")));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.insert(0, new Todo("too early")));
    }

    @Test
    public void deleteThenInsert_atTheSameNumber_restoresTheOriginalOrder() {
        TaskList tasks = threeTasks();
        String before = tasks.format();
        Task removed = tasks.delete(2);
        tasks.insert(2, removed);
        assertEquals(before, tasks.format());
    }

    @Test
    public void isEmpty_afterTheLastTaskIsDeleted_isTrueAgain() {
        TaskList tasks = new TaskList(List.of(new Todo("only")));
        assertFalse(tasks.isEmpty());
        tasks.delete(1);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void getTasks_theViewReturned_cannotBeChanged() {
        TaskList tasks = threeTasks();
        List<Task> view = tasks.getTasks();
        assertEquals(3, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("nope")));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
    }

    @Test
    public void getTasks_afterTheListChanges_theViewShowsTheChange() {
        TaskList tasks = threeTasks();
        List<Task> view = tasks.getTasks();
        tasks.add(new Todo("fourth"));
        assertEquals(4, view.size());
    }

    @Test
    public void format_withATest_keepsEachTaskNumberFromTheFullList() {
        TaskList tasks = threeTasks();
        assertEquals("2. [D][ ] return book (by: Oct 15 2019)",
                tasks.format(task -> task instanceof Deadline));
    }

    @Test
    public void format_withATestNothingPasses_isEmpty() {
        assertEquals("", threeTasks().format(task -> false));
    }

    @Test
    public void format_withATestEverythingPasses_matchesTheFullList() {
        TaskList tasks = threeTasks();
        assertEquals(tasks.format(), tasks.format(task -> true));
    }

    @Test
    public void contains_aTaskMatchingOneStored_isTrue() {
        TaskList tasks = threeTasks();
        assertTrue(tasks.contains(new Todo("read book")));
        assertTrue(tasks.contains(new Deadline("return book", OCT_15)));
        assertTrue(tasks.contains(new Event("meeting", OCT_15, OCT_16)));
    }

    @Test
    public void contains_aTaskMatchingNothingStored_isFalse() {
        TaskList tasks = threeTasks();
        assertFalse(tasks.contains(new Todo("buy milk")));
        assertFalse(tasks.contains(new Todo("return book")));
        assertFalse(tasks.contains(new Deadline("return book", OCT_16)));
    }

    @Test
    public void contains_onAnEmptyList_isFalse() {
        assertFalse(new TaskList(List.of()).contains(new Todo("read book")));
    }
}
