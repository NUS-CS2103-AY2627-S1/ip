package alzara.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.storage.Storage;
import alzara.task.Deadline;
import alzara.task.Event;
import alzara.task.TaskList;
import alzara.task.ToDo;
import alzara.ui.Ui;

/**
 * Tests for {@link ViewCommand}. Read-only, so {@link Storage} is only
 * passed through (never used) - each test still supplies one pointed at a
 * fresh JUnit {@code @TempDir} to match {@link Command#execute}'s signature.
 */
class ViewCommandTest {
    // A date matching nothing should show a distinct "nothing scheduled" message.
    @Test
    void execute_dateMatchesNothing_showsNothingScheduledMessage(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);

        new ViewCommand(LocalDate.of(2019, 10, 20)).execute(memory, ui, new Storage(dataDir));

        assertEquals("I see nothing on Oct 20 2019.", ui.getAndClearResponse());
    }

    // A ToDo has no date, so it should never match any queried date.
    @Test
    void execute_toDoNeverMatchesAnyDate(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);

        new ViewCommand(LocalDate.of(2019, 10, 15)).execute(memory, ui, new Storage(dataDir));

        assertEquals("I see nothing on Oct 15 2019.", ui.getAndClearResponse());
    }

    // Matching Deadlines and Events should be shown sorted by their own date
    // (Deadline's due date, Event's start date), not by their order in the list.
    @Test
    void execute_matchingDeadlineAndEvent_showsSortedByOwnDate(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        memory.add(new Event("trip", LocalDate.of(2019, 10, 14), LocalDate.of(2019, 10, 16)));
        Ui ui = new Ui(true);

        new ViewCommand(LocalDate.of(2019, 10, 15)).execute(memory, ui, new Storage(dataDir));

        assertEquals("I see all on Oct 15 2019:"
                + "\n1.[E][ ] trip (from: Oct 14 2019 to: Oct 16 2019)"
                + "\n2.[D][ ] return book (by: Oct 15 2019)",
                ui.getAndClearResponse());
    }

    // An Event spanning the queried date but not starting or ending on it
    // (the inclusive-range middle) should still match.
    @Test
    void execute_dateWithinEventRange_matches(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new Event("trip", LocalDate.of(2019, 10, 14), LocalDate.of(2019, 10, 18)));
        Ui ui = new Ui(true);

        new ViewCommand(LocalDate.of(2019, 10, 16)).execute(memory, ui, new Storage(dataDir));

        assertEquals("I see all on Oct 16 2019:\n1.[E][ ] trip (from: Oct 14 2019 to: Oct 18 2019)",
                ui.getAndClearResponse());
    }
}
