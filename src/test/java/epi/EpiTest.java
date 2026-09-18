package epi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks current command responses and persistence without accessing the user's task file.
 */
class EpiTest {
    @TempDir
    Path temporaryDirectory;

    private Path taskFile;
    private Epi epi;

    @BeforeEach
    void setUp() {
        taskFile = temporaryDirectory.resolve("task data/epi.txt");
        epi = new Epi(taskFile.toString(), false);
    }

    @Test
    void processCommand_emptyList_returnsExistingMessage() {
        assertEquals(List.of("Purr! There is no task in your list"), epi.processCommand("list"));
    }

    @Test
    void processCommand_addMixedTasks_returnsCompleteResponsesInOrder() {
        assertEquals(new CommandResult(List.of(
                "More work? Fine. I have added this task:",
                "[T][ ] read book",
                "Now you have 1 tasks in the list."), false), epi.processCommandResult("todo read book"));
        assertEquals(new CommandResult(List.of(
                "A deadline? Better not miss it. I have added this task:",
                "[D][ ] return book (by: Dec 02 2019, 6:00 PM)",
                "Now you have 2 tasks in the list."), false),
                epi.processCommandResult("deadline return book /by 2019-12-02 1800"));
        assertEquals(new CommandResult(List.of(
                "An event? I hope there will be treats. I have added this task:",
                "[E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                "Now you have 3 tasks in the list."), false),
                epi.processCommandResult("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600"));
        List<String> expected = List.of(
                "Here is your pile of tasks:",
                "1. [T][ ] read book",
                "2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)",
                "3. [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)");
        assertEquals(new CommandResult(expected, false), epi.processCommandResult("list"));
        assertEquals(expected, new Epi(taskFile.toString(), false).processCommand("list"));
    }

    @Test
    void processCommand_markAndUnmark_persistsEachStatus() {
        epi.processCommand("todo read book");
        assertEquals(List.of(
                "About time you finished something. I've marked it as done:",
                "[T][X] read book"), epi.processCommand("mark 1"));
        assertEquals(List.of("Here is your pile of tasks:", "1. [T][X] read book"),
                new Epi(taskFile.toString(), false).processCommand("list"));

        assertEquals(List.of(
                "Slacking off, are we? I've marked this as not done:",
                "[T][ ] read book"), epi.processCommand("unmark 1"));
        assertEquals(List.of("Here is your pile of tasks:", "1. [T][ ] read book"),
                new Epi(taskFile.toString(), false).processCommand("list"));
    }

    @Test
    void processCommand_markCompletedTask_reportsStatusWithoutSaving() throws IOException {
        epi.processCommand("todo read book");
        epi.processCommand("mark 1");
        Epi reloaded = new Epi(taskFile.toString(), false);
        List<String> original = reloaded.processCommand("list");
        String saved = Files.readString(taskFile);
        Files.setLastModifiedTime(taskFile, FileTime.from(Instant.parse("2000-01-01T00:00:00Z")));
        FileTime lastModified = Files.getLastModifiedTime(taskFile);

        for (String command : List.of("mark 1", "  MaRk   1  ")) {
            CommandResult result = reloaded.processCommandResult(command);
            assertFalse(result.error());
            assertEquals(List.of("Purr! Task 1 is already marked as done.", "[T][X] read book"), result.lines());
            assertEquals(original, reloaded.processCommand("list"));
            assertEquals(saved, Files.readString(taskFile));
            assertEquals(lastModified, Files.getLastModifiedTime(taskFile));
        }
    }

    @Test
    void processCommand_unmarkIncompleteTask_reportsStatusWithoutSaving() throws IOException {
        epi.processCommand("todo read book");
        List<String> original = epi.processCommand("list");
        String saved = Files.readString(taskFile);
        Files.setLastModifiedTime(taskFile, FileTime.from(Instant.parse("2000-01-01T00:00:00Z")));
        FileTime lastModified = Files.getLastModifiedTime(taskFile);

        for (String command : List.of("unmark 1", "  UnMaRk   1  ")) {
            CommandResult result = epi.processCommandResult(command);
            assertFalse(result.error());
            assertEquals(List.of("Meow! Task 1 is already marked as not done.", "[T][ ] read book"), result.lines());
            assertEquals(original, epi.processCommand("list"));
            assertEquals(saved, Files.readString(taskFile));
            assertEquals(lastModified, Files.getLastModifiedTime(taskFile));
        }
    }

    @Test
    void processCommand_repeatedStatusOnDatedTasks_preservesTaskDetails() {
        epi.processCommand("deadline report /by 2019-12-02 1800");
        epi.processCommand("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        for (int taskNumber = 1; taskNumber <= 2; taskNumber++) {
            CommandResult marked = epi.processCommandResult("mark " + taskNumber);
            assertFalse(marked.error());
            assertEquals(new CommandResult(List.of("Purr! Task " + taskNumber + " is already marked as done.",
                    marked.lines().get(1)), false), epi.processCommandResult("mark " + taskNumber));

            CommandResult unmarked = epi.processCommandResult("unmark " + taskNumber);
            assertFalse(unmarked.error());
            assertEquals(new CommandResult(List.of("Meow! Task " + taskNumber + " is already marked as not done.",
                    unmarked.lines().get(1)), false), epi.processCommandResult("unmark " + taskNumber));
        }
    }

    @Test
    void processCommand_redundantStatusWithUnavailableSave_returnsInformation() throws IOException {
        epi.processCommand("todo incomplete");
        epi.processCommand("todo complete");
        epi.processCommand("mark 2");
        List<String> original = epi.processCommand("list");
        Files.delete(taskFile);

        assertEquals(new CommandResult(List.of("Purr! Task 2 is already marked as done.", "[T][X] complete"), false),
                epi.processCommandResult("mark 2"));
        assertEquals(new CommandResult(List.of("Meow! Task 1 is already marked as not done.", "[T][ ] incomplete"),
                false), epi.processCommandResult("unmark 1"));
        assertTrue(epi.processCommandResult("mark 1").error());
        assertTrue(epi.processCommandResult("unmark 2").error());
        assertEquals(original, epi.processCommand("list"));
        assertFalse(Files.exists(taskFile));
    }

    @Test
    void processCommand_deleteMiddleTask_preservesOrderAndPersistsRemoval() {
        epi.processCommand("todo first");
        epi.processCommand("todo second");
        epi.processCommand("todo third");

        assertEquals(List.of("Noted, I'll remove that from the task pile", "Now you have 2 tasks in the list"),
                epi.processCommand("delete 2"));
        List<String> expected = List.of("Here is your pile of tasks:", "1. [T][ ] first", "2. [T][ ] third");
        assertEquals(expected, epi.processCommand("list"));
        assertEquals(expected, new Epi(taskFile.toString(), false).processCommand("list"));
    }

    @Test
    void processCommand_deleteOnlyTask_persistsEmptyList() throws IOException {
        epi.processCommand("todo read book");
        assertEquals(List.of("Noted, I'll remove that from the task pile", "Now you have 0 tasks in the list"),
                epi.processCommand("delete 1"));
        assertEquals("", Files.readString(taskFile));
        assertEquals(List.of("Purr! There is no task in your list"),
                new Epi(taskFile.toString(), false).processCommand("list"));
    }

    @Test
    void processCommand_findPartialMatch_retainsFullListNumbers() {
        epi.processCommand("todo buy groceries");
        epi.processCommand("todo read Book");
        epi.processCommand("todo cook dinner");
        epi.processCommand("todo return book");

        assertEquals(new CommandResult(List.of("Here are the matching tasks in your list:",
                "2. [T][ ] read Book", "4. [T][ ] return book"), false), epi.processCommandResult("find BOO"));
    }

    @Test
    void processCommandResult_findIdenticalTasks_keepsDistinctOriginalNumbers() {
        epi.processCommand("todo notes");
        epi.processCommand("todo read book");
        epi.processCommand("todo read book");

        assertEquals(new CommandResult(List.of("Here are the matching tasks in your list:",
                "2. [T][ ] read book", "3. [T][ ] read book"), false), epi.processCommandResult("find book"));
    }

    @Test
    void processCommand_findNoMatches_returnsInformationWithoutChangingTasks() throws IOException {
        epi.processCommand("todo read book");
        List<String> original = epi.processCommand("list");
        String saved = Files.readString(taskFile);
        Files.setLastModifiedTime(taskFile, FileTime.from(Instant.parse("2000-01-01T00:00:00Z")));
        FileTime lastModified = Files.getLastModifiedTime(taskFile);

        for (String keyword : List.of("movie", "Movie night")) {
            CommandResult result = epi.processCommandResult("  FiNd   " + keyword + "  ");
            assertFalse(result.error());
            assertEquals(List.of("Meow! I couldn't find any tasks matching \"" + keyword + "\"."), result.lines());
            assertEquals(original, epi.processCommand("list"));
            assertEquals(saved, Files.readString(taskFile));
            assertEquals(lastModified, Files.getLastModifiedTime(taskFile));
        }
    }

    @Test
    void processCommand_findInEmptyList_returnsInformationalNoMatch() throws IOException {
        Files.setLastModifiedTime(taskFile, FileTime.from(Instant.parse("2000-01-01T00:00:00Z")));
        FileTime lastModified = Files.getLastModifiedTime(taskFile);

        assertEquals(new CommandResult(List.of("Meow! I couldn't find any tasks matching \"book\"."), false),
                epi.processCommandResult("find book"));
        assertEquals(List.of("Purr! There is no task in your list"), epi.processCommand("list"));
        assertEquals("", Files.readString(taskFile));
        assertEquals(lastModified, Files.getLastModifiedTime(taskFile));
    }

    @Test
    void processCommand_findWithoutKeyword_returnsValidationError() {
        for (String command : List.of("find", "  FiNd   ", "find\t ")) {
            assertEquals(new CommandResult(List.of("Please provide a keyword to search for."), true),
                    epi.processCommandResult(command), command);
        }
    }

    @Test
    void processCommand_invalidCommands_preservesMemoryAndSavedFile() throws IOException {
        epi.processCommand("todo read book");
        String saved = Files.readString(taskFile);
        List<String> expectedTasks = epi.processCommand("list");
        Map<String, String> errors = Map.ofEntries(
                Map.entry("todo", "The description of a todo cannot be empty"),
                Map.entry("deadline", "The description of a deadline cannot be empty!"),
                Map.entry("event", "The description of a event cannot be empty!"),
                Map.entry("find", "Please provide a keyword to search for."),
                Map.entry("mark", "You need to give me a task number, human."),
                Map.entry("mark abc", "That is not a valid number"),
                Map.entry("unmark 0", "That task number doesn't exist in my memory!"),
                Map.entry("delete 2", "That task number doesn't exist in my memory!"),
                Map.entry("unknown", "I do not understand what that means, Human."),
                Map.entry("", "I do not understand what that means, Human."));

        for (Map.Entry<String, String> error : errors.entrySet()) {
            assertEquals(new CommandResult(List.of(error.getValue()), true),
                    epi.processCommandResult(error.getKey()), error.getKey());
            assertEquals(expectedTasks, epi.processCommand("list"), error.getKey());
            assertEquals(saved, Files.readString(taskFile), error.getKey());
        }
    }

    @Test
    void processCommand_invalidDates_rejectsTasksWithoutSaving() throws IOException {
        String expected = "Invalid date format! Please use: yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)";
        assertEquals(new CommandResult(List.of(expected), true),
                epi.processCommandResult("deadline homework /by Sunday"));
        assertEquals(new CommandResult(List.of(expected), true),
                epi.processCommandResult("event meeting /from 2019-12-02 1400 /to who knows"));
        assertEquals(List.of("Purr! There is no task in your list"), epi.processCommand("list"));
        assertEquals("", Files.readString(taskFile));
    }

    @Test
    void processCommand_mixedCaseAndWhitespace_preservesDescriptionCase() {
        assertEquals(List.of("More work? Fine. I have added this task:", "[T][ ] Read Book",
                "Now you have 1 tasks in the list."), epi.processCommand("  ToDo   Read Book  "));
        assertEquals(List.of("Here is your pile of tasks:", "1. [T][ ] Read Book"), epi.processCommand("LIST"));
    }

    @Test
    void processCommand_readOnlyCommands_leavesSavedFileUnchanged() throws IOException {
        epi.processCommand("todo read book");
        String saved = Files.readString(taskFile);
        epi.processCommand("list");
        epi.processCommand("find book");
        assertEquals(List.of("Meow for now. See you later!"), epi.processCommand("bye"));
        assertEquals(saved, Files.readString(taskFile));
    }

    @Test
    void processCommandResult_invalidArgumentsOnEmptyList_reportsUsageBeforeEmptyList() throws IOException {
        Map<String, String> errors = Map.of(
                "  LiSt   extra  ", "Meow! Use: list",
                "  ByE   extra  ", "Meow! Use: bye",
                "sort", "Meow! Use: sort date",
                "sort date desc", "Meow! Use: sort date");

        for (Map.Entry<String, String> error : errors.entrySet()) {
            assertEquals(new CommandResult(List.of(error.getValue()), true),
                    epi.processCommandResult(error.getKey()), error.getKey());
        }
        assertEquals(new CommandResult(List.of("Purr! There is no task in your list"), true),
                epi.processCommandResult("list"));
        assertEquals(new CommandResult(List.of("Purr! There is no task in your list"), true),
                epi.processCommandResult("sort date"));
        assertEquals(new CommandResult(List.of("Meow for now. See you later!"), false),
                epi.processCommandResult("  BYE  "));
        assertEquals("", Files.readString(taskFile));
    }

    @Test
    void processCommand_sortDate_preservesListNumbersAndSavedFile() throws IOException {
        epi.processCommand("todo notes");
        epi.processCommand("deadline report /by 2019-12-02 1800");
        epi.processCommand("event meeting /from 2019-12-01 1400 /to 2019-12-03 1600");
        epi.processCommand("deadline book /by 2019-12-01 1400");
        List<String> originalList = epi.processCommand("list");
        String saved = Files.readString(taskFile);
        Files.setLastModifiedTime(taskFile, FileTime.from(Instant.parse("2000-01-01T00:00:00Z")));
        FileTime lastModified = Files.getLastModifiedTime(taskFile);
        List<String> expected = List.of(
                "Here is your pile of tasks, sorted by date (original task numbers):",
                "3. [E][ ] meeting (from: Dec 01 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)",
                "4. [D][ ] book (by: Dec 01 2019, 2:00 PM)",
                "2. [D][ ] report (by: Dec 02 2019, 6:00 PM)",
                "1. [T][ ] notes");

        assertEquals(expected, epi.processCommand("sort date"));
        assertEquals(originalList, epi.processCommand("list"));
        assertEquals(saved, Files.readString(taskFile));
        assertEquals(lastModified, Files.getLastModifiedTime(taskFile));
        Epi reloaded = new Epi(taskFile.toString(), false);
        assertEquals(originalList, reloaded.processCommand("list"));
        assertEquals(expected, reloaded.processCommand("sort date"));
    }

    @Test
    void processCommand_afterSorting_usesOriginalNumbersForMutations() {
        epi.processCommand("todo notes");
        epi.processCommand("deadline later /by 2019-12-03 1800");
        epi.processCommand("deadline earlier /by 2019-12-01 1800");
        epi.processCommand("sort date");

        assertEquals(List.of("About time you finished something. I've marked it as done:",
                "[D][X] earlier (by: Dec 01 2019, 6:00 PM)"), epi.processCommand("mark 3"));
        assertEquals(List.of("Slacking off, are we? I've marked this as not done:",
                "[D][ ] earlier (by: Dec 01 2019, 6:00 PM)"), epi.processCommand("unmark 3"));
        assertEquals(List.of("Noted, I'll remove that from the task pile", "Now you have 2 tasks in the list"),
                epi.processCommand("delete 3"));
        assertEquals(List.of("Here is your pile of tasks:", "1. [T][ ] notes",
                "2. [D][ ] later (by: Dec 03 2019, 6:00 PM)"), epi.processCommand("list"));
    }

    @Test
    void processCommand_sortEmptyList_returnsExistingEmptyMessage() {
        assertEquals(List.of("Purr! There is no task in your list"), epi.processCommand("sort date"));
    }

    @Test
    void processCommand_invalidSortArguments_reportsUsageWithoutMutation() throws IOException {
        epi.processCommand("todo notes");
        String saved = Files.readString(taskFile);
        List<String> originalList = epi.processCommand("list");

        for (String command : List.of("sort", "sort name", "sort dates", "sort date desc", "sort /order desc")) {
            assertEquals(List.of("Meow! Use: sort date"), epi.processCommand(command), command);
            assertEquals(originalList, epi.processCommand("list"), command);
            assertEquals(saved, Files.readString(taskFile), command);
        }
    }

    @Test
    void processCommand_sortMixedCaseAndWhitespace_acceptsDateKeyword() {
        epi.processCommand("deadline book /by 2019-12-02 1800");

        assertEquals(List.of("Here is your pile of tasks, sorted by date (original task numbers):",
                "1. [D][ ] book (by: Dec 02 2019, 6:00 PM)"), epi.processCommand("  SoRt   DaTe  "));
    }

    @Test
    void processCommand_sortIdenticalTasks_displaysDistinctOriginalNumbers() {
        epi.processCommand("todo notes");
        epi.processCommand("deadline book /by 2019-12-02 1800");
        epi.processCommand("deadline book /by 2019-12-02 1800");

        assertEquals(List.of("Here is your pile of tasks, sorted by date (original task numbers):",
                "2. [D][ ] book (by: Dec 02 2019, 6:00 PM)",
                "3. [D][ ] book (by: Dec 02 2019, 6:00 PM)",
                "1. [T][ ] notes"), epi.processCommand("sort date"));
    }

    @Test
    void processCommand_newValidationErrors_leaveTasksAndFileUnchanged() throws IOException {
        epi.processCommand("todo read book");
        List<String> original = epi.processCommand("list");
        String saved = Files.readString(taskFile);
        for (String command : List.of("list extra", "bye extra", "todo a|b", "todo first\nsecond",
                "deadline book /by 2020-02-30 1200", "deadline book /by 2020-01-01 2400",
                "event meeting /from 2020-01-01 1300 /to 2020-01-01 1200",
                "event meeting /from 2020-01-01 1200 /to 2020-01-01 1200",
                "mark -2147483648", "delete 2147483647", "unmark 99999999999999999999",
                "deadline book /by 2020-01-01 1200 /by 2020-01-02 1200")) {
            CommandResult result = epi.processCommandResult(command);
            assertTrue(result.error(), command);
            assertEquals(1, result.lines().size(), command);
            assertEquals(original, epi.processCommand("list"), command);
            assertEquals(saved, Files.readString(taskFile), command);
        }
    }

    @Test
    void processCommand_saveUnavailable_rollsBackEveryMutation() throws IOException {
        epi.processCommand("todo incomplete");
        epi.processCommand("todo complete");
        epi.processCommand("mark 2");
        List<String> original = epi.processCommand("list");
        String saved = Files.readString(taskFile);
        Files.delete(taskFile);
        Files.createDirectory(taskFile);
        for (String command : List.of("todo new", "deadline book /by 2020-01-01 1200",
                "event meeting /from 2020-01-01 1200 /to 2020-01-01 1300",
                "delete 1", "mark 1", "unmark 2")) {
            CommandResult result = epi.processCommandResult(command);
            assertTrue(result.error(), command);
            assertEquals(List.of("Meow! I couldn't save your tasks. No task changes were kept. "
                    + "Check the file and its permissions, then try again."), result.lines(), command);
            assertEquals(original, epi.processCommand("list"), command);
            assertTrue(Files.isDirectory(taskFile));
        }
        Files.delete(taskFile);
        Files.writeString(taskFile, saved);
        assertFalse(epi.processCommandResult("mark 1").error());
        assertEquals("1. [T][X] incomplete", new Epi(taskFile.toString(), false).processCommand("list").get(1));
    }

    @Test
    void constructor_corruptedFile_showsValidTasksButProtectsAllOriginalBytes() throws IOException {
        String damaged = "T | 1 | valid\nbroken\nT | 0 | last\n";
        Files.writeString(taskFile, damaged);
        Epi recovered = new Epi(taskFile.toString(), false);
        List<String> original = List.of("Here is your pile of tasks:", "1. [T][X] valid", "2. [T][ ] last");
        assertEquals(original, recovered.processCommand("list"));
        assertEquals(2, recovered.getLoadingWarnings().size());
        for (String command : List.of("todo new", "mark 2", "unmark 1", "delete 1")) {
            assertTrue(recovered.processCommandResult(command).error());
            assertEquals(original, recovered.processCommand("list"));
            assertEquals(damaged, Files.readString(taskFile));
        }
        assertFalse(recovered.processCommandResult("find valid").error());
        assertFalse(recovered.processCommandResult("sort date").error());
        assertFalse(recovered.processCommandResult("bye").error());
    }

    @Test
    void processCommand_externalEdit_doesNotOverwriteNewerData() throws IOException {
        epi.processCommand("todo original");
        Files.writeString(taskFile, "T | 0 | external\n");
        assertTrue(epi.processCommandResult("delete 1").error());
        assertEquals("T | 0 | external\n", Files.readString(taskFile));
        assertEquals(List.of("Here is your pile of tasks:", "1. [T][ ] original"), epi.processCommand("list"));
        Epi restarted = new Epi(taskFile.toString(), false);
        assertEquals(List.of(), restarted.getLoadingWarnings());
        assertEquals("1. [T][ ] external", restarted.processCommand("list").get(1));
    }

    @Test
    void processCommandResult_userTextThatLooksLikeError_isStillSuccessful() {
        CommandResult result = epi.processCommandResult("todo Invalid date format! Meow!");
        assertFalse(result.error());
        assertThrows(UnsupportedOperationException.class, () -> result.lines().clear());
        assertTrue(epi.processCommandResult("dance").error());
    }
}
