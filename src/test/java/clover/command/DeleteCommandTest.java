package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.storage.Storage;
import clover.task.Deadline;
import clover.task.Event;
import clover.task.TaskList;
import clover.task.ToDo;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

public class DeleteCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void delete_invalidTaskNumber_exceptionThrown() throws CloverException {
        String command = "delete 3";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), ui, storage));

        assertEquals("Choose a valid quest number to clear.", exception.getMessage());

    }

    @Test
    public void validTaskNumber_correctFormat_taskDeleted() throws CloverException {
        String command = "delete 2";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("do cooking"));
        taskList.add(new Event("do baking",
                LocalDate.of(2021, 12, 21),
                LocalDate.of(2022, 12, 21)));
        taskList.add(new Deadline("read book",
                LocalDate.of(2021, 12, 21)));
        cmd.execute(taskList, new TutoreeList(), ui, storage);

        assertEquals(2, taskList.size());
        ToDo todo = assertInstanceOf(ToDo.class, taskList.get(0));
        Deadline deadline = assertInstanceOf(Deadline.class, taskList.get(1));
        assertEquals("do cooking", todo.getDescription());
        assertEquals("read book", deadline.getDescription());
        assertEquals(LocalDate.of(2021, 12, 21), deadline.getEndBy());
        assertFalse(todo.isDone());
    }
}
