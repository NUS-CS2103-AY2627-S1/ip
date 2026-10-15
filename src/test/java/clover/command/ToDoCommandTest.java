package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.task.ToDo;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

class ToDoCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void execute_blankDescription_exceptionThrown() throws CloverException {
        String command = "todo ";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), ui, storage));

        assertEquals("To plant a task, use: todo DESCRIPTION. Optional: add /for TUTOREE NAME.",
                exception.getMessage());

    }

    @Test
    void execute_validDescription_taskAdded() throws CloverException {
        String command = "todo do cooking";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        cmd.execute(taskList, new TutoreeList(), ui, storage);

        assertEquals(1, taskList.size());
        ToDo todo = assertInstanceOf(ToDo.class, taskList.get(0));
        assertEquals("do cooking", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    void execute_duplicateDescription_exceptionThrownWithoutAddingSecondTask() throws CloverException {
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        new ToDoCommand("do cooking").execute(taskList, new TutoreeList(), new Ui(), storage);

        CloverException exception = assertThrows(CloverException.class, () -> new ToDoCommand("do cooking")
                .execute(taskList, new TutoreeList(), new Ui(), storage));

        assertEquals("That study quest is already in the grove.", exception.getMessage());
        assertEquals(1, taskList.size());
    }
}
