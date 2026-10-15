package alexa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests GUI-ready responses provided by {@link Alexa}. */
class AlexaTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_todoCommand_returnsAdditionConfirmation() {
        Alexa alexa = new Alexa(temporaryDirectory.resolve("data").resolve("alexa.txt"));

        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                alexa.getResponse("todo read book"));
    }

    @Test
    void getResponse_invalidCommand_returnsFormattedError() {
        Alexa alexa = new Alexa(temporaryDirectory.resolve("data").resolve("alexa.txt"));

        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-(", alexa.getResponse("dance"));
    }

    @Test
    void getResponse_duplicateTodo_rejectsAdditionAndPreservesSavedTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("alexa.txt");
        Alexa alexa = new Alexa(dataFile);

        alexa.getResponse("todo read book");
        alexa.getResponse("mark 1");

        assertEquals("OOPS!!! This task already exists in task 1.", alexa.getResponse("todo read book"));
        assertEquals("Here are the tasks in your list:\n1.[T][X] read book", alexa.getResponse("list"));
        assertEquals(List.of("T | 1 | read book"), Files.readAllLines(dataFile));
    }

    @Test
    void getResponse_commandWithLeadingTrailingAndTabWhitespace_addsTask() {
        Alexa alexa = new Alexa(temporaryDirectory.resolve("data").resolve("alexa.txt"));

        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                alexa.getResponse("  todo\tread book  "));
    }

    @Test
    void getResponse_listFindAndFarewellCommands_returnExpectedMessages() {
        Alexa alexa = new Alexa(temporaryDirectory.resolve("data").resolve("alexa.txt"));

        alexa.getResponse("todo read book");
        alexa.getResponse("deadline return book /by 2026-10-15");

        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book\n2.[D][ ] return book (by: Oct 15 2026)",
                alexa.getResponse("list"));
        String expectedMatches = "Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n2.[D][ ] return book (by: Oct 15 2026)";
        assertEquals(expectedMatches, alexa.getResponse("find book"));
        assertEquals("Bye. Hope to see you again soon!", alexa.getResponse("bye"));
    }

    @Test
    void getResponse_markUnmarkAndDeleteCommands_updateAndPersistTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("alexa.txt");
        Alexa alexa = new Alexa(dataFile);
        alexa.getResponse("todo read book");

        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                alexa.getResponse("mark 1"));
        assertEquals("Ok, I've marked this task as not done yet:\n  [T][ ] read book",
                alexa.getResponse("unmark 1"));
        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\nNow you have 0 tasks in the list.",
                alexa.getResponse("delete 1"));
        assertEquals(List.of(), Files.readAllLines(dataFile));
    }

    @Test
    void getResponse_invalidTaskCommands_returnErrorsAndLeaveTasksUnchanged() {
        Alexa alexa = new Alexa(temporaryDirectory.resolve("data").resolve("alexa.txt"));
        alexa.getResponse("todo read book");

        assertEquals("OOPS!!! The keyword for a find command cannot be empty.", alexa.getResponse("find"));
        assertEquals("OOPS!!! Please provide a task number, for example: mark 1.",
                alexa.getResponse("mark one"));
        assertEquals("OOPS!!! There is no task 2. Use list to see the task numbers.",
                alexa.getResponse("delete 2"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", alexa.getResponse("list"));
    }

    @Test
    void constructor_invalidSavedData_startsWithEmptyTaskList() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("alexa.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "invalid data");

        Alexa alexa = new Alexa(dataFile);

        assertEquals("Here are the tasks in your list:", alexa.getResponse("list"));
    }
}
