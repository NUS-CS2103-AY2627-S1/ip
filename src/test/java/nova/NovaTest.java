package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Drives the whole command surface through a chatbot whose save files live in a
 * temporary folder, so every command path and every error message is checked
 * without touching the user's own data.
 */
public class NovaTest {
    @TempDir
    Path tempDir;

    private Nova newNova() {
        return new Nova(new Storage(saveFile().toString(), archiveFile().toString()));
    }

    private Path saveFile() {
        return tempDir.resolve("data/nova.txt");
    }

    private Path archiveFile() {
        return tempDir.resolve("data/nova-archive.txt");
    }

    @Test
    public void executeCommand_blankInput_asksForACommand() {
        CommandResult result = newNova().executeCommand("   ");
        assertTrue(result.isError());
        assertEquals(Ui.MESSAGE_EMPTY_INPUT, result.text());
    }

    @Test
    public void executeCommand_unknownCommand_reportsError() {
        assertTrue(newNova().executeCommand("frobnicate").isError());
    }

    @Test
    public void executeCommand_mixedCaseCommand_isAccepted() {
        assertFalse(newNova().executeCommand("LiSt").isError());
    }

    @Test
    public void todo_addsTaskAndCountsIt() {
        CommandResult result = newNova().executeCommand("todo read book");
        assertFalse(result.isError());
        assertTrue(result.text().contains("[T][ ] read book"));
        assertTrue(result.text().contains("Now you have 1 task in the list."));
    }

    @Test
    public void todo_emptyDescription_reportsError() {
        assertTrue(newNova().executeCommand("todo").isError());
    }

    @Test
    public void list_noTasks_explainsHowToAddOne() {
        CommandResult result = newNova().executeCommand("list");
        assertFalse(result.isError());
        assertEquals(Ui.MESSAGE_LIST_EMPTY, result.text());
    }

    @Test
    public void list_withTasks_numbersThemFromOne() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        nova.executeCommand("todo write essay");
        CommandResult result = nova.executeCommand("list");
        assertTrue(result.text().startsWith("1. [T][ ] read book"));
        assertTrue(result.text().contains("2. [T][ ] write essay"));
    }

    @Test
    public void list_extraArguments_reportsError() {
        assertTrue(newNova().executeCommand("list 5").isError());
    }

    @Test
    public void help_listsTheCommands() {
        CommandResult result = newNova().executeCommand("help");
        assertFalse(result.isError());
        assertTrue(result.text().contains("todo <description>"));
        assertTrue(result.text().contains("bye"));
    }

    @Test
    public void help_extraArguments_reportsError() {
        assertTrue(newNova().executeCommand("help me").isError());
    }

    @Test
    public void taskCounts_trackAddsAndMarks() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        nova.executeCommand("todo write essay");
        nova.executeCommand("mark 1");
        assertEquals(2, nova.getTaskCount());
        assertEquals(1, nova.getDoneCount());
    }

    @Test
    public void deadline_validDate_addsTask() {
        CommandResult result = newNova().executeCommand("deadline return book /by 2026-09-20");
        assertFalse(result.isError());
        assertTrue(result.text().contains("return book"));
    }

    @Test
    public void deadline_invalidDate_reportsError() {
        assertTrue(newNova().executeCommand("deadline return book /by 2026-13-45").isError());
    }

    @Test
    public void deadline_repeatedByParameter_reportsError() {
        assertTrue(newNova().executeCommand("deadline return book /by 2026-09-20 /by 2026-09-21").isError());
    }

    @Test
    public void event_endBeforeStart_reportsError() {
        CommandResult result = newNova().executeCommand("event trip /from 2026-09-20 /to 2026-09-18");
        assertTrue(result.isError());
        assertEquals(Ui.MESSAGE_INVALID_EVENT_RANGE, result.text());
    }

    @Test
    public void event_validDates_addsTask() {
        assertFalse(newNova().executeCommand("event trip /from 2026-09-18 /to 2026-09-20").isError());
    }

    @Test
    public void mark_marksTaskDone() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        CommandResult result = nova.executeCommand("mark 1");
        assertFalse(result.isError());
        assertTrue(result.text().contains("[T][X] read book"));
    }

    @Test
    public void mark_outOfRangeNumber_reportsError() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        CommandResult result = nova.executeCommand("mark 9");
        assertTrue(result.isError());
        assertTrue(result.text().contains("no task at number 9"));
    }

    @Test
    public void mark_twoNumbers_reportsError() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        assertEquals(Ui.MESSAGE_ONE_TASK_NUMBER, nova.executeCommand("mark 1 2").text());
    }

    @Test
    public void unmark_afterMark_makesTheTaskPendingAgain() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        nova.executeCommand("mark 1");
        assertTrue(nova.executeCommand("unmark 1").text().contains("[T][ ] read book"));
    }

    @Test
    public void delete_removesTheTaskAndReportsAnEmptyList() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        CommandResult result = nova.executeCommand("delete 1");
        assertFalse(result.isError());
        assertTrue(result.text().contains("Your list is empty."));
    }

    @Test
    public void find_matchesPartOfADescriptionIgnoringCase() {
        Nova nova = newNova();
        nova.executeCommand("todo Read Book");
        CommandResult result = nova.executeCommand("find book");
        assertTrue(result.text().contains("Read Book"));
    }

    @Test
    public void find_withoutKeyword_reportsError() {
        assertTrue(newNova().executeCommand("find").isError());
    }

    @Test
    public void archive_movesCompletedTasksToTheArchiveFile() throws Exception {
        Nova nova = newNova();
        nova.executeCommand("todo done task");
        nova.executeCommand("todo open task");
        nova.executeCommand("mark 1");
        CommandResult result = nova.executeCommand("archive");
        assertFalse(result.isError());
        assertTrue(result.text().contains("done task"));
        assertTrue(Files.readString(archiveFile()).contains("done task"));
        String list = nova.executeCommand("list").text();
        assertTrue(list.contains("open task"));
        assertFalse(list.contains("done task"));
    }

    @Test
    public void archive_whenNothingIsCompleted_saysSo() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        assertEquals(Ui.MESSAGE_ARCHIVE_EMPTY, nova.executeCommand("archive").text());
    }

    @Test
    public void bye_reportsFarewellAndIsRecognisedAsTheExitCommand() {
        CommandResult result = newNova().executeCommand("bye");
        assertFalse(result.isError());
        assertTrue(result.text().contains("Signing off"));
        assertTrue(newNova().isExitCommand("BYE"));
    }

    @Test
    public void todo_isPersistedAndReloadedByTheNextRun() {
        Nova nova = newNova();
        nova.executeCommand("todo read book");
        nova.executeCommand("mark 1");
        Nova reloaded = newNova();
        assertTrue(reloaded.executeCommand("list").text().contains("[T][X] read book"));
    }

    @Test
    public void startup_reportsUnreadableSaveFileLines() throws Exception {
        Files.createDirectories(saveFile().getParent());
        Files.writeString(saveFile(), "T | NOT_DONE | keep me\nGARBAGE LINE\n");
        Nova nova = newNova();
        assertFalse(nova.getStartupWarning().isEmpty());
        assertTrue(nova.executeCommand("list").text().contains("keep me"));
    }
}
