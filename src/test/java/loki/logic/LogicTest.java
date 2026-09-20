package loki.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests GUI-facing command processing in {@link Logic}. */
class LogicTest {
    private static final String UPDATE_USAGE = "Usage: update <task number> <field> [<field>...]\n"
            + "Fields: todo=/title; deadline=/title,/by; event=/title,/from,/to\n"
            + "Examples:\n"
            + "update 1 /title Buy groceries\n"
            + "update 2 /by 2019-06-06\n"
            + "update 3 /from 2019-08-06 1400 /to 2019-08-06 1600";

    @TempDir
    Path temporaryDirectory;

    @Test
    void processCommand_todoAndList_returnsAddedTask() {
        Logic logic = createLogic();

        String addResponse = logic.processCommand("todo Buy groceries");
        String listResponse = logic.processCommand("list");

        assertTrue(addResponse.contains("A cunning addition to your saga:"));
        assertTrue(addResponse.contains("[T][ ] Buy groceries"));
        assertTrue(listResponse.contains("1. [T][ ] Buy groceries"));
    }

    @Test
    void processCommand_listEmptyTasks_returnsEmptyListResponse() {
        Logic logic = createLogic();

        assertEquals("You lack any tasks", logic.processCommand("list"));
    }

    @Test
    void processCommand_help_returnsCommandGuide() {
        Logic logic = createLogic();

        String response = logic.processCommand("help");

        assertTrue(response.startsWith("Commands:"));
        assertTrue(response.contains("todo <task description>"));
        assertTrue(response.contains("To-do fields: /title <new title>"));
        assertTrue(response.contains("Deadline fields: /title <new title>, /by <date/time>"));
        assertTrue(response.contains("Event fields: /title <new title>, /from <date/time>, /to <date/time>"));
        assertTrue(response.contains("update 3 /title Final consultation /from 20/9/2026 1400"));
        assertTrue(response.contains("exit (alias: faretheewell)"));
    }

    @Test
    void processCommand_helpWithArguments_returnsError() {
        Logic logic = createLogic();

        String response = logic.processCommand("help todo");

        assertTrue(response.startsWith("Loki error:"));
    }

    @Test
    void processCommand_listMixedTasks_preservesOrderNumberingAndStatus() {
        Logic logic = createLogic();
        logic.processCommand("todo Read book");
        logic.processCommand("deadline Return book /by 2019-06-06");
        logic.processCommand("event Meeting /from 2019-08-06 1400 /to 2019-08-06 1600");
        logic.processCommand("todo Read book");
        logic.processCommand("mark 2");

        String expected = String.join(System.lineSeparator(),
                "1. [T][ ] Read book",
                "2. [D][X] Return book (by: Jun 06 2019)",
                "3. [E][ ] Meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)",
                "4. [T][ ] Read book");
        assertEquals(expected, logic.processCommand("list"));
        assertEquals(expected, logic.processCommand("list"));
    }

    @Test
    void processCommand_listAfterDeletion_renumbersRemainingTasks() {
        Logic logic = createLogic();
        logic.processCommand("todo First task");
        logic.processCommand("todo Second task");
        logic.processCommand("todo Third task");
        logic.processCommand("delete 2");

        assertEquals(String.join(System.lineSeparator(), "1. [T][ ] First task", "2. [T][ ] Third task"),
                logic.processCommand("list"));
    }

    @Test
    void processCommand_markAndUnmark_updatesTaskStatus() {
        Logic logic = createLogic();
        logic.processCommand("todo Write report");

        String markResponse = logic.processCommand("mark 1");
        String unmarkResponse = logic.processCommand("unmark 1");

        assertTrue(markResponse.contains("[T][X] Write report"));
        assertTrue(unmarkResponse.contains("[T][ ] Write report"));
    }

    @Test
    void processCommand_invalidCommand_returnsErrorResponse() {
        Logic logic = createLogic();

        String response = logic.processCommand("dance wildly");

        assertTrue(response.contains("Loki error:"));
    }

