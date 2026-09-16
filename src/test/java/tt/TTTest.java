package tt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tt.storage.TaskStorage;

class TTTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void constructor_nullStorage_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TT(null));
    }

    @Test
    void clearTasks_validTaskTypes_removesOnlyMatchingTasks() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        TT taskManager = new TT(storage);
        taskManager.getResponse("todo read book");
        taskManager.getResponse("todo buy milk");
        taskManager.getResponse("deadline submit report /by 2026-09-10");
        taskManager.getResponse("event meeting /from 2pm /to 3pm");

        assertEquals("Cleared 2 todo tasks.", taskManager.getResponse("clear todo"));
        String remainingTasks = taskManager.getResponse("list");
        assertFalse(remainingTasks.contains("read book"));
        assertFalse(remainingTasks.contains("buy milk"));
        assertTrue(remainingTasks.contains("submit report"));
        assertTrue(remainingTasks.contains("meeting"));

        assertEquals("Cleared 1 deadline task.", taskManager.getResponse("clear deadlines"));
        assertEquals("Cleared 1 event task.", taskManager.getResponse("clear event"));
        assertEquals(0, storage.load().size());
    }

    @Test
    void clearTasks_all_removesEveryTask() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        TT taskManager = new TT(storage);
        taskManager.getResponse("todo read book");
        taskManager.getResponse("event meeting /from 2pm /to 3pm");

        assertEquals("Cleared 2 tasks.", taskManager.getResponse("clear all"));
        assertTrue(taskManager.getResponse("list").contains("There are no tasks to show."));
    }

    @Test
    void findTasks_matchingDescriptions_returnsOnlyCaseInsensitiveMatches() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        TT taskManager = new TT(storage);
        taskManager.getResponse("todo read book");
        taskManager.getResponse("todo buy milk");
        taskManager.getResponse("deadline return book /by 2026-09-10");

        String matchingTasks = taskManager.getResponse("find BOOK");

        assertTrue(matchingTasks.contains("read book"));
        assertTrue(matchingTasks.contains("return book"));
        assertFalse(matchingTasks.contains("buy milk"));
    }

    @Test
    void sortTasks_mixedCaseDescriptions_sortsAndPersistsAlphabetically() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        TT taskManager = new TT(storage);
        taskManager.getResponse("todo write report");
        taskManager.getResponse("todo Buy milk");
        taskManager.getResponse("todo call Alice");

        assertEquals("""
                Tasks sorted alphabetically:
                1. [T][ ] Buy milk
                2. [T][ ] call Alice
                3. [T][ ] write report""", taskManager.getResponse("sort"));

        TT reloadedTaskManager = new TT(storage);
        assertEquals("""
                Here are the tasks in your list:
                1. [T][ ] Buy milk
                2. [T][ ] call Alice
                3. [T][ ] write report""", reloadedTaskManager.getResponse("list"));
    }

    @Test
    void sortTasks_additionalArguments_returnsError() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        TT taskManager = new TT(storage);

        assertEquals("I couldn't process that: the sort command does not take extra details.",
                taskManager.getResponse("sort deadline"));
    }

    @Test
    void getCommandResult_mixedWhitespaceAndCase_normalizesAndProcessesCommand() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));

        CommandResult result = taskManager.getCommandResult("  \tToDo   read   a   book  ");

        assertFalse(result.isError());
        assertTrue(result.message().contains("[T][ ] read a book"));
    }

    @Test
    void getCommandResult_nullOrBlankInput_returnsErrorResult() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));

        CommandResult nullResult = taskManager.getCommandResult(null);
        CommandResult blankResult = taskManager.getCommandResult(" \t ");

        assertTrue(nullResult.isError());
        assertTrue(blankResult.isError());
        assertTrue(nullResult.message().contains("please enter a command"));
    }

    @Test
    void commandsWithoutParameters_additionalArguments_returnErrors() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));

        assertTrue(taskManager.getCommandResult("list now").isError());
        assertTrue(taskManager.getCommandResult("sort descending").isError());
        assertTrue(taskManager.getCommandResult("bye now").isError());
    }

    @Test
    void addDeadline_missingDuplicateOrInvalidDate_returnsErrors() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));

        assertTrue(taskManager.getCommandResult("deadline submit report").isError());
        assertTrue(taskManager.getCommandResult("deadline submit /by report /by 2026-09-15").isError());
        assertTrue(taskManager.getCommandResult("deadline submit report /by 2026-02-30").isError());
        assertTrue(taskManager.getResponse("list").contains("There are no tasks to show."));
    }

    @Test
    void addEvent_missingDuplicateOrReversedSeparators_returnsErrors() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));

        assertTrue(taskManager.getCommandResult("event meeting /from 2pm").isError());
        assertTrue(taskManager.getCommandResult("event meeting /from 2pm /from 3pm /to 4pm").isError());
        assertTrue(taskManager.getCommandResult("event meeting /to 4pm /from 2pm").isError());
        assertTrue(taskManager.getResponse("list").contains("There are no tasks to show."));
    }

    @Test
    void addEvent_separatorTextInsideDescription_addsEvent() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));

        CommandResult result = taskManager.getCommandResult("event go /today /from 2pm /to 3pm");

        assertFalse(result.isError());
        assertTrue(result.message().contains("go /today"));
    }

    @Test
    void addTask_reservedStorageCharacter_returnsErrorWithoutSavingTask() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        TT taskManager = new TT(storage);

        CommandResult result = taskManager.getCommandResult("todo compare A | B");

        assertTrue(result.isError());
        assertEquals(0, storage.load().size());
    }

    @Test
    void taskNumber_missingNonNumericOrOutOfRange_returnsErrors() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));
        taskManager.getResponse("todo read book");

        assertTrue(taskManager.getCommandResult("mark").isError());
        assertTrue(taskManager.getCommandResult("delete first").isError());
        assertTrue(taskManager.getCommandResult("unmark 2").isError());
    }

    @Test
    void successfulCommands_useClearTaskFocusedWording() {
        TT taskManager = new TT(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));

        assertTrue(taskManager.getResponse("todo read book").startsWith("Got it. I've added this task:"));
        assertEquals("Bye. Hope to see you again soon!", taskManager.getResponse("bye"));
    }

    @Test
    void constructor_malformedSaveFile_reportsStartupErrorAndProtectsFile() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "invalid task data");
        TT taskManager = new TT(new TaskStorage(saveFile));

        assertTrue(taskManager.getStartupError().contains("invalid data on line 1"));
        assertTrue(taskManager.getCommandResult("todo read book").isError());
        assertEquals("invalid task data", Files.readString(saveFile));
        assertFalse(taskManager.getCommandResult("bye").isError());
    }

    @Test
    void addTask_storageWriteFailure_returnsErrorInsteadOfCrashing() throws Exception {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "blocking file");
        TT taskManager = new TT(new TaskStorage(parentFile.resolve("tasks.txt")));

        CommandResult result = taskManager.getCommandResult("todo read book");

        assertTrue(result.isError());
        assertTrue(result.message().contains("couldn't save"));
    }
}
