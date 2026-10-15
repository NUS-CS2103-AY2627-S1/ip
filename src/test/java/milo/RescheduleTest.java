package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import milo.parser.RescheduleParser;
import milo.storage.Storage;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.TaskList;
import milo.task.ToDo;

/** Tests date replacements and rejected commands without saving to the user data file. */
public class RescheduleTest {
    @Test
    public void parse_deadline_replacesDateAndPreservesDescription() throws MiloException {
        Task original = new Deadline("read  book", "2026-09-10 1800");
        Task updated = RescheduleParser.parse(original, "/by 15/9/2026");

        assertEquals("D | 0 | read  book | 2026-09-15 0000", updated.storageString());
        assertEquals("D | 0 | read  book | 2026-09-10 1800", original.storageString());
        assertEquals(updated.storageString(), Storage.restoreTask(updated.storageString()).storageString());
    }

    @Test
    public void parse_eventPartialAndReversedFlags_keepsOmittedEndpoint() throws MiloException {
        Task task = new Event("exam", "2026-09-10 0900", "2026-09-10 1100");

        task = RescheduleParser.parse(task, "/from 2026-09-20");
        assertEquals("E | 0 | exam | 2026-09-20 0000 | 2026-09-10 1100", task.storageString());
        task = RescheduleParser.parse(task, "/to 2026-09-21 1200");
        assertEquals("E | 0 | exam | 2026-09-20 0000 | 2026-09-21 1200", task.storageString());
        task = RescheduleParser.parse(task, "/to 1/1/2019 1000 /from 1/1/2019 1000");
        assertEquals("E | 0 | exam | 2019-01-01 1000 | 2019-01-01 1000", task.storageString());
        task = RescheduleParser.parse(task, "/to 1/1/2019 1000");
        assertEquals("E | 0 | exam | 2019-01-01 1000 | 2019-01-01 1000", task.storageString());
        assertEquals(task.storageString(), Storage.restoreTask(task.storageString()).storageString());
    }

    @Test
    public void parse_invalidFlagPairs_rejectsWithoutChangingOriginal() {
        Task original = new Event("exam", "2026-09-10 0900", "2026-09-10 1100");
        for (String input : List.of("", "/from", "/unknown 2026-09-11",
                "/from 2026-09-11 /from 2026-09-12", "/by 2026-09-11",
                "/from 2026-09-11 /to invalid", "extra /from 2026-09-11")) {
            assertThrows(MiloException.class, () -> RescheduleParser.parse(original, input), input);
        }
        assertEquals("E | 0 | exam | 2026-09-10 0900 | 2026-09-10 1100", original.storageString());
    }

    @Test
    public void execute_invalidInputs_neverChangesTask() {
        Task original = new Event("exam", "2026-09-10 0900", "2026-09-10 1100");
        TaskList tasks = new TaskList(List.of(original));
        Logic logic = new Logic(tasks);
        List<String> commands = List.of(
                "reschedule", "reschedule x /from 2026-09-11", "reschedule 0 /from 2026-09-11",
                "reschedule -1 /from 2026-09-11", "reschedule 2 /from 2026-09-11",
                "reschedule 999999999999 /from 2026-09-11", "reschedule 1",
                "reschedule 1 /from 2026-09-11 /to invalid", "reschedule 1 /from 2026-02-30",
                "reschedule 1 /from 2026-09-11 /from 2026-09-12", "reschedule 1 /by 2026-09-11",
                "reschedule 1 /unknown 2026-09-11", "reschedule 1 /from",
                "reschedule 1 /from tomorrow", "reschedule 1 /from 2026-09-11 extra",
                "reschedule 1 /from 2026-09-11 /to", "reschedule 1 /FROM 2026-09-11",
                "reschedule 1 2 /from 2026-09-11", "Reschedule 1 /from 2026-09-11",
                " reschedule 1 /from 2026-09-11",
                "reschedule  1 /from 2026-09-11",
                "reschedule 1 /from 2026-09-11  0900");
        for (String command : commands) {
            String reply = logic.execute(command);
            assertTrue(!reply.contains("Rescheduled task:"), command);
            assertSame(original, tasks.get(0), command);
        }
    }

    @Test
    public void execute_invalidTaskNumbers_usesSharedCommandErrors() {
        Logic logic = new Logic(new TaskList(List.of(new Deadline("read", "2026-09-10"))));

        for (String number : List.of("x", "0", "-1", "2", "999999999999")) {
            assertEquals(logic.execute("mark " + number),
                    logic.execute("reschedule " + number + " /by 2026-09-11"));
        }
        assertEquals("Ermm... What do you want me to reschedule?", logic.execute("reschedule"));
        assertEquals(logic.execute("markx"), logic.execute("reschedulex /by 2026-09-11"));
    }

    @Test
    public void execute_missingOrWrongFlags_returnsFlagGuidance() {
        Logic logic = new Logic(new TaskList(List.of(new Deadline("read", "2026-09-10"),
                new Event("exam", "2026-09-10", "2026-09-11"))));
        for (String command : List.of("reschedule 1", "reschedule 2",
                "reschedule 1 2026-09-11", "reschedule 1 /unknown 2026-09-11",
                "reschedule 1 /from 2026-09-11", "reschedule 1 /to 2026-09-11",
                "reschedule 2 /by 2026-09-11")) {
            assertEquals("Let's untangle those dates! Use /by for deadlines, "
                    + "or /from and /to for events.", logic.execute(command), command);
        }
    }

    @Test
    public void execute_ineligibleTasksAndWrongDeadlineFlag_rejected() {
        Task completed = new Deadline("done", "2026-09-10");
        completed.markAsDone();
        Task completedEvent = new Event("done event", "2026-09-10", "2026-09-11");
        completedEvent.markAsDone();
        TaskList tasks = new TaskList(List.of(new ToDo("todo"), completed, completedEvent,
                new Deadline("deadline", "2026-09-10")));
        List<Task> originals = tasks.asList();
        Logic logic = new Logic(tasks);

        assertEquals("Hmmm... How does one reschedule a task without a date?",
                logic.execute("reschedule 1 /by 2026-09-11"));
        assertEquals("You've already completed this task!", logic.execute("reschedule 2 /by 2026-09-11"));
        assertEquals("You've already completed this task!", logic.execute("reschedule 3 /to 2026-09-11"));
        assertEquals("Let's untangle those dates! Use /by for deadlines, "
                + "or /from and /to for events.", logic.execute("reschedule 4 /from 2026-09-11"));
        assertEquals(originals, tasks.asList());
    }

}
