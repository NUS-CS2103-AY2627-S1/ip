package yourhand.tasks;

import yourhand.exceptions.YourHandException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link TaskList}. */
class TaskListTest {

    @Test
    public void newTaskList_isEmptyAndHasSizeZero() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
    }

    @Test
    public void addTask_addsTaskAndUpdatesSizeAndEmptyState() {
        TaskList taskList = new TaskList();
        Todo task = new Todo("read book");

        taskList.add(task);

        assertFalse(taskList.isEmpty());
        assertEquals(1, taskList.size());
        assertSame(task, taskList.getTasks().getFirst());
    }

    @Test
    public void addTask_nullTask_throwsIllegalArgumentException() {
        TaskList taskList = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> taskList.add(null));
    }

    @Test
    public void addAt_validIndex_insertsTaskAtRequestedPosition() throws YourHandException {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("first");
        Todo insertedTask = new Todo("inserted");
        taskList.add(firstTask);

        taskList.addAt(0, insertedTask);

        assertSame(insertedTask, taskList.getTask(1));
        assertSame(firstTask, taskList.getTask(2));
    }

    @Test
    public void addAt_invalidIndexOrTask_throwsIllegalArgumentException() {
        TaskList taskList = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> taskList.addAt(-1, new Todo("task")));
        assertThrows(IllegalArgumentException.class, () -> taskList.addAt(1, new Todo("task")));
        assertThrows(IllegalArgumentException.class, () -> taskList.addAt(0, null));
    }

    @Test
    public void getTask_validOneBasedNumber_returnsRequestedTask() throws YourHandException {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        taskList.add(firstTask);
        taskList.add(secondTask);

        assertSame(firstTask, taskList.getTask(1));
        assertSame(secondTask, taskList.getTask(2));
    }

    @Test
    public void getTask_emptyList_throwsYourHandException() {
        TaskList taskList = new TaskList();

        assertThrows(YourHandException.class, () -> taskList.getTask(1));
    }

    @Test
    public void getTask_numberOutsideList_throwsYourHandException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(YourHandException.class, () -> taskList.getTask(0));
        assertThrows(YourHandException.class, () -> taskList.getTask(2));
    }

    @Test
    public void removeTask_validOneBasedNumber_returnsTaskAndClosesGap() throws YourHandException {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        Todo thirdTask = new Todo("buy bread");
        taskList.add(firstTask);
        taskList.add(secondTask);
        taskList.add(thirdTask);

        Task removedTask = taskList.removeTask(2);

        assertSame(secondTask, removedTask);
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.getTask(1));
        assertSame(thirdTask, taskList.getTask(2));
    }

    @Test
    public void removeTask_emptyList_throwsYourHandException() {
        TaskList taskList = new TaskList();

        assertThrows(YourHandException.class, () -> taskList.removeTask(1));
    }

    @Test
    public void removeTask_numberOutsideList_throwsYourHandException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(YourHandException.class, () -> taskList.removeTask(0));
        assertThrows(YourHandException.class, () -> taskList.removeTask(2));
    }

    @Test
    public void getTasks_returnsUnmodifiableSnapshotInDisplayOrder() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        taskList.add(firstTask);

        List<Task> taskSnapshot = taskList.getTasks();
        taskList.add(secondTask);

        assertEquals(List.of(firstTask), taskSnapshot);
        assertThrows(UnsupportedOperationException.class, () -> taskSnapshot.add(secondTask));
    }

    @Test
    public void findTaskNumberByDescription_emptyList_taskNotFound() {
        TaskList taskList = new TaskList();

        assertEquals(-1, taskList.findTaskNumberByDescription("read book"));
    }

    @Test
    public void findTaskNumberByDescription_matchingDescription_returnsOneBasedTaskNumber() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));

        assertEquals(2, taskList.findTaskNumberByDescription("return book"));
    }

    @Test
    public void findTaskNumberByDescription_differentLetterCase_returnsMatchingTaskNumber() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read Book"));

        assertEquals(1, taskList.findTaskNumberByDescription("read book"));
    }

    @Test
    public void findTaskNumberByDescription_duplicateDescriptions_returnsFirstMatchingTaskNumber() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));
        taskList.add(new Todo("read book"));

        assertEquals(1, taskList.findTaskNumberByDescription("read book"));
    }

    @Test
    public void findTaskNumberByDescription_nonMatchingDescription_taskNotFound() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertEquals(-1, taskList.findTaskNumberByDescription("buy bread"));
    }

    @Test
    public void findTaskNumbersByDescriptionKeyword_matchingSubstring_returnsAllTaskNumbers() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy bread"));
        taskList.add(new Todo("return book"));

        assertEquals(List.of(1, 3), taskList.findTaskNumbersByDescriptionKeyword("book"));
    }

    @Test
    public void findTaskNumbersByDescriptionKeyword_differentLetterCase_matchesIgnoringCase() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read BOOK"));

        assertEquals(List.of(1), taskList.findTaskNumbersByDescriptionKeyword("book"));
    }

    @Test
    public void findTaskNumbersByDescriptionKeyword_regexCharacters_areTreatedAsLiteralText() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("review C++ notes [week 1]"));
        taskList.add(new Todo("review Java notes"));

        assertEquals(List.of(1), taskList.findTaskNumbersByDescriptionKeyword("C++"));
        assertEquals(List.of(1), taskList.findTaskNumbersByDescriptionKeyword("[week 1]"));
    }

    @Test
    public void findTaskNumbersByDescriptionKeyword_noMatches_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertEquals(List.of(), taskList.findTaskNumbersByDescriptionKeyword("milk"));
    }

    @Test
    public void findTaskNumbersForDate_matchingDeadlinesAndEvents_returnsChronologicalNumbers() {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("late report", new TaskDateTime(LocalDateTime.of(2026, 9, 10, 15, 0))));
        taskList.add(new Event("morning meeting",
                new TaskDateTime(LocalDateTime.of(2026, 9, 10, 9, 0)),
                new TaskDateTime(LocalDateTime.of(2026, 9, 10, 10, 0))));
        taskList.add(new Todo("buy milk"));

        assertEquals(List.of(2, 1), taskList.findTaskNumbersForDate(LocalDate.of(2026, 9, 10)));
    }

    @Test
    public void findTaskNumbersForDate_multiDayEventOverlapsDate_returnsEventNumber() {
        TaskList taskList = new TaskList();
        taskList.add(new Event("conference",
                new TaskDateTime(LocalDate.of(2026, 9, 9)),
                new TaskDateTime(LocalDate.of(2026, 9, 11))));

        assertEquals(List.of(1), taskList.findTaskNumbersForDate(LocalDate.of(2026, 9, 10)));
    }

    @Test
    public void findTaskNumbersForDate_noScheduledTasks_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));

        assertEquals(List.of(), taskList.findTaskNumbersForDate(LocalDate.of(2026, 9, 10)));
    }
}
