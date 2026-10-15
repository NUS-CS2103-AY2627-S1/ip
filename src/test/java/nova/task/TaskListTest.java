package nova.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import nova.exception.NovaException;

/** Tests task-list operations that drive the chatbot's core task behavior. */
class TaskListTest {
    /** Verifies that added tasks remain in insertion order. */
    @Test
    void addAndGet_tasksRemainInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
    }

    /** Verifies that adding a duplicate todo is rejected with a clear error. */
    @Test
    void addIfNotDuplicate_duplicateTodo_throwsClearError() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.addIfNotDuplicate(new Todo("read book"));
        Todo duplicateTask = new Todo("read book");

        NovaException exception = assertThrows(NovaException.class, () -> tasks.addIfNotDuplicate(duplicateTask));

        assertEquals("A task with the same details already exists.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Verifies that a deadline with the same description and date is rejected. */
    @Test
    void addIfNotDuplicate_duplicateDeadline_throwsClearError() throws NovaException {
        TaskList tasks = new TaskList();
        LocalDate deadlineDate = LocalDate.of(2026, 8, 24);
        tasks.addIfNotDuplicate(new Deadline("submit report", deadlineDate));

        NovaException exception = assertThrows(NovaException.class, () -> tasks.addIfNotDuplicate(
                new Deadline("submit report", deadlineDate)));

        assertEquals("A task with the same details already exists.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Verifies that an event with the same description and date-time range is rejected. */
    @Test
    void addIfNotDuplicate_duplicateEvent_throwsClearError() throws NovaException {
        TaskList tasks = new TaskList();
        String eventStart = "2026-08-24 09:00";
        String eventEnd = "2026-08-24 10:00";
        tasks.addIfNotDuplicate(new Event("meeting", eventStart, eventEnd));

        NovaException exception = assertThrows(NovaException.class, () -> tasks.addIfNotDuplicate(
                new Event("meeting", eventStart, eventEnd)));

        assertEquals("A task with the same details already exists.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Verifies that completion status and tags do not change task identity. */
    @Test
    void addIfNotDuplicate_differentStatusAndTags_stillThrowsDuplicateError() throws NovaException {
        TaskList tasks = new TaskList();
        Todo existingTask = new Todo("read book");
        tasks.addIfNotDuplicate(existingTask);
        existingTask.markAsDone();
        existingTask.addTag("#reading");
        Todo duplicateTask = new Todo("read book");

        NovaException exception = assertThrows(NovaException.class, () -> tasks.addIfNotDuplicate(duplicateTask));

        assertEquals("A task with the same details already exists.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Verifies that a valid user-facing task number returns the task. */
    @Test
    void getByNumber_validNumberReturnsTask_zeroBasedCollectionRemainsHidden() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        assertEquals("first", tasks.getByNumber(1).getDescription());
    }

    /** Verifies that an invalid task number produces a clear error. */
    @Test
    void getByNumber_invalidNumber_throwsClearError() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        NovaException exception = assertThrows(NovaException.class, () -> tasks.getByNumber(0));

        assertEquals("Please provide a valid task number.", exception.getMessage());
    }

    /** Verifies that marking operations affect only the selected task. */
    @Test
    void markOperations_changeOnlyTheSelectedTask() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        tasks.markAsDone(2);
        assertFalse(tasks.get(0).getStatusIcon().equals("X"));
        assertEquals("X", tasks.get(1).getStatusIcon());

        tasks.markAsNotDone(2);
        assertEquals(" ", tasks.get(1).getStatusIcon());
    }

    /** Verifies that adding a duplicate tag is an ignored no-op. */
    @Test
    void addTag_duplicateTag_isIgnored() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.addTag(1, "#reading"));
        assertFalse(tasks.addTag(1, "#reading"));

        assertEquals(1, tasks.get(0).getTags().size());
        assertEquals("[T][ ] read book #reading", tasks.get(0).toString());
    }

    /** Verifies that an existing tag can be removed. */
    @Test
    void removeTag_existingTag_isRemoved() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.addTag(1, "#reading");

        tasks.removeTag(1, "#reading");

        assertTrue(tasks.get(0).getTags().isEmpty());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /** Verifies that removing an absent tag produces a clear error. */
    @Test
    void removeTag_missingTag_throwsClearError() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        NovaException exception = assertThrows(NovaException.class, () -> tasks.removeTag(1, "#reading"));

        assertEquals("Task does not have the tag #reading.", exception.getMessage());
    }

    /** Verifies that removal preserves the order of remaining tasks. */
    @Test
    void removeByNumber_removesSelectedTaskAndPreservesRemainingOrder() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        Task removed = tasks.removeByNumber(2);

        assertEquals("second", removed.getDescription());
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("third", tasks.get(1).getDescription());
    }

    /** Verifies that invalid removal leaves the task list unchanged. */
    @Test
    void removeByNumber_invalidNumber_throwsWithoutChangingList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        assertThrows(NovaException.class, () -> tasks.removeByNumber(2));
        assertEquals(1, tasks.size());
    }

    /** Verifies that date filtering returns matching deadlines and events only. */
    @Test
    void getTasksOnDate_returnsMatchingDeadlinesAndEvents_only() throws NovaException {
        LocalDate targetDate = LocalDate.of(2026, 8, 24);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("ordinary task"));
        tasks.add(new Deadline("deadline", targetDate));
        tasks.add(new Event("event", "2026-08-24 09:00", "2026-08-24 10:00"));
        tasks.add(new Deadline("different date", targetDate.plusDays(1)));

        ArrayList<Task> matchingTasks = tasks.getTasksOnDate(targetDate);

        assertEquals(2, matchingTasks.size());
        assertEquals("deadline", matchingTasks.get(0).getDescription());
        assertEquals("event", matchingTasks.get(1).getDescription());
        assertTrue(matchingTasks.stream().noneMatch(task -> task instanceof Todo));
    }

    /** Verifies that invalid event date-time ranges are rejected by the event model. */
    @Test
    void event_invalidDateTimeRange_throwsClearError() {
        String reversedEvent = "2026-08-24 10:00";
        String eventEnd = "2026-08-24 09:00";
        NovaException exception = assertThrows(NovaException.class, () -> new Event("event", reversedEvent, eventEnd));

        assertEquals("Event start and end must be valid, and start must be before end.",
                exception.getMessage());
    }

    /** Verifies that find matches description substrings without case sensitivity. */
    @Test
    void find_keywordMatchesDescriptionsCaseInsensitively() throws NovaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read a book"));
        tasks.add(new Deadline("Return BOOK", LocalDate.of(2026, 8, 24)));
        tasks.add(new Event("Attend meeting", "2026-08-24 09:00", "2026-08-24 10:00"));

        ArrayList<Task> matchingTasks = tasks.findByKeyword("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("Read a book", matchingTasks.get(0).getDescription());
        assertEquals("Return BOOK", matchingTasks.get(1).getDescription());
    }

    /** Verifies that find returns an empty result when no description matches. */
    @Test
    void find_unknownKeyword_returnsNoMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read a book"));

        assertTrue(tasks.findByKeyword("calendar").isEmpty());
    }
}
