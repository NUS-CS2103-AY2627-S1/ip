package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.task.Task;

class FindByDateCommandTest extends CommandTestSupport {
    @Test
    void execute_validDate_displaysMatchingTasks() throws YunoException {
        Task todo = taskList.addTask("read book");
        Task deadline = taskList.addTask(
                "submit report", LocalDateTime.of(2026, 8, 30, 18, 0));
        taskList.addTask("future report", LocalDateTime.of(2026, 8, 31, 18, 0));

        CommandResult commandResult = new FindByDateCommand("2026-08-30")
                .execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(List.of(todo, deadline), ui.getDisplayedDateTasks());
    }

    @Test
    void execute_invalidDate_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new FindByDateCommand("2026-02-30").execute(taskList, ui, storage));
    }
}
