package crystal.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import crystal.task.Deadline;
import crystal.task.Event;
import crystal.task.TaskList;
import crystal.task.Todo;
import crystal.task.WithinPeriod;

/**
 * Tests complete and date-filtered task listing through {@link ListCommand}.
 */
public class ListCommandTest extends CommandTestBase {
    /** Verifies numbered complete-list output without a storage mutation. */
    @Test
    public void execute_withoutDate_displaysNumberedCompleteListWithoutSaving() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Deadline("submit report", "2Dec26")));
        ListCommand command = new ListCommand(Optional.empty());

        command.execute(tasks, createUi(), createStorage());

        assertEquals("Crystal: Ta-da!!! Here are all your tasks:\n"
                + "         1.[T][ ] read book\n"
                + "         2.[D][ ] submit report (by: 02 Dec 2026)\n", getOutput());
        assertFalse(Files.exists(tempDirectory.resolve("data").resolve("crystal.txt")));
    }

    /** Verifies unnumbered date-filtered output containing only matching tasks. */
    @Test
    public void execute_withDate_displaysOnlyUnnumberedMatchingDatedTasks() {
        TaskList tasks = new TaskList(List.of(
                new Todo("mention 02 Dec 2026"),
                new Deadline("submit report", "2Dec26 0900"),
                new Deadline("later", "3Dec26"),
                new Event("conference", "1Dec26", "3Dec26"),
                new WithinPeriod("collect certificate", "1Dec26", "3Dec26")));
        ListCommand command = new ListCommand(Optional.of(LocalDate.of(2026, 12, 2)));

        command.execute(tasks, createUi(), createStorage());

        assertEquals("Crystal: Yay, here are your dated tasks on 02 Dec 2026:\n"
                + "         - [D][ ] submit report (by: 02 Dec 2026 0900)\n"
                + "         - [E][ ] conference (from: 01 Dec 2026 to: 03 Dec 2026)\n"
                + "         - [W][ ] collect certificate "
                + "(from: 01 Dec 2026 to: 03 Dec 2026)\n",
                getOutput());
    }

    /** Verifies the date-specific message when no tasks match. */
    @Test
    public void execute_withDateAndNoMatches_displaysEmptyFilteredMessage() {
        TaskList tasks = new TaskList(List.of(new Deadline("submit report", "3Dec26")));
        ListCommand command = new ListCommand(Optional.of(LocalDate.of(2026, 12, 2)));

        command.execute(tasks, createUi(), createStorage());

        assertEquals("Crystal: No dated tasks on 02 Dec 2026, yay!\n",
                getOutput());
    }
}
