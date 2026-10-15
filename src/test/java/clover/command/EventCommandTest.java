package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.storage.Storage;
import clover.task.Event;
import clover.task.Task;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

class EventCommandTest {
    @TempDir
    Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {
        "event do homework",
        "event do art /from /to 2021-12-21",
        "event do coding /from 2021-12-21 /to",
        "event /from 2021-12-21 /to 2022-12-21"
    })
    void execute_invalidStructure_exceptionThrownWithoutAddingTask(String command) throws CloverException {

        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), ui, storage));

        assertEquals("To schedule a grove event, use: event DESCRIPTION /from START /to END. Optional: add "
                + "/for TUTOREE NAME.", exception.getMessage());
        assertTrue(taskList.asList().isEmpty());
    }

    @Test
    void execute_validEvent_taskAddedAndSaved() throws IOException, CloverException {
        String command = "event do cooking /from 2021-12-21 /to 2022-12-21";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        cmd.execute(taskList, new TutoreeList(), ui, storage);

        assertEquals(1, taskList.size());
        Event event = assertInstanceOf(Event.class, taskList.get(0));
        assertEquals("do cooking", event.getDescription());
        assertEquals(LocalDate.of(2021, 12, 21), event.getStart());
        assertEquals(LocalDate.of(2022, 12, 21), event.getEnd());
        assertFalse(event.isDone());

        List<Task> savedTasks = storage.load();
        Event savedEvent = assertInstanceOf(Event.class, savedTasks.getFirst());
        assertEquals(event.getDescription(), savedEvent.getDescription());
        assertEquals(event.getStart(), savedEvent.getStart());
        assertEquals(event.getEnd(), savedEvent.getEnd());
    }

    @Test
    void execute_invalidStartDate_exceptionThrownWithoutAddingTask() throws CloverException {
        String command = "event do cooking /from today /to 2022-12-21";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), ui, storage));

        assertEquals("Enter a date in yyyy-MM-dd format, for example 2026-02-28.", exception.getMessage());
        assertTrue(taskList.asList().isEmpty());
    }

    @Test
    void execute_invalidEndDate_exceptionThrownWithoutAddingTask() throws CloverException {
        String command = "event do cooking /from 2021-12-21 /to tomorrow";
        Command cmd = Parser.parse(command);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("clover.txt"));
        TaskList taskList = new TaskList();
        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), ui, storage));

        assertEquals("Enter a date in yyyy-MM-dd format, for example 2026-02-28.", exception.getMessage());
        assertTrue(taskList.asList().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "event meeting /from 2021-12-21 /to 2021-12-21",
        "event meeting /from 2021-12-22 /to 2021-12-21"
    })
    void execute_endDateNotAfterStartDate_exceptionThrownWithoutAddingTask(String command) throws CloverException {
        Command cmd = Parser.parse(command);
        TaskList taskList = new TaskList();

        CloverException exception = assertThrows(CloverException.class, () -> cmd.execute(
                taskList, new TutoreeList(), new Ui(), new Storage(tempDir.resolve("clover.txt"))));

        assertEquals("An event's end date must be after its start date.", exception.getMessage());
        assertTrue(taskList.asList().isEmpty());
    }
}
