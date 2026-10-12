package echo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.EchoException;
import echo.storage.Storage;

/** Tests task creation, management, and persistence through {@link TaskManager}. */
public class TaskManagerTest {
    /** Temporary directory used to isolate persistence tests from application data. */
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void newManager_startsEmpty() {
        TaskManager manager = createManager();

        assertTrue(manager.isEmpty());
        assertEquals(0, manager.size());
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void addTodo_addsTodoTaskInInsertionOrder() throws EchoException {
        TaskManager manager = createManager();

        Task task = manager.addTodo("read book");

        assertInstanceOf(TodoTask.class, task);
        assertEquals(1, manager.size());
        assertFalse(manager.isEmpty());
        assertSame(task, manager.getTask(1));
        assertEquals("[To-Do][ ] read book", task.toString());
    }

    @Test
    public void addDeadline_addsDeadlineTask() throws EchoException {
        TaskManager manager = createManager();

        Task task = manager.addDeadline("submit report", "30-09-2026 16:00");

        assertInstanceOf(DeadlineTask.class, task);
        assertSame(task, manager.getTask(1));
        assertEquals("[Deadline][ ] submit report (by: Sep 30 2026, 4:00pm)",
                task.toString());
    }

    @Test
    public void addEvent_addsEventTask() throws EchoException {
        TaskManager manager = createManager();

        Task task = manager.addEvent("team meeting", "30-09-2026 14:00",
                "30-09-2026 16:00");

        assertInstanceOf(EventTask.class, task);
        assertSame(task, manager.getTask(1));
        assertEquals("[Event][ ] team meeting (from: Sep 30 2026, 2:00pm, "
                        + "to: Sep 30 2026, 4:00pm)", task.toString());
    }

    @Test
    public void addTasks_preservesInsertionOrder() throws EchoException {
        TaskManager manager = createManager();
        Task todo = manager.addTodo("read book");
        Task deadline = manager.addDeadline("submit report", "30-09-2026");
        Task event = manager.addEvent("team meeting", "01-10-2026", "02-10-2026");

        assertEquals(List.of(todo, deadline, event), manager.getTasks());
        assertSame(todo, manager.getTask(1));
        assertSame(deadline, manager.getTask(2));
        assertSame(event, manager.getTask(3));
        assertEquals(3, manager.size());
    }

    @Test
    public void findByKeyword_returnsMatchingTasksInInsertionOrder() throws EchoException {
        TaskManager manager = createManager();
        Task firstMatch = manager.addTodo("read book");
        manager.addTodo("submit report");
        Task secondMatch = manager.addEvent("book club meeting", "01-10-2026", "02-10-2026");

        List<Task> matchingTasks = manager.findByKeyword("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    public void findByKeyword_withNoMatches_returnsEmptyList() throws EchoException {
        TaskManager manager = createManager();
        manager.addTodo("read book");

        assertTrue(manager.findByKeyword("holiday").isEmpty());
    }

    @Test
    public void getTasks_returnsReadOnlySnapshot() throws EchoException {
        TaskManager manager = createManager();
        manager.addTodo("first task");
        List<Task> snapshot = manager.getTasks();

        assertThrows(UnsupportedOperationException.class, snapshot::clear);

        manager.addTodo("second task");
        assertEquals(1, snapshot.size());
        assertEquals(2, manager.getTasks().size());
    }

    @Test
    public void markAndUnmarkTask_updatesStatusAndPersists() throws EchoException {
        TaskManager manager = createManager();
        Task task = manager.addTodo("read book");

        Task markedTask = manager.markTask(1);
        assertSame(task, markedTask);
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | read book", task.toFileFormat());

        Task unmarkedTask = manager.unmarkTask(1);
        assertSame(task, unmarkedTask);
        assertEquals(" ", task.getStatusIcon());
        assertEquals("T | 0 | read book", task.toFileFormat());
    }

    @Test
    public void deleteTask_removesAndReindexesTasks() throws EchoException {
        TaskManager manager = createManager();
        Task first = manager.addTodo("first task");
        Task second = manager.addTodo("second task");
        Task third = manager.addTodo("third task");

        Task deletedTask = manager.deleteTask(2);

        assertSame(second, deletedTask);
        assertEquals(2, manager.size());
        assertSame(first, manager.getTask(1));
        assertSame(third, manager.getTask(2));
    }

    @Test
    public void taskOperationsWithInvalidNumber_throwEchoException() throws EchoException {
        TaskManager manager = createManager();
        manager.addTodo("only task");

        EchoException zeroException = assertThrows(
                EchoException.class, () -> manager.markTask(0));
        assertEquals(noTaskMessage(0), zeroException.getMessage());

        EchoException missingException = assertThrows(
                EchoException.class, () -> manager.unmarkTask(2));
        assertEquals(noTaskMessage(2), missingException.getMessage());

        EchoException negativeException = assertThrows(
                EchoException.class, () -> manager.deleteTask(-1));
        assertEquals(noTaskMessage(-1), negativeException.getMessage());

        assertEquals(1, manager.size());
        assertEquals(" ", manager.getTask(1).getStatusIcon());
    }

    @Test
    public void taskChanges_areSavedAndCanBeLoadedByAnotherManager() throws EchoException {
        Storage storage = new Storage(storagePath());
        TaskManager manager = new TaskManager(new TaskList(), storage);
        manager.addTodo("read book");
        manager.addDeadline("submit report", "30-09-2026");
        manager.addEvent("team meeting", "01-10-2026", "02-10-2026");
        manager.markTask(2);

        assertTrue(Files.exists(storagePath()));
        TaskManager reloadedManager = new TaskManager(
                new TaskList(storage.load()),
                storage
        );

        assertEquals(3, reloadedManager.size());
        assertInstanceOf(TodoTask.class, reloadedManager.getTask(1));
        assertInstanceOf(DeadlineTask.class, reloadedManager.getTask(2));
        assertInstanceOf(EventTask.class, reloadedManager.getTask(3));
        assertEquals(" ", reloadedManager.getTask(1).getStatusIcon());
        assertEquals("X", reloadedManager.getTask(2).getStatusIcon());
        assertEquals("T | 0 | read book", reloadedManager.getTask(1).toFileFormat());
        assertEquals("D | 1 | submit report | 30-09-2026",
                reloadedManager.getTask(2).toFileFormat());
    }

    @Test
    public void sortTasks_byDate_sortsChronologicallyWithUndatedAtEnd() throws EchoException {
        TaskManager manager = createManager();
        Task todo = manager.addTodo("buy groceries");
        Task deadlineLate = manager.addDeadline("final paper", "20-10-2026 18:00");
        Task deadlineEarly = manager.addDeadline("homework", "05-10-2026");
        Task eventMid = manager.addEvent("conference", "10-10-2026 09:00", "12-10-2026 17:00");

        List<Task> sorted = manager.sortTasks(SortCriteria.DATE);

        assertEquals(List.of(deadlineEarly, eventMid, deadlineLate, todo), sorted);
        assertEquals(List.of(deadlineEarly, eventMid, deadlineLate, todo), manager.getTasks());
    }

    @Test
    public void sortTasks_byName_sortsAlphabetically() throws EchoException {
        TaskManager manager = createManager();
        Task zebra = manager.addTodo("zebra crossing");
        Task apple = manager.addDeadline("apple picking", "20-10-2026");
        Task banana = manager.addEvent("banana festival", "10-10-2026", "11-10-2026");

        List<Task> sorted = manager.sortTasks(SortCriteria.NAME);

        assertEquals(List.of(apple, banana, zebra), sorted);
        assertEquals(List.of(apple, banana, zebra), manager.getTasks());
    }

    @Test
    public void sortTasks_persistsSortedList() throws EchoException {
        Storage storage = new Storage(storagePath());
        TaskManager manager = new TaskManager(new TaskList(), storage);
        manager.addTodo("read book");
        manager.addDeadline("submit report", "01-10-2026");

        manager.sortTasks(SortCriteria.DATE);

        TaskManager reloadedManager = new TaskManager(
                new TaskList(storage.load()),
                storage
        );
        assertEquals(2, reloadedManager.size());
        assertInstanceOf(DeadlineTask.class, reloadedManager.getTask(1));
        assertInstanceOf(TodoTask.class, reloadedManager.getTask(2));
    }

    @Test
    public void sortTasks_onEmptyList_returnsEmptyList() throws EchoException {
        TaskManager manager = createManager();

        assertTrue(manager.sortTasks(SortCriteria.DATE).isEmpty());
        assertTrue(manager.sortTasks(SortCriteria.NAME).isEmpty());
    }

    @Test
    public void addTodo_duplicateDescription_throwsEchoException() throws EchoException {
        TaskManager manager = createManager();
        manager.addTodo("read book");

        EchoException exception = assertThrows(
                EchoException.class, () -> manager.addTodo("read book"));
        assertTrue(exception.getMessage().contains("already exists"));

        EchoException caseException = assertThrows(
                EchoException.class, () -> manager.addTodo("READ BOOK"));
        assertTrue(caseException.getMessage().contains("already exists"));
    }

    @Test
    public void addDeadline_duplicateDetails_throwsEchoException() throws EchoException {
        TaskManager manager = createManager();
        manager.addDeadline("submit report", "30-09-2026 18:00");

        EchoException exception = assertThrows(
                EchoException.class, () -> manager.addDeadline("submit report", "30-09-2026 18:00"));
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void addDeadline_sameDescriptionDifferentDate_success() throws EchoException {
        TaskManager manager = createManager();
        manager.addDeadline("submit report", "30-09-2026");
        manager.addDeadline("submit report", "01-10-2026");

        assertEquals(2, manager.size());
    }

    @Test
    public void addEvent_duplicateDetails_throwsEchoException() throws EchoException {
        TaskManager manager = createManager();
        manager.addEvent("camp", "01-10-2026", "05-10-2026");

        EchoException exception = assertThrows(
                EchoException.class, () -> manager.addEvent("camp", "01-10-2026", "05-10-2026"));
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void markTask_alreadyMarked_throwsEchoException() throws EchoException {
        TaskManager manager = createManager();
        manager.addTodo("read book");
        manager.markTask(1);

        EchoException exception = assertThrows(
                EchoException.class, () -> manager.markTask(1));
        assertTrue(exception.getMessage().contains("already marked as completed"));
    }

    @Test
    public void unmarkTask_alreadyUnmarked_throwsEchoException() throws EchoException {
        TaskManager manager = createManager();
        manager.addTodo("read book");

        EchoException exception = assertThrows(
                EchoException.class, () -> manager.unmarkTask(1));
        assertTrue(exception.getMessage().contains("already marked as in-progress"));
    }

    @Test
    public void operationsOnEmptyList_throwsEchoException() {
        TaskManager manager = createManager();

        EchoException markException = assertThrows(
                EchoException.class, () -> manager.markTask(1));
        assertTrue(markException.getMessage().contains("Directive backlog is empty"));

        EchoException unmarkException = assertThrows(
                EchoException.class, () -> manager.unmarkTask(1));
        assertTrue(unmarkException.getMessage().contains("Directive backlog is empty"));

        EchoException deleteException = assertThrows(
                EchoException.class, () -> manager.deleteTask(1));
        assertTrue(deleteException.getMessage().contains("Directive backlog is empty"));
    }

    @Test
    public void constructors_initializeCorrectly() {
        TaskManager defaultManager = new TaskManager();
        assertTrue(defaultManager.isEmpty());

        TaskManager listManager = new TaskManager(new TaskList());
        assertTrue(listManager.isEmpty());
    }

    @Test
    public void sortTasks_byDateSameDate_sortsAlphabetically() throws EchoException {
        TaskManager manager = createManager();
        Task beta = manager.addDeadline("beta", "15-10-2026");
        Task alpha = manager.addDeadline("alpha", "15-10-2026");

        List<Task> sorted = manager.sortTasks(SortCriteria.DATE);

        assertEquals(List.of(alpha, beta), sorted);
    }

    @Test
    public void sortTasks_byNameSameDescription_sortsByDateTime() throws EchoException {
        TaskManager manager = createManager();
        Task todo = manager.addTodo("project");
        Task deadlineLate = manager.addDeadline("project", "20-10-2026");
        Task deadlineEarly = manager.addDeadline("project", "10-10-2026");

        List<Task> sorted = manager.sortTasks(SortCriteria.NAME);

        assertEquals(List.of(deadlineEarly, deadlineLate, todo), sorted);
    }

    /** Creates a manager whose storage file is isolated to the current test. */
    private TaskManager createManager() {
        return new TaskManager(new TaskList(), new Storage(storagePath()));
    }

    /** Returns the temporary persistence path used by this test. */
    private Path storagePath() {
        return temporaryDirectory.resolve("echo.txt");
    }

    /** Returns the error message used when a task number is unavailable. */
    private String noTaskMessage(int taskNumber) {
        return "There is no task numbered " + taskNumber
                + ". Use 'list' to see the available task numbers.";
    }
}
