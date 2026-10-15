package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import task.DeadlineTask;
import task.EventTask;
import task.Task;
import task.TaskList;

/**
 * Tests for parsing and executing commands.
 */
public class ParserTest {
    @Test
    public void testCommandParse() {
        assertEquals(
                new ArrayList<>(List.of("Added: [T][ ] hello")),
                Parser.parseCommand("todo hello", new TaskList())
        );
    }

    @Test
    public void testDuplicateTodoIsRejected() {
        TaskList tasks = new TaskList();
        Parser.parseCommand("todo hello", tasks);

        assertEquals(
                new ArrayList<>(List.of("REEEEAAAUUU: TASK ALREADY EXISTS!!!!")),
                Parser.parseCommand("todo hello", tasks)
        );
        assertEquals(1, tasks.size());
    }

    @Test
    public void testDuplicateDeadlineIsRejected() {
        TaskList tasks = new TaskList();
        Parser.parseCommand("deadline report /by 09/09/2026 10:00", tasks);

        assertEquals(
                new ArrayList<>(List.of("REEEEAAAUUU: TASK ALREADY EXISTS!!!!")),
                Parser.parseCommand("deadline report /by 09/09/2026 10:00", tasks)
        );
        assertEquals(1, tasks.size());
    }

    @Test
    public void testDuplicateEventIsRejected() {
        TaskList tasks = new TaskList();
        Parser.parseCommand(
                "event meeting /from 09/09/2026 10:00 /to 09/09/2026 11:00", tasks);

        assertEquals(
                new ArrayList<>(List.of("REEEEAAAUUU: TASK ALREADY EXISTS!!!!")),
                Parser.parseCommand(
                        "event meeting /from 09/09/2026 10:00 /to 09/09/2026 11:00", tasks)
        );
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseCommand_deadlineWithInvalidDateFormat_returnsError() {
        // Matches the dd/MM/yyyy HH:mm shape but is not a real date (month 13).
        ArrayList<Object> result = Parser.parseCommand(
                "deadline report /by 09/13/2026 10:00", new TaskList());

        assertEquals(new ArrayList<>(List.of("REEEEAAAUUU: WRONG TIME FORMAT!!!")), result);
    }

    @Test
    public void parseCommand_eventWithInvalidDateFormat_returnsError() {
        // Matches the dd/MM/yyyy HH:mm shape but is not a real date (month 13).
        ArrayList<Object> result = Parser.parseCommand(
                "event meeting /from 09/13/2026 10:00 /to 09/09/2026 11:00", new TaskList());

        assertEquals(new ArrayList<>(List.of("REEEEAAAUUU: WRONG TIME FORMAT!!!")), result);
    }

    @Test
    public void parseCommand_todoSignature_addsTaskWithoutDuplicateCheck() {
        TaskList tasks = new TaskList();
        ArrayList<Object> result = Parser.parseCommand("T|1|buy milk", tasks);

        // Signature commands (used when loading persisted data) report the parsed task
        // back to the caller rather than mutating the passed-in list themselves.
        assertEquals("Added: [T][X] buy milk", result.get(0));
        assertEquals(new Task("buy milk", true), result.get(1));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void parseCommand_deadlineSignature_addsTask() {
        TaskList tasks = new TaskList();
        ArrayList<Object> result = Parser.parseCommand(
                "D|0|submit report|09/09/2026 10:00", tasks);

        assertEquals("Added: [D][ ] submit report (by: 09/09/2026 10:00)", result.get(0));
    }

    @Test
    public void parseCommand_eventSignature_addsTask() {
        TaskList tasks = new TaskList();
        ArrayList<Object> result = Parser.parseCommand(
                "E|0|team meeting|09/09/2026 10:00|09/09/2026 11:00", tasks);

        assertEquals(
                "Added: [E][ ] team meeting (from: 09/09/2026 10:00 to: 09/09/2026 11:00)",
                result.get(0));
    }

    @Test
    public void parseCommand_list_emptyListReturnsErrorMessage() {
        assertEquals(
                new ArrayList<>(List.of("RIEEEEAAAU: HISTORY IS EMPTY")),
                Parser.parseCommand("list", new TaskList())
        );
    }

    @Test
    public void parseCommand_list_returnsNumberedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));

