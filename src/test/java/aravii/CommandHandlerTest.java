package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Covers command execution after parsing has validated the command syntax.
 */
class CommandHandlerTest {
    @Test
    void execute_addEveryTaskType_reportsTaskAndUpdatedCount() {
        TaskList tasks = new TaskList();
        CommandHandler handler = new CommandHandler();

        assertEquals("Added: [T] [ ] read\nNow you have 1 task in the list.",
                handler.execute(tasks, Parser.parse("todo read")));
        handler.execute(tasks, Parser.parse("mark 1"));
        assertEquals("Added: [D] [ ] report (by: Sep 20 2026)\nNow you have 2 tasks in the list.",
                handler.execute(tasks, Parser.parse("deadline report /by 2026-09-20")));
        assertEquals("Added: [E] [ ] meeting (from: Sep 21 2026, 14:00 to: Sep 21 2026, 15:00)"
                + "\nNow you have 3 tasks in the list.", handler.execute(tasks,
                        Parser.parse("event meeting /from 2026-09-21 14:00 /to 2026-09-21 15:00")));
        handler.execute(tasks, Parser.parse("delete 1"));
        assertEquals("Added: [T] [ ] revise\nNow you have 3 tasks in the list.",
                handler.execute(tasks, Parser.parse("todo revise")));
    }

    @Test
    void execute_queryCommands_returnUsefulResponses() {
        TaskList tasks = new TaskList();
        CommandHandler handler = new CommandHandler();

        assertTrue(handler.execute(tasks, Parser.parse("help")).contains("deadline"));
        assertEquals("There are no tasks.", handler.execute(tasks, Parser.parse("list")));
        assertEquals("No matching tasks found.", handler.execute(tasks, Parser.parse("find missing")));
        assertEquals("Bye. Hope to see you again soon!", handler.execute(tasks, Parser.parse("bye")));
    }

    @Test
    void execute_mutatingCommands_updateAndFormatTasks() {
        TaskList tasks = new TaskList();
        CommandHandler handler = new CommandHandler();

        assertTrue(handler.execute(tasks, Parser.parse("todo write tests")).contains("write tests"));
        assertTrue(handler.execute(tasks, Parser.parse("mark 1")).contains("[X]"));
        assertTrue(handler.execute(tasks, Parser.parse("unmark 1")).contains("[ ]"));
        assertTrue(handler.execute(tasks, Parser.parse("delete 1")).contains("write tests"));
        assertEquals("There are no tasks.", handler.execute(tasks, Parser.parse("list")));
    }
}
