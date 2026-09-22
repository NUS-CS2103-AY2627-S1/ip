package friday.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import friday.parser.Parser;
import friday.task.Deadline;
import friday.task.Event;
import friday.task.TaskList;
import friday.task.Todo;
import friday.ui.Ui;

/** Tests the default behavior shared by commands. */
class CommandTest {

    /** UI test double that records output instead of printing it. */
    private static class RecordingUi extends Ui {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void showLine(String message) {
            messages.add(message);
        }
    }

    @Test
    void isExit_regularCommand_returnsFalse() {
        Command command = new Command(Command.Type.LIST, "list") { };

        assertFalse(command.isExit());
    }

    @Test
    void execute_todoCommand_addsTodo() {
        TaskList tasks = new TaskList();

        Parser.parse("todo buy milk").execute(tasks, new RecordingUi());

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] buy milk", tasks.get(0).toString());
    }

    @Test
    void execute_markCommand_marksSelectedTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));

        Parser.parse("mark 1").execute(tasks, new RecordingUi());

        assertEquals("[T][X] buy milk", tasks.get(0).toString());
    }

    @Test
    void execute_invalidTaskNumber_reportsErrorWithoutChangingTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        RecordingUi ui = new RecordingUi();

        Parser.parse("mark 2").execute(tasks, ui);

        assertEquals("[T][ ] buy milk", tasks.get(0).toString());
        assertTrue(ui.messages.get(0).contains("does not exist"));
    }

    @Test
    void execute_deadlineCommand_addsFormattedDeadline() {
        TaskList tasks = new TaskList();

        Parser.parse("deadline submit report /by 2026-08-30")
                .execute(tasks, new RecordingUi());

        assertEquals("[D][ ] submit report (by: Aug 30 2026)", tasks.get(0).toString());
    }

    @Test
    void execute_eventCommand_addsEventWithStartAndEnd() {
        TaskList tasks = new TaskList();

        Parser.parse("event project meeting /from 2pm /to 3pm")
                .execute(tasks, new RecordingUi());

        assertEquals("[E][ ] project meeting (from: 2pm to: 3pm)", tasks.get(0).toString());
    }

    @Test
    void execute_findCommand_displaysAllMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Todo("return book"));
        RecordingUi ui = new RecordingUi();

        Parser.parse("find book").execute(tasks, ui);

        assertEquals(List.of(
                "Okay here's the task ah:",
                "1.[T][ ] read book",
                "2.[T][ ] return book"), ui.messages);
    }

    @Test
    void execute_findWithoutKeyword_reportsError() {
        RecordingUi ui = new RecordingUi();

        Parser.parse("find").execute(new TaskList(), ui);

        assertTrue(ui.messages.get(0).contains("specify a keyword"));
    }

    @Test
    void execute_sortCommand_sortsTasksAlphabetically() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("write report"));
        tasks.add(new Todo("buy milk"));

        Parser.parse("sort").execute(tasks, new RecordingUi());

        assertEquals("[T][ ] buy milk", tasks.get(0).toString());
        assertEquals("[T][ ] write report", tasks.get(1).toString());
    }

    @Test
    void execute_sortReverseCommand_sortsTasksReverseAlphabetically() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("write report"));

        Parser.parse("sort reverse").execute(tasks, new RecordingUi());

        assertEquals("[T][ ] write report", tasks.get(0).toString());
        assertEquals("[T][ ] buy milk", tasks.get(1).toString());
    }

    @Test
    void execute_sortTypeCommand_groupsTasksByType() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        tasks.add(new Event(new String[] {"meeting", "2pm", "3pm"}));
        tasks.add(new Deadline(new String[] {"submit report", "2026-09-30"}));

        Parser.parse("sort type").execute(tasks, new RecordingUi());

        assertTrue(tasks.get(0) instanceof Deadline);
        assertTrue(tasks.get(1) instanceof Event);
        assertTrue(tasks.get(2) instanceof Todo);
    }

    @Test
    void execute_sortWithInvalidDirection_reportsUsageWithoutSorting() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("write report"));
        tasks.add(new Todo("buy milk"));
        RecordingUi ui = new RecordingUi();

        Parser.parse("sort sideways").execute(tasks, ui);

        assertEquals("[T][ ] write report", tasks.get(0).toString());
        assertTrue(ui.messages.get(0).contains("sort type"));
    }

    @Test
    void execute_invalidArguments_reportsErrorsWithoutAddingTasks() {
        List<String> inputs = List.of(
                "event meeting /from 2pm /from 3pm /to 4pm",
                "event meeting /to 4pm /from 2pm /to 5pm",
                "event meeting /from 2pm /to 3pm /to 4pm",
                "event meeting /from 2pm /to",
                "event meeting /from 3pm /to 2pm",
                "deadline report /by 2026-02-30",
                "deadline report /by 2026-09-01 /by 2026-09-02",
                "todo   ");
        for (String input : inputs) {
            TaskList tasks = new TaskList();
            RecordingUi ui = new RecordingUi();
            Parser.parse(input).execute(tasks, ui);
            assertTrue(tasks.isEmpty(), input);
            assertFalse(ui.messages.isEmpty(), input);
        }
    }

    @Test
    void execute_duplicateCompletedTask_reportsErrorWithoutSuccessMessage() {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        task.markAsDone();
        tasks.add(task);
        RecordingUi ui = new RecordingUi();

        Parser.parse("todo read book").execute(tasks, ui);

        assertEquals(1, tasks.size());
        assertEquals(1, ui.messages.size());
        assertTrue(ui.messages.getFirst().contains("already exists"));
    }

    @Test
    void execute_extraWhitespace_acceptsCommand() {
        TaskList tasks = new TaskList();
        Parser.parse("  event\t meeting   /from\t2pm   /to  3pm  ")
                .execute(tasks, new RecordingUi());
        assertEquals(1, tasks.size());
        assertTrue(Parser.parse("  bye  ").isExit());
    }

}
