package bany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bany.commands.CommandOutcome;
import bany.commands.CommandParser;
import bany.commands.CommandResult;
import bany.commands.MessageLevel;
import bany.gui.Responder;
import bany.tasks.Deadline;
import bany.tasks.Event;
import bany.utilities.CommandValidator;

/** Tests command outcomes returned through {@link BanyService}. */
class BanyServiceTest {
    private BanyService banyService;
    private TaskStorage taskStorage;
    @TempDir
    private Path temporaryDirectory;

    /** Creates an isolated service before each test. */
    @BeforeEach
    void setUp() {
        taskStorage = new TaskStorage();
        banyService = new BanyService(
                new TaskFileRepository(temporaryDirectory.resolve("bany.txt")),
                taskStorage,
                new CommandParser(new CommandValidator()),
                new Responder());
    }

    @Test
    void executeCommand_todoWithoutDescription_returnsDescriptionError() {
        CommandResult result = banyService.executeCommand("todo");

        assertEquals("I need a task description before I can add it.", result.messages().get(0).text());
        assertFalse(result.shouldExit());
        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_deadlineWithWrongTag_returnsTagErrorWithoutCreatingTask() {
        CommandResult result = banyService.executeCommand(
                "deadline run /from 21-02-2026 21:03");

        assertEquals("I couldn't schedule that task because its required tags are missing or invalid.",
                result.messages().get(0).text());
        assertFalse(result.shouldExit());
        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_validTaskCommands_createTasks() {
        banyService.executeCommand("todo read book");
        banyService.executeCommand("deadline submit report /by 21-02-2026 21:03");
        banyService.executeCommand("event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        assertEquals(3, taskStorage.getSize());
    }

    @Test
    void executeCommand_deleteOnlyTask_returnsEmptyMessageAndPersistsDeletion() throws IOException {
        banyService.executeCommand("todo read book");

        CommandResult result = banyService.executeCommand("delete 1");

        assertEquals(CommandOutcome.SUCCESS, result.outcome());
        assertEquals("Removed from your task list:" + System.lineSeparator()
                        + "   [T][  ] read book" + System.lineSeparator()
                        + "Your list is now empty.", result.messages().get(0).text());
        assertEquals(0, taskStorage.getSize());
        assertEquals("Your list is empty.", banyService.executeCommand("list").messages().get(0).text());

        TaskStorage reloadedStorage = new TaskStorage();
        new TaskFileRepository(temporaryDirectory.resolve("bany.txt")).load(reloadedStorage);
        assertEquals(0, reloadedStorage.getSize());
    }

    @Test
    void executeCommand_eventCriticalTagsInAnyOrder_createsTask() {
        CommandResult result = banyService.executeCommand(
                "event meeting /to 22-02-2026 21:03 /from 21-02-2026 21:03");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Added to your task list:")));
        assertEquals(1, taskStorage.getSize());
    }

    @Test
    void executeCommand_blankDescriptions_doNotCreateTasks() {
        banyService.executeCommand("todo");
        banyService.executeCommand("deadline /by 21-02-2026 21:03");
        banyService.executeCommand("event /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_missingCriticalTags_doNotCreateTasks() {
        banyService.executeCommand("deadline submit report");
        banyService.executeCommand("event meeting /from 21-02-2026 21:03");
        banyService.executeCommand("event meeting /to 22-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_invalidCriticalTagValues_doNotCreateTasks() {
        banyService.executeCommand("deadline submit report /by 31-02-2026 21:03");
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 20-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_duplicateCriticalTags_doNotCreateTasks() {
        banyService.executeCommand(
                "deadline submit report /by 21-02-2026 21:03 /by 22-02-2026 21:03");
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /from 22-02-2026 21:03"
                        + " /to 23-02-2026 21:03");
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03"
                        + " /to 23-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_extraOrDuplicateNonCriticalTags_warnsAndCreatesTask() {
        CommandResult result = banyService.executeCommand("todo read book /open now /open later");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.level() == MessageLevel.WARNING));
        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Added to your task list:")));
        assertEquals(1, taskStorage.getSize());
    }

    @Test
    void executeCommand_invalidCommand_doesNotCreateTask() {
        banyService.executeCommand("reminder read book");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_find_preservesListNumbersAndActionsTargetTheMatch() {
        banyService.executeCommand("todo groceries");
        banyService.executeCommand("todo report draft");
        banyService.executeCommand("todo laundry");
        banyService.executeCommand("todo report review");

        CommandResult result = banyService.executeCommand("find report");

        assertEquals(CommandOutcome.SUCCESS, result.outcome());
        assertEquals("Tasks matching your search:" + System.lineSeparator()
                + "2. [T][  ] report draft" + System.lineSeparator()
                + "4. [T][  ] report review", result.messages().getFirst().text());
        banyService.executeCommand("mark 2");
        assertFalse(taskStorage.getTask(0).orElseThrow().isDone());
        assertTrue(taskStorage.getTask(1).orElseThrow().isDone());
        banyService.executeCommand("delete 4");
        assertEquals(List.of("groceries", "report draft", "laundry"),
                taskStorage.getTasks().stream().map(task -> task.getDescription()).toList());
    }

    @Test
    void executeCommand_findAfterDeletion_usesCurrentPositionsInsteadOfTaskIds() {
        banyService.executeCommand("todo old task");
        banyService.executeCommand("todo groceries");
        banyService.executeCommand("todo report");
        banyService.executeCommand("delete 1");

        CommandResult result = banyService.executeCommand("find report");

        assertEquals("Tasks matching your search:" + System.lineSeparator()
                + "2. [T][  ] report", result.messages().getFirst().text());
    }

    @Test
    void executeCommand_findWithoutMatches_returnsNoMatchError() {
        banyService.executeCommand("todo groceries");

        CommandResult result = banyService.executeCommand("find report");

        assertEquals(CommandOutcome.ERROR, result.outcome());
        assertEquals("No tasks on your list match that search.", result.messages().getFirst().text());
        assertEquals(1, taskStorage.getSize());
    }

    @Test
    void executeCommand_reservedTags_cannotExitOrChangeTasksOrSavedData() throws IOException {
        banyService.executeCommand("todo groceries");
        banyService.executeCommand("todo report");
        Path taskFile = temporaryDirectory.resolve("bany.txt");
        String originalData = Files.readString(taskFile);

        for (String input : List.of("todo example /command BYE", "delete 1 /description 2",
                "mark 1 /DESCRIPTION 2", "reschedule 1 /description 2 /note urgent",
                "todo example /CoMmAnD DELETE /description 2",
                "todo example /subCOMMAND value", "delete 1 /description_suffix 2",
                "reschedule 1 /preDescriptionPost urgent")) {
            CommandResult result = banyService.executeCommand(input);

            assertEquals(CommandOutcome.ERROR, result.outcome(), input);
            assertFalse(result.shouldExit(), input);
            assertEquals(List.of("groceries", "report"),
                    taskStorage.getTasks().stream().map(task -> task.getDescription()).toList(), input);
            assertTrue(taskStorage.getTasks().stream().noneMatch(task -> task.isDone()), input);
            assertTrue(taskStorage.getTasks().stream().allMatch(task -> task.getTags().isEmpty()), input);
            assertEquals(originalData, Files.readString(taskFile), input);
        }
    }

    @Test
    void executeCommand_rescheduleDeadline_updatesByTag() {
        banyService.executeCommand("deadline submit report /by 21-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /by 22-02-2026 21:03");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Schedule refreshed for this task:")));
        Deadline deadline = (Deadline) taskStorage.getTask(0).orElseThrow();
        assertEquals("22-02-2026 21:03", deadline.getBy());
    }

    @Test
    void executeCommand_rescheduleEventWithMisorderedCriticalTags_updatesWithWarning() {
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /to 24-02-2026 21:03 /from 23-02-2026 21:03");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.level() == MessageLevel.WARNING));
        Event event = (Event) taskStorage.getTask(0).orElse(null);
        assertEquals("23-02-2026 21:03", event.getFrom());
        assertEquals("24-02-2026 21:03", event.getTo());
    }

    @Test
    void executeCommand_rescheduleWithNonCriticalTag_updatesWithWarning() {
        banyService.executeCommand("todo read book");

        CommandResult result = banyService.executeCommand("reschedule 1 /note urgent");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.level() == MessageLevel.WARNING));
        assertEquals("urgent", taskStorage.getTask(0).get().getTag("note")
                .orElseThrow().value().orElseThrow());
    }

    @Test
    void executeCommand_rescheduleDuplicateCriticalTag_rejectsWithoutChangingTask() {
        banyService.executeCommand("deadline submit report /by 21-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /by 22-02-2026 21:03 /BY 23-02-2026 21:03");

        assertEquals("Use the tag /by only once.", result.messages().get(0).text());
        assertEquals("21-02-2026 21:03", ((Deadline) taskStorage.getTask(0).get()).getBy());
    }

    @Test
    void executeCommand_rescheduleInvalidEventRange_rejectsWithoutChangingTask() {
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /from 24-02-2026 21:03 /to 23-02-2026 21:03");

        assertEquals("An event must end after it begins.",
                result.messages().get(0).text());
        Event event = (Event) taskStorage.getTask(0).get();
        assertEquals("21-02-2026 21:03", event.getFrom());
        assertEquals("22-02-2026 21:03", event.getTo());
    }

    @Test
    void executeCommand_rescheduleSaveFailure_rollsBackTagUpdate() {
        TaskStorage failingStorage = new TaskStorage();
        Event event = new Event("Meeting", "21-02-2026 21:03",
                "22-02-2026 21:03", 1);
        failingStorage.addTask(event);
        BanyService failingService = new BanyService(
                new TaskFileRepository(temporaryDirectory),
                failingStorage,
                new CommandParser(new CommandValidator()),
                new Responder());

        CommandResult result = failingService.executeCommand(
                "reschedule 1 /from 20-02-2026 21:03");

        assertEquals("I couldn't save your task list.", result.messages().get(0).text());
        assertEquals("21-02-2026 21:03", event.getFrom());
    }
}
