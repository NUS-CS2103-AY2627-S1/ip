package mono.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;
import mono.storage.Storage;
import mono.task.Task;
import mono.testutil.StorageTestSupport;

/** Tests command tools that validate arguments or delegate simple operations. */
public class BasicToolTest {
    @BeforeEach
    public void resetStorage() throws IOException {
        StorageTestSupport.resetStorage();
    }

    @Test
    public void todo_validArguments_trimsAddsTaskAndContinues() throws MonoException {
        ToolSignal signal = new TodoTool().invoke("  buy milk  ", new MonoBot());

        assertEquals(ToolSignal.CONTINUE, signal);
        assertEquals("T | 0 | buy milk", onlyStoredTask().toFileString());
    }

    @Test
    public void todo_blankArguments_throwsFormatException() {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new TodoTool().invoke(" \t ", new MonoBot()));

        assertEquals("Todo format: todo <description>", exception.getMessage());
    }

    @Test
    public void todo_unsafeDescription_throwsFormatExceptionWithoutSaving() {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new TodoTool().invoke("buy | sell", new MonoBot()));

        assertEquals("Todo format: todo <description>", exception.getMessage());
        assertEquals(0, new Storage().readStorage().length);
    }

    @Test
    public void todo_nullArguments_throwsFormatException() {
        assertThrows(WrongFormatException.class, () ->
                new TodoTool().invoke(null, new MonoBot()));
    }

    @Test
    public void list_withoutArguments_displaysAndContinues() throws MonoException {
        assertEquals(ToolSignal.CONTINUE, new ListTool().invoke("", new MonoBot()));
    }

    @Test
    public void list_withArguments_throwsFormatException() {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new ListTool().invoke("unexpected", new MonoBot()));

        assertEquals("list does not accept arguments.", exception.getMessage());
    }

    @Test
    public void bye_withoutArguments_exits() throws MonoException {
        assertEquals(ToolSignal.EXIT, new ByeTool().invoke("", new MonoBot()));
    }

    @Test
    public void bye_withArguments_throwsFormatException() {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new ByeTool().invoke("now", new MonoBot()));

        assertEquals("bye does not accept arguments.", exception.getMessage());
    }

    @Test
    public void delete_markUnmark_updatesTask() throws MonoException {
        MonoBot bot = new MonoBot();
        new TodoTool().invoke("task to keep", bot);
        new TodoTool().invoke("task to remove", bot);

        assertEquals(ToolSignal.CONTINUE, new MarkTool().invoke("1", bot));
        assertEquals("T | 1 | task to keep", onlyStoredTaskAt(0).toFileString());
        assertEquals(ToolSignal.CONTINUE, new UnmarkTool().invoke("1", bot));
        assertEquals("T | 0 | task to keep", onlyStoredTaskAt(0).toFileString());
        assertEquals(ToolSignal.CONTINUE, new DeleteTool().invoke("2", bot));
        assertEquals(1, new Storage().readStorage().length);
    }

    @Test
    public void delete_invalidIds_propagateErrors() {
        MonoBot bot = new MonoBot();

        assertThrows(WrongFormatException.class, () -> new DeleteTool().invoke("x", bot));
        assertThrows(WrongFormatException.class, () -> new MarkTool().invoke("0", bot));
        assertThrows(WrongFormatException.class, () -> new UnmarkTool().invoke("", bot));
        assertThrows(MonoException.class, () -> new DeleteTool().invoke("1", bot));
    }

    private Task onlyStoredTask() {
        return onlyStoredTaskAt(0);
    }

    private Task onlyStoredTaskAt(int index) {
        Task[] tasks = new Storage().readStorage();
        return tasks[index];
    }
}
