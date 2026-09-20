package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests malformed commands, duplicate identity, and transactional task changes. */
class ErrorHandlingTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void processCommand_invalidInputsBetweenValidChanges_preservesMemoryAndFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        assertFalse(Bkxss.processCommandResult("todo read book", tasks, storage).isError());
        List<String> invalidCommands = List.of(
                "", "   ", "todo   ", "todo bad | record", "todo bad\nrecord", "todo bad\u0000record",
                "list extra", "bye extra", "mark", "mark 1 2", "mark +1", "mark 99999999999999999999",
                "mark 0", "mark -1", "unmark 2", "delete", "delete 1.0", "delete 2", "find   ",
                "deadline x", "deadline /by 2026-09-12 0900", "deadline x /by",
                "deadline x /by 2026-09-12 0900 /by 2026-09-13 0900",
                "deadline x /from 2026-09-12 0900", "deadline x /by 2026-02-30 0900",
                "deadline x /by 2026-09-12 2400", "deadline bad | record /by 2026-09-12 0900",
                "event x /to 2026-09-12 0900 /from 2026-09-12 1000",
                "event x /from 2026-09-12 0900 /from 2026-09-12 1000",
                "event x /from 2026-09-12 0900 /to 2026-09-12 1000 /to 2026-09-12 1100",
                "event x /from /to 2026-09-12 1000", "event x /from 2026-09-12 0900 /to",
                "event /from 2026-09-12 0900 /to 2026-09-12 1000",
                "event x /from 2026-02-30 0900 /to 2026-03-01 1000",
                "event x /from 2026-09-12 1000 /to 2026-09-12 1000",
                "event x /from 2026-09-12 1100 /to 2026-09-12 1000",
                "event x /from Mon 2pm /to 2026-09-12 1000",
                "event x /from Mon 2pm /to 4pm",
                "findfree 1 /to 2026-09-12 0900 /from 2026-09-12 1000",
                "findfree 1 /from 2026-09-12 0900 /to 2026-09-12 1000 /by 2026-09-12 1000",
                "findfree 999999999999999 /from 2026-09-12 0900 /to 2026-09-12 1000",
                "findfree +1 /from 2026-09-12 0900 /to 2026-09-12 1000",
                "findfree 1.5 /from 2026-09-12 0900 /to 2026-09-12 1000",
                "findfree 1 /from 2026-09-12 0900 /to 2026-09-12 0900");

        for (String command : invalidCommands) {
            String savedText = Files.readString(dataFile);
            CommandResult result = Bkxss.processCommandResult(command, tasks, storage);
            assertTrue(result.isError(), command);
            assertFalse(result.message().contains("I've added"), command);
            assertEquals(1, tasks.size(), command);
            assertFalse(tasks.getFirst().isDone(), command);
            assertEquals(savedText, Files.readString(dataFile), command);
            assertFalse(Bkxss.processCommandResult("mark 1", tasks, storage).isError(), command);
            assertFalse(Bkxss.processCommandResult("unmark 1", tasks, storage).isError(), command);
        }
        assertTrue(Bkxss.processCommandResult(null, tasks, storage).isError());
    }

    @Test
    void processCommand_extraWhitespaceAndUnicode_normalizesAndPersists() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();

        assertFalse(Bkxss.processCommandResult(" \ttodo\t  买书 & read!  ", tasks, storage).isError());
        assertEquals("[T][ ] 买书 & read!", tasks.getFirst().toString());
        assertFalse(Bkxss.processCommandResult(
                " deadline\treturn   book /by\t2028-02-29   1800 ", tasks, storage).isError());
        assertFalse(Bkxss.processCommandResult(
                "event meeting /from 2028-02-29 2300 /to 2028-03-01 0100", tasks, storage).isError());
        assertFalse(Bkxss.processCommandResult(" list\t ", tasks, storage).isError());
        assertEquals(3, storage.load().size());
    }

    @Test
    void processCommand_duplicateTasks_ignoresCompletionButIncludesTypeAndDates() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        List<String> commands = List.of("todo read book", "deadline read book /by 2026-09-12 0900",
                "event read book /from 2026-09-12 0900 /to 2026-09-12 1000");

        for (String command : commands) {
            assertFalse(Bkxss.processCommandResult(command, tasks, storage).isError());
            assertFalse(Bkxss.processCommandResult("mark " + tasks.size(), tasks, storage).isError());
            CommandResult duplicate = Bkxss.processCommandResult(command, tasks, storage);
            assertTrue(duplicate.isError());
            assertTrue(duplicate.message().contains("already exists"));
        }
        assertTrue(Bkxss.processCommandResult(" todo  read   book ", tasks, storage).isError());
        assertEquals(3, tasks.size());
        assertFalse(Bkxss.processCommandResult("deadline read book /by 2026-09-13 0900", tasks, storage).isError());
        assertFalse(Bkxss.processCommandResult(
                "event read book /from 2026-09-12 1000 /to 2026-09-12 1100", tasks, storage).isError());
        assertEquals(5, storage.load().size());
    }

    @Test
    void processCommand_saveFailure_rollsBackEveryMutationAndAllowsRetry() throws Exception {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "keep this file");
        Storage storage = new Storage(blockedParent.resolve("tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        secondTask.markAsDone();
        tasks.add(firstTask);
        tasks.add(secondTask);

        for (String command : List.of("todo new", "mark 1", "unmark 2", "delete 1")) {
            CommandResult result = Bkxss.processCommandResult(command, tasks, storage);
            assertTrue(result.isError(), command);
            assertTrue(result.message().contains("No changes were applied"), command);
            assertFalse(result.message().contains("Nice!"), command);
            assertFalse(result.message().contains("Got it"), command);
            assertFalse(result.message().contains("Noted"), command);
            assertEquals(2, tasks.size());
            assertSame(firstTask, tasks.getFirst());
            assertSame(secondTask, tasks.getLast());
            assertFalse(firstTask.isDone());
            assertTrue(secondTask.isDone());
            assertEquals("keep this file", Files.readString(blockedParent));
        }
        Files.delete(blockedParent);
        assertFalse(Bkxss.processCommandResult("mark 1", tasks, storage).isError());
        assertTrue(storage.load().getFirst().isDone());
    }

    @Test
    void processCommand_legacyEvent_canBeDisplayedAndDeletedButCannotBeScheduled() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "E | 0 | meeting | Mon 2pm to 4pm\n");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = storage.load();

        assertFalse(Bkxss.processCommandResult("list", tasks, storage).isError());
        assertTrue(Bkxss.processCommandResult(
                "findfree 1 /from 2026-09-12 0900 /to 2026-09-12 1700", tasks, storage).isError());
        assertFalse(Bkxss.processCommandResult("delete 1", tasks, storage).isError());
        assertTrue(storage.load().isEmpty());
    }
}
