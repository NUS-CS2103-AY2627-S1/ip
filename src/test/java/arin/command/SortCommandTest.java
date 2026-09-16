package arin.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import arin.exception.ArinException;
import arin.storage.Storage;
import arin.task.Deadline;
import arin.task.Event;
import arin.task.Task;
import arin.task.Todo;
import arin.ui.Ui;

/**
 * Tests sorting, saved task positions, feedback, and invalid sort options.
 */
class SortCommandTest {
    @TempDir
    Path temporaryDirectory;

    private Path taskFile;
    private Storage storage;
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        taskFile = temporaryDirectory.resolve("arin.txt");
        storage = new Storage(taskFile);
        output = new ByteArrayOutputStream();
        ui = new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @Test
    void execute_nameWithMixedTaskTypes_sortsIgnoringCaseAndSavesPositions() throws Exception {
        Task todo = new Todo("Zoo visit");
        Task deadline = new Deadline("apple delivery", LocalDate.of(2026, 9, 12));
        Task event = new Event("Book club", "2pm", "4pm");
        deadline.markTask();
        List<Task> tasks = new ArrayList<>(List.of(todo, event, deadline));

        new SortCommand("name").execute(tasks, ui, storage);

        assertEquals(List.of(deadline, event, todo), tasks);
        assertEquals(List.of(
                "D | X | apple delivery | 2026-09-12",
                "E |   | Book club | 2pm | 4pm",
                "T |   | Zoo visit"
        ), Files.readAllLines(taskFile));
        assertEquals(String.join(System.lineSeparator(),
                "________________________________",
                "Here are the tasks in your list:",
                "1. [D][X] apple delivery (by: Sep 12 2026)",
                "2. [E][ ] Book club (from: 2pm to: 4pm)",
                "3. [T][ ] Zoo visit",
                "________________________________", ""), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_equalNames_preservesRelativeOrderAndDuplicates() throws Exception {
        Task first = new Todo("read book");
        Task second = new Deadline("READ BOOK", LocalDate.of(2026, 9, 12));
        Task earlier = new Todo("buy milk");
        List<Task> tasks = new ArrayList<>(List.of(first, second, earlier, first));

        new SortCommand("name").execute(tasks, ui, storage);

        assertEquals(List.of(earlier, first, second, first), tasks);
    }

    @Test
    void execute_deadlineWithMixedTasks_sortsDatesBeforeUndatedTasks() throws Exception {
        Task todo = new Todo("buy milk");
        Task event = new Event("meeting", "tomorrow", "Friday");
        Task later = new Deadline("January task", LocalDate.of(2026, 1, 1));
        Task earlier = new Deadline("December task", LocalDate.of(2025, 12, 31));
        Task latest = new Deadline("distant task", LocalDate.MAX);
        earlier.markTask();
        List<Task> tasks = new ArrayList<>(List.of(todo, later, event, latest, earlier));

        new SortCommand("deadline").execute(tasks, ui, storage);

        assertEquals(List.of(earlier, later, latest, todo, event), tasks);
        assertEquals(List.of(
                "D | X | December task | 2025-12-31",
                "D |   | January task | 2026-01-01",
                "D |   | distant task | +999999999-12-31",
                "T |   | buy milk",
                "E |   | meeting | tomorrow | Friday"
        ), Files.readAllLines(taskFile));
    }

    @Test
    void execute_equalDeadlines_preservesRelativeOrder() throws Exception {
        Task first = new Deadline("zebra", LocalDate.of(2026, 9, 12));
        Task second = new Deadline("apple", LocalDate.of(2026, 9, 12));
        Task earlier = new Deadline("book", LocalDate.of(2026, 9, 11));
        List<Task> tasks = new ArrayList<>(List.of(first, earlier, second));

        new SortCommand("deadline").execute(tasks, ui, storage);

        assertEquals(List.of(earlier, first, second), tasks);
    }

    @Test
    void execute_noDeadlines_preservesUndatedOrder() throws Exception {
        List<Task> originalTasks = List.of(new Todo("zebra"), new Event("apple", "2pm", "4pm"));
        List<Task> tasks = new ArrayList<>(originalTasks);

        new SortCommand("deadline").execute(tasks, ui, storage);

        assertEquals(originalTasks, tasks);
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "deadline"})
    void execute_emptyList_savesAndDisplaysEmptyList(String sortBy) throws Exception {
        List<Task> tasks = new ArrayList<>();

        new SortCommand(sortBy).execute(tasks, ui, storage);

        assertEquals(List.of(), tasks);
        assertEquals(List.of(), Files.readAllLines(taskFile));
        assertEquals(String.join(System.lineSeparator(),
                "________________________________",
                "Here are the tasks in your list:",
                "________________________________", ""), output.toString(StandardCharsets.UTF_8));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "deadline"})
    void execute_singleTask_preservesTask(String sortBy) throws Exception {
        Task task = new Todo("read book");
        List<Task> tasks = new ArrayList<>(List.of(task));

        new SortCommand(sortBy).execute(tasks, ui, storage);

        assertEquals(List.of(task), tasks);
        assertEquals(List.of("T |   | read book"), Files.readAllLines(taskFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "deadline"})
    void execute_alreadySortedTasks_isIdempotent(String sortBy) throws Exception {
        Task first = new Deadline("apple", LocalDate.of(2026, 9, 11));
        Task second = new Deadline("zebra", LocalDate.of(2026, 9, 12));
        List<Task> tasks = new ArrayList<>(List.of(first, second));
        SortCommand command = new SortCommand(sortBy);

        command.execute(tasks, ui, storage);
        command.execute(tasks, ui, storage);

        assertEquals(List.of(first, second), tasks);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "date", "Name", "name extra", "deadline extra"})
    void constructor_unsupportedOption_throwsUsageError(String sortBy) {
        ArinException exception = assertThrows(ArinException.class, () -> new SortCommand(sortBy));

        assertEquals("Use: sort name OR sort deadline.", exception.getMessage());
    }

    @Test
    void execute_saveFails_preservesOrderAndDoesNotShowSuccess() throws Exception {
        Task first = new Todo("zebra");
        Task second = new Todo("apple");
        List<Task> tasks = new ArrayList<>(List.of(first, second));
        Storage invalidStorage = new Storage(temporaryDirectory);

        assertThrows(IOException.class, () -> new SortCommand("name").execute(tasks, ui, invalidStorage));

        assertEquals(List.of(first, second), tasks);
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void isExit_sortCommand_returnsFalse() throws ArinException {
        assertFalse(new SortCommand("name").isExit());
    }
}
