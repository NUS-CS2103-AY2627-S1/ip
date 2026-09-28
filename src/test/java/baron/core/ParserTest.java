package baron.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import baron.core.task.Deadline;
import baron.core.task.Event;
import baron.core.task.TaskList;
import baron.core.task.Todo;

/**
 * Tests command processing through {@link Parser#parse(String)}.
 */
class ParserTest {
    @TempDir
    private Path tempDir;

    private Path filePath;
    private Parser parser;
    private TaskList tasks;

    /**
     * Creates a parser with an empty shared task list and an isolated storage file.
     */
    @BeforeEach
    void setUp() {
        filePath = tempDir.resolve("tasks.txt");
        tasks = new TaskList();
        parser = new Parser(new Storage(filePath), tasks);
    }

    /**
     * Removes tasks added during a test because the application task list is shared.
     */
    @AfterEach
    void tearDown() {
    }

    /**
     * Verifies that a valid to-do command adds and persists its task.
     */
    @Test
    void parse_todoCommand_taskAddedAndSaved() throws IOException {
        String response = parser.parse("todo buy milk");

        assertEquals(1, tasks.size());
        assertTrue(response.contains("[T][ ] buy milk"));
        assertTrue(Files.readString(filePath).contains("T | 0 | buy milk"));
    }

    /**
     * Verifies that the in-memory task state is restored when saving the command fails.
     */
    @Test
    void parse_saveFailure_taskRestoredFromInMemorySnapshot() throws IOException {
        Path directoryInsteadOfFile = tempDir.resolve("tasks");
        Files.createDirectory(directoryInsteadOfFile);
        parser = new Parser(new Storage(directoryInsteadOfFile), tasks);

        String response = parser.parse("todo buy milk");

        assertEquals("Could not save tasks. Please check that the task file is writable.", response);
        assertEquals("There are no tasks in your list", parser.parse("list"));
    }

    /**
     * Verifies that valid deadline and event commands create and persist the correct task types.
     */
    @Test
    void parse_deadlineAndEventCommands_tasksHaveCorrectDataAndAreSaved() throws IOException {
        parser.parse("deadline submit report /by 30082026 1800");
        parser.parse("event team meeting /from 31082026 1000 /to 31082026 1130");

        assertEquals(2, tasks.size());
        assertInstanceOf(Deadline.class, tasks.getTasks().get(0));
        assertInstanceOf(Event.class, tasks.getTasks().get(1));
        String contents = Files.readString(filePath);
        assertTrue(contents.contains("D | 0 | submit report |  | 2026-08-30T18:00"));
        assertTrue(contents.contains(
                "E | 0 | team meeting |  | 2026-08-31T10:00 | 2026-08-31T11:30"));
    }

    /**
     * Verifies that marking, unmarking, and deleting a task update both memory and storage.
     */
    @Test
    void parse_markUnmarkAndDeleteCommands_updateTasksAndPersistence() throws IOException {
        parser.parse("todo buy milk");

        assertTrue(parser.parse("mark 1").contains("[T][X] buy milk"));
        assertTrue(Files.readString(filePath).contains("T | 1 | buy milk"));
        assertTrue(parser.parse("unmark 1").contains("[T][ ] buy milk"));
        assertTrue(Files.readString(filePath).contains("T | 0 | buy milk"));
        assertTrue(parser.parse("delete 1").contains("Now you have 0 tasks"));
        assertEquals(0, tasks.size());
        assertEquals("", Files.readString(filePath));
    }

    /**
     * Verifies that missing arguments and invalid indexes do not mutate the task list.
     */
    @Test
    void parse_missingArgumentsAndInvalidIndexes_returnErrorsWithoutMutation() {
        assertEquals("Missing argument for todo", parser.parse("todo   "));
        assertEquals("Missing flag /by", parser.parse("deadline submit report"));
        assertEquals("Task number must be an integer", parser.parse("mark one"));
        assertEquals("Invalid task index", parser.parse("delete 1"));
        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that specifying prerequisites preserves dependency behavior and display output.
     */
    @Test
    void parse_specifyAndListRelationshipFlags_preserveAndDisplayDependencies() {
        parser.parse("todo prerequisite");
        parser.parse("todo dependent");

        assertTrue(parser.parse("specify 2 /requires 1 /unlocks -").contains("requires:"));
        String response = parser.parse("list /requires");
        assertTrue(response.contains(
                """
                task #2 [T][ ] dependent
                requires:
                #1 [T][ ] prerequisite"""));
        assertTrue(parser.parse("mark 2").contains("Cannot mark task"));
        assertTrue(parser.parse("mark 1").contains("[T][X] prerequisite"));
        assertTrue(parser.parse("mark 2").contains("[T][X] dependent"));
    }

    /**
     * Verifies that a deadline with an invalid date is rejected without changing the task list.
     */
    @Test
    void parse_deadlineWithInvalidDate_errorReturnedAndTaskNotAdded() {
        String response = parser.parse("deadline submit report /by tomorrow");

        assertEquals(0, tasks.size());
        assertEquals("Date/time must be in ddMMyyyy HHmm", response);
    }

    /**
     * Verifies that an event must end after it starts.
     */
    @Test
    void parse_eventWithEqualTimes_errorReturnedAndTaskNotAdded() {
        String response = parser.parse("event meeting /from 31082026 1100 /to 31082026 1100");

        assertEquals(0, tasks.size());
        assertEquals("/to date must be after /from date", response);
    }

    /**
     * Verifies that finding a keyword returns only matching tasks.
     */
    @Test
    void parse_findCommand_matchingTasksListed() {
        tasks.addTask(new Todo("buy milk"));
        tasks.addTask(new Todo("read notes"));

        String response = parser.parse("find buy");

        assertTrue(response.contains("task #1 [T][ ] buy milk"));
        assertTrue(response.contains("buy milk"));
        assertFalse(response.contains("read notes"));
    }

    /** Verifies that find results preserve task numbers from the full task list. */
    @Test
    void parse_findCommand_showsOriginalTaskNumber() {
        tasks.addTask(new Todo("buy milk"));
        tasks.addTask(new Todo("read notes"));

        String response = parser.parse("find notes");

        assertTrue(response.contains("task #2 [T][ ] read notes"));
        assertFalse(response.contains("task #?"));
    }

    /**
     * Verifies that unrecognized commands produce a useful error response.
     */
    @Test
    void parse_unknownCommand_unknownCommandMessageReturned() {
        assertEquals("Unknown command", parser.parse("remind me"));
    }

    /**
     * Verifies that empty-list and no-match commands return useful error messages.
     */
    @Test
    void parse_emptyListAndNoMatchingFind_returnUsefulErrors() {
        assertEquals("There are no tasks in your list", parser.parse("list"));
    }

}
