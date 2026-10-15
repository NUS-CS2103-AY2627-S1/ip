package lumine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command responses, undo behavior, and greetings from {@link Lumine}.
 */
class LumineTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getGreeting_newSession_returnsGreeting() {
        Lumine lumine = createLumine();

        assertEquals("Hello, I'm Lumine!\nWhat can I do for you today?", lumine.getGreeting());
    }

    @Test
    void getResponse_emptyList_returnsEmptyListMessage() {
        Lumine lumine = createLumine();

        assertEquals("Nice, there are no task on your list!", lumine.getResponse("list"));
    }

    @Test
    void getResponse_deleteOnlyTask_returnsEmptyListMessage() {
        Lumine lumine = createLumine();
        lumine.getResponse("todo temporary task");
        lumine.getResponse("delete 1");

        assertEquals("Nice, there are no task on your list!", lumine.getResponse("list"));
    }

    @Test
    void getResponse_addTodoCommand_returnsCommandOutput() {
        Lumine lumine = createLumine();

        assertEquals("Got it. I've added this task:\n  [T][ ] read textbook\n"
                        + "Now, you have 1 tasks in the list.",
                lumine.getResponse("todo read textbook"));
    }

    @Test
    void getResponse_undoAddCommand_removesAddedTask() {
        Lumine lumine = createLumine();
        lumine.getResponse("todo read textbook");
        lumine.getResponse("list");
        lumine.getResponse("find textbook");
        lumine.getResponse("todo");
        lumine.getResponse("delete 99");

        assertEquals("Done! I had undo the latest command. :D (todo read textbook)", lumine.getResponse("undo"));
        assertEquals("Nice, there are no task on your list!", lumine.getResponse("list"));

        Lumine reloadedLumine = createLumine();
        assertEquals("Nice, there are no task on your list!", reloadedLumine.getResponse("list"));
    }

    @Test
    void getResponse_undoDeleteCommand_restoresTaskAtOriginalPosition() {
        Lumine lumine = createLumine();
        lumine.getResponse("todo first task");
        lumine.getResponse("todo second task");
        lumine.getResponse("delete 1");

        assertEquals("Done! I had undo the latest command. :D (delete 1)", lumine.getResponse("undo"));

        assertEquals("Here are the tasks in your list:\n"
                        + "1.[T][ ] first task\n"
                        + "2.[T][ ] second task",
                lumine.getResponse("list"));
    }

    @Test
    void getResponse_undoMarkAndUnmarkCommands_restoresPreviousStatuses() {
        Lumine lumine = createLumine();
        lumine.getResponse("todo test task");
        lumine.getResponse("mark 1");

        assertEquals("Done! I had undo the latest command. :D (mark 1)", lumine.getResponse("undo"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] test task",
                lumine.getResponse("list"));

        lumine.getResponse("mark 1");
        lumine.getResponse("unmark 1");
        assertEquals("Done! I had undo the latest command. :D (unmark 1)", lumine.getResponse("undo"));
        assertEquals("Here are the tasks in your list:\n1.[T][X] test task",
                lumine.getResponse("list"));
    }

    @Test
    void getResponse_undoDeadlineAndEventCommands_reportsFullEditingCommand() {
        Lumine lumine = createLumine();
        String deadlineCommand = "deadline return book /by 2019 10 15";
        lumine.getResponse(deadlineCommand);

        assertEquals("Done! I had undo the latest command. :D (" + deadlineCommand + ")",
                lumine.getResponse("undo"));
        assertEquals("Nice, there are no task on your list!", lumine.getResponse("list"));

        String eventCommand = "event meeting /from 2pm /to 3pm";
        lumine.getResponse(eventCommand);

        assertEquals("Done! I had undo the latest command. :D (" + eventCommand + ")",
                lumine.getResponse("undo"));
        assertEquals("Nice, there are no task on your list!", lumine.getResponse("list"));
    }

    @Test
    void getResponse_undoCommandWithSurroundingWhitespace_reportsTrimmedCommand() {
        Lumine lumine = createLumine();
        lumine.getResponse("  todo test task  ");

        assertEquals("Done! I had undo the latest command. :D (todo test task)", lumine.getResponse("undo"));
    }

    @Test
    void getResponse_undoWithoutPreviousChange_returnsError() {
        Lumine lumine = createLumine();

        assertEquals("Sorry, there is no command to undo. :C", lumine.getResponse("undo"));

        lumine.getResponse("todo test task");
        lumine.getResponse("undo");
        assertEquals("Sorry, there is no command to undo. :C", lumine.getResponse("undo"));
    }

    @Test
    void getResponse_exitCommand_returnsFarewellAndRequestsExit() {
        Lumine lumine = createLumine();

        assertEquals("Bye. Hope to see you again soon!", lumine.getResponse("bye"));
        assertTrue(lumine.isExitRequested());
    }

    private Lumine createLumine() {
        return new Lumine(temporaryDirectory.resolve("lumine.txt").toString());
    }
}
