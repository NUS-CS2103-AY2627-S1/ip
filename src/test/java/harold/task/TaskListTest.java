package harold.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import harold.HaroldException;

class TaskListTest {
    private static final int MAX_TASK_COUNT = 100;

    @Test
    void constructor_noInitialTasks_createsEmptyList() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertEquals(0, tasks.getDiscardedTaskCount());
    }

    @Test
    void constructor_initialTasks_copiesTasksInOrder() {
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        List<Task> initialTasks = new ArrayList<>(List.of(firstTask, secondTask));

        TaskList tasks = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
        assertEquals(0, tasks.getDiscardedTaskCount());
    }

    @Test
    void constructor_tooManyInitialTasks_discardsOverflow() {
        List<Task> initialTasks = createTasks(MAX_TASK_COUNT + 2);

        TaskList tasks = new TaskList(initialTasks);

        assertEquals(MAX_TASK_COUNT, tasks.size());
        assertEquals(2, tasks.getDiscardedTaskCount());
        assertSame(initialTasks.get(MAX_TASK_COUNT - 1), tasks.get(tasks.size() - 1));
    }

    @Test
    void add_availableSpace_addsTaskAtEnd() throws HaroldException {
        TaskList tasks = new TaskList(new Todo("first"));
        Todo addedTask = new Todo("second");

        tasks.add(addedTask);

        assertEquals(2, tasks.size());
        assertSame(addedTask, tasks.get(1));
    }

    @Test
    void add_fullList_throwsExceptionWithoutChangingList() {
        TaskList tasks = new TaskList(createTasks(MAX_TASK_COUNT));
        Todo overflowTask = new Todo("overflow");

        HaroldException exception = assertThrows(
                HaroldException.class, () -> tasks.add(overflowTask));

        assertEquals(
                "Your task list is full. Complete some tasks before adding more.",
                exception.getMessage()
        );
        assertEquals(MAX_TASK_COUNT, tasks.size());
    }

    @Test
    void add_sameTaskDetails_throwsExceptionWithoutChangingList() {
        TaskList tasks = new TaskList(new Deadline(
                "submit report",
                LocalDate.of(2026, 9, 20)
        ));

        HaroldException exception = assertThrows(
                HaroldException.class, () -> tasks.add(new Deadline(
                        "submit report", LocalDate.of(2026, 9, 20)))
        );

        assertEquals("That task is already in your list.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    void add_sameDescriptionWithDifferentDetails_addsTask() throws HaroldException {
        TaskList tasks = new TaskList(new Deadline(
                "submit report",
                LocalDate.of(2026, 9, 20)
        ));

        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 21)));
        tasks.add(new Todo("submit report"));

        assertEquals(3, tasks.size());
    }

    @Test
    void constructor_duplicateInitialTasks_discardsDuplicates() {
        Todo firstTask = new Todo("feed Harold");
        Todo duplicateTask = new Todo("feed Harold");

        TaskList tasks = new TaskList(firstTask, duplicateTask, new Todo("walk Harold"));

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertEquals(1, tasks.getDiscardedTaskCount());
    }

    @Test
    void delete_middleTask_removesAndReturnsTask() {
        Todo firstTask = new Todo("first");
        Todo middleTask = new Todo("middle");
        Todo lastTask = new Todo("last");
        TaskList tasks = new TaskList(firstTask, middleTask, lastTask);

        Task deletedTask = tasks.delete(1);

        assertSame(middleTask, deletedTask);
        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(lastTask, tasks.get(1));
    }

    @Test
    void delete_invalidIndexes_throwException() {
        TaskList tasks = new TaskList(new Todo("only task"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(1));
        assertEquals(1, tasks.size());
    }

    @Test
    void mark_incompleteTask_marksAndReturnsTask() {
        Todo task = new Todo("task");
        TaskList tasks = new TaskList(task);

        Task markedTask = tasks.mark(0);

        assertSame(task, markedTask);
        assertTrue(task.isDone());
    }

    @Test
    void unmark_completedTask_unmarksAndReturnsTask() {
        Todo task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(task);

        Task unmarkedTask = tasks.unmark(0);

        assertSame(task, unmarkedTask);
        assertFalse(task.isDone());
    }

    @Test
    void markAndUnmark_invalidIndexes_throwException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(0));
    }

    @Test
    void find_keywordInSomeDescriptions_returnsMatchesInOriginalOrder() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy groceries");
        Todo secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(firstMatch, nonMatch, secondMatch);

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstMatch, matchingTasks.get(0));
        assertSame(secondMatch, matchingTasks.get(1));
        assertEquals(3, tasks.size());
    }

    @Test
    void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList(new Todo("read book"));

        List<Task> matchingTasks = tasks.find("magazine");

        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    void findSimilar_misspelledKeyword_returnsClosestTasksFirst() {
        Todo closestMatch = new Todo("return book");
        Todo secondMatch = new Todo("book a room");
        Todo unrelatedTask = new Todo("buy groceries");
        TaskList tasks = new TaskList(secondMatch, unrelatedTask, closestMatch);

        List<Task> similarTasks = tasks.findSimilar("return bok", 3);

        assertEquals(2, similarTasks.size());
        assertSame(closestMatch, similarTasks.get(0));
        assertSame(secondMatch, similarTasks.get(1));
    }

    @Test
    void findSimilar_differentCapitalization_matchesIgnoringCase() {
        Todo matchingTask = new Todo("read book");
        TaskList tasks = new TaskList(matchingTask);

        List<Task> similarTasks = tasks.findSimilar("BOOOK", 3);

        assertEquals(1, similarTasks.size());
        assertSame(matchingTask, similarTasks.get(0));
    }

    @Test
    void findSimilar_unrelatedKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(new Todo("read book"));

        List<Task> similarTasks = tasks.findSimilar("magazine", 3);

        assertTrue(similarTasks.isEmpty());
    }

    @Test
    void findSimilar_moreMatchesThanLimit_returnsOnlyRequestedCount() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Todo("return book"),
                new Todo("book a room")
        );

        List<Task> similarTasks = tasks.findSimilar("boook", 2);

        assertEquals(2, similarTasks.size());
    }

    private static List<Task> createTasks(int taskCount) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            tasks.add(new Todo("task " + i));
        }
        return tasks;
    }
}
