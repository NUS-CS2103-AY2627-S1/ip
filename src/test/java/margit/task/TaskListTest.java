package margit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the collection operations provided by {@link TaskList}. */
class TaskListTest {
    @Test
    void add_multipleTasks_appendsThemInOrderAndUpdatesSize() {
        TaskList tasks = new TaskList();
        Task first = new TodoTask("first");
        Task second = new TodoTask("second");

        tasks.add(first, second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    void get_emptyList_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.get(0));
    }

    @Test
    void remove_emptyList_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.remove(0));
    }

    @Test
    void remove_middleTask_returnsRemovedTaskAndShiftsLaterTasks() {
        TaskList tasks = new TaskList();
        Task first = new TodoTask("first");
        Task middle = new TodoTask("middle");
        Task last = new TodoTask("last");
        tasks.add(first);
        tasks.add(middle);
        tasks.add(last);

        Task removed = tasks.remove(1);

        assertSame(middle, removed);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(last, tasks.get(1));
    }

    @Test
    void findByKeyword_matchingTasks_returnsMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        Task firstMatch = new TodoTask("read book");
        Task nonMatch = new TodoTask("buy milk");
        Task secondMatch = new TodoTask("return book");
        tasks.add(firstMatch);
        tasks.add(nonMatch);
        tasks.add(secondMatch);

        TaskList matchingTasks = tasks.findByKeyword("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstMatch, matchingTasks.get(0));
        assertSame(secondMatch, matchingTasks.get(1));
    }

    @Test
    void findByKeyword_noMatchingTasks_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("buy milk"));

        TaskList matchingTasks = tasks.findByKeyword("book");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    void sort_mixedTasks_groupsAndPermanentlyOrdersTasks() {
        Task firstTodo = new TodoTask("first todo");
        Task secondTodo = new TodoTask("second todo");
        Task unscheduledDeadline = new DeadlineTask("someday", TaskDateTime.parse("someday"));
        Task sameTimeEvent = new EventTask("event", TaskDateTime.parse("3/12/2019 1000"),
                TaskDateTime.parse("3/12/2019 1100"));
        Task sameTimeDeadline = new DeadlineTask("deadline", TaskDateTime.parse("3/12/2019 1000"));
        Task dateOnlyDeadline = new DeadlineTask("date only", TaskDateTime.parse("2/12/2019"));
        Task earlierEvent = new EventTask("earlier event", TaskDateTime.parse("2/12/2019 1800"),
                TaskDateTime.parse("2/12/2019 1900"));
        TaskList tasks = new TaskList();
        tasks.add(firstTodo, secondTodo, unscheduledDeadline, sameTimeEvent, sameTimeDeadline,
                dateOnlyDeadline, earlierEvent);

        SortedTaskLists sortedTasks = tasks.sort(SortOrder.ASCENDING);

        assertTaskOrder(sortedTasks.getTodoTasks(), firstTodo, secondTodo);
        assertTaskOrder(sortedTasks.getUnscheduledTasks(), unscheduledDeadline);
        assertTaskOrder(sortedTasks.getScheduledTasks(), earlierEvent, dateOnlyDeadline,
                sameTimeDeadline, sameTimeEvent);
        assertTaskOrder(tasks, firstTodo, secondTodo, unscheduledDeadline, earlierEvent,
                dateOnlyDeadline, sameTimeDeadline, sameTimeEvent);
    }

    /** Verifies that a task list contains the expected task objects in order. */
    private void assertTaskOrder(TaskList tasks, Task... expectedTasks) {
        assertEquals(expectedTasks.length, tasks.size());
        for (int i = 0; i < expectedTasks.length; i++) {
            assertSame(expectedTasks[i], tasks.get(i));
        }
    }
}
