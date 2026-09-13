package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void findTasks_matchingKeywordIgnoringCase_returnsMatchesInListOrder() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("attend lecture");
        Task secondMatch = new Todo("return BOOK");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matchingTasks = taskList.findTasks("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void findTasks_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), taskList.findTasks("report"));
    }

    @Test
    void containsEquivalentTask_sameTaskDetails_returnsTrueRegardlessOfCompletionStatus() {
        Todo savedTodo = new Todo("read book");
        savedTodo.markAsDone();
        TaskList taskList = new TaskList(List.of(savedTodo,
                new Deadline("submit report", LocalDateTime.of(2024, 3, 2, 18, 0)),
                new Event("meeting", LocalDateTime.of(2024, 3, 2, 9, 0),
                        LocalDateTime.of(2024, 3, 2, 10, 0))));

        assertEquals(true, taskList.containsEquivalentTask(new Todo("read book")));
        assertEquals(true, taskList.containsEquivalentTask(
                new Deadline("submit report", LocalDateTime.of(2024, 3, 2, 18, 0))));
        assertEquals(true, taskList.containsEquivalentTask(
                new Event("meeting", LocalDateTime.of(2024, 3, 2, 9, 0),
                        LocalDateTime.of(2024, 3, 2, 10, 0))));
    }

    @Test
    void containsEquivalentTask_differentTaskDetails_returnsFalse() {
        TaskList taskList = new TaskList(List.of(
                new Deadline("submit report", LocalDateTime.of(2024, 3, 2, 18, 0)),
                new Event("meeting", LocalDateTime.of(2024, 3, 2, 9, 0),
                        LocalDateTime.of(2024, 3, 2, 10, 0))));

        assertEquals(false, taskList.containsEquivalentTask(
                new Deadline("submit report", LocalDateTime.of(2024, 3, 3, 18, 0))));
        assertEquals(false, taskList.containsEquivalentTask(
                new Event("meeting", LocalDateTime.of(2024, 3, 2, 9, 0),
                        LocalDateTime.of(2024, 3, 2, 11, 0))));
    }

    @Test
    void getTasksSortedChronologically_ordersDatedTasksStablyWithoutChangingList() {
        Task firstTodo = new Todo("first todo");
        Task lateDeadline = new Deadline("late deadline", LocalDateTime.of(2024, 3, 3, 0, 0));
        Task sameTimeEvent = new Event("same-time event", LocalDateTime.of(2024, 3, 2, 9, 0),
                LocalDateTime.of(2024, 3, 2, 10, 0));
        Task sameTimeDeadline = new Deadline("same-time deadline", LocalDateTime.of(2024, 3, 2, 9, 0));
        Task secondTodo = new Todo("second todo");
        Task earlyEvent = new Event("early event", LocalDateTime.of(2024, 3, 1, 9, 0),
                LocalDateTime.of(2024, 3, 1, 10, 0));
        List<Task> insertionOrder = List.of(firstTodo, lateDeadline, sameTimeEvent, sameTimeDeadline,
                secondTodo, earlyEvent);
        TaskList taskList = new TaskList(insertionOrder);

        List<Task> ascendingTasks = taskList.getTasksSortedChronologically(true);
        List<Task> descendingTasks = taskList.getTasksSortedChronologically(false);

        assertEquals(List.of(earlyEvent, sameTimeEvent, sameTimeDeadline, lateDeadline, firstTodo, secondTodo),
                ascendingTasks);
        assertEquals(List.of(firstTodo, secondTodo, lateDeadline, sameTimeEvent, sameTimeDeadline, earlyEvent),
                descendingTasks);
        assertEquals(insertionOrder, getTaskOrder(taskList));
    }

    @Test
    void add_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
        assertThrows(AssertionError.class, () -> taskList.add(0, null));
    }

    @Test
    void addAtIndex_validAndInvalidIndices_insertsOrThrowsAssertionError() {
        Todo firstTask = new Todo("first task");
        Todo secondTask = new Todo("second task");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.add(0, secondTask);

        assertEquals(secondTask, taskList.get(0));
        assertEquals(firstTask, taskList.get(1));
        assertThrows(AssertionError.class, () -> taskList.add(-1, new Todo("invalid task")));
        assertThrows(AssertionError.class, () -> taskList.add(3, new Todo("invalid task")));
    }

    @Test
    void access_invalidIndex_assertionErrorThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> taskList.get(-1));
        assertThrows(AssertionError.class, () -> taskList.remove(1));
    }

    private List<Task> getTaskOrder(TaskList taskList) {
        java.util.ArrayList<Task> taskOrder = new java.util.ArrayList<>();
        taskList.forEach(taskOrder::add);
        return taskOrder;
    }
}
