package twizzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list operations. */
class TaskListTest {
    @Test
    void findTasks_mixedCaseKeyword_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("return Book"),
                new Todo("join club")));

        List<String> matchingDescriptions = tasks.findTasks("bOoK")
                .stream()
                .map(Task::getDescription)
                .toList();

        assertEquals(List.of("read book", "return Book"), matchingDescriptions);
    }

    @Test
    void getActiveTasks_snoozedTask_excludesItUntilItsReturnDate() {
        Todo activeTask = new Todo("active task");
        Todo snoozedTask = new Todo("snoozed task");
        snoozedTask.snoozeUntil(LocalDate.of(2099, 12, 31));
        TaskList tasks = new TaskList(List.of(activeTask, snoozedTask));

        assertEquals(List.of(activeTask), tasks.getActiveTasks(LocalDate.of(2099, 12, 30)));
        assertEquals(List.of(snoozedTask), tasks.getSnoozedTasks(LocalDate.of(2099, 12, 30)));
        assertEquals(List.of(activeTask, snoozedTask), tasks.getActiveTasks(LocalDate.of(2099, 12, 31)));
    }

    @Test
    void getActiveTasks_mixedTypes_groupsTasksInDisplayedOrder() {
        Todo todo = new Todo("read notes");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 22));
        TaskList tasks = new TaskList(List.of(event, todo, deadline));

        assertEquals(List.of(todo, deadline, event), tasks.getActiveTasks(LocalDate.of(2026, 9, 20)));
    }

    @Test
    void containsDuplicate_sameTaskDetailsIgnoringCase_returnsTrue() {
        Todo completedTask = new Todo("completed task");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(new Todo("Prepare Slides"),
                new Deadline("submit report", LocalDate.of(2026, 9, 30)), completedTask));

        assertEquals(true, tasks.containsDuplicate(new Todo("prepare slides")));
        assertEquals(true, tasks.containsDuplicate(new Deadline("SUBMIT REPORT", LocalDate.of(2026, 9, 30))));
        assertEquals(false, tasks.containsDuplicate(new Deadline("submit report", LocalDate.of(2026, 10, 1))));
        assertEquals(false, tasks.containsDuplicate(new Todo("completed task")));
    }
}
