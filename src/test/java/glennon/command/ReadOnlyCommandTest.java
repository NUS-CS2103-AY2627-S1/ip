package glennon.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import glennon.Storage;
import glennon.Ui;
import glennon.exception.GlennonException;
import glennon.task.Deadline;
import glennon.task.Event;
import glennon.task.Task;
import glennon.task.TaskList;
import glennon.task.Todo;

/**
 * Checks read-only commands' exact responses and guarantees that they never save or alter missions.
 */
class ReadOnlyCommandTest {
    private final Todo todo = new Todo("read book");
    private final Deadline deadline = new Deadline("read report", LocalDateTime.of(2026, 9, 17, 18, 0));
    private final Event event = new Event("meet team",
            LocalDateTime.of(2026, 9, 17, 9, 0), LocalDateTime.of(2026, 9, 18, 10, 0));
    private final TaskList missions = new TaskList(List.of(todo, deadline, event));

    @Test
    void execute_listMixedMissions_preservesOrderAndCompletion() throws GlennonException {
        deadline.markAsDone();
        assertReadOnlyResponse(new ListCommand(), "Mission log:\n"
                + "1. [T][ ] read book\n"
                + "2. [D][X] read report (by: Sep 17 2026, 6:00 PM)\n"
                + "3. [E][ ] meet team (from: Sep 17 2026, 9:00 AM to: Sep 18 2026, 10:00 AM)\n");
    }

    @Test
    void execute_findMultipleMatches_preservesFullLogNumbers() throws GlennonException {
        todo.markAsDone();
        assertReadOnlyResponse(new FindCommand("read"), "Matching missions located:\n"
                + "1. [T][X] read book\n"
                + "2. [D][ ] read report (by: Sep 17 2026, 6:00 PM)\n");
        assertReadOnlyResponse(new FindCommand("report"), "Matching missions located:\n"
                + "2. [D][ ] read report (by: Sep 17 2026, 6:00 PM)\n");
    }

    @Test
    void execute_findNoMatches_printsOnlyHeading() throws GlennonException {
        assertReadOnlyResponse(new FindCommand("READ"), "Matching missions located:\n");
    }

    @Test
    void execute_onMatchingDate_displaysOnlyScheduledMissions() throws GlennonException {
        assertReadOnlyResponse(new OnCommand(LocalDate.of(2026, 9, 17)), "Missions on Sep 17 2026:\n"
                + "2. [D][ ] read report (by: Sep 17 2026, 6:00 PM)\n"
                + "3. [E][ ] meet team (from: Sep 17 2026, 9:00 AM to: Sep 18 2026, 10:00 AM)\n");
    }

    @Test
    void execute_onNoMatches_printsOnlyDateHeading() throws GlennonException {
        assertReadOnlyResponse(new OnCommand(LocalDate.of(2026, 9, 19)), "Missions on Sep 19 2026:\n");
    }

    @Test
    void execute_emptyLog_displaysEachHeadingWithoutSaving() throws GlennonException {
        while (missions.size() > 0) {
            missions.remove(0);
        }
        assertReadOnlyResponse(new ListCommand(), "Mission log:\n");
        assertReadOnlyResponse(new FindCommand("read"), "Matching missions located:\n");
        assertReadOnlyResponse(new OnCommand(LocalDate.of(2026, 9, 17)), "Missions on Sep 17 2026:\n");
    }

    @Test
    void execute_exit_reportsExitWithoutSavingOrChangingMissions() throws GlennonException {
        assertReadOnlyResponse(new ExitCommand(), "Signing off. Catch you on the next mission!\n");
    }

    /**
     * Verifies output, exit state, task identities and statuses, and the absence of persistence calls.
     */
    private void assertReadOnlyResponse(Command command, String expected) throws GlennonException {
        List<Task> originalMissions = List.copyOf(missions.asList());
        List<String> originalDisplays = originalMissions.stream().map(Task::toString).toList();
        StringWriter output = new StringWriter();
        Ui ui = new Ui(new PrintWriter(output, true));

        command.execute(missions, ui, new ReadOnlyStorage());

        assertEquals(expected, output.toString().replace("\r\n", "\n"));
        assertEquals(originalMissions, missions.asList());
        assertEquals(originalDisplays, missions.asList().stream().map(Task::toString).toList());
        assertFalse(ui.hasError());
        if (command instanceof ExitCommand) {
            assertTrue(command.isExit());
        } else {
            assertFalse(command.isExit());
        }
    }

    /**
     * Fails immediately if a read-only command attempts to persist anything.
     */
    private static final class ReadOnlyStorage extends Storage {
        @Override
        public void saveMissions(List<Task> savedMissions) {
            throw new AssertionError("Read-only commands must not save missions");
        }
    }
}
