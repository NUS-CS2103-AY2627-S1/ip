package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.storage.Storage;
import clover.task.Deadline;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

public class DeadlineCommandTest {
    @TempDir
    Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {
        "deadline do homework",
        "deadline do art /by",
        "deadline /by 2021-10-21",
    })
    public void execute_invalidFormat_exceptionThrown(String command) throws CloverException {
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), ui, storage));

        assertEquals("To set a deadline, use: deadline DESCRIPTION /by DUE DATE. Optional: add /for TUTOREE NAME.",
                exception.getMessage());

    }

    @Test
    public void validFormat_correctFormat_taskAdded() throws CloverException {
        String command = "deadline do cooking /by 2021-12-21";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        cmd.execute(taskList, new TutoreeList(), ui, storage);

        assertEquals(1, taskList.size());
        Deadline deadline = assertInstanceOf(Deadline.class, taskList.get(0));
        assertEquals("do cooking", deadline.getDescription());
        assertEquals(LocalDate.of(2021, 12, 21), deadline.getEndBy());
        assertFalse(deadline.isDone());
    }

    @Test
    public void execute_invalidDate_exceptionThrown() throws CloverException {
        String command = "deadline do art /by tomorrow ";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), ui, storage));

        assertEquals("Enter a date in yyyy-MM-dd format, for example 2026-02-28.", exception.getMessage());

    }
}
