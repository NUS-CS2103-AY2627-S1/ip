package axiom.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import axiom.AxiomException;

class TaskListTest {
    private TaskList taskList;
    private Todo firstTask;
    private Todo secondTask;

    @BeforeEach
    void setUp() {
        taskList = new TaskList();
        firstTask = new Todo("read book");
        secondTask = new Todo("return book");
    }

    @Test
    void add_singleTask_increasesSize() throws AxiomException {
        taskList.add(firstTask);
        assertEquals(1, taskList.size());
        assertEquals(firstTask, taskList.get(0));
    }

    @Test
    void add_multipleTasks_preservesOrder() throws AxiomException {
        taskList.add(firstTask);
        taskList.add(secondTask);
        assertEquals(2, taskList.size());
        assertEquals(firstTask, taskList.get(0));
        assertEquals(secondTask, taskList.get(1));
    }

    @Test
    void add_duplicateTodo_exceptionThrown() throws AxiomException {
        taskList.add(firstTask);
        AxiomException exception = assertThrows(AxiomException.class, () -> taskList.add(new Todo("read book")));
        assertEquals("That task is already in your list.", exception.getMessage());
        assertEquals(1, taskList.size());
    }

    @Test
    void add_sameDescriptionDifferentType_allowed() throws AxiomException {
        taskList.add(firstTask);
        taskList.add(new Deadline("read book", LocalDateTime.of(2019, 6, 6, 0, 0)));
        assertEquals(2, taskList.size());
    }

    @Test
    void add_sameDeadlineDifferentDate_allowed() throws AxiomException {
        taskList.add(new Deadline("homework", LocalDateTime.of(2019, 6, 6, 0, 0)));
        taskList.add(new Deadline("homework", LocalDateTime.of(2019, 10, 15, 0, 0)));
        assertEquals(2, taskList.size());
    }

    @Test
    void add_doneCopyOfExistingTask_exceptionThrown() throws AxiomException {
        firstTask.markAsDone();
        taskList.add(firstTask);
        AxiomException exception = assertThrows(AxiomException.class, () -> taskList.add(new Todo("read book")));
        assertEquals("That task is already in your list.", exception.getMessage());
    }

    @Test
    void delete_existingTask_returnsRemovedTask() throws AxiomException {
        taskList.add(firstTask);
        taskList.add(secondTask);
        Task removed = taskList.delete(0);
        assertEquals(firstTask, removed);
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.get(0));
    }

    @Test
    void iterator_containsAddedTasks() throws AxiomException {
        taskList.add(firstTask);
        ArrayList<Task> iterated = new ArrayList<>();
        for (Task task : taskList) {
            iterated.add(task);
        }
        assertEquals(1, iterated.size());
        assertEquals(firstTask, iterated.get(0));
    }

    @Test
    void constructor_withExistingList_doesNotAliasCallerList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(firstTask);
        TaskList loadedList = new TaskList(tasks);
        tasks.add(secondTask);
        assertEquals(1, loadedList.size());
    }

    @Test
    void markAsDone_validIndex_marksTaskDone() throws AxiomException {
        taskList.add(firstTask);
        taskList.markAsDone(0);
        assertTrue(taskList.get(0).isDone());
    }

    @Test
    void markAsNotDone_doneTask_marksTaskNotDone() throws AxiomException {
        taskList.add(firstTask);
        taskList.markAsDone(0);
        taskList.markAsNotDone(0);
        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void constructor_withExistingList_usesProvidedTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(firstTask);
        TaskList loadedList = new TaskList(tasks);
        assertEquals(1, loadedList.size());
        assertEquals(firstTask, loadedList.get(0));
    }

    @Test
    void findMatchingIndexes_matchingKeyword_returnsZeroBasedIndexes() throws AxiomException {
        taskList.add(firstTask);
        taskList.add(new Todo("buy bread"));
        taskList.add(secondTask);
        ArrayList<Integer> matches = taskList.findMatchingIndexes("book");
        assertEquals(2, matches.size());
        assertEquals(0, matches.get(0));
        assertEquals(2, matches.get(1));
    }

    @Test
    void findMatchingIndexes_caseInsensitive_returnsMatches() throws AxiomException {
        taskList.add(new Todo("Read Book"));
        ArrayList<Integer> matches = taskList.findMatchingIndexes("book");
        assertEquals(1, matches.size());
        assertEquals(0, matches.get(0));
    }

    @Test
    void findMatchingIndexes_noMatch_returnsEmptyList() throws AxiomException {
        taskList.add(firstTask);
        assertTrue(taskList.findMatchingIndexes("xyz").isEmpty());
    }

    @Test
    void sortChronologically_deadlinesOutOfOrder_ordersByDate() throws AxiomException {
        Deadline later = new Deadline("later", LocalDateTime.of(2019, 10, 15, 0, 0));
        Deadline earlier = new Deadline("earlier", LocalDateTime.of(2019, 6, 6, 0, 0));
        taskList.add(later);
        taskList.add(earlier);

        taskList.sortChronologically();

        assertEquals(earlier, taskList.get(0));
        assertEquals(later, taskList.get(1));
    }

    @Test
    void sortChronologically_mixedTypes_placesTodosAfterDatedTasks() throws AxiomException {
        Todo todo = new Todo("read book");
        Event event = new Event("meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0),
                LocalDateTime.of(2019, 8, 6, 16, 0));
        Deadline deadline = new Deadline("homework", LocalDateTime.of(2019, 6, 6, 0, 0));
        taskList.add(todo);
        taskList.add(event);
        taskList.add(deadline);

        taskList.sortChronologically();

        assertEquals(deadline, taskList.get(0));
        assertEquals(event, taskList.get(1));
        assertEquals(todo, taskList.get(2));
    }

    @Test
    void sortChronologically_sameDate_keepsRelativeOrder() throws AxiomException {
        Deadline first = new Deadline("first", LocalDateTime.of(2019, 6, 6, 0, 0));
        Deadline second = new Deadline("second", LocalDateTime.of(2019, 6, 6, 0, 0));
        taskList.add(first);
        taskList.add(second);

        taskList.sortChronologically();

        assertEquals(first, taskList.get(0));
        assertEquals(second, taskList.get(1));
    }

    @Test
    void sortChronologically_emptyList_remainsEmpty() {
        taskList.sortChronologically();
        assertEquals(0, taskList.size());
    }
}
