package luna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.task.Todo;
import luna.ui.Ui;

/**
 * Tests for the GUI-facing command API in {@link Luna}.
 */
public class LunaTest {
    @Test
    public void getWelcomeMessage_guiMode_returnsBriefIntroduction() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String welcomeMessage = luna.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Plan your night\nAdd tasks, track deadlines"));
        assertTrue(welcomeMessage.contains("Type help to view all commands."));
        assertFalse(welcomeMessage.contains("Available commands:"));
        assertFalse(welcomeMessage.contains("_______________________________________________________________"));
        assertFalse(welcomeMessage.contains("|_____|"));
    }

    @Test
    public void getResponse_helpCommand_returnsCommandReference() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String response = luna.getResponse("help");

        assertTrue(response.startsWith("Available commands:"));
        assertTrue(response.contains("> todo <desc>:"));
        assertTrue(response.contains("> help: Displays this command guide."));
        assertTrue(luna.isLatestResponseHelp());
        assertFalse(luna.isLatestResponseError());
    }

    @Test
    public void getResponse_todoCommand_returnsConfirmationAndPersistsTask() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("todo read book");

        assertTrue(response.contains("It's on your radar"));
        assertTrue(response.contains("[T][ ] read book"));
        assertFalse(response.contains("___"));
        assertFalse(luna.isLatestResponseError());
        assertEquals(List.of("T | 0 | read book"), storage.getSavedTasks());
    }

    @Test
    public void getResponse_eventCommand_returnsConfirmationAndPersistsTask() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse(
                "event meeting /from 2026-09-15 0900 /to 2026-09-15 1030");

        assertTrue(response.contains("[E][ ] meeting (from: 15 Sep 2026, 9:00 AM "
                + "to: 15 Sep 2026, 10:30 AM)"));
        assertEquals(List.of("E | 0 | meeting | 2026-09-15T09:00 | 2026-09-15T10:30"),
                storage.getSavedTasks());
    }

    @Test
    public void getResponse_markCommand_updatesAndPersistsTask() {
        InMemoryStorage storage = new InMemoryStorage(List.of(new Todo("read book")));
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("mark 1");

        assertTrue(response.contains("[T][X] read book"));
        assertEquals(List.of("T | 1 | read book"), storage.getSavedTasks());
    }

    @Test
    public void getResponse_unmarkCommand_updatesAndPersistsTask() {
        Todo completedTask = new Todo("read book");
        completedTask.markAsDone();
        InMemoryStorage storage = new InMemoryStorage(List.of(completedTask));
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("unmark 1");

        assertTrue(response.contains("[T][ ] read book"));
        assertEquals(List.of("T | 0 | read book"), storage.getSavedTasks());
    }

    @Test
    public void getResponse_deleteCommand_removesAndPersistsRemainingTasks() {
        InMemoryStorage storage = new InMemoryStorage(List.of(
                new Todo("read book"), new Todo("submit quiz")));
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("delete 1");

        assertTrue(response.contains("Cleared from your path:\n[T][ ] read book"));
        assertEquals(List.of("T | 0 | submit quiz"), storage.getSavedTasks());
    }

    @Test
    public void getResponse_listCommand_displaysAllActiveTasks() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage(List.of(
                new Todo("read book"), new Todo("submit quiz"))));

        String response = luna.getResponse("list");

        assertEquals("Here's what's on your radar:\n"
                + "1. [T][ ] read book\n"
                + "2. [T][ ] submit quiz", response);
    }

    @Test
    public void getResponse_listCommandWithNoTasks_returnsHelpfulEmptyState() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String response = luna.getResponse("list");

        assertEquals("Your radar is clear - there are no active tasks yet.\n"
                + "Try todo <description> to add one.", response);
    }

    @Test
    public void getResponse_findWithNoMatch_returnsEmptyResultHeading() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage(List.of(new Todo("read book"))));

        String response = luna.getResponse("find meeting");

        assertEquals("These tasks came into view:", response);
    }

    @Test
    public void getResponse_findWithNonConsecutiveMatches_preservesOriginalTaskNumbers() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage(List.of(
                new Todo("read book"),
                new Todo("submit quiz"),
                new Todo("return book"))));

        String response = luna.getResponse("find book");

        assertEquals("These tasks came into view:\n"
                + "1. [T][ ] read book\n"
                + "3. [T][ ] return book", response);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String response = luna.getResponse("unknown");

        assertTrue(response.contains("I lost that signal. I don't know this command :("));
        assertTrue(luna.isLatestResponseError());
    }

    @Test
    public void getResponse_byeCommand_returnsFarewellBeforeExit() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String response = luna.getResponse("bye");

        assertEquals("The moon is setting. Rest well - I'll keep your tasks safe.", response);
        assertTrue(luna.isExitRequested());
        assertFalse(luna.isLatestResponseError());
    }

    @Test
    public void getResponse_blankInput_returnsEmptyStringAndKeepsRunning() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String response = luna.getResponse("   ");

        assertEquals("", response);
        assertFalse(luna.isExitRequested());
    }

    @Test
    public void getResponse_nullInput_returnsEmptyStringAndKeepsRunning() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String response = luna.getResponse(null);

        assertEquals("", response);
        assertFalse(luna.isExitRequested());
    }

    @Test
    public void getResponse_archiveIndex_archivesSelectedTaskAndSavesRemainingTasks() {
        InMemoryStorage storage = new InMemoryStorage(List.of(
                new Todo("read book"),
                new Todo("submit quiz")));
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("archive 1");

        assertEquals("Tucked away 1 task to data/archive.txt.\n"
                + "You have 1 task still on your radar.", response);
        assertEquals(List.of("T | 0 | read book"), storage.getArchivedTasks());
        assertEquals(List.of("T | 0 | submit quiz"), storage.getSavedTasks());
    }

    @Test
    public void getResponse_archiveAll_archivesEveryTaskAndClearsActiveList() {
        InMemoryStorage storage = new InMemoryStorage(List.of(
                new Todo("read book"),
                new Todo("submit quiz")));
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("  ARCHIVE   ALL  ");

        assertEquals("Tucked away 2 tasks to data/archive.txt.\n"
                + "You have 0 tasks still on your radar.", response);
        assertEquals(List.of("T | 0 | read book", "T | 0 | submit quiz"), storage.getArchivedTasks());
        assertTrue(storage.getSavedTasks().isEmpty());
    }

    @Test
    public void getResponse_archiveAllWithEmptyList_returnsNoOpMessage() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("archive all");

        assertEquals("Your active list is already clear - there is nothing to archive.", response);
        assertTrue(storage.getArchivedTasks().isEmpty());
    }

    @Test
    public void getResponse_listArchived_displaysPersistedArchivedTasks() {
        InMemoryStorage storage = new InMemoryStorage();
        storage.archiveTasks(List.of(new Todo("read book")));
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("list archived");

        assertEquals("These tasks are resting in your archive:\n1. [T][ ] read book", response);
    }

    @Test
    public void getResponse_archiveWithInvalidArgument_returnsUsageErrorWithoutArchiving() {
        InMemoryStorage storage = new InMemoryStorage(List.of(new Todo("read book")));
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("archive book");

        assertEquals("I lost that signal. Please use archive <index> or archive all.", response);
        assertTrue(storage.getArchivedTasks().isEmpty());
    }

    @Test
    public void getResponse_deadlineWithRepeatedWhitespace_addsTask() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("  deadline   submit report   /by   2026-09-30  ");

        assertTrue(response.contains("[D][ ] submit report (by: 30 Sep 2026)"));
        assertEquals(List.of("D | 0 | submit report | 2026-09-30"), storage.getSavedTasks());
    }

    @Test
    public void getResponse_nonExistentDate_returnsErrorWithoutAddingTask() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("deadline submit report /by 2026-02-30");

        assertTrue(response.contains("2026-02-30 is not a real calendar date."
                + " Please check the day and month."));
        assertTrue(luna.isLatestResponseError());
        assertTrue(storage.getSavedTasks().isEmpty());
    }

    @Test
    public void getResponse_deadlineDateHasWrongFormat_returnsFormatSpecificError() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("deadline submit report /by 30-09-2026");

        assertTrue(response.contains("Please enter the deadline date in yyyy-MM-dd format, "
                + "for example 2026-09-30."));
        assertTrue(luna.isLatestResponseError());
        assertTrue(storage.getSavedTasks().isEmpty());
    }

    @Test
    public void getResponse_descriptionContainsControlCharacter_returnsErrorWithoutAddingTask() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("todo read\nbook");

        assertTrue(response.contains("A task description must not contain control characters."));
        assertTrue(storage.getSavedTasks().isEmpty());
    }

    @Test
    public void getResponse_eventDoesNotEndAfterStart_returnsErrorWithoutAddingTask() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse(
                "event meeting /from 2026-09-30 1900 /to 2026-09-30 1900");

        assertTrue(response.contains("The event end time must be later than its start time."));
        assertTrue(storage.getSavedTasks().isEmpty());
    }

    @Test
    public void getResponse_deadlineWithRepeatedParameter_returnsUsageError() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse(
                "deadline submit report /by 2026-09-30 /by 2026-10-01");

        assertTrue(response.contains("Please use deadline <description> /by <yyyy-MM-dd>."));
        assertTrue(storage.getSavedTasks().isEmpty());
    }

    @Test
    public void getResponse_eventWithRepeatedOrOutOfOrderParameter_returnsUsageError() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);

        String response = luna.getResponse("event meeting /to 2026-09-30 1800 "
                + "/from 2026-09-30 1700 /to 2026-09-30 1800");

        assertTrue(response.contains("Please use event <description>"));
        assertTrue(storage.getSavedTasks().isEmpty());
    }

    @Test
    public void getResponse_duplicateTask_returnsErrorAndKeepsSingleTask() {
        InMemoryStorage storage = new InMemoryStorage();
        Luna luna = new Luna(new Ui(false), storage);
        luna.getResponse("todo Read Book");

        String response = luna.getResponse("todo read book");

        assertTrue(response.contains("That task is already on your active list."));
        assertEquals(List.of("T | 0 | Read Book"), storage.getSavedTasks());
    }

    @Test
    public void getResponse_markWithMultipleValues_returnsPreciseFormatError() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage(List.of(new Todo("read book"))));

        String response = luna.getResponse("mark 1 2");

        assertTrue(response.contains("Please provide exactly one positive whole task number to mark."));
    }

    @Test
    public void getResponse_taskNumberIsTooLarge_returnsPreciseError() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage(List.of(new Todo("read book"))));

        String response = luna.getResponse("mark 999999999999999999999999");

        assertTrue(response.contains("That task number is too large."));
    }

    @Test
    public void getResponse_noActiveTasks_returnsActionSpecificError() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage());

        String response = luna.getResponse("unmark 1");

        assertTrue(response.contains("There are no active tasks to unmark."));
    }

    @Test
    public void getResponse_taskNumberOutsideList_returnsRangeError() {
        Luna luna = new Luna(new Ui(false), new InMemoryStorage(List.of(new Todo("read book"))));

        String response = luna.getResponse("delete 2");

        assertTrue(response.contains("Task 2 does not exist. Choose a number from 1 to 1."));
    }

    @Test
    public void getResponse_addSaveFails_doesNotAddTaskInMemory() {
        Luna luna = new Luna(new Ui(false), new FailingSaveStorage(List.of()));

        String errorResponse = luna.getResponse("todo read book");
        String listResponse = luna.getResponse("list");

        assertTrue(errorResponse.contains("I could not save your tasks to disk."));
        assertFalse(listResponse.contains("read book"));
    }

    @Test
    public void getResponse_markSaveFails_restoresOriginalStatus() {
        Luna luna = new Luna(new Ui(false), new FailingSaveStorage(List.of(new Todo("read book"))));

        String errorResponse = luna.getResponse("mark 1");
        String listResponse = luna.getResponse("list");

        assertTrue(errorResponse.contains("I could not save your tasks to disk."));
        assertTrue(listResponse.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_unmarkSaveFails_restoresOriginalStatus() {
        Todo completedTask = new Todo("read book");
        completedTask.markAsDone();
        Luna luna = new Luna(new Ui(false), new FailingSaveStorage(List.of(completedTask)));

        String errorResponse = luna.getResponse("unmark 1");
        String listResponse = luna.getResponse("list");

        assertTrue(errorResponse.contains("I could not save your tasks to disk."));
        assertTrue(listResponse.contains("[T][X] read book"));
    }

    @Test
    public void getResponse_deleteSaveFails_keepsTaskInMemory() {
        Luna luna = new Luna(new Ui(false), new FailingSaveStorage(List.of(new Todo("read book"))));

        String errorResponse = luna.getResponse("delete 1");
        String listResponse = luna.getResponse("list");

        assertTrue(errorResponse.contains("I could not save your tasks to disk."));
        assertTrue(listResponse.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_startupLoadFails_blocksChangesToProtectFile() {
        FailingLoadStorage storage = new FailingLoadStorage();
        Luna luna = new Luna(new Ui(false), storage);
        String loadingError = luna.consumePendingResponse();

        String response = luna.getResponse("todo read book");

        assertTrue(loadingError.contains("changes are disabled to protect the existing file"));
        assertTrue(response.contains("Changes are disabled because your saved task file could not be loaded."));
        assertFalse(storage.wasSaveAttempted());
    }

    @Test
    public void constructor_storageReturnsNull_assertsStorageContract() {
        assertThrows(AssertionError.class, () -> new Luna(new Ui(false), new NullStorage()));
    }

    /**
     * Test double that keeps task data in memory.
     */
    private static class InMemoryStorage extends Storage {
        private final List<Task> loadedTasks;
        private final List<Task> archivedTasks;
        private final List<String> savedTasks;

        /**
         * Creates empty in-memory storage.
         */
        InMemoryStorage() {
            this(List.of());
        }

        /**
         * Creates in-memory storage with predefined tasks.
         *
         * @param loadedTasks Tasks returned when Luna loads saved data.
         */
        InMemoryStorage(List<Task> loadedTasks) {
            this.loadedTasks = new ArrayList<>(loadedTasks);
            this.archivedTasks = new ArrayList<>();
            this.savedTasks = new ArrayList<>();
        }

        @Override
        public List<Task> loadTasks() {
            return new ArrayList<>(loadedTasks);
        }

        @Override
        public void saveTasks(TaskList tasks) {
            savedTasks.clear();
            for (Task task : tasks.asList()) {
                savedTasks.add(task.toStorageString());
            }
        }

        @Override
        public void archiveTasks(List<Task> tasks) {
            archivedTasks.addAll(tasks);
        }

        @Override
        public List<Task> loadArchivedTasks() {
            return new ArrayList<>(archivedTasks);
        }

        /**
         * Returns the serialized tasks most recently saved.
         *
         * @return Saved task lines.
         */
        public List<String> getSavedTasks() {
            return new ArrayList<>(savedTasks);
        }

        /**
         * Returns the serialized tasks currently kept in the test archive.
         *
         * @return Archived task lines.
         */
        public List<String> getArchivedTasks() {
            return archivedTasks.stream()
                    .map(Task::toStorageString)
                    .toList();
        }
    }

    /**
     * Test double that violates the storage loading contract.
     */
    private static class NullStorage extends Storage {
        @Override
        public List<Task> loadTasks() {
            return null;
        }
    }

    /**
     * Storage double that loads normally but rejects every save.
     */
    private static class FailingSaveStorage extends Storage {
        private final List<Task> loadedTasks;

        /**
         * Creates failing storage with predefined tasks.
         *
         * @param loadedTasks Tasks returned when Luna starts.
         */
        FailingSaveStorage(List<Task> loadedTasks) {
            this.loadedTasks = new ArrayList<>(loadedTasks);
        }

        @Override
        public List<Task> loadTasks() {
            return new ArrayList<>(loadedTasks);
        }

        @Override
        public void saveTasks(TaskList tasks) throws IOException {
            throw new IOException("Storage unavailable");
        }
    }

    /**
     * Storage double that cannot load existing task data.
     */
    private static class FailingLoadStorage extends Storage {
        private boolean wasSaveAttempted;

        @Override
        public List<Task> loadTasks() throws IOException {
            throw new IOException("Storage unavailable");
        }

        @Override
        public void saveTasks(TaskList tasks) {
            wasSaveAttempted = true;
        }

        /**
         * Returns whether Luna attempted to overwrite storage after the loading failure.
         *
         * @return {@code true} if a save was attempted.
         */
        public boolean wasSaveAttempted() {
            return wasSaveAttempted;
        }
    }
}
