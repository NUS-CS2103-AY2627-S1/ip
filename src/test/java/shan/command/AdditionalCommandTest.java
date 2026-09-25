package shan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shan.exception.ShanException;
import shan.storage.Storage;
import shan.task.Deadline;
import shan.task.TaskList;
import shan.task.ToDo;

class AdditionalCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void listCommand_emptyAndNonEmptyLists_returnsNumberedTasks() {
        TaskList tasks = new TaskList();
        ListCommand command = new ListCommand();

        assertEquals("Here are the tasks in your list:", command.execute(tasks, storage()));

        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] first\n2.[T][ ] second",
                command.execute(tasks, storage()));
        assertFalse(command.isExit());
        assertFalse(command.isUndoable());
    }

    @Test
    void exitCommand_execute_returnsFarewellAndSignalsExit() {
        ExitCommand command = new ExitCommand();

        assertEquals("Bye! See you soon.",
                command.execute(new TaskList(), storage()));
        assertTrue(command.isExit());
        assertFalse(command.isUndoable());
    }

    @Test
    void onCommand_singleDateWithMatch_returnsMatchingTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report",
                LocalDateTime.of(2026, 9, 15, 18, 0)));

        LocalDate date = LocalDate.of(2026, 9, 15);
        String response = new OnCommand(date, date)
                .execute(tasks, storage());

        assertTrue(response.contains("Got your deadlines and events on Sep 15 2026:"));
        assertTrue(response.contains("1.[D][ ] submit report"));
    }

    @Test
    void onCommand_singleDateWithoutMatch_returnsNoTaskMessage() {
        LocalDate date = LocalDate.of(2026, 9, 15);
        String response = new OnCommand(date, date)
                .execute(new TaskList(), storage());

        assertEquals("There are no deadlines or events on Sep 15 2026.", response);
    }

    @Test
    void onCommand_rangeWithoutMatch_returnsNoTaskMessage() {
        String response = new OnCommand(
                LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17))
                .execute(new TaskList(), storage());

        assertEquals("There are no deadlines or events from Sep 15 2026 to Sep 17 2026.",
                response);
    }

    @Test
    void undoCommand_recordedState_restoresTasksAndConsumesHistory() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("original"));
        TaskList.State originalState = tasks.createState();
        tasks.add(new ToDo("new task"));
        tasks.recordUndoState(originalState);

        String response = new UndoCommand().execute(tasks, storage());

        assertEquals("Got it. I've undone your last command.", response);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] original", tasks.get(1).toString());
        assertFalse(tasks.canUndo());
    }

    @Test
    void undoCommand_withoutHistory_throwsShanException() {
        UndoCommand command = new UndoCommand();

        ShanException exception = assertThrows(ShanException.class, () ->
                command.execute(new TaskList(), storage()));

        assertEquals("There is no command to undo.", exception.getMessage());
    }

    @Test
    void mutatingCommands_isUndoable_returnsTrue() {
        assertTrue(new AddCommand(new ToDo("task")).isUndoable());
        assertTrue(new DeleteCommand(0).isUndoable());
        assertTrue(new MarkCommand(0).isUndoable());
        assertTrue(new UnmarkCommand(0).isUndoable());
    }

    private Storage storage() {
        return new Storage(tempDir.resolve("data.txt"));
    }
}
