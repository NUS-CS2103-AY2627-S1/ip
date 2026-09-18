package milo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import milo.exception.MiloException;
import milo.task.TaskList;
import milo.task.Todo;

/**
 * Tests Command.resolveTaskIndex, the shared task-number validation used by
 * mark, unmark and delete. It is protected static, so this test (in the same
 * package) can call it directly without needing a concrete subclass.
 */
class CommandTest {
    @Test
    void resolveTaskIndex_emptyList_throwsMiloException() {
        TaskList tasks = new TaskList();
        assertThrows(MiloException.class, () ->
                Command.resolveTaskIndex(tasks, "mark", "1"));
    }

    @Test
    void resolveTaskIndex_nonNumericArgument_throwsMiloException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () ->
                Command.resolveTaskIndex(tasks, "mark", "abc"));
    }

    @Test
    void resolveTaskIndex_missingArgument_throwsMiloException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () ->
                Command.resolveTaskIndex(tasks, "mark", ""));
    }

    @Test
    void resolveTaskIndex_zero_throwsMiloException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () ->
                Command.resolveTaskIndex(tasks, "mark", "0"));
    }

    @Test
    void resolveTaskIndex_negative_throwsMiloException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () ->
                Command.resolveTaskIndex(tasks, "mark", "-1"));
    }

    @Test
    void resolveTaskIndex_oneMoreThanSize_throwsMiloException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () ->
                Command.resolveTaskIndex(tasks, "mark", "2"));
    }

    @Test
    void resolveTaskIndex_lastValidNumber_returnsLastIndex() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        assertEquals(1, Command.resolveTaskIndex(tasks, "mark", "2"));
    }

    @Test
    void resolveTaskIndex_firstValidNumber_returnsIndexZero() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(0, Command.resolveTaskIndex(tasks, "mark", "1"));
    }

    @Test
    void resolveTaskIndex_argumentWithWhitespace_isTrimmed() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(0, Command.resolveTaskIndex(tasks, "mark", " 1 "));
    }
}
