package aether.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void taskOperationsChangeOnlyTheRequestedTaskAndPreserveOrder() {
        Todo readBook = new Todo("read book");
        Deadline returnBook = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event meeting = new Event("project meeting", LocalDate.of(2019, 10, 16), LocalDate.of(2019, 10, 17));
        TaskList tasks = new TaskList(readBook, returnBook, meeting);

        assertSame(returnBook, tasks.markTask(1));
        assertEquals(TaskStatus.COMPLETED, tasks.getTaskStatus(1));
        assertEquals(TaskStatus.PENDING, tasks.getTaskStatus(0));
        assertSame(returnBook, tasks.unmarkTask(1));
        assertEquals(TaskStatus.PENDING, tasks.getTaskStatus(1));

        assertSame(returnBook, tasks.deleteTask(1));
        assertEquals(2, tasks.getTaskCount());
        assertEquals(List.of(readBook, meeting), tasks.asList());
    }

    @Test
    void restoreTaskAndSetTaskStatusUndoFailedChangesAtTheirOriginalIndex() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(first, second);

        Task deleted = tasks.deleteTask(0);
        tasks.restoreTask(0, deleted);
        tasks.setTaskStatus(1, TaskStatus.COMPLETED);

        assertEquals(List.of(first, second), tasks.asList());
        assertEquals(TaskStatus.COMPLETED, tasks.getTaskStatus(1));
        assertEquals("[T][X] second", tasks.asList().get(1).toString());
    }

    @Test
    void formatTaskListNumbersTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Oct 15 2019)", tasks.formatTaskList());
    }

    @Test
    void formatMatchingTasksFiltersDescriptionsCaseInsensitively() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("buy milk"));
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("write report"));
        tasks.addTask(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        tasks.markTask(3);

        assertEquals("Here are the matching tasks in your list:\n"
                + "2.[T][ ] read book\n"
                + "4.[D][X] return book (by: Oct 15 2019)", tasks.formatMatchingTasks("BOOK"));
        assertEquals("Here are the matching tasks in your list:", tasks.formatMatchingTasks("missing"));
    }

    @Test
    void sortByDateOrdersDatedTasksStablyAndPlacesTodosLast() {
        Todo buyMilk = new Todo("buy milk");
        Deadline submitReport = new Deadline("submit report", LocalDate.of(2026, 9, 12));
        Event workshop = new Event("workshop", LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 6));
        Deadline renewPass = new Deadline("renew pass", LocalDate.of(2026, 9, 5));
        Todo readBook = new Todo("read book");
        TaskList tasks = new TaskList(buyMilk, submitReport, workshop, renewPass, readBook);

        List<Task> previousOrder = tasks.sortByDate();

        assertEquals(List.of(buyMilk, submitReport, workshop, renewPass, readBook), previousOrder);
        assertEquals(List.of(workshop, renewPass, submitReport, buyMilk, readBook), tasks.asList());

        tasks.restoreOrder(previousOrder);

        assertEquals(previousOrder, tasks.asList());
    }

    @Test
    void asListCannotBeChangedOutsideTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        List<Task> taskView = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () -> taskView.add(new Todo("borrow book")));
        assertEquals(1, tasks.getTaskCount());
    }

    @Test
    void containsEquivalentTaskComparesTypeDescriptionAndDates() {
        TaskList tasks = new TaskList(
                new Todo("Read book"),
                new Deadline("submit report", LocalDate.of(2026, 9, 20)),
                new Event("camp", LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 22)));

        assertTrue(tasks.containsEquivalentTask(new Todo("read book")));
        assertFalse(tasks.containsEquivalentTask(
                new Deadline("submit report", LocalDate.of(2026, 9, 21))));
        assertFalse(tasks.containsEquivalentTask(
                new Event("camp", LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 23))));
    }
}
