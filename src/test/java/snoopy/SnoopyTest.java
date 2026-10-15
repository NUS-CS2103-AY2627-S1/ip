package snoopy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import snoopy.exception.SnoopyException;
import snoopy.storage.Storage;
import snoopy.task.TaskList;

/**
 * Tests the response API shared by Snoopy's text and graphical interfaces.
 */
public class SnoopyTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addMarkAndList_updatesSharedTaskState() {
        Snoopy snoopy = createSnoopy("commands.txt");

        assertEquals(" Got it. I've added this task:\n"
                        + "   [T][ ] read book\n"
                        + " Now you have 1 tasks in the list.",
                snoopy.getResponse("todo read book"));
        assertEquals(" Nice! I've marked this task as done:\n"
                        + "   [T][X] read book",
                snoopy.getResponse("mark 1"));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][X] read book",
                snoopy.getResponse("list"));
    }

    @Test
    public void getResponse_addAllTaskTypes_formatsAndPersistsTasks() {
        Path dataFile = temporaryDirectory.resolve("all-types.txt");
        Snoopy snoopy = new Snoopy(new Storage(dataFile));

        snoopy.getResponse("todo read book");
        snoopy.getResponse("deadline return book /by 2026-08-30");
        snoopy.getResponse("event meeting /from 2026-09-01 /to 2026-09-02");

        Snoopy reloadedSnoopy = new Snoopy(new Storage(dataFile));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][ ] read book\n"
                        + " 2.[D][ ] return book (by: Aug 30 2026)\n"
                        + " 3.[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)",
                reloadedSnoopy.getResponse("list"));
    }

    @Test
    public void getResponse_unmarkAndDelete_updateCorrectTasks() {
        Snoopy snoopy = createSnoopy("unmark-delete.txt");
        snoopy.getResponse("todo first");
        snoopy.getResponse("todo second");
        snoopy.getResponse("mark 1");

        assertEquals(" OK, I've marked this task as not done yet:\n"
                        + "   [T][ ] first",
                snoopy.getResponse("unmark 1"));
        assertEquals(" Noted. I've removed this task:\n"
                        + "   [T][ ] first\n"
                        + " Now you have 1 tasks in the list.",
                snoopy.getResponse("delete 1"));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][ ] second",
                snoopy.getResponse("list"));
    }

    @Test
    public void getResponse_findMatchesDescriptionsAndRenumbersResults() {
        Snoopy snoopy = createSnoopy("find.txt");
        snoopy.getResponse("todo read book");
        snoopy.getResponse("deadline return book /by 2026-08-30");
        snoopy.getResponse("todo call Alice");

        assertEquals(" I sniffed out these matching tasks:\n"
                        + " 1.[T][ ] read book\n"
                        + " 2.[D][ ] return book (by: Aug 30 2026)",
                snoopy.getResponse("find book"));
        assertEquals(" I sniffed out these matching tasks:",
                snoopy.getResponse("find Book"));
        assertEquals(" OOPS! I need a scent to follow. Please provide a keyword to find.",
                snoopy.getResponse("find"));
    }

    @Test
    public void getResponse_invalidTaskNumbers_returnSpecificErrors() {
        Snoopy snoopy = createSnoopy("task-numbers.txt");

        assertEquals(" OOPS! Please provide a task number, for example 'mark 2'.",
                snoopy.getResponse("mark"));
        assertEquals(" OOPS! Your task list is empty, so there is no task to delete.",
                snoopy.getResponse("delete 1"));
        snoopy.getResponse("todo only task");
        assertEquals(" OOPS! 'abc' is not a valid task number.",
                snoopy.getResponse("unmark abc"));
        assertEquals(" OOPS! Task 0 does not exist. Choose a number from 1 to 1.",
                snoopy.getResponse("mark 0"));
        assertEquals(" OOPS! Task 2 does not exist. Choose a number from 1 to 1.",
                snoopy.getResponse("delete 2"));
    }

    @Test
    public void getResponse_nullBlankAndUnknownCommands_returnCommandHelp() {
        Snoopy snoopy = createSnoopy("unknown.txt");
        String helpResponse = " OOPS! Sorry, I don't recognize that command. Available commands: "
                + "bye, list, mark, unmark, delete, find, update, todo, deadline, event.";

        assertEquals(helpResponse, snoopy.getResponse(null));
        assertEquals(helpResponse, snoopy.getResponse("   "));
        assertEquals(helpResponse, snoopy.getResponse("unknown"));
    }

    @Test
    public void getResponse_malformedTaskDetails_returnSpecificErrors() {
        Snoopy snoopy = createSnoopy("malformed.txt");

        assertEquals(" OOPS! Please tell me what to add after 'todo'.",
                snoopy.getResponse("todo"));
        assertEquals(" OOPS! Please use: deadline <description> /by <date or time>.",
                snoopy.getResponse("deadline report"));
        assertEquals(" OOPS! A deadline needs both a description and a '/by' value.",
                snoopy.getResponse("deadline /by 2026-08-30"));
        assertEquals(" OOPS! Please use: event <description> /from <start> /to <end>.",
                snoopy.getResponse("event meeting /to 2026-09-02 /from 2026-09-01"));
        assertEquals(" OOPS! An event needs a description, a '/from' value, and a '/to' value.",
                snoopy.getResponse("event /from 2026-09-01 /to 2026-09-02"));
        assertEquals(" OOPS! An event needs a description, a '/from' value, and a '/to' value.",
                snoopy.getResponse("event meeting /from /to 2026-09-02"));
        assertEquals(" OOPS! Please enter event dates as yyyy-MM-dd, for example 2019-10-15.",
                snoopy.getResponse("event meeting /from Monday /to 2026-09-02"));
    }

    @Test
    public void getResponse_storageSaveFails_returnsErrorResponse() {
        Snoopy snoopy = new Snoopy(new FailingStorage(temporaryDirectory.resolve("save-failure.txt"), false));

        assertEquals(" OOPS! I couldn't save the task list.", snoopy.getResponse("todo read book"));
    }

    @Test
    public void getResponse_invalidCommand_preservesExistingTaskState() {
        Snoopy snoopy = createSnoopy("invalid-command.txt");
        snoopy.getResponse("todo anchor");

        assertEquals(" OOPS! Please enter the deadline date as yyyy-MM-dd, "
                        + "for example 2019-10-15.",
                snoopy.getResponse("deadline impossible /by 2026-02-30"));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][ ] anchor",
                snoopy.getResponse("list"));
    }

    @Test
    public void getResponse_eventEndingBeforeStart_rejectsEventAndPreservesState() {
        Snoopy snoopy = createSnoopy("reversed-event.txt");
        snoopy.getResponse("todo anchor");

        assertEquals(" OOPS! The event end date cannot be before its start date.",
                snoopy.getResponse("event holiday /from 2026-09-10 /to 2026-09-09"));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][ ] anchor",
                snoopy.getResponse("list"));
    }

    @Test
    public void getResponse_updateTask_preservesTypeDateAndCompletionState() {
        Snoopy snoopy = createSnoopy("update.txt");
        snoopy.getResponse("deadline return book /by 2026-08-30");
        snoopy.getResponse("mark 1");

        assertEquals(" Got it. I've updated this task:\n"
                        + "   [D][X] return library books (by: Aug 30 2026)",
                snoopy.getResponse("update 1 return library books"));
        Snoopy reloadedSnoopy = createSnoopy("update.txt");
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[D][X] return library books (by: Aug 30 2026)",
                reloadedSnoopy.getResponse("list"));
    }

    @Test
    public void getResponse_updateWithoutDescription_rejectsUpdateAndPreservesState() {
        Snoopy snoopy = createSnoopy("invalid-update.txt");
        snoopy.getResponse("todo original description");

        assertEquals(" OOPS! Please use: update <task number> <new description>.",
                snoopy.getResponse("update 1"));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][ ] original description",
                snoopy.getResponse("list"));
    }

    @Test
    public void getResponse_repeatedDelimitersAndEqualDates_preservesExistingState() {
        Snoopy snoopy = createSnoopy("repeated-delimiters.txt");
        snoopy.getResponse("todo anchor");

        assertEquals(" OOPS! A deadline must contain exactly one '/by' separator.",
                snoopy.getResponse("deadline report /by 2026-09-01 /by 2026-09-02"));
        assertEquals(" OOPS! An event must contain exactly one '/from' and one '/to' separator.",
                snoopy.getResponse(
                        "event trip /from 2026-09-01 /from 2026-09-02 /to 2026-09-03"));
        assertEquals(" OOPS! The event end date must be after its start date.",
                snoopy.getResponse("event one-day /from 2026-09-01 /to 2026-09-01"));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][ ] anchor",
                snoopy.getResponse("list"));
    }

    @Test
    public void getResponse_saveFailure_rollsBackEveryMutation() {
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("save-failure.txt"));
        Snoopy snoopy = new Snoopy(storage);
        snoopy.getResponse("todo first");
        snoopy.getResponse("todo second");
        snoopy.getResponse("mark 1");
        storage.failSaves();

        String saveError = " OOPS! I couldn't save the task list.";
        assertEquals(saveError, snoopy.getResponse("todo third"));
        assertEquals(saveError, snoopy.getResponse("mark 2"));
        assertEquals(saveError, snoopy.getResponse("unmark 1"));
        assertEquals(saveError, snoopy.getResponse("delete 1"));
        assertEquals(" Here are the tasks in your list:\n"
                        + " 1.[T][X] first\n"
                        + " 2.[T][ ] second",
                snoopy.getResponse("list"));
    }

    @Test
    public void getResponse_bye_requestsApplicationExit() {
        Snoopy snoopy = createSnoopy("bye.txt");

        assertFalse(snoopy.shouldExit());
        assertEquals(" Bye. Hope to see you again soon!", snoopy.getResponse("bye"));
        assertTrue(snoopy.shouldExit());
    }

    @Test
    public void getWelcomeMessage_corruptedStorage_includesSafeRecoveryMessage() throws IOException {
        Path dataFile = temporaryDirectory.resolve("corrupted.txt");
        Files.writeString(dataFile, "D | 0 | invalid | 2026-02-30\n");

        Snoopy snoopy = new Snoopy(new Storage(dataFile));

        assertEquals("Hi! I'm Snoopy, your happy little helper.\n"
                        + "What can I do for you?\n\n"
                        + "OOPS! The data file is corrupted at line 1.",
                snoopy.getWelcomeMessage());
    }

    @Test
    public void getGuiWelcomeMessage_normalStartup_usesSnoopyIdentity() {
        Snoopy snoopy = createSnoopy("gui-welcome.txt");

        assertEquals("Hi! I'm Snoopy, your happy little helper.\n"
                        + "What can I do for you?",
                snoopy.getGuiWelcomeMessage());
        assertEquals("Snoopy", snoopy.getDisplayName());
    }

    @Test
    public void getResponse_findCommand_usesSnoopyPersonality() {
        Snoopy snoopy = createSnoopy("personality-find.txt");
        snoopy.getResponse("todo read book");

        assertEquals(" I sniffed out these matching tasks:\n"
                        + " 1.[T][ ] read book",
                snoopy.getResponse("find book"));
        assertEquals(" OOPS! I need a scent to follow. Please provide a keyword to find.",
                snoopy.getResponse("find"));
    }

    @Test
    public void getWelcomeMessage_normalStorage_returnsGreetingWithoutError() {
        Snoopy snoopy = createSnoopy("welcome.txt");

        assertEquals("Hi! I'm Snoopy, your happy little helper.\n"
                        + "What can I do for you?",
                snoopy.getWelcomeMessage());
    }

    @Test
    public void getWelcomeMessage_storageReadFails_includesGenericLoadError() {
        Snoopy snoopy = new Snoopy(new FailingStorage(temporaryDirectory.resolve("load-failure.txt"), true));

        assertEquals("Hi! I'm Snoopy, your happy little helper.\n"
                        + "What can I do for you?\n\n"
                        + "OOPS! I couldn't load the saved tasks.",
                snoopy.getWelcomeMessage());
        assertEquals(" Here are the tasks in your list:", snoopy.getResponse("list"));
    }

    @Test
    public void constructor_nullStorage_failsAssertion() {
        assertThrows(AssertionError.class, () -> new Snoopy(null));
    }

    /**
     * Creates Snoopy with an isolated data file for one test.
     *
     * @param fileName Name of the temporary data file.
     * @return Snoopy backed by isolated storage.
     */
    private Snoopy createSnoopy(String fileName) {
        return new Snoopy(new Storage(temporaryDirectory.resolve(fileName)));
    }

    /**
     * Storage test double that can simulate read or write failures.
     */
    private static class FailingStorage extends Storage {
        private final boolean shouldFailLoad;
        private boolean shouldFailSave;

        FailingStorage(Path filePath) {
            super(filePath);
            shouldFailLoad = false;
            shouldFailSave = false;
        }

        FailingStorage(Path filePath, boolean shouldFailLoad) {
            super(filePath);
            this.shouldFailLoad = shouldFailLoad;
            shouldFailSave = !shouldFailLoad;
        }

        void failSaves() {
            shouldFailSave = true;
        }

        @Override
        public TaskList load() throws IOException, SnoopyException {
            if (shouldFailLoad) {
                throw new IOException("Simulated read failure");
            }
            return super.load();
        }

        @Override
        public void save(TaskList tasks) throws IOException {
            if (shouldFailSave) {
                throw new IOException("Simulated write failure");
            }
            super.save(tasks);
        }
    }
}
