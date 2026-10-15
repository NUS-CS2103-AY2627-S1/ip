package apollo.task;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TaskListTest {
    @TempDir
    Path tempDir;

    @Test
    public void add_addTodo_todoInTaskList() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("todo");

        taskList.add(todo);

        // Assert
        Assertions.assertEquals(1, taskList.size());
        Assertions.assertSame(todo, taskList.get(0));
    }

    @Test
    public void delete_deleteMiddleTask_returnDeletedTask() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("todo");

        LocalDateTime from = LocalDateTime.parse("2026-08-24T00:00:00");
        LocalDateTime to = LocalDateTime.parse("2026-08-25T23:59:00");
        Event event = new Event("event", from, to);

        Deadline deadline = new Deadline("deadline", to);

        taskList.add(todo);
        taskList.add(event);
        taskList.add(deadline);

        Task removedTask = taskList.delete(1);

        // Assert
        Assertions.assertEquals(2, taskList.size());
        Assertions.assertSame(todo, taskList.get(0));
        Assertions.assertSame(deadline, taskList.get(1));
        Assertions.assertSame(removedTask, event);
    }

    @Test
    public void add_atIndex_insertsTaskAtSpecifiedPosition() {
        TaskList taskList = new TaskList();
        Todo firstTodo = new Todo("first");
        Todo restoredTodo = new Todo("restored");
        Todo lastTodo = new Todo("last");
        taskList.add(firstTodo, lastTodo);

        taskList.add(1, restoredTodo);

        Assertions.assertEquals(3, taskList.size());
        Assertions.assertSame(firstTodo, taskList.get(0));
        Assertions.assertSame(restoredTodo, taskList.get(1));
        Assertions.assertSame(lastTodo, taskList.get(2));
    }

    @Test
    public void getDeadlinesDueOn_mixedTasks_returnOnlyMatchingDeadlines() {
        TaskList taskList = new TaskList();

        LocalDateTime aug25 = LocalDateTime.parse("2026-08-25T23:59:00");
        LocalDateTime aug24 = LocalDateTime.parse("2026-08-24T00:00:00");

        Todo todo = new Todo("dummy");
        Deadline matchingDeadline = new Deadline("deadline1", aug25);
        Deadline differentDeadline = new Deadline("deadline2", aug24);

        taskList.add(matchingDeadline);
        taskList.add(differentDeadline);
        taskList.add(todo);

        List<Deadline> results = taskList.getDeadlinesDueOn(LocalDate.of(2026, 8, 25));

        Assertions.assertEquals(1, results.size());
        Assertions.assertSame(matchingDeadline, results.get(0));
    }

    @Test
    public void getDeadlinesDueOn_multipleMatches_returnsAllMatchesInOriginalOrder() {
        TaskList taskList = new TaskList();
        Deadline firstMatch = new Deadline("morning deadline",
                LocalDateTime.of(2026, 8, 25, 9, 0));
        Deadline otherDate = new Deadline("other deadline",
                LocalDateTime.of(2026, 8, 26, 9, 0));
        Deadline secondMatch = new Deadline("evening deadline",
                LocalDateTime.of(2026, 8, 25, 18, 0));
        taskList.add(firstMatch, new Todo("todo"), otherDate, secondMatch);

        List<Deadline> results = taskList.getDeadlinesDueOn(LocalDate.of(2026, 8, 25));

        Assertions.assertEquals(List.of(firstMatch, secondMatch), results);
    }

    @Test
    public void getEventsOngoingAt_boundaryTimes_includesStartAndEndOnly() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 25, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 25, 10, 0);
        Event event = new Event("lecture", start, end);
        TaskList taskList = new TaskList();
        taskList.add(event);

        Assertions.assertEquals(List.of(event), taskList.getEventsOngoingAt(start));
        Assertions.assertEquals(List.of(event), taskList.getEventsOngoingAt(end));
        Assertions.assertTrue(taskList.getEventsOngoingAt(start.minusNanos(1)).isEmpty());
        Assertions.assertTrue(taskList.getEventsOngoingAt(end.plusNanos(1)).isEmpty());
    }

    @Test
    public void find_mixedTasks_returnsCaseInsensitiveSubstringMatchesInOriginalOrder() {
        TaskList taskList = new TaskList();
        Todo firstMatch = new Todo("Read CS2103T textbook");
        Deadline nonMatch = new Deadline("submit reflection",
                LocalDateTime.of(2026, 8, 25, 23, 59));
        Event secondMatch = new Event("CS2103T tutorial",
                LocalDateTime.of(2026, 8, 26, 9, 0),
                LocalDateTime.of(2026, 8, 26, 10, 0));
        taskList.add(firstMatch, nonMatch, secondMatch);

        List<Task> results = taskList.find("cs2103t");

        Assertions.assertEquals(List.of(firstMatch, secondMatch), results);
    }

    @Test
    public void getTasks_modifiedReturnedList_doesNotModifyTaskList() {
        Todo original = new Todo("original");
        TaskList taskList = new TaskList();
        taskList.add(original);
        List<Task> returnedTasks = taskList.getTasks();

        returnedTasks.clear();
        returnedTasks.add(new Todo("replacement"));

        Assertions.assertEquals(1, taskList.size());
        Assertions.assertSame(original, taskList.get(0));
    }

    @Test
    public void constructor_modifiedSourceList_doesNotModifyTaskList() {
        Todo original = new Todo("original");
        List<Task> source = new ArrayList<>();
        source.add(original);
        TaskList taskList = new TaskList(source);

        source.clear();

        Assertions.assertEquals(1, taskList.size());
        Assertions.assertSame(original, taskList.get(0));
    }
}
