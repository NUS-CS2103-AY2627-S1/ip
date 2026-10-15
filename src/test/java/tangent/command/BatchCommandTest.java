package tangent.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tangent.exception.TangentException;
import tangent.storage.Storage;
import tangent.task.Task;
import tangent.task.TaskList;
import tangent.task.ToDo;
import tangent.ui.Ui;

public class BatchCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void delete_saveFailure_batchIsRolledBack() {
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");
        ToDo third = new ToDo("third");
        TaskList tasks = new TaskList(first, second, third);
        Storage storage = new Storage(tempDir.toString());

        assertThrows(TangentException.class, () -> new DeleteCommand(List.of(0, 2))
                .execute(tasks, new Ui(_ -> { }), storage));

        assertFalse(tasks.isEmpty());
        assertFalse(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
        assertFalse(tasks.get(2).isDone());
    }

    @Test
    public void mark_saveFailure_batchStatusesAreRolledBack() {
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"));
        Storage storage = new Storage(tempDir.toString());

        assertThrows(TangentException.class, () -> new MarkCommand(List.of(0, 1))
                .execute(tasks, new Ui(_ -> { }), storage));

        assertFalse(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void addCommand_successfullyAddsSavesAndReportsTask() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        TaskList tasks = new TaskList();
        ToDo task = new ToDo("read book");
        List<String> messages = new ArrayList<>();

        new AddCommand(task).execute(tasks, new Ui(messages::add), new Storage(dataFile.toString()));

        assertEquals(List.of(task), tasks.toList());
        assertEquals(List.of("got it! you have a new task: ", "[T][ ] read book",
                "you now have 1 task in the list!"), messages);
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(dataFile));
    }

    @Test
    public void addCommand_saveFailure_removesAddedTask() {
        TaskList tasks = new TaskList(new ToDo("existing"));
        Storage storage = new Storage(tempDir.toString());

        assertThrows(TangentException.class, () -> new AddCommand(new ToDo("new")).execute(
                tasks, new Ui(_ -> { }), storage));

        assertEquals("existing", tasks.get(0).getDescription());
        assertEquals(1, tasks.size());
    }

    @Test
    public void deleteCommand_successfullyRemovesSelectedTasksAndReportsThem() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");
        ToDo third = new ToDo("third");
        TaskList tasks = new TaskList(first, second, third);
        List<String> messages = new ArrayList<>();

        new DeleteCommand(List.of(0, 2)).execute(tasks, new Ui(messages::add), new Storage(dataFile.toString()));

