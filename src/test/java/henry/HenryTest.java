package henry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import henry.parser.CommandType;

/**
 * Tests Henry's end-to-end command processing API used by the GUI.
 */
public class HenryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addAndListTask_returnsExpectedResponses() {
        Henry henry = new Henry(temporaryDirectory.resolve("henry.txt"));

        String addResponse = henry.getResponse("todo borrow book");
        String listResponse = henry.getResponse("list");

        assertEquals("Got it. I've added this to our route:\n"
                + "[T][ ] borrow book\n"
                + "You now have 1 task on the list.", addResponse);
        assertEquals("Here's what's ahead:\n"
                + "1. [T][ ] borrow book", listResponse);
        assertEquals(CommandType.LIST, henry.getLastCommandType());
    }

    @Test
    public void getResponse_aliases_executeCanonicalCommands() {
        Henry henry = new Henry(temporaryDirectory.resolve("henry.txt"));

        String addResponse = henry.getResponse("t borrow book");
        String markResponse = henry.getResponse("m 1");
        String listResponse = henry.getResponse("l");

        assertEquals("Got it. I've added this to our route:\n"
                + "[T][ ] borrow book\n"
                + "You now have 1 task on the list.", addResponse);
        assertEquals("Nice, that one's done.\n"
                + "[T][X] borrow book", markResponse);
        assertEquals("Here's what's ahead:\n"
                + "1. [T][X] borrow book", listResponse);
        assertEquals(CommandType.LIST, henry.getLastCommandType());
    }

    @Test
    public void getResponse_findWithNoMatches_returnsNotFoundMessage() {
        Henry henry = new Henry(temporaryDirectory.resolve("henry.txt"));
        henry.getResponse("todo borrow book");

        String response = henry.getResponse("find movie");

        assertEquals("I couldn't find a task for that keyword", response);
        assertEquals(CommandType.FIND, henry.getLastCommandType());
    }

    @Test
    public void getResponse_help_returnsCommandList() {
        Henry henry = new Henry(temporaryDirectory.resolve("henry.txt"));

        String response = henry.getResponse("help");

        assertEquals("Here are the commands I can help with:\n"
                + "todo DESCRIPTION - Add a todo.\n"
                + "deadline DESCRIPTION /by DATE - Add a deadline.\n"
                + "event DESCRIPTION /from START_DATE /to END_DATE - Add an event.\n"
                + "list - Show all tasks.\n"
                + "find KEYWORD - Find tasks by description.\n"
                + "mark TASK_NUMBER - Mark a task as completed.\n"
                + "unmark TASK_NUMBER - Mark a task as not completed.\n"
                + "delete TASK_NUMBER - Delete a task.\n"
                + "help - Show this command list.\n"
                + "bye - Exit Henry.", response);
        assertEquals(CommandType.HELP, henry.getLastCommandType());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorWithoutThrowing() {
        Henry henry = new Henry(temporaryDirectory.resolve("henry.txt"));

        String response = henry.getResponse("dance");

        assertEquals("I'm not quite sure what you mean. Try todo, deadline, event, list, find, "
                + "mark, unmark, delete, help, or bye.", response);
        assertEquals(CommandType.UNKNOWN, henry.getLastCommandType());
    }

    @Test
    public void getStartupMessage_malformedRecords_reportsSkippedRecordCount() throws IOException {
        Path dataFile = temporaryDirectory.resolve("henry.txt");
        Files.writeString(dataFile, "invalid record\nalso invalid\n");

        Henry henry = new Henry(dataFile);

        assertEquals("I skipped 2 malformed task records while loading " + dataFile + ".",
                henry.getStartupMessage());
    }

    @Test
    public void getResponse_saveFailures_rollsBackAllTaskChanges() throws IOException {
        Path dataFile = temporaryDirectory.resolve("henry.txt");
        Henry henry = new Henry(dataFile);
        henry.getResponse("todo existing task");
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        String addResponse = henry.getResponse("todo unsaved task");
        String markResponse = henry.getResponse("mark 1");
        String deleteResponse = henry.getResponse("delete 1");
        String listResponse = henry.getResponse("list");

        String saveError = "I couldn't save that change. Your task list is unchanged.";
        assertEquals(saveError, addResponse);
        assertEquals(saveError, markResponse);
        assertEquals(saveError, deleteResponse);
        assertEquals("Here's what's ahead:\n"
                + "1. [T][ ] existing task", listResponse);
    }
}
