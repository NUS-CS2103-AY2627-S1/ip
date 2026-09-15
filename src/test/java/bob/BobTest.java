package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.task.Deadline;

/**
 * Tests user commands executed by {@link Bob}.
 */
class BobTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void executeUserCommandWithResult_validCommand_successResultReturned() {
        Bob bob = new Bob(temporaryDirectory.resolve("valid-command-data.txt"));

        CommandResult result = bob.executeUserCommandWithResult("todo read book");

        assertFalse(result.isError());
        assertEquals(
                "Looks like you have added a task. All the best and remember, "
                        + "anything is paw-sible!\n"
                        + "[T][ ] read book\n"
                        + "You now have 1 tasks.",
                result.message());
    }

    @Test
    void executeUserCommandWithResult_invalidCommand_errorResultReturned() {
        Bob bob = new Bob(temporaryDirectory.resolve("invalid-command-data.txt"));

        CommandResult result = bob.executeUserCommandWithResult("dance");

        assertTrue(result.isError());
        assertEquals("Invalid command :( If you need help, type 'help'.", result.message());
    }

    @Test
    void executeUserCommandWithResult_invalidArguments_errorResultReturned() {
        Bob bob = new Bob(temporaryDirectory.resolve("invalid-arguments-data.txt"));

        CommandResult result = bob.executeUserCommandWithResult("deadline return book");

        assertTrue(result.isError());
        assertEquals("Use: deadline <description> /by <yyyy-MM-dd>.", result.message());
    }

    @Test
    void executeUserCommandWithResult_nullCommand_errorResultReturned() {
        Bob bob = new Bob(temporaryDirectory.resolve("null-command-data.txt"));

        CommandResult result = bob.executeUserCommandWithResult(null);

        assertTrue(result.isError());
        assertEquals(
                "Enter a command! If you need a list of commands, please type 'help'. ",
                result.message());
    }

    @Test
    void executeUserCommand_invalidCommand_errorMessageReturned() {
        Bob bob = new Bob(temporaryDirectory.resolve("string-response-data.txt"));

        String response = bob.executeUserCommand("dance");

        assertEquals("Invalid command :( If you need help, type 'help'.", response);
    }

    @Test
    void executeUserCommand_addTodo_taskAndCountShown() {
        Bob bob = new Bob(temporaryDirectory.resolve("todo-data.txt"));

        String response = bob.executeUserCommand("todo read book");

        assertEquals(
                "Looks like you have added a task. All the best and remember, "
                        + "anything is paw-sible!\n"
                        + "[T][ ] read book\n"
                        + "You now have 1 tasks.",
                response);
    }

    @Test
    void executeUserCommand_addDeadline_taskAndCountShown() {
        Bob bob = new Bob(temporaryDirectory.resolve("deadline-data.txt"));

        String response = bob.executeUserCommand("deadline return book /by 2026-09-20");

        assertEquals(
                "Looks like you have added a task. All the best and remember, "
                        + "anything is paw-sible!\n"
                        + "[D][ ] return book (by: Sep 20 2026)\n"
                        + "You now have 1 tasks.",
                response);
    }

    @Test
    void executeUserCommand_addEvent_taskAndCountShown() {
        Bob bob = new Bob(temporaryDirectory.resolve("event-data.txt"));

        String response = bob.executeUserCommand(
                "event project meeting /from 2026-09-20 /to 2026-09-21");

        assertEquals(
                "Looks like you have added a task. All the best and remember, "
                        + "anything is paw-sible!\n"
                        + "[E][ ] project meeting (from: Sep 20 2026 to: Sep 21 2026)\n"
                        + "You now have 1 tasks.",
                response);
    }

    @Test
    void executeUserCommand_listWithTask_personalisedTaskListShown() {
        Bob bob = new Bob(temporaryDirectory.resolve("list-data.txt"));
        bob.executeUserCommand("todo read book");

        String response = bob.executeUserCommand("list");

        assertEquals(
                "Here you go! This is your task list, let's try our best to complete it! "
                        + "Anything is paw-sible!!"
                        + System.lineSeparator()
                        + "1. [T][ ] read book",
                response);
    }

    @Test
    void executeUserCommand_deleteTask_personalisedMessageAndCountShown() {
        Bob bob = new Bob(temporaryDirectory.resolve("delete-data.txt"));
        bob.executeUserCommand("todo read book");
        bob.executeUserCommand("todo write code");

        String response = bob.executeUserCommand("delete 1");

        assertEquals(
                "Looks like you have deleted the task! Remember to complete the rest!\n"
                        + "You now have 1 tasks.",
                response);
    }

    @Test
    void executeUserCommand_validEdit_taskUpdatedAndSaved() throws IOException {
        Path dataFile = temporaryDirectory.resolve("bob.txt");
        Bob bob = new Bob(dataFile);
        bob.executeUserCommand("deadline return book /by 2026-09-10");

        String response = bob.executeUserCommand("edit 1 by 2026-09-20");

        Deadline expectedDeadline = new Deadline("return book", LocalDate.of(2026, 9, 20));
        assertEquals("Nice! I've updated this task:\n" + expectedDeadline, response);
        assertEquals(
                List.of("D | 0 | return book | 2026-09-20"),
                Files.readAllLines(dataFile));
    }
}