    @Test
    void processCommand_invalidTodo_returnsUsageTip() {
        Logic logic = createLogic();

        String response = logic.processCommand("todo");

        assertTrue(response.contains("Usage: todo <task description>"));
        assertTrue(response.contains("Example: todo Buy groceries"));
    }

    @Test
    void processCommand_invalidDeadline_returnsUsageTip() {
        Logic logic = createLogic();

        String response = logic.processCommand("deadline Return book");

        assertTrue(response.contains("Usage: deadline <task description> /by <date/time>"));
        assertTrue(response.contains("Example: deadline Return book /by 2019-06-06"));
    }

    @Test
    void processCommand_invalidEvent_returnsUsageTip() {
        Logic logic = createLogic();

        String response = logic.processCommand("event Project meeting /from Aug 6th 2pm");

        assertTrue(response.contains("Usage: event <title> /from <date/time> /to <date/time>"));
        assertTrue(response.contains("Example: event Project meeting /from 2019-08-06 1400"));
    }

    @Test
    void processCommand_exit_savesTasks() {
        Logic logic = createLogic();
        logic.processCommand("todo Remember to rest");

        String response = logic.processCommand("exit");
        Logic reloadedLogic = createLogic();

        assertTrue(response.contains("Farewell, mortal. May your next quest be worthy of legend."));
        assertTrue(reloadedLogic.processCommand("list").contains("Remember to rest"));
    }

    @Test
    void processCommand_updateMixedFields_preservesTypeStatusAndOrder() {
        Logic logic = createLogic();
        logic.processCommand("todo Read notes");
        logic.processCommand("deadline Submit report /by 2026-09-20 1800");
        logic.processCommand("event Consultation /from 2026-09-20 1400 /to 2026-09-20 1600");
        logic.processCommand("mark 2");

        String response = logic.processCommand(
                "UPDATE 3 /TO 20/9/2026 1700 /TITLE Project consultation /FROM 20/9/2026 1400");

        assertEquals("Updated: [E][ ] Project consultation (from: Sep 20 2026, 2:00 PM to: Sep 20 2026, 5:00 PM)",
                response);
        assertEquals(String.join(System.lineSeparator(),
                "1. [T][ ] Read notes",
                "2. [D][X] Submit report (by: Sep 20 2026, 6:00 PM)",
                "3. [E][ ] Project consultation (from: Sep 20 2026, 2:00 PM to: Sep 20 2026, 5:00 PM)"),
                logic.processCommand("list"));
    }

    @Test
    void processCommand_updateIdenticalValue_succeeds() {
        Logic logic = createLogic();
        logic.processCommand("todo Buy groceries");

        assertEquals("Updated: [T][ ] Buy groceries", logic.processCommand("update 1 /title Buy groceries"));
    }

    @Test
    void processCommand_invalidUpdate_returnsUsageAndLeavesTaskUnchanged() {
        Logic logic = createLogic();
        logic.processCommand("todo Keep this task");
        String before = logic.processCommand("list");

        String response = logic.processCommand("update 1 /by 2026-09-20");

        assertTrue(response.startsWith("Loki error:"));
        assertTrue(response.endsWith(UPDATE_USAGE));
        assertEquals(before, logic.processCommand("list"));
    }

    @Test
    void processCommand_updateIsSavedOnlyAtNormalSavePoint() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Logic logic = new Logic(taskFile.toString());
        logic.processCommand("deadline Submit report /by 2026-09-20 1800");
        logic.processCommand("update 1 /title Submit final report");

        assertFalse(Files.exists(taskFile));

        logic.processCommand("exit");
        Logic reloadedLogic = new Logic(taskFile.toString());
        assertEquals("1. [D][ ] Submit final report (by: Sep 20 2026, 6:00 PM)",
                reloadedLogic.processCommand("list"));
    }

    /**
     * Creates logic backed by an isolated temporary file.
     *
     * @return isolated command-processing logic.
     */
    private Logic createLogic() {
        return new Logic(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
