package chausistant.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests searching task descriptions through the task-list boundary. */
class TaskListTest {

    @Test
    void taskList_varargsConstructor_preservesTaskOrder() {
        Task todo = new TodoTask("read book");
        Task deadline = new DeadlineTask("return book", LocalDateTime.of(2026, 6, 6, 12, 0), true);

        TaskList taskList = new TaskList(todo, deadline);

        assertEquals(List.of(todo, deadline), taskList.getTasks());
    }

    @Test
    void findMatchingTasks_caseInsensitive_returnsMatchesInOriginalOrder() {
        Task todo = new TodoTask("read Book");
        Task deadline = new DeadlineTask("return BOOK", LocalDateTime.of(2026, 6, 6, 12, 0), true);
        Task event = new EventTask("Book club meeting", LocalDateTime.of(2026, 6, 7, 14, 0), true,
                LocalDateTime.of(2026, 6, 7, 16, 0), true);
        TaskList taskList = new TaskList(todo, deadline, event);

        assertEquals(List.of(todo, deadline, event), taskList.findMatchingTasks("book"));
    }

    @Test
    void findMatchingTasks_multiWordPhrase_returnsOnlyMatchingTask() {
        Task todo = new TodoTask("read book");
        Task deadline = new DeadlineTask("return book", LocalDateTime.of(2026, 6, 6, 12, 0), true);
        TaskList taskList = new TaskList(todo, deadline);

        assertEquals(List.of(deadline), taskList.findMatchingTasks("RETURN BOOK"));
    }

    @Test
    void findMatchingTasks_noMatches_returnsEmptyList() {
        TaskList taskList = new TaskList(new TodoTask("read book"));

        assertEquals(List.of(), taskList.findMatchingTasks("receipt"));
    }

    @Test
    void hasTaskWithSameDetails_matchesTypeDescriptionAndScheduleButNotCompletionState() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 0);
        TodoTask completedTodo = new TodoTask("read book");
        completedTodo.setCompleted(true);
        TaskList taskList = new TaskList(completedTodo,
                new DeadlineTask("return book", start, true),
                new EventTask("meeting", start, true, start.plusHours(1), true));

        assertTrue(taskList.hasTaskWithSameDetails(new TodoTask("read book")));
        assertTrue(taskList.hasTaskWithSameDetails(new DeadlineTask("return book", start, true)));
        assertTrue(taskList.hasTaskWithSameDetails(
                new EventTask("meeting", start, true, start.plusHours(1), true)));
        assertFalse(taskList.hasTaskWithSameDetails(new DeadlineTask("return book", start, false)));
        assertFalse(taskList.hasTaskWithSameDetails(
                new EventTask("meeting", start, true, start.plusHours(2), true)));
        assertFalse(taskList.hasTaskWithSameDetails(new TodoTask("read another book")));
    }

    @Test
    void getIncompleteDeadlinesDueBetween_filtersAndSortsDeadlinesInInclusiveWindow() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 0);
        DeadlineTask laterDeadline = new DeadlineTask("later", start.plusDays(2), true);
        DeadlineTask firstTiedDeadline = new DeadlineTask("first tied", start.plusDays(1), true);
        DeadlineTask secondTiedDeadline = new DeadlineTask("second tied", start.plusDays(1), true);
        DeadlineTask deadlineAtWindowStart = new DeadlineTask("start", start, true);
        DeadlineTask deadlineAtWindowEnd = new DeadlineTask("end", start.plusDays(7), true);
        DeadlineTask pastDeadline = new DeadlineTask("past", start.minusMinutes(1), true);
        DeadlineTask outsideDeadline = new DeadlineTask("outside", start.plusDays(7).plusMinutes(1), true);
        DeadlineTask completedDeadline = new DeadlineTask("completed", start.plusDays(1), true);
        completedDeadline.setCompleted(true);

        TaskList taskList = new TaskList(laterDeadline, firstTiedDeadline, secondTiedDeadline,
                deadlineAtWindowStart, deadlineAtWindowEnd, pastDeadline, outsideDeadline,
                completedDeadline, new TodoTask("todo"), new EventTask("event", start.plusDays(1), true,
                        start.plusDays(1).plusHours(1), true));

        assertEquals(List.of(deadlineAtWindowStart, firstTiedDeadline, secondTiedDeadline,
                laterDeadline, deadlineAtWindowEnd),
                taskList.getIncompleteDeadlinesDueBetween(start, start.plusDays(7)));
    }
}
