package herta.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;
import herta.storage.PersistenceState;
import herta.storage.Storage;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.TaskList;
import herta.task.Todo;
import herta.ui.Ui;

/**
 * Tests list, filter, find, upcoming, sort, and unknown command behavior.
 */
class QueryCommandTest {
    private static final int BROAD_UPCOMING_WINDOW_DAYS = 100_000_000;
    private static final LocalDateTime FAR_FUTURE_DATE_TIME =
            LocalDateTime.of(9999, 12, 31, 23, 59);

    @TempDir
    Path temporaryDirectory;

    @Test
    void listFilterAndSort_executeDisplayExpectedTaskSelections() throws Exception {
        Todo todo = new Todo("buy milk");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 10, 15, 18, 0));
        Event event = new Event("project meeting", LocalDateTime.of(2019, 10, 14, 23, 0),
                LocalDateTime.of(2019, 10, 16, 1, 0));
        TaskList tasks = new TaskList(List.of(todo, deadline, event));

        assertListCommandDisplaysTasks(tasks);
        assertFilterCommandDisplaysMatchingTasks(tasks);
        assertFindCommandDisplaysMatchingTasks(tasks);
        assertSortCommandDisplaysTasksInDateOrder(tasks, todo);
    }

    @Test
    void filterCommand_noMatchesDisplaysEmptyMessage() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("buy milk")));

        String output = CommandTestSupport.captureOutput(() -> new FilterCommand(
                LocalDate.of(2019, 10, 15)).execute(tasks, new Ui(), null));

        assertTrue(output.contains("No tasks on Oct 15 2019. A remarkably empty date."));
    }

    @Test
    void findCommand_noMatchesDisplaysEmptyMessage() throws Exception {
        String output = CommandTestSupport.captureOutput(() -> new FindCommand("missing")
                .execute(new TaskList(List.of(new Todo("buy milk"))), new Ui(), null));

        assertTrue(output.contains("Nothing matched. Try a more useful keyword."));
        assertFalse(output.contains("Found them. Here are the matches:"));
    }

    @Test
    void upcomingCommand_executeShowsOnlyIncompleteFutureTasks() throws Exception {
        Deadline upcoming = new Deadline("upcoming report", FAR_FUTURE_DATE_TIME);
        Deadline completed = new Deadline("completed report", FAR_FUTURE_DATE_TIME);
        completed.markAsDone();
        TaskList tasks = new TaskList(List.of(new Todo("buy milk"), upcoming, completed));

        String output = CommandTestSupport.captureOutput(() -> new UpcomingCommand(
                BROAD_UPCOMING_WINDOW_DAYS).execute(tasks, new Ui(), null));

        assertTrue(output.contains("The next " + BROAD_UPCOMING_WINDOW_DAYS
                + " days, arranged for you:"));
        assertTrue(output.contains("2. [D][ ] upcoming report"));
        assertFalse(output.contains("buy milk"));
        assertFalse(output.contains("completed report"));
    }

    @Test
    void upcomingCommand_noMatchesDisplaysEmptyMessage() throws Exception {
        String output = CommandTestSupport.captureOutput(() -> new UpcomingCommand(2).execute(
                new TaskList(List.of(new Todo("buy milk"))), new Ui(), null));

        assertTrue(output.contains("Nothing upcoming. Enjoy the silence while it lasts."));
    }

    @Test
    void unknownCommand_executeReportsEmptyAndInvalidInputs() {
        HertaException emptyException = assertThrows(HertaException.class, () ->
                new UnknownCommand("").execute(null, null, null));
        HertaException invalidException = assertThrows(HertaException.class, () ->
                new UnknownCommand("blah").execute(null, null, null));

        assertTrue(emptyException.getMessage().startsWith("Nothing? Use a command."));
        assertTrue(invalidException.getMessage().startsWith(
                "That command isn't in my vocabulary."));
    }

    @Test
    void constructors_queryArguments_rejectCommands() {
        assertThrows(IllegalArgumentException.class, () -> new FindCommand(null));
        assertThrows(IllegalArgumentException.class, () -> new FindCommand("   "));
        assertThrows(IllegalArgumentException.class, () -> new UpcomingCommand(0));
        assertThrows(NullPointerException.class, () -> new ArchiveCommand(null));
        assertThrows(IllegalArgumentException.class, () -> new RestoreCommand(-1));
    }

    @Test
    void queries_emptyLists_reportExpectedMessages() throws Exception {
        TaskList emptyTasks = new TaskList();

        String listOutput = CommandTestSupport.captureOutput(() ->
                new ListCommand().execute(emptyTasks, new Ui(), null));
        String findOutput = CommandTestSupport.captureOutput(() -> new FindCommand("missing")
                .execute(emptyTasks, new Ui(), null));
        String filterOutput = CommandTestSupport.captureOutput(() -> new FilterCommand(
                LocalDate.of(2019, 10, 15)).execute(emptyTasks, new Ui(), null));
        String sortOutput = CommandTestSupport.captureOutput(() -> new SortCommand()
                .execute(emptyTasks, new Ui(), null));
        String upcomingOutput = CommandTestSupport.captureOutput(() -> new UpcomingCommand(2)
                .execute(emptyTasks, new Ui(), null));

        assertEquals("     No tasks to show. Give me something to organize."
                + System.lineSeparator(), listOutput);
        assertEquals(0, emptyTasks.size());
        assertTrue(findOutput.contains("Nothing matched."));
        assertTrue(filterOutput.contains("No tasks on Oct 15 2019."));
        assertTrue(sortOutput.contains("There. Your tasks are in date order."));
        assertTrue(upcomingOutput.contains("Nothing upcoming."));
    }

    @Test
    void listCommand_doesNotModifyTaskListOrStorage() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | persisted task\n");
        byte[] originalStorageContents = Files.readAllBytes(dataFile);
        Todo task = new Todo("buy milk");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = new Storage(dataFile.toString());

        CommandTestSupport.captureOutput(() -> new ListCommand().execute(tasks, new Ui(), storage));

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
        assertArrayEquals(originalStorageContents, Files.readAllBytes(dataFile));
        assertEquals(PersistenceState.NOT_ATTEMPTED, storage.getLastPersistenceState());
    }

    @Test
    void queries_unicodeDuplicatesAndTies_preserveExpectedOrder() throws Exception {
        Todo firstDuplicate = new Todo("Überraschung");
        Todo secondDuplicate = new Todo("Überraschung");
        Deadline firstDeadline = new Deadline("first due", LocalDateTime.of(2019, 10, 15, 18, 0));
        Deadline secondDeadline = new Deadline("second due", LocalDateTime.of(2019, 10, 15, 18, 0));
        TaskList tasks = new TaskList(List.of(firstDuplicate, secondDuplicate,
                firstDeadline, secondDeadline));

        String findOutput = CommandTestSupport.captureOutput(() -> new FindCommand("ÜBER")
                .execute(tasks, new Ui(), null));
        String sortOutput = CommandTestSupport.captureOutput(() -> new SortCommand()
                .execute(tasks, new Ui(), null));

        assertTrue(findOutput.contains("1. [T][ ] Überraschung"));
        assertTrue(findOutput.contains("2. [T][ ] Überraschung"));
        assertTrue(sortOutput.indexOf("3. [D][ ] first due")
                < sortOutput.indexOf("4. [D][ ] second due"));
        assertSame(firstDuplicate, tasks.get(0));
        assertSame(firstDeadline, tasks.get(2));
    }

    private void assertListCommandDisplaysTasks(TaskList tasks) throws Exception {
        String output = CommandTestSupport.captureOutput(() -> new ListCommand()
                .execute(tasks, new Ui(), null));
        assertTrue(output.contains("Let's see what you've managed to pile up:"));
        assertTrue(output.contains("1. [T][ ] buy milk"));
        assertTrue(output.contains("2. [D][ ] submit report"));
        assertTrue(output.contains("3. [E][ ] project meeting"));
    }

    private void assertFilterCommandDisplaysMatchingTasks(TaskList tasks) throws Exception {
        String output = CommandTestSupport.captureOutput(() -> new FilterCommand(
                LocalDate.of(2019, 10, 15)).execute(tasks, new Ui(), null));
        assertTrue(output.contains("Here's what's scheduled for Oct 15 2019. Try not to miss it:"));
        assertTrue(output.contains("2. [D][ ] submit report"));
        assertTrue(output.contains("3. [E][ ] project meeting"));
        assertFalse(output.contains("1. [T][ ] buy milk"));
    }

    private void assertFindCommandDisplaysMatchingTasks(TaskList tasks) throws Exception {
        String output = CommandTestSupport.captureOutput(() -> new FindCommand("REPORT")
                .execute(tasks, new Ui(), null));
        assertTrue(output.contains("Found them. Here are the matches:"));
        assertTrue(output.contains("2. [D][ ] submit report"));
        assertFalse(output.contains("1. [T][ ] buy milk"));
        assertFalse(output.contains("3. [E][ ] project meeting"));
    }

    private void assertSortCommandDisplaysTasksInDateOrder(TaskList tasks, Todo todo)
            throws Exception {
        String output = CommandTestSupport.captureOutput(() -> new SortCommand()
                .execute(tasks, new Ui(), null));
        assertTrue(output.contains("There. Your tasks are in date order."));
        assertTrue(output.indexOf("3. [E][ ] project meeting")
                < output.indexOf("2. [D][ ] submit report"));
        assertTrue(output.indexOf("2. [D][ ] submit report")
                < output.indexOf("1. [T][ ] buy milk"));
        assertSame(todo, tasks.get(0));
    }
}
