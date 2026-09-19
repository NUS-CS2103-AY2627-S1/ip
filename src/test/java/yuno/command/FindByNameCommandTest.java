package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.task.Task;

class FindByNameCommandTest extends CommandTestSupport {
    @Test
    void execute_matchingDescription_displaysMatchingTasks() throws YunoException {
        Task firstMatch = taskList.addTask("read project requirements");
        taskList.addTask("buy groceries");
        Task secondMatch = taskList.addTask("submit project report");

        CommandResult commandResult = new FindByNameCommand("project")
                .execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(List.of(firstMatch, secondMatch), ui.getDisplayedNameTasks());
    }

    @Test
    void execute_noMatchingDescription_displaysEmptyList() throws YunoException {
        taskList.addTask("read book");

        CommandResult commandResult = new FindByNameCommand("project")
                .execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertTrue(ui.getDisplayedNameTasks().isEmpty());
    }

    @Test
    void execute_lowercaseUnicodeSubstring_matchesMixedCaseDescription() throws YunoException {
        Task matchingTask = taskList.addTask("完成 PROJECT 报告");

        new FindByNameCommand("project").execute(taskList, ui, storage);

        assertEquals(List.of(matchingTask), ui.getDisplayedNameTasks());
    }

    @Test
    void execute_blankSearchText_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class,
                () -> new FindByNameCommand("").execute(taskList, ui, storage));
    }
}
