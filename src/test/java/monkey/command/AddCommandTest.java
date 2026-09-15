package monkey.command;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.Deadline;
import monkey.model.Event;
import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validDeadline_addsAndSavesTask() throws Exception {
        TaskList tasks = new TaskList();
        Path saveFile = temporaryDirectory.resolve("deadline.txt");
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.DEADLINE, "submit /by 2099-01-01")
                .execute(tasks, createUi(output), new Storage(saveFile.toString()));

        assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("D | 0 | submit | 2099-01-01", Files.readString(saveFile).trim());
        assertTrue(output.toString().contains("[D][ ] submit (by: Jan 01 2099)"));
    }

    @Test
    void execute_validEvent_addsAndSavesTask() throws Exception {
        TaskList tasks = new TaskList();
        Path saveFile = temporaryDirectory.resolve("event.txt");
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.EVENT, "class /from 2pm /to 3pm")
                .execute(tasks, createUi(output), new Storage(saveFile.toString()));

        assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("E | 0 | class | 2pm | 3pm", Files.readString(saveFile).trim());
        assertTrue(output.toString().contains("[E][ ] class (from: 2pm to: 3pm)"));
    }

    @Test
    void execute_missingDeadlineOrEventParts_rejectsInput() {
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.DEADLINE, " /by 2099-01-01")
                .execute(new TaskList(), createUi(output), createStorage());
        new AddCommand(Command.EVENT, "class /from  /to 3pm")
                .execute(new TaskList(), createUi(output), createStorage());

        assertTrue(output.toString().contains("A deadline needs a description"));
        assertTrue(output.toString().contains("An event needs a description"));
    }

    @Test
    void execute_duplicateEvent_rejectsSecondTask() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();
        AddCommand command = new AddCommand(Command.EVENT, "class /from 2pm /to 3pm");

        command.execute(tasks, createUi(output), createStorage());
        command.execute(tasks, createUi(output), createStorage());

        assertEquals(1, tasks.size());
        assertTrue(output.toString().endsWith("OOPS! Monkey says: That task is already in your list."));
    }

    @Test
    void execute_invalidDeadlineDate_rejectsInput() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.DEADLINE, "submit /by 2026-02-30")
                .execute(tasks, createUi(output), createStorage());

        assertTrue(tasks.isEmpty());
        assertTrue(output.toString().startsWith("Use a date like"));
    }

    @Test
    void execute_deadlineWithRepeatedByMarker_rejectsInput() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.DEADLINE, "submit /by 2026-09-20 /by 2026-09-21")
                .execute(tasks, new Ui(output::append), createStorage());

        assertTrue(tasks.isEmpty());
        assertEquals("OOPS! Monkey says: Use exactly one /by marker for a deadline.", output.toString());
    }

    @Test
    void execute_eventWithReversedMarkers_rejectsInput() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.EVENT, "meeting /to 2026-09-20 1600 /from 2026-09-20 1400")
                .execute(tasks, new Ui(output::append), createStorage());

        assertTrue(tasks.isEmpty());
        assertEquals("OOPS! Monkey says: Use one /from marker followed by one /to marker for an event.",
                output.toString());
    }

    @Test
    void execute_eventEndingAtStart_rejectsInput() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.EVENT, "meeting /from 2026-09-20 1400 /to 2026-09-20 1400")
                .execute(tasks, new Ui(output::append), createStorage());

        assertTrue(tasks.isEmpty());
        assertEquals("An event must end after it starts.", output.toString());
    }

    @Test
    void execute_eventWithImpossibleDate_rejectsInput() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.EVENT, "meeting /from 2026-02-30 1400 /to 2026-03-01 1400")
                .execute(tasks, new Ui(output::append), createStorage());

        assertTrue(tasks.isEmpty());
        assertEquals("Use event times like yyyy-mm-dd HHmm, d/M/yyyy HHmm, or h:mm am/pm.", output.toString());
    }

    @Test
    void execute_eventWithImpossibleDateOnly_rejectsInput() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.EVENT, "conference /from 2026-02-30 /to 2026-03-01")
                .execute(tasks, new Ui(output::append), createStorage());

        assertTrue(tasks.isEmpty());
        assertEquals("Use event times like yyyy-mm-dd HHmm, d/M/yyyy HHmm, or h:mm am/pm.", output.toString());
    }

    @Test
    void execute_eventWithEndDateBeforeStartDate_rejectsInput() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.EVENT, "conference /from 2026-09-21 /to 2026-09-20")
                .execute(tasks, new Ui(output::append), createStorage());

        assertTrue(tasks.isEmpty());
        assertEquals("An event must end after it starts.", output.toString());
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("monkey.txt").toString());
    }

    private static Ui createUi(StringBuilder output) {
        return new Ui(message -> {
            if (!output.isEmpty()) {
                output.append('\n');
            }
            output.append(message);
        });
    }
}
