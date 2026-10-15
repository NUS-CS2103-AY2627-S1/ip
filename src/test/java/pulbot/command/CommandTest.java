package pulbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.task.Todo;
import pulbot.ui.Ui;

/** Tests the effects produced by each command type. */
public class CommandTest {
    private final RecordingUi ui = new RecordingUi();
    private final RecordingStorage storage = new RecordingStorage();

    @Test
    public void addCommand_execute_addsDisplaysAndSavesTask() throws PulbotException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        new AddCommand(task).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
        assertSame(task, ui.addedTask);
        assertEquals(1, ui.taskCount);
        assertSame(tasks, storage.savedTasks);
    }

    @Test
    public void addCommand_duplicateTask_throwsExceptionWithoutAddingOrSaving() {
        Task existingTask = new Todo("read book");
        existingTask.markAsDone();
        TaskList tasks = new TaskList(java.util.List.of(existingTask));

        PulbotException exception = assertThrows(PulbotException.class, () ->
                new AddCommand(new Todo("READ BOOK")).execute(tasks, ui, storage));

        assertEquals("This task already exists in your list.", exception.getMessage());
        assertEquals(1, tasks.size());
        assertEquals(0, storage.saveCount);
    }

    @Test
    public void markAndUnmarkCommand_execute_updatesSelectedTaskAndSaves() throws PulbotException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(java.util.List.of(first, second));

        new MarkCommand("2").execute(tasks, ui, storage);
        assertTrue(second.isDone());
        assertSame(second, ui.markedTask);

        new UnmarkCommand("2").execute(tasks, ui, storage);
        assertFalse(second.isDone());
        assertSame(second, ui.unmarkedTask);
        assertEquals(2, storage.saveCount);
    }

    @Test
    public void taskNumberCommand_invalidNumber_throwsExceptionWithoutChangingList() {
        Task onlyTask = new Todo("only task");
        TaskList tasks = new TaskList(java.util.List.of(onlyTask));

        assertThrows(PulbotException.class, () ->
                new MarkCommand("0").execute(tasks, ui, storage));
        assertThrows(PulbotException.class, () ->
                new MarkCommand("2").execute(tasks, ui, storage));
        assertThrows(PulbotException.class, () ->
                new MarkCommand("abc").execute(tasks, ui, storage));
        assertThrows(PulbotException.class, () ->
                new UnmarkCommand("").execute(tasks, ui, storage));
        assertThrows(PulbotException.class, () ->
                new DeleteCommand("-1").execute(tasks, ui, storage));
        assertEquals(1, tasks.size());
        assertSame(onlyTask, tasks.get(0));
        assertFalse(onlyTask.isDone());
        assertEquals(0, storage.saveCount);
    }

    @Test
    public void deleteCommand_execute_removesSelectedTaskAndSaves() throws PulbotException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(java.util.List.of(first, second));

        new DeleteCommand("1").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
        assertSame(first, ui.deletedTask);
        assertEquals(1, ui.taskCount);
        assertEquals(1, storage.saveCount);
    }

    @Test
    public void displayCommands_execute_delegateToUi() throws PulbotException {
        TaskList tasks = new TaskList(java.util.List.of(new Todo("read book")));
        LocalDate date = LocalDate.of(2019, 12, 2);

        new ListCommand().execute(tasks, ui, storage);
        new FindCommand("book").execute(tasks, ui, storage);
        new OnCommand(date).execute(tasks, ui, storage);
        new ExitCommand().execute(tasks, ui, storage);

        assertSame(tasks, ui.listedTasks);
        assertSame(tasks, ui.searchedTasks);
        assertEquals("book", ui.keyword);
        assertSame(tasks, ui.datedTasks);
        assertEquals(date, ui.date);
        assertTrue(ui.byeShown);
        assertFalse(new ListCommand().isExit());
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void modifyingCommands_saveFailure_restoresTasksWithoutSuccessMessage() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(java.util.List.of(first, second));
        Storage failingStorage = new FailingStorage();

        assertThrows(PulbotException.class, () ->
                new AddCommand(new Todo("third")).execute(tasks, ui, failingStorage));
        assertThrows(PulbotException.class, () ->
                new DeleteCommand("1").execute(tasks, ui, failingStorage));
        assertThrows(PulbotException.class, () ->
                new MarkCommand("2").execute(tasks, ui, failingStorage));
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertFalse(second.isDone());

        second.markAsDone();
        assertThrows(PulbotException.class, () ->
                new UnmarkCommand("2").execute(tasks, ui, failingStorage));
        assertTrue(second.isDone());
        assertNull(ui.addedTask);
        assertNull(ui.deletedTask);
        assertNull(ui.markedTask);
        assertNull(ui.unmarkedTask);
    }

    @Test
    public void completionCommands_saveFailure_preservesAlreadyRequestedStatus() {
        Task task = new Todo("first");
        TaskList tasks = new TaskList(java.util.List.of(task));
        Storage failingStorage = new FailingStorage();

        assertThrows(PulbotException.class, () ->
                new UnmarkCommand("1").execute(tasks, ui, failingStorage));
        assertFalse(task.isDone());
        task.markAsDone();
        assertThrows(PulbotException.class, () ->
                new MarkCommand("1").execute(tasks, ui, failingStorage));
        assertTrue(task.isDone());
    }

    /** Simulates an unwritable destination without depending on OS permissions. */
    private static class FailingStorage extends Storage {
        FailingStorage() {
            super("unused");
        }

        @Override
        public void save(TaskList tasks) throws PulbotException {
            throw new PulbotException("Simulated write failure");
        }
    }

    /** Records UI calls so command tests can focus on command behavior. */
    private static class RecordingUi extends Ui {
        private Task addedTask;
        private Task markedTask;
        private Task unmarkedTask;
        private Task deletedTask;
        private int taskCount;
        private TaskList listedTasks;
        private TaskList searchedTasks;
        private String keyword;
        private TaskList datedTasks;
        private LocalDate date;
        private boolean byeShown;

        @Override
        public void showAddedTask(Task task, int taskCount) {
            addedTask = task;
            this.taskCount = taskCount;
        }

        @Override
        public void showMarked(Task task) {
            markedTask = task;
        }

        @Override
        public void showUnmarked(Task task) {
            unmarkedTask = task;
        }

        @Override
        public void showDeleted(Task task, int taskCount) {
            deletedTask = task;
            this.taskCount = taskCount;
        }

        @Override
        public void showList(TaskList tasks) {
            listedTasks = tasks;
        }

        @Override
        public void showMatchingTasks(TaskList tasks, String keyword) {
            searchedTasks = tasks;
            this.keyword = keyword;
        }

        @Override
        public void showTasksOnDate(TaskList tasks, LocalDate date) {
            datedTasks = tasks;
            this.date = date;
        }

        @Override
        public void showBye() {
            byeShown = true;
        }
    }

    /** Records save calls without writing command-test data to disk. */
    private static class RecordingStorage extends Storage {
        private int saveCount;
        private TaskList savedTasks;

        RecordingStorage() {
            super("unused");
        }

        @Override
        public void save(TaskList tasks) {
            saveCount++;
            savedTasks = tasks;
        }
    }
}
