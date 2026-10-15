package pebby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pebby.parser.CommandType;
import pebby.storage.Storage;
import pebby.task.TaskList;
import pebby.ui.Ui;

class CommandTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void addCommand_validTaskTypes_addsTasksAndReportsCount() {
        TaskList tasks = new TaskList();
        StringBuilder response = new StringBuilder();

        execute(new AddCommand(CommandType.TODO, "read book"), tasks, response);
        execute(new AddCommand(CommandType.DEADLINE, "return book /by 15 June 2026"), tasks, response);
        execute(new AddCommand(CommandType.EVENT, "meeting /from 2026-06-15 /to 2026-06-16"), tasks, response);

        assertEquals(3, tasks.size());
        assertEquals("Got it. I've added this task: \n  [T] [ ] read book\n"
                        + "Now you have 1 tasks in the list.\n"
                        + "Got it. I've added this task: \n  [D] [ ] return book (by: Jun 15 2026)\n"
                        + "Now you have 2 tasks in the list.\n"
                        + "Got it. I've added this task: \n"
                        + "  [E] [ ] meeting (from: 2026-06-15 to: 2026-06-16)\n"
                        + "Now you have 3 tasks in the list.\n",
                normalizeLineEndings(response.toString()));
    }

    @Test
    void addCommand_invalidArguments_reportsSpecificValidationErrors() {
        assertAddError(CommandType.TODO, "", "Invalid todo: Please provide a description.\n");
        assertAddError(CommandType.DEADLINE, "return book",
                "Invalid deadline: Please include exactly one /by followed by a deadline.\n");
        assertAddError(CommandType.EVENT, "meeting /to 2026-06-16 /from 2026-06-15",
                "Invalid event: Please put /from before /to.\n");
    }

    @Test
    void listCommand_emptyAndPopulatedLists_formatsNumberedTasks() {
        TaskList tasks = new TaskList();
        StringBuilder response = new StringBuilder();

        execute(new ListCommand(), tasks, response);
        tasks.addTodo("read book");
        execute(new ListCommand(), tasks, response);

        assertEquals("Here are the tasks in your list:\n"
                        + "Here are the tasks in your list:\n1. [T] [ ] read book\n",
                normalizeLineEndings(response.toString()));
    }

    @Test
    void deleteCommand_validAndInvalidNumbers_updatesListOrShowsError() {
        TaskList tasks = new TaskList();
        tasks.addTodo("first task");
        tasks.addTodo("second task");
        StringBuilder response = new StringBuilder();

        execute(new DeleteCommand("1"), tasks, response);
        execute(new DeleteCommand("zero"), tasks, response);
        execute(new DeleteCommand("3"), tasks, response);

        assertEquals(1, tasks.size());
        assertEquals("second task", tasks.get(0).getDescription());
        assertEquals("Noted. I've removed this task: \n[T] [ ] first task\n"
                        + "Now you have 1 tasks in the list.\n"
                        + "Invalid delete: Please provide a whole task number.\n"
                        + "Invalid delete: Please choose a task number from 1 to 1.\n",
                normalizeLineEndings(response.toString()));
    }

    @Test
    void scheduleCommand_matchingEmptyAndInvalidDates_formatsExpectedResponse() {
        TaskList tasks = new TaskList();
        tasks.addDeadline("return book", "2026-06-15");
        StringBuilder response = new StringBuilder();

        execute(new ScheduleCommand("15 June 2026"), tasks, response);
        execute(new ScheduleCommand("2026-06-16"), tasks, response);
        execute(new ScheduleCommand("tomorrow"), tasks, response);

        assertEquals("Schedule for 2026-06-15:\n1. [D] [ ] return book (by: Jun 15 2026)\n"
                        + "No tasks scheduled for 2026-06-16.\n"
                        + "Invalid schedule: Please use a valid date as yyyy-MM-dd or d MMMM yyyy, "
                        + "for example 2019-12-02 or 15 June 2026.\n",
                normalizeLineEndings(response.toString()));
    }

    @Test
    void exitCommand_isExit_returnsTrue() {
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), new Ui(new StringBuilder()), storage());

        assertTrue(command.isExit());
        assertFalse(new ListCommand().isExit());
    }

    private void assertAddError(CommandType type, String argument, String expectedResponse) {
        StringBuilder response = new StringBuilder();

        execute(new AddCommand(type, argument), new TaskList(), response);

        assertEquals(expectedResponse, normalizeLineEndings(response.toString()));
    }

    private void execute(Command command, TaskList tasks, StringBuilder response) {
        command.execute(tasks, new Ui(response), storage());
    }

    private Storage storage() {
        return new Storage(temporaryDirectory.resolve("pebby.txt"));
    }

    /**
     * Converts platform-specific line separators to the newline convention used by expected test strings.
     */
    private String normalizeLineEndings(String response) {
        return response.replace("\r\n", "\n");
    }
}
