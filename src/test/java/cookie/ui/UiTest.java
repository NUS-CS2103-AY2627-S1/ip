package cookie.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import cookie.command.CookieException;
import cookie.task.DateTimeValue;
import cookie.task.Deadline;
import cookie.task.Event;
import cookie.task.SortCriterion;
import cookie.task.SortDirection;
import cookie.task.TaskList;
import cookie.task.Todo;

/** Tests the UI's messages and date-based task filtering. */
public class UiTest {
    private final ReplyCollector replyCollector = new ReplyCollector();

    @Test
    public void messageMethods_printExpectedTaskInformation() {
        Ui ui = new Ui(replyCollector);
        Todo todo = new Todo("buy milk");
        TaskList tasks = new TaskList();
        tasks.add(todo);

        ui.greet();
        ui.showTaskAdded(todo, 1);
        ui.showTaskList(tasks);
        ui.showTaskMarked(todo);
        ui.showTaskUnmarked(todo);
        ui.showTaskDeleted(todo, 0);
        ui.showError(new CookieException("bad command"));
        ui.showSaveError("save details");
        ui.showLoadError("load details");
        ui.showMalformedRecords(List.of(2, 5));
        ui.exit();

        String printed = replyCollector.getReply();
        assertTrue(printed.contains("Hello! I'm your favourite chatbot Cookie."));
        assertTrue(printed.contains("Ok. I've added this task:"));
        assertTrue(printed.contains("1. [T][ ] buy milk"));
        assertTrue(printed.contains("Wow you actually got work done..."));
        assertTrue(printed.contains("I can't believe you lied to me..."));
        assertTrue(printed.contains("Now you have 0 task(s) in the list."));
        assertTrue(printed.contains("Bruh... bad command"));
        assertTrue(printed.contains("couldn't save your tasks: save details"));
        assertTrue(printed.contains("couldn't load your tasks: load details"));
        assertTrue(printed.contains("malformed saved task records on lines 2, 5"));
        assertTrue(printed.contains("Bye. I'm going to sleep."));
    }

    @Test
    public void defaultUi_exit_printsCompleteConsoleMessage() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream printedBytes = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(printedBytes, true, StandardCharsets.UTF_8));
            new Ui().exit();
        } finally {
            System.setOut(originalOutput);
        }

        String separator = "____________________________________________________________";
        String expected = separator + System.lineSeparator()
                + "Bye. I'm going to sleep." + System.lineSeparator()
                + separator + System.lineSeparator();
        assertEquals(expected, printedBytes.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void showMalformedRecords_singleLine_usesSingularNoun() {
        Ui ui = new Ui(replyCollector);

        ui.showMalformedRecords(List.of(3));

        assertTrue(replyCollector.getReply().contains("records on line 3"));
    }

    @Test
    public void showTasksOnDate_listsOnlyMatchingDeadlinesAndOverlappingEvents() {
        Ui ui = new Ui(replyCollector);
        LocalDate queriedDate = LocalDate.of(2026, 8, 27);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("ordinary todo"));
        tasks.add(new Deadline("matching deadline",
                new DateTimeValue(queriedDate, LocalTime.of(17, 0))));
        tasks.add(new Event("overlapping event",
                new DateTimeValue(LocalDate.of(2026, 8, 26), LocalTime.of(23, 0)),
                new DateTimeValue(queriedDate, LocalTime.of(1, 0))));
        tasks.add(new Event("non-matching event",
                new DateTimeValue(LocalDate.of(2026, 8, 28), LocalTime.of(9, 0)),
                new DateTimeValue(LocalDate.of(2026, 8, 28), LocalTime.of(10, 0))));

        ui.showTasksOnDate(queriedDate, tasks.findOn(queriedDate));

        String printed = replyCollector.getReply();
        assertTrue(printed.contains("Here are the task(s) on Aug 27 2026:"));
        assertTrue(printed.contains("2. [D][ ] matching deadline"));
        assertTrue(printed.contains("3. [E][ ] overlapping event"));
        assertTrue(!printed.contains("ordinary todo"));
        assertTrue(!printed.contains("non-matching event"));
    }

    @Test
    public void showMatchingTasks_listsOnlyTasksContainingKeyword() {
        Ui ui = new Ui(replyCollector);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", new DateTimeValue(
                LocalDate.of(2026, 8, 27), LocalTime.of(17, 0))));
        tasks.add(new Todo("buy groceries"));

        ui.showMatchingTasks(tasks.find("book"));

        String printed = replyCollector.getReply();
        assertTrue(printed.contains("Here are the matching tasks in your list:"));
        assertTrue(printed.contains("1. [T][ ] read book"));
        assertTrue(printed.contains("2. [D][ ] return book"));
        assertTrue(!printed.contains("buy groceries"));
    }

    @Test
    public void showSortedTasks_printsCriterionDirectionAndOriginalNumbers() {
        Ui ui = new Ui(replyCollector);
        Todo firstTask = new Todo("zebra");
        Todo secondTask = new Todo("apple");
        List<TaskList.IndexedTask> sortedTasks = List.of(
                new TaskList.IndexedTask(2, secondTask),
                new TaskList.IndexedTask(1, firstTask));

        ui.showSortedTasks(sortedTasks, SortCriterion.DESCRIPTION, SortDirection.ASCENDING);

        assertEquals("Here are the task(s) sorted by description (ascending):"
                + System.lineSeparator()
                + "2. [T][ ] apple"
                + System.lineSeparator()
                + "1. [T][ ] zebra", replyCollector.getReply());
    }

    @Test
    public void showSortedTasks_emptyDateDescending_printsHeaderWithoutTaskLines() {
        Ui ui = new Ui(replyCollector);

        ui.showSortedTasks(List.of(), SortCriterion.DATE, SortDirection.DESCENDING);

        assertEquals("Here are the task(s) sorted by date (descending):", replyCollector.getReply());
    }

    @Test
    public void combinedOutput_multipleMessages_forwardsInOrderToEveryOutput() {
        ReplyCollector firstOutput = new ReplyCollector();
        ReplyCollector secondOutput = new ReplyCollector();
        Output output = Output.combine(firstOutput, secondOutput);

        output.show("first");
        output.show("second");

        String expected = "first" + System.lineSeparator() + "second";
        assertEquals(expected, firstOutput.getReply());
        assertEquals(expected, secondOutput.getReply());
    }

    @Test
    public void combine_changedSourceArray_keepsOriginalOutputSnapshot() {
        ReplyCollector originalOutput = new ReplyCollector();
        ReplyCollector replacementOutput = new ReplyCollector();
        Output[] outputs = {originalOutput};
        Output combinedOutput = Output.combine(outputs);
        outputs[0] = replacementOutput;

        combinedOutput.show("message");

        assertEquals("message", originalOutput.getReply());
        assertEquals("", replacementOutput.getReply());
        assertThrows(NullPointerException.class, () -> Output.combine(originalOutput, null));
    }

    @Test
    public void replyCollector_clear_removesPreviouslyCollectedMessages() {
        replyCollector.show("first");
        replyCollector.show("second");

        replyCollector.clear();

        assertEquals("", replyCollector.getReply());
    }
}