        assertEquals(List.of(second), tasks.toList());
        assertEquals(List.of("got it! i've removed these tasks:", "[T][ ] first", "[T][ ] third",
                "removed 2 tasks, 1 task(s) remaining!"), messages);
        assertEquals(List.of("T | 0 | second"), Files.readAllLines(dataFile));
    }

    @Test
    public void deleteCommand_invalidIndex_doesNotMutateOrReport() {
        TaskList tasks = new TaskList(new ToDo("first"));
        List<String> messages = new ArrayList<>();

        assertThrows(TangentException.class, () -> new DeleteCommand(List.of(1)).execute(
                tasks, new Ui(messages::add), new Storage(tempDir.resolve("tangent.txt").toString())));

        assertEquals(1, tasks.size());
        assertTrue(messages.isEmpty());
    }

    @Test
    public void markAndUnmarkCommands_successfullyChangeStatusesAndReportTasks() throws TangentException {
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"), new ToDo("third"));
        Storage storage = new Storage(tempDir.resolve("tangent.txt").toString());
        List<String> markMessages = new ArrayList<>();

        new MarkCommand(List.of(2, 0)).execute(tasks, new Ui(markMessages::add), storage);

        assertTrue(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
        assertTrue(tasks.get(2).isDone());
        assertEquals(List.of("i've marked 2 tasks as done!", "[T][X] first", "[T][X] third"), markMessages);

        List<String> unmarkMessages = new ArrayList<>();
        new UnmarkCommand(List.of(0)).execute(tasks, new Ui(unmarkMessages::add), storage);

        assertFalse(tasks.get(0).isDone());
        assertEquals(List.of("i've marked 1 task as undone!", "[T][ ] first"), unmarkMessages);
    }

    @Test
    public void statusCommand_saveFailure_restoresOriginalStatus() {
        ToDo task = new ToDo("first");
        task.markAsDone();
        TaskList tasks = new TaskList(task);

        assertThrows(TangentException.class, () -> new UnmarkCommand(List.of(0)).execute(
                tasks, new Ui(_ -> { }), new Storage(tempDir.toString())));

        assertTrue(task.isDone());
    }

    @Test
    public void statusCommand_batchSkipsTasksAlreadyAtTargetStatus() throws TangentException {
        ToDo alreadyDone = new ToDo("already done");
        alreadyDone.markAsDone();
        ToDo notDone = new ToDo("not done");
        ToDo alreadyDoneAgain = new ToDo("already done again");
        alreadyDoneAgain.markAsDone();
        TaskList tasks = new TaskList(alreadyDone, notDone, alreadyDoneAgain);
        List<String> messages = new ArrayList<>();

        new MarkCommand(List.of(0, 1, 2)).execute(tasks, new Ui(messages::add),
                new Storage(tempDir.resolve("tangent.txt").toString()));

        assertTrue(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
        assertTrue(tasks.get(2).isDone());
        assertEquals(List.of("i've marked 1 task as done!", "[T][X] not done",
                "the following task(s) were already marked as done:", "[T][X] already done",
                "[T][X] already done again"), messages);
    }

    @Test
    public void statusCommand_allTasksAlreadyAtTargetStatus_reportsWithoutSaving() throws TangentException {
        ToDo doneTask = new ToDo("done");
        doneTask.markAsDone();
        ToDo doneTaskAgain = new ToDo("done again");
        doneTaskAgain.markAsDone();
        TaskList doneTasks = new TaskList(doneTask, doneTaskAgain);
        List<String> markMessages = new ArrayList<>();

        new MarkCommand(List.of(0, 1)).execute(doneTasks, new Ui(markMessages::add),
                new Storage(tempDir.toString()));

        TaskList undoneTasks = new TaskList(new ToDo("undone"), new ToDo("undone again"));
        List<String> unmarkMessages = new ArrayList<>();
        new UnmarkCommand(List.of(0, 1)).execute(undoneTasks, new Ui(unmarkMessages::add),
                new Storage(tempDir.toString()));

        assertEquals(List.of("all selected tasks are already marked as done!"), markMessages);
        assertEquals(List.of("all selected tasks are already marked as undone!"), unmarkMessages);
    }

    @Test
    public void statusCommand_singleTaskAlreadyAtTargetStatus_reportsSingleTaskMessage() throws TangentException {
        ToDo doneTask = new ToDo("done");
        doneTask.markAsDone();
        List<String> markMessages = new ArrayList<>();
        new MarkCommand(List.of(0)).execute(new TaskList(doneTask), new Ui(markMessages::add),
                new Storage(tempDir.toString()));

        List<String> unmarkMessages = new ArrayList<>();
        new UnmarkCommand(List.of(0)).execute(new TaskList(new ToDo("undone")), new Ui(unmarkMessages::add),
                new Storage(tempDir.toString()));

        assertEquals(List.of("the selected task is already marked as done!"), markMessages);
        assertEquals(List.of("the selected task is already marked as undone!"), unmarkMessages);
    }

    @Test
    public void findAndListCommands_reportMatchingAndAllTasks() {
        TaskList tasks = new TaskList(new ToDo("read book"), new ToDo("watch movie"));
        List<String> findMessages = new ArrayList<>();
        List<String> listMessages = new ArrayList<>();
        Ui findUi = new Ui(findMessages::add);
        Ui listUi = new Ui(listMessages::add);

        new FindCommand("BOOK").execute(tasks, findUi, null);
        new ListCommand().execute(tasks, listUi, null);

        assertEquals(List.of("here are the tasks matching the keyword BOOK:", "1. [T][ ] read book"), findMessages);
        assertEquals(List.of("hello! here are your current tasks:", "1. [T][ ] read book", "2. [T][ ] watch movie"),
                listMessages);
    }

    @Test
    public void findThenDelete_usesMatchingTaskIndex() throws TangentException {
        TaskList tasks = new TaskList(new ToDo("first task"), new ToDo("play wordle"),
                new ToDo("last task"));
        Storage storage = new Storage(tempDir.resolve("tangent.txt").toString());

        new FindCommand("wordle").execute(tasks, new Ui(_ -> { }), storage);
        new DeleteCommand(List.of(0)).execute(tasks, new Ui(_ -> { }), storage);

        assertEquals(List.of("first task", "last task"),
                tasks.toList().stream().map(Task::getDescription).toList());
    }

    @Test
    public void findThenMarkAndUnmark_useMatchingTaskIndex() throws TangentException {
        TaskList tasks = new TaskList(new ToDo("first task"), new ToDo("play wordle"),
                new ToDo("last task"));
        Storage storage = new Storage(tempDir.resolve("tangent.txt").toString());

        new FindCommand("wordle").execute(tasks, new Ui(_ -> { }), storage);
        new MarkCommand(List.of(0)).execute(tasks, new Ui(_ -> { }), storage);
        assertTrue(tasks.get(1).isDone());

        new FindCommand("wordle").execute(tasks, new Ui(_ -> { }), storage);
        new UnmarkCommand(List.of(0)).execute(tasks, new Ui(_ -> { }), storage);
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void exitCommand_isExitAndReportsGoodbye() {
        List<String> messages = new ArrayList<>();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), new Ui(messages::add), null);

        assertTrue(command.isExit());
        assertEquals(List.of("bye o/ hope to see you again soon"), messages);
    }

    @Test
    public void nonExitCommands_reportNotExit() {
        assertFalse(new ListCommand().isExit());
    }
}
