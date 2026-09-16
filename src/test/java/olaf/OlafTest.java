package olaf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Olaf's interface-independent command facade.
 */
class OlafTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void executeCommand_nullInput_nullPointerExceptionThrown() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        assertThrows(NullPointerException.class, () -> olaf.executeCommand(null));
    }

    @Test
    void commandResult_nullMessageOrStatus_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class,
                () -> new Olaf.CommandResult(null, Olaf.CommandStatus.CONTINUE));
        assertThrows(NullPointerException.class,
                () -> new Olaf.CommandResult("message", null));
    }

    @Test
    void executeCommand_invalidInput_memoryAndFileUnchangedAndSessionContinues() throws Exception {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo original task");
        String originalList = olaf.executeCommand("list").message();
        byte[] originalFile = Files.readAllBytes(dataFile);
        String[] commands = {"", "\u2003", "\u00a0", "unknown", "list extra", "bye extra",
                "todo", "todo\u2003", "find", "deadline /by 2026-09-01", "deadline book /by",
                "event meeting /from /to 2026-09-01", "event meeting /from 2026-09-01 /to",
                "mark", "mark one", "mark 1.5", "delete 2147483648", "mark 0", "unmark -1", "delete 2",
                "deadline book /by 2026-9-01", "deadline book /by 2026-02-29",
                "event meeting /from 2026-09-02 /to 2026-09-01", "todo read\nbook",
                "todo read\rbook", "todo read\r\nbook", "\nbye", "bye\n"};

        for (String command : commands) {
            Olaf.CommandResult result = olaf.executeCommand(command);

            assertEquals(Olaf.CommandStatus.CONTINUE, result.status(), command);
            assertTrue(result.message().startsWith("error: "), command);
            assertEquals(originalList, olaf.executeCommand("list").message(), command);
            assertArrayEquals(originalFile, Files.readAllBytes(dataFile), command);
        }
        assertFalse(olaf.executeCommand("todo next task").message().startsWith("error:"));
        assertEquals(olaf.executeCommand("list").message(), new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_taskNumberOutsideList_currentRangeReturned() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));
        olaf.executeCommand("todo task one");
        olaf.executeCommand("todo task two");

        for (String command : new String[] {"mark 0", "unmark -1", "delete 3", "reschedule 3 /by 2026-09-01"}) {
            assertEquals("error: Task number must be between 1 and 2.", olaf.executeCommand(command).message());
        }
    }

    @Test
    void executeCommand_findThenModify_displayedNumbersTargetMatchingTasks() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));
        olaf.executeCommand("todo buy groceries");
        olaf.executeCommand("todo read book");
        olaf.executeCommand("todo exercise");
        olaf.executeCommand("deadline return book /by 2026-09-01");

        assertEquals(String.join(System.lineSeparator(), " Here are the matching tasks in your list:",
                " 2.[T][ ] read book", " 4.[D][ ] return book (by: Sep 01 2026)"),
                olaf.executeCommand("find BOOK").message());
        assertTrue(olaf.executeCommand("mark 2").message().contains("[T][X] read book"));
        assertTrue(olaf.executeCommand("unmark 2").message().contains("[T][ ] read book"));
        assertTrue(olaf.executeCommand("reschedule 4 /by 2026-09-20").message()
                .contains("return book (by: Sep 20 2026)"));
        assertTrue(olaf.executeCommand("delete 2").message().contains("[T][ ] read book"));
        assertEquals(" Here are the matching tasks in your list:" + System.lineSeparator()
                + " 3.[D][ ] return book (by: Sep 20 2026)", olaf.executeCommand("find book").message());
        assertTrue(olaf.executeCommand("list").message().contains("1.[T][ ] buy groceries"));
        assertTrue(olaf.executeCommand("list").message().contains("2.[T][ ] exercise"));
    }

    @Test
    void executeCommand_saveFailsForEveryMutation_previousStatePreservedAndRetrySucceeds() throws Exception {
        String[] commands = {"todo added task", "delete 1", "mark 1", "unmark 2",
                "reschedule 2 /by 2026-09-20", "reschedule 3 /from 2026-09-20 /to 2026-09-22"};
        List<FailingStorageFiles.Failure> failures = List.of(FailingStorageFiles.Failure.WRITE,
                FailingStorageFiles.Failure.INTERRUPTED_WRITE, FailingStorageFiles.Failure.REPLACE,
                FailingStorageFiles.Failure.UNSUPPORTED_MOVE);
        for (int index = 0; index < commands.length; index++) {
            for (FailingStorageFiles.Failure failure : failures) {
                Path dataFile = temporaryDirectory.resolve("olaf" + index + failure + ".txt");
                FailingStorageFiles files = new FailingStorageFiles();
                Olaf olaf = new Olaf(new Storage(dataFile, files));
                olaf.executeCommand("todo read book");
                olaf.executeCommand("deadline return book /by 2026-09-01");
                olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
                olaf.executeCommand("mark 2");
                olaf.executeCommand("mark 3");
                String originalList = olaf.executeCommand("list").message();
                byte[] originalFile = Files.readAllBytes(dataFile);
                files.failure = failure;

                Olaf.CommandResult result = olaf.executeCommand(commands[index]);

                assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
                assertTrue(result.message().contains("change was not saved"));
                assertEquals(originalList, olaf.executeCommand("list").message());
                assertArrayEquals(originalFile, Files.readAllBytes(dataFile));
                assertEquals(originalList, new Olaf(dataFile).executeCommand("list").message());

                files.failure = FailingStorageFiles.Failure.NONE;
                Olaf.CommandResult retried = olaf.executeCommand(commands[index]);
                assertEquals(Olaf.CommandStatus.CONTINUE, retried.status());
                assertFalse(retried.message().startsWith("error:"), retried.message());
                assertEquals(olaf.executeCommand("list").message(),
                        new Olaf(dataFile).executeCommand("list").message());
            }
        }
    }

    @Test
    void executeCommand_addAndDelete_exactCountMessagesReturned() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        assertEquals(" Got it. I've added this task:\n   [T][ ] read book"
                + "\n Now you have 1 task in the list.", olaf.executeCommand("todo read book").message());
        assertEquals(" Got it. I've added this task:\n   [T][ ] write notes"
                + "\n Now you have 2 tasks in the list.", olaf.executeCommand("todo write notes").message());
        assertEquals(" Noted. I've removed this task:\n   [T][ ] read book"
                + "\n Now you have 1 task in the list.", olaf.executeCommand("delete 1").message());
        assertEquals(" Noted. I've removed this task:\n   [T][ ] write notes"
                + "\n Now you have 0 tasks in the list.", olaf.executeCommand("delete 1").message());
    }

    @Test
    void executeCommand_allTaskTypes_listFormattingPreserved() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));
        olaf.executeCommand("todo read book");
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");

        String[] taskDescriptions = {"[T][ ] read book", "[D][ ] return book (by: Sep 01 2026)",
                "[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)"};
        for (int index = 0; index < taskDescriptions.length; index++) {
            Olaf.CommandResult result = olaf.executeCommand("mark " + (index + 1));
            assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
            assertEquals(" Nice! I've marked this task as done:\n   "
                    + taskDescriptions[index].replace("[ ]", "[X]"), result.message());
        }

        String expectedList = String.join(System.lineSeparator(), " Here are the tasks in your list:",
                " 1.[T][X] read book", " 2.[D][X] return book (by: Sep 01 2026)",
                " 3.[E][X] meeting (from: Sep 01 2026 to: Sep 02 2026)");
        assertEquals(expectedList, olaf.executeCommand("list").message());
    }

    @Test
    void executeCommand_markThenReload_completionPersisted() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");

        Olaf.CommandResult result = olaf.executeCommand("mark 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" Nice! I've marked this task as done:\n   [T][X] read book", result.message());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator() + " 1.[T][X] read book",
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_unmarkThenReload_completionPersisted() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("mark 1");

        Olaf.CommandResult result = olaf.executeCommand("unmark 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've marked this task as not done yet:\n   [T][ ] read book", result.message());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator() + " 1.[T][ ] read book",
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_deleteThenReload_remainingTaskRenumbered() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("todo write notes");

        Olaf.CommandResult result = olaf.executeCommand("delete 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator() + " 1.[T][ ] write notes",
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_addThenReload_taskWasPersisted() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);

        Olaf.CommandResult addResult = olaf.executeCommand("todo read book");
        Olaf.CommandResult listResult = new Olaf(dataFile).executeCommand("list");

        assertEquals(Olaf.CommandStatus.CONTINUE, addResult.status());
        assertTrue(addResult.message().contains("[T][ ] read book"));
        assertEquals(Olaf.CommandStatus.CONTINUE, listResult.status());
        assertTrue(listResult.message().contains("1.[T][ ] read book"));
    }

    @Test
    void executeCommand_unknownCommand_recoverableErrorReturned() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("dance");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertTrue(result.message().startsWith("error: Unknown command."));
    }

    @Test
    void executeCommand_invalidTaskNumber_recoverableErrorReturned() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("mark 1");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals("error: There are no tasks in your list.", result.message());
    }

    @Test
    void executeCommand_bye_exitRequested() throws StorageException {
        Olaf olaf = new Olaf(temporaryDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("bye");

        assertEquals(Olaf.CommandStatus.EXIT_REQUESTED, result.status());
        assertEquals("Bye. Hope to see you again soon!", result.message());
    }

    @Test
    void executeCommand_storageCannotSave_recoverableErrorReturned()
            throws IOException, StorageException {
        Path fileInsteadOfDirectory = temporaryDirectory.resolve("blocked");
        Files.writeString(fileInsteadOfDirectory, "not a directory");
        Olaf olaf = new Olaf(fileInsteadOfDirectory.resolve("olaf.txt"));

        Olaf.CommandResult result = olaf.executeCommand("todo read book");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertTrue(result.message().startsWith("error: Unable to save task data"));
    }

    @Test
    void executeCommand_rescheduleDeadlineThenReload_onlyDateChanged() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
        olaf.executeCommand("mark 2");

        Olaf.CommandResult result = olaf.executeCommand("reschedule 2 /by 2026-09-20");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've rescheduled this task:\n   [D][X] return book (by: Sep 20 2026)",
                result.message());
        String expectedList = String.join(System.lineSeparator(), " Here are the tasks in your list:",
                " 1.[T][ ] read book", " 2.[D][X] return book (by: Sep 20 2026)",
                " 3.[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)");
        assertEquals(expectedList, olaf.executeCommand("list").message());
        assertEquals(expectedList, new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_rescheduleEventThenReload_onlyDatesChanged() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
        olaf.executeCommand("todo write notes");

        Olaf.CommandResult result = olaf.executeCommand("reschedule 2 /from 2026-09-20 /to 2026-09-22");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've rescheduled this task:\n"
                + "   [E][ ] meeting (from: Sep 20 2026 to: Sep 22 2026)", result.message());
        String expectedList = String.join(System.lineSeparator(), " Here are the tasks in your list:",
                " 1.[D][ ] return book (by: Sep 01 2026)",
                " 2.[E][ ] meeting (from: Sep 20 2026 to: Sep 22 2026)", " 3.[T][ ] write notes");
        assertEquals(expectedList, olaf.executeCommand("list").message());
        assertEquals(expectedList, new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_rescheduleInvalid_memoryAndFileUnchanged() throws IOException, StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);
        olaf.executeCommand("todo read book");
        olaf.executeCommand("deadline return book /by 2026-09-01");
        olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
        olaf.executeCommand("mark 3");
        String originalList = olaf.executeCommand("list").message();
        String originalFile = Files.readString(dataFile);
        String[] invalidCommands = {"reschedule", "reschedule 0 /by 2026-09-20",
                "reschedule -1 /by 2026-09-20", "reschedule 4 /by 2026-09-20",
                "reschedule 4 /from 2026-09-20 /to 2026-09-22", "reschedule 1 /by 2026-09-20",
                "reschedule 1 /from 2026-09-20 /to 2026-09-22",
                "reschedule 2 /from 2026-09-20 /to 2026-09-22", "reschedule 3 /by 2026-09-20",
                "reschedule 2 /by 2026-02-29", "reschedule 2 /by 2026-09-20 extra",
                "reschedule 2 /by 2026-09-20 /by 2026-09-21",
                "reschedule 3 /from 2026-09-20 /to 2026-02-29",
                "reschedule 3 /from 2026-09-22 /to 2026-09-20"};

        for (String command : invalidCommands) {
            Olaf.CommandResult result = olaf.executeCommand(command);

            assertEquals(Olaf.CommandStatus.CONTINUE, result.status(), command);
            assertTrue(result.message().startsWith("error: "), command);
            assertEquals(originalList, olaf.executeCommand("list").message(), command);
            assertEquals(originalFile, Files.readString(dataFile), command);
        }
    }

    @Test
    void executeCommand_rescheduleEmptyList_recoverableErrorWithoutCreatingFile() throws StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Olaf olaf = new Olaf(dataFile);

        Olaf.CommandResult result = olaf.executeCommand("reschedule 1 /by 2026-09-20");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals("error: There are no tasks in your list.", result.message());
        assertTrue(Files.notExists(dataFile));
    }

    @Test
    void executeCommand_rescheduleLegacyReversedEvent_validRangeSaved() throws IOException, StorageException {
        Path dataFile = temporaryDirectory.resolve("olaf.txt");
        Files.writeString(dataFile, "E | 0 | meeting | 2026-09-02 | 2026-09-01\n");
        Olaf reloadedOlaf = new Olaf(dataFile);

        Olaf.CommandResult result = reloadedOlaf.executeCommand(
                "reschedule 1 /from 2026-09-20 /to 2026-09-20");

        assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
        assertEquals(" OK, I've rescheduled this task:\n"
                + "   [E][ ] meeting (from: Sep 20 2026 to: Sep 20 2026)", result.message());
        assertEquals(reloadedOlaf.executeCommand("list").message(),
                new Olaf(dataFile).executeCommand("list").message());
    }

    @Test
    void executeCommand_rescheduleCannotSave_recoverableErrorReturned() throws IOException, StorageException {
        String[] commands = {"reschedule 1 /by 2026-09-20",
                "reschedule 2 /from 2026-09-20 /to 2026-09-22"};
        for (int index = 0; index < commands.length; index++) {
            Path dataFile = temporaryDirectory.resolve("olaf" + index + ".txt");
            Olaf olaf = new Olaf(dataFile);
            olaf.executeCommand("deadline return book /by 2026-09-01");
            olaf.executeCommand("event meeting /from 2026-09-01 /to 2026-09-02");
            Files.delete(dataFile);
            Files.createDirectory(dataFile);

            Olaf.CommandResult result = olaf.executeCommand(commands[index]);

            assertEquals(Olaf.CommandStatus.CONTINUE, result.status());
            assertTrue(result.message().startsWith("error: Unable to save task data"));
        }
    }
}
