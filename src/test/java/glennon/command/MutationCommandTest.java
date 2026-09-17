package glennon.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.Deadline;
import glennon.task.Event;
import glennon.task.Task;
import glennon.task.TaskList;
import glennon.task.Todo;

/**
 * Tests that mission changes commit only after saving succeeds and can be
 * retried without losing the original mission state.
 */
class MutationCommandTest {
    @TempDir
    Path temporaryDirectory;

    private final StringWriter output = new StringWriter();
    private final Ui ui = new Ui(new PrintWriter(output, true));

    @Test
    void execute_addSaveFails_preservesMissionsAndRetryAddsOnce() throws GlennonException {
        Todo existingMission = new Todo("existing");
        existingMission.markAsDone();
        Todo addedMission = new Todo("new mission");
        TaskList missions = new TaskList(List.of(existingMission));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));
        AddCommand command = new AddCommand(addedMission);

        assertFailedSavePreservesMissions(command, missions, storage);
        assertFalse(addedMission.isDone());

        storage.shouldFail = false;
        command.execute(missions, ui, storage);

        assertSameMissionOrder(List.of(existingMission, addedMission), missions);
        assertEquals("Mission added: [T][ ] new mission\n"
                        + "Mission log now has 2 missions.\n",
                output.toString().replace("\r\n", "\n"));
        assertStoredMissions(missions, storage);
    }

    @Test
    void execute_deleteSaveFails_preservesMissionsAndRetryDeletesCorrectMission() throws GlennonException {
        Todo firstMission = new Todo("first");
        Todo deletedMission = new Todo("delete me");
        deletedMission.markAsDone();
        Todo lastMission = new Todo("last");
        TaskList missions = new TaskList(List.of(firstMission, deletedMission, lastMission));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));
        DeleteCommand command = new DeleteCommand(1);

        assertFailedSavePreservesMissions(command, missions, storage);

        storage.shouldFail = false;
        command.execute(missions, ui, storage);

        assertSameMissionOrder(List.of(firstMission, lastMission), missions);
        assertTrue(deletedMission.isDone());
        assertEquals("Mission removed:\n"
                        + "  [T][X] delete me\n"
                        + "Mission log now has 2 missions.\n",
                output.toString().replace("\r\n", "\n"));
        assertStoredMissions(missions, storage);
    }

    @Test
    void execute_markSaveFails_preservesPendingStatusAndRetryCompletesMission() throws GlennonException {
        Todo targetMission = new Todo("finish report");
        Todo otherMission = new Todo("other");
        TaskList missions = new TaskList(List.of(targetMission, otherMission));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));
        MarkCommand command = new MarkCommand(0, true);

        assertFailedSavePreservesMissions(command, missions, storage);
        assertFalse(targetMission.isDone());

        storage.shouldFail = false;
        command.execute(missions, ui, storage);

        assertSameMissionOrder(List.of(targetMission, otherMission), missions);
        assertTrue(targetMission.isDone());
        assertFalse(otherMission.isDone());
        assertEquals("Mission marked complete:\n  [T][X] finish report\n",
                output.toString().replace("\r\n", "\n"));
        assertStoredMissions(missions, storage);
    }

    @Test
    void execute_unmarkSaveFails_preservesCompleteStatusAndRetryUnmarksMission() throws GlennonException {
        Todo otherMission = new Todo("other");
        Todo targetMission = new Todo("revise report");
        targetMission.markAsDone();
        TaskList missions = new TaskList(List.of(otherMission, targetMission));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));
        MarkCommand command = new MarkCommand(1, false);

        assertFailedSavePreservesMissions(command, missions, storage);
        assertTrue(targetMission.isDone());

        storage.shouldFail = false;
        command.execute(missions, ui, storage);

        assertSameMissionOrder(List.of(otherMission, targetMission), missions);
        assertFalse(targetMission.isDone());
        assertFalse(otherMission.isDone());
        assertEquals("Mission marked incomplete:\n  [T][ ] revise report\n",
                output.toString().replace("\r\n", "\n"));
        assertStoredMissions(missions, storage);
    }

    @Test
    void execute_sortSaveFails_preservesOrderAndRetrySortsMissions() throws GlennonException {
        Todo unscheduledMission = new Todo("unscheduled");
        Deadline laterDeadline = new Deadline("later", LocalDateTime.of(2026, 9, 3, 18, 0));
        Event earlierEvent = new Event("earlier",
                LocalDateTime.of(2026, 9, 1, 9, 0), LocalDateTime.of(2026, 9, 1, 10, 0));
        earlierEvent.markAsDone();
        TaskList missions = new TaskList(List.of(unscheduledMission, laterDeadline, earlierEvent));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));
        SortCommand command = new SortCommand();

        assertFailedSavePreservesMissions(command, missions, storage);

        storage.shouldFail = false;
        command.execute(missions, ui, storage);

        assertSameMissionOrder(List.of(earlierEvent, laterDeadline, unscheduledMission), missions);
        assertTrue(earlierEvent.isDone());
        assertEquals("Mission log sorted chronologically:\n"
                        + "1. [E][X] earlier (from: Sep 1 2026, 9:00 AM to: Sep 1 2026, 10:00 AM)\n"
                        + "2. [D][ ] later (by: Sep 3 2026, 6:00 PM)\n"
                        + "3. [T][ ] unscheduled\n",
                output.toString().replace("\r\n", "\n"));
        assertStoredMissions(missions, storage);
    }

    @Test
    void execute_repeatedMarkAndUnmark_preservesIdempotentBehavior() throws GlennonException {
        Todo mission = new Todo("repeat status update");
        TaskList missions = new TaskList(List.of(mission));
        Storage storage = new Storage(temporaryDirectory.resolve("missions.txt"));
        MarkCommand markCommand = new MarkCommand(0, true);
        MarkCommand unmarkCommand = new MarkCommand(0, false);

        markCommand.execute(missions, ui, storage);
        markCommand.execute(missions, ui, storage);

        assertTrue(mission.isDone());
        assertStoredMissions(missions, storage);

        unmarkCommand.execute(missions, ui, storage);
        unmarkCommand.execute(missions, ui, storage);

        assertFalse(mission.isDone());
        assertSameMissionOrder(List.of(mission), missions);
        assertStoredMissions(missions, storage);
        assertEquals(("Mission marked complete:\n  [T][X] repeat status update\n").repeat(2)
                        + ("Mission marked incomplete:\n  [T][ ] repeat status update\n").repeat(2),
                output.toString().replace("\r\n", "\n"));
    }

    @Test
    void execute_idempotentMarkSaveFails_preservesCompleteStatus() throws GlennonException {
        Todo mission = new Todo("already completed");
        mission.markAsDone();
        TaskList missions = new TaskList(List.of(mission));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));

        assertFailedSavePreservesMissions(new MarkCommand(0, true), missions, storage);

        assertTrue(mission.isDone());
    }

    @Test
    void execute_idempotentUnmarkSaveFails_preservesPendingStatus() throws GlennonException {
        Todo mission = new Todo("already pending");
        TaskList missions = new TaskList(List.of(mission));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));

        assertFailedSavePreservesMissions(new MarkCommand(0, false), missions, storage);

        assertFalse(mission.isDone());
    }

    @Test
    void execute_invalidDeleteOrMarkIndex_doesNotSaveOrChangeMissions() {
        Todo mission = new Todo("only mission");
        TaskList missions = new TaskList(List.of(mission));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("missions.txt"));
        List<Command> invalidCommands = List.of(
                new DeleteCommand(-1), new DeleteCommand(1),
                new MarkCommand(-1, true), new MarkCommand(1, false));

        for (Command command : invalidCommands) {
            GlennonException exception = assertThrows(
                    GlennonException.class, () -> command.execute(missions, ui, storage));
            assertEquals("Please enter a valid mission number.", exception.getMessage());
        }

        assertSameMissionOrder(List.of(mission), missions);
        assertFalse(mission.isDone());
        assertEquals(0, storage.saveAttempts);
        assertEquals("", output.toString());
    }

    /**
     * Checks both live task identities and saved values across an unsuccessful
     * command, including the absence of a success response.
     */
    private void assertFailedSavePreservesMissions(
            Command command, TaskList missions, FailingStorage storage) throws GlennonException {
        List<Task> originalMissions = List.copyOf(missions.asList());
        List<String> originalDisplays = originalMissions.stream().map(Task::toString).toList();
        storage.saveMissions(missions.asList());
        storage.shouldFail = true;

        GlennonException exception = assertThrows(
                GlennonException.class, () -> command.execute(missions, ui, storage));

        assertSame(storage.failure, exception);
        assertSameMissionOrder(originalMissions, missions);
        assertEquals(originalDisplays, missions.asList().stream().map(Task::toString).toList());
        assertEquals(originalDisplays, storage.loadMissions().stream().map(Task::toString).toList());
        assertEquals("", output.toString());
    }

    /**
     * Checks that successful persistence contains the same ordered task details
     * and completion states as the live mission log.
     */
    private void assertStoredMissions(TaskList missions, Storage storage) throws GlennonException {
        assertEquals(missions.asList().stream().map(Task::toString).toList(),
                storage.loadMissions().stream().map(Task::toString).toList());
    }

    /**
     * Checks mission count and identity without relying on task equality rules.
     */
    private void assertSameMissionOrder(List<Task> expectedMissions, TaskList actualMissions) {
        assertEquals(expectedMissions.size(), actualMissions.size());
        for (int i = 0; i < expectedMissions.size(); i++) {
            assertSame(expectedMissions.get(i), actualMissions.asList().get(i));
        }
    }

    /**
     * Allows a real stored log to be read while selectively rejecting save
     * attempts, so commands can be retried after the failure is removed.
     */
    private static final class FailingStorage extends Storage {
        private final GlennonException failure = new GlennonException("Simulated save failure.");
        private boolean shouldFail;
        private int saveAttempts;

        private FailingStorage(Path dataPath) {
            super(dataPath);
        }

        @Override
        public void saveMissions(List<Task> missions) throws GlennonException {
            saveAttempts++;
            if (shouldFail) {
                throw failure;
            }
            super.saveMissions(missions);
        }
    }
}
