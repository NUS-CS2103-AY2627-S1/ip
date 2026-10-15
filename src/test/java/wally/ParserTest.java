package wally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests command routing, responses, and validation through the parser's public API. */
public class ParserTest {
    @Test
    public void processCommand_emptyListAndSchedule_returnsHeaders() {
        Tasklist tasks = new Tasklist();
        assertEquals("Here are the tasks in your list:", Parser.processCommand("list", tasks));
        assertEquals("Here are the tasks in your schedule:", Parser.processCommand("schedule", tasks));
    }

    @Test
    public void processCommand_addAndList_preservesInsertionOrder() {
        Tasklist tasks = new Tasklist();
        assertEquals("The following task has been added:\n[T][ ] read\nNow you have 1 tasks in the list.",
                Parser.processCommand("todo read", tasks));
        assertEquals("The following task has been added:\n[D][ ] submit (by: 14 Sep 2026, 12:00)"
                + "\nNow you have 2 tasks in the list.",
                Parser.processCommand("deadline submit /by 2026-09-14 12:00", tasks));
        assertEquals("The following task has been added:"
                + "\n[E][ ] meet (from: 12 Sep 2026, 10:00 to: 12 Sep 2026, 11:00)"
                + "\nNow you have 3 tasks in the list.",
                Parser.processCommand("event meet /from 2026-09-12 10:00 /to 2026-09-12 11:00", tasks));
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read"
                + "\n2. [D][ ] submit (by: 14 Sep 2026, 12:00)"
                + "\n3. [E][ ] meet (from: 12 Sep 2026, 10:00 to: 12 Sep 2026, 11:00)",
                Parser.processCommand("list", tasks));
    }

    @Test
    public void processCommand_markUnmarkAndDelete_updatesTaskAndResponse() {
        Tasklist tasks = new Tasklist();
        Parser.processCommand("todo read", tasks);
        assertEquals("The following task has been marked as done:\n[T][X] read",
                Parser.processCommand("mark 1", tasks));
        assertEquals("The following task has been marked as not done yet:\n[T][ ] read",
                Parser.processCommand("unmark 1", tasks));
        assertEquals("The following task has been removed:\n[T][ ] read\nNow you have 0 tasks in the list.",
                Parser.processCommand("delete 1", tasks));
        assertEquals(0, tasks.getSize());
    }

    @Test
    public void processCommand_find_matchesCaseInsensitivelyAndKeepsOriginalNumbers() {
        Tasklist tasks = new Tasklist();
        Parser.processCommand("todo write", tasks);
        Parser.processCommand("todo Read book", tasks);
        Parser.processCommand("todo read notes", tasks);
        assertEquals("Here are the matching tasks in your list:\n2.[T][ ] Read book\n3.[T][ ] read notes",
                Parser.processCommand("find READ", tasks));
        assertEquals("Here are the matching tasks in your list:", Parser.processCommand("find missing", tasks));
    }

    @Test
    public void processCommand_invalidIndices_returnsErrorsWithoutMutation() {
        Tasklist tasks = new Tasklist();
        for (String command : new String[] {"mark", "unmark", "delete"}) {
            assertEquals("You have no tasklist yet!", Parser.processCommand(command + " 1", tasks));
        }
        Parser.processCommand("todo read", tasks);
        for (String command : new String[] {"mark", "unmark", "delete"}) {
            assertEquals("Enter an index between 1 and 1", Parser.processCommand(command + " 0", tasks));
            assertEquals("Enter an index between 1 and 1", Parser.processCommand(command + " 2", tasks));
        }
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read", Parser.processCommand("list", tasks));
    }

    @Test
    public void processCommand_invalidCommandOrFormat_returnsHelpfulErrors() {
        Tasklist tasks = new Tasklist();
        assertEquals("Invalid Command Entered!", Parser.processCommand("unknown", tasks));
        assertEquals("Invalid Command Entered!", Parser.processCommand("", tasks));
        assertEquals("Format: deadline <name> /by <date: yyyy-MM-dd> <time: HH:mm>",
                Parser.processCommand("deadline read /by tomorrow", tasks));
        assertEquals("Format: event <name> /from <date: yyyy-MM-dd> <time: HH:mm>"
                + " /to <date: yyyy-MM-dd> <time: HH:mm>", Parser.processCommand("event read", tasks));
        assertThrows(AssertionError.class, () -> Parser.processCommand(null, tasks));
        assertEquals(0, tasks.getSize());
    }

    @Test
    public void processCommand_invalidDateTimes_returnsErrorWithoutAddingTasks() {
        Tasklist tasklist = new Tasklist();
        Parser.processCommand("todo existing task", tasklist);
        String[] invalidDates = {
            "2026-02-29 10:00", "2026-02-30 10:00", "2026-04-31 10:00",
            "2026-13-01 10:00", "2026-00-01 10:00", "2026-01-00 10:00",
            "2026-01-01 24:00", "2026-01-01 12:60"
        };
        for (String date : invalidDates) {
            String[] commands = {
                "deadline task /by " + date,
                "event task /from " + date + " /to 2027-01-01 10:00",
                "event task /from 2025-01-01 10:00 /to " + date
            };
            for (String command : commands) {
                assertEquals("Invalid date or time. Use a valid date and time in yyyy-MM-dd HH:mm format.",
                        Parser.processCommand(command, tasklist));
                assertEquals(1, tasklist.getSize());
                assertEquals("[T][ ] existing task", tasklist.getTask(1).toString());
            }
        }
    }

    @Test
    public void processCommand_validLeapDay_addsDeadlineAndEvent() {
        Tasklist tasklist = new Tasklist();
        Parser.processCommand("deadline leap submission /by 2028-02-29 23:59", tasklist);
        Parser.processCommand("event leap meeting /from 2028-02-29 00:00 /to 2028-03-01 00:00", tasklist);
        assertEquals(2, tasklist.getSize());
        assertEquals("deadline leap submission /by 2028-02-29 23:59", tasklist.getTask(1).getCommand());
        assertEquals("event leap meeting /from 2028-02-29 00:00 /to 2028-03-01 00:00",
                tasklist.getTask(2).getCommand());
    }

    @Test
    public void processCommand_duplicateDescriptions_rejectsAllTaskTypes() {
        Tasklist tasklist = new Tasklist();
        Parser.processCommand("todo meeting", tasklist);
        Parser.processCommand("mark 1", tasklist);
        String[] duplicates = {
            "todo meeting",
            "todo  MEETING ",
            "deadline meeting /by 2026-09-14 12:00",
            "event meeting /from 2026-09-14 10:00 /to 2026-09-14 11:00"
        };
        for (String command : duplicates) {
            assertEquals("A task with this description already exists.", Parser.processCommand(command, tasklist));
            assertEquals(1, tasklist.getSize());
            assertEquals("[T][X] meeting", tasklist.getTask(1).toString());
        }
        Parser.processCommand("todo different task", tasklist);
        assertEquals(2, tasklist.getSize());
        Parser.processCommand("delete 1", tasklist);
        Parser.processCommand("todo meeting", tasklist);
        assertEquals(2, tasklist.getSize());
        assertEquals("[T][ ] meeting", tasklist.getTask(2).toString());
    }

    @Test
    public void parseBackgroundColour_supportedColours_returnsCssColour() throws InvalidBackgroundColourException {
        assertEquals("black", Parser.parseBackgroundColour("black"));
        assertEquals("white", Parser.parseBackgroundColour("white"));
        assertEquals("lightblue", Parser.parseBackgroundColour("light blue"));
        assertEquals("lightblue", Parser.parseBackgroundColour("blue"));
        assertEquals("white", Parser.parseBackgroundColour(" WHITE "));
    }

    @Test
    public void parseBackgroundColour_unsupportedOrMissingColour_throwsWithMessage() {
        for (String colour : new String[] {"red", "", "blue extra", "#000000"}) {
            InvalidBackgroundColourException exception =
                    assertThrows(InvalidBackgroundColourException.class, () -> Parser.parseBackgroundColour(colour));
            assertEquals("Only black, white and blue supported", exception.getMessage());
        }
    }

    @Test
    public void processCommand_byeCommand_returnsTerminationResponse() {
        assertEquals("TERMINATE_PROGRAM", Parser.processCommand("bye", null));
    }

    @Test
    public void processCommand_scheduleCommand_ordersDatedTasksBeforeTodos()
            throws InvalidEventException, DuplicateTaskException {
        Tasklist tasklist = new Tasklist();
        tasklist.addTask(new ToDo("write report"));
        tasklist.addTask(new Deadline("submit assignment", "2026-09-14 23:59"));
        tasklist.addTask(new Event("team meeting", "2026-09-12 10:00", "2026-09-12 11:00"));

        String expected = "Here are the tasks in your schedule:"
                + "\n3. [E][ ] team meeting (from: 12 Sep 2026, 10:00 to: 12 Sep 2026, 11:00)"
                + "\n2. [D][ ] submit assignment (by: 14 Sep 2026, 23:59)"
                + "\n1. [T][ ] write report";

        assertEquals(expected, Parser.processCommand("schedule", tasklist));
    }

    @Test
    public void processCommand_eventStartNotBeforeEnd_rejectsWithoutAddingTask() throws DuplicateTaskException {
        Tasklist tasklist = new Tasklist();
        tasklist.addTask(new ToDo("existing task"));
        for (String end : new String[] {"2026-09-12 10:00", "2026-09-12 09:00", "2026-09-11 11:00"}) {
            assertEquals("Event start must be before end.", Parser.processCommand(
                    "event meeting /from 2026-09-12 10:00 /to " + end, tasklist));
            assertEquals(1, tasklist.getSize());
            assertEquals("[T][ ] existing task", tasklist.getTask(1).toString());
        }
    }

    @Test
    public void processCommand_eventAcrossMidnight_addsTask() {
        Tasklist tasklist = new Tasklist();
        Parser.processCommand("event night shift /from 2026-09-12 23:00 /to 2026-09-13 01:00", tasklist);
        assertEquals(1, tasklist.getSize());
        assertEquals("[E][ ] night shift (from: 12 Sep 2026, 23:00 to: 13 Sep 2026, 01:00)",
                tasklist.getTask(1).toString());
    }
}