        assertEquals(
                new ArrayList<>(List.of("1. [T][ ] buy milk")),
                Parser.parseCommand("list", tasks)
        );
    }

    @Test
    public void parseCommand_mark_marksTaskAsDone() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));

        Parser.parseCommand("mark 1", tasks);

        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void parseCommand_unmark_unmarksTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk", true));

        Parser.parseCommand("unmark 1", tasks);

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void parseCommand_mark_invalidIndexReturnsError() {
        ArrayList<Object> result = Parser.parseCommand("mark 5", new TaskList());
        assertEquals(new ArrayList<>(List.of("RIEEEEAAAU: INVALID INDEX!")), result);
    }

    @Test
    public void parseCommand_mark_numberTooLargeReturnsError() {
        ArrayList<Object> result = Parser.parseCommand("mark 99999999999999", new TaskList());

        assertTrue(Parser.isErrorMessage((String) result.get(0)));
    }

    @Test
    public void parseCommand_delete_removesTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));

        ArrayList<Object> result = Parser.parseCommand("delete 1", tasks);

        assertEquals(new ArrayList<>(List.of("Removed: [T][ ] buy milk")), result);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void parseCommand_delete_invalidIndexReturnsError() {
        ArrayList<Object> result = Parser.parseCommand("delete 1", new TaskList());
        assertEquals(new ArrayList<>(List.of("RIEEEEAAAU: INVALID INDEX!")), result);
    }

    @Test
    public void parseCommand_find_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));
        tasks.add(new Task("read book"));

        ArrayList<Object> result = Parser.parseCommand("find milk", tasks);

        assertEquals(
                new ArrayList<>(List.of("Here are the matching tasks in your list:\n1. [T][ ] buy milk")),
                result
        );
    }

    @Test
    public void parseCommand_find_noMatchesReturnsError() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("buy milk"));

        ArrayList<Object> result = Parser.parseCommand("find bicycle", tasks);

        assertEquals(new ArrayList<>(List.of("RIEEEEAAAU: NO MATCHING TASKS!")), result);
    }

    @Test
    public void parseCommand_exit_returnsFarewellMessage() {
        ArrayList<Object> result = Parser.parseCommand("bye", new TaskList());
        assertEquals(new ArrayList<>(List.of("seeya cutie ;)")), result);
    }

    @Test
    public void parseCommand_unknownCommand_returnsError() {
        ArrayList<Object> result = Parser.parseCommand("gibberish", new TaskList());
        assertEquals(new ArrayList<>(List.of("REEEEAAAUUU!!!")), result);
    }

    @Test
    public void isErrorMessage_recognisesBothErrorPrefixes() {
        assertTrue(Parser.isErrorMessage("REEEEAAAUUU: WRONG TIME FORMAT!!!"));
        assertTrue(Parser.isErrorMessage("RIEEEEAAAU: INVALID INDEX!"));
    }

    @Test
    public void isErrorMessage_returnsFalseForNormalMessages() {
        assertFalse(Parser.isErrorMessage("Added: [T][ ] buy milk"));
        assertFalse(Parser.isErrorMessage(null));
    }

    @Test
    public void parseTaskSignature_parsesTodoSignature() {
        assertEquals(new Task("buy milk", true), Parser.parseTaskSignature("T|1|buy milk"));
    }

    @Test
    public void parseTaskSignature_parsesDeadlineSignature() {
        assertEquals(
                new DeadlineTask("submit report", LocalDateTime.of(2026, 9, 9, 10, 0)),
                Parser.parseTaskSignature("D|0|submit report|09/09/2026 10:00"));
    }

    @Test
    public void parseTaskSignature_parsesEventSignature() {
        assertEquals(
                new EventTask(
                        "team meeting",
                        LocalDateTime.of(2026, 9, 9, 10, 0),
                        LocalDateTime.of(2026, 9, 9, 11, 0)),
                Parser.parseTaskSignature(
                        "E|0|team meeting|09/09/2026 10:00|09/09/2026 11:00"));
    }

    @Test
    public void parseTaskSignature_malformedSignatureReturnsNull() {
        assertNull(Parser.parseTaskSignature("not a valid signature"));
    }

    @Test
    public void parseTaskSignature_emptyStringReturnsNull() {
        assertNull(Parser.parseTaskSignature(""));
    }
}
