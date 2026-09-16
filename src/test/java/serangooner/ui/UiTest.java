package serangooner.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import serangooner.storage.Storage;
import serangooner.task.Deadline;
import serangooner.task.Event;
import serangooner.task.Task;
import serangooner.task.TaskDateTime;
import serangooner.task.TaskList;
import serangooner.task.Todo;

public class UiTest {
    private final Ui ui = new Ui();

    private static List<Task> threeTasks() {
        return List.of(new Todo("read book"), new Todo("write essay"), new Todo("nap"));
    }

    private static List<String> lines(String message) {
        return List.of(message.split(System.lineSeparator(), -1));
    }

    @Test
    public void formatWelcome_always_greetsAndPointsToHelpAndBye() {
        assertEquals(List.of("serangooner at your service. what's up?",
                "to see commands, type 'help'",
                "done? type 'bye' to exit :("), lines(ui.formatWelcome()));
    }

    @Test
    public void formatFarewell_always_saysBye() {
        assertEquals("bye~", ui.formatFarewell());
    }

    @Test
    public void formatHelp_severalCommands_numbersThemAndEndsWithTheDateHint() {
        List<String> commands = List.of("todo <description> - add a task",
                "list - view all saved tasks", "bye - exit serangooner");

        List<String> result = lines(ui.formatHelp(commands));

        assertEquals("serangooner commands:", result.get(0));
        assertEquals("1. todo <description> - add a task", result.get(1));
        assertEquals("3. bye - exit serangooner", result.get(3));
        assertEquals(TaskDateTime.FORMAT_HINT, result.get(commands.size() + 1));
    }

    @Test
    public void formatHelp_noCommands_returnsTheHeadingAndTheDateHintAlone() {
        assertEquals(List.of("serangooner commands:", TaskDateTime.FORMAT_HINT),
                lines(ui.formatHelp(List.of())));
    }

    @Test
    public void formatLoadReport_nothingLoaded_saysNothing() {
        assertEquals("", ui.formatLoadReport(new Storage.LoadResult(List.of(), 0, "", "")));
    }

    @Test
    public void formatLoadReport_tasksLoaded_reportsCount() {
        assertEquals("loaded 3 task(s) from your last visit",
                ui.formatLoadReport(new Storage.LoadResult(threeTasks(), 0, "", "")));
    }

    @Test
    public void formatLoadReport_linesSkipped_reportsBothCounts() {
        assertEquals("loaded 3 task(s) from your last visit; skipped 2 unreadable line(s)",
                ui.formatLoadReport(new Storage.LoadResult(threeTasks(), 2, "", "")));
    }

    @Test
    public void formatLoadReport_loadFailed_reportsReason() {
        assertEquals("couldn't read it",
                ui.formatLoadReport(new Storage.LoadResult(List.of(), 0, "couldn't read it", "")));
    }

    @Test
    public void formatLoadReport_backupMade_addsItOnItsOwnLine() {
        assertEquals(List.of("loaded 3 task(s) from your last visit; skipped 1 unreadable line(s)",
                        "kept a copy"),
                lines(ui.formatLoadReport(new Storage.LoadResult(threeTasks(), 1, "", "kept a copy"))));
        assertEquals(List.of("couldn't read it", "kept a copy"),
                lines(ui.formatLoadReport(
                        new Storage.LoadResult(List.of(), 0, "couldn't read it", "kept a copy"))));
    }

    @Test
    public void formatTaskAdded_task_namesItsKindAndTheCount() {
        assertEquals(List.of("added todo: [T][ ] read book",
                "you now have 3 pending task(s) :c"),
                lines(ui.formatTaskAdded(new Todo("read book"), 3)));
    }

    @Test
    public void formatTaskAdded_deadline_callsItADeadline() {
        assertTrue(ui.formatTaskAdded(new Deadline("submit ip", "2026-09-01"), 1)
                .startsWith("added deadline: "));
    }

    @Test
    public void formatTaskAdded_event_callsItAnEvent() {
        assertTrue(ui.formatTaskAdded(new Event("demo", "2026-09-05", "2026-09-06"), 1)
                .startsWith("added event: "));
    }

    @Test
    public void formatTaskAdded_lastTaskInTheList_reportsTheCountItWasGiven() {
        assertEquals("you now have 1 pending task(s) :c",
                lines(ui.formatTaskAdded(new Todo("read book"), 1)).get(1));
    }

    @Test
    public void formatTaskMarked_task_saysItIsDone() {
        assertEquals("marked task as done: [T][ ] read book",
                ui.formatTaskMarked(new Todo("read book")));
    }

    @Test
    public void formatTaskUnmarked_task_saysItIsIncomplete() {
        Task task = new Todo("read book");
        task.markDone();

        assertEquals("marked task as incomplete: [T][✓] read book",
                ui.formatTaskUnmarked(task));
    }

    @Test
    public void formatTaskDeleted_task_saysItIsGone() {
        assertEquals("deleted task: [T][ ] read book",
                ui.formatTaskDeleted(new Todo("read book")));
    }

    @Test
    public void formatUndo_editUndone_saysSo() {
        assertEquals("undid your last edit", ui.formatUndo(true));
    }

    @Test
    public void formatUndo_nothingToUndo_saysSo() {
        assertEquals("there's nothing to undo >:(", ui.formatUndo(false));
    }

    @Test
    public void formatTasks_severalTasks_numbersThemUnderAHeading() {
        List<String> result = lines(ui.formatTasks(new TaskList(threeTasks()).getEntries()));

        assertEquals("your list", result.get(0));
        assertEquals(" 1. [T][ ] read book", result.get(1));
        assertEquals(" 3. [T][ ] nap", result.get(3));
    }

    @Test
    public void formatTasks_emptyList_saysSo() {
        assertTrue(ui.formatTasks(new TaskList().getEntries()).startsWith("your list is empty"));
    }

    @Test
    public void formatTasksInRange_singleDate_headsTheListingWithThatDay() {
        TaskList tasks = new TaskList(List.of(new Deadline("submit ip", "2026-09-01")));
        LocalDate date = LocalDate.of(2026, 9, 1);

        String result = ui.formatTasksInRange(tasks.getEntriesWithin(date, date), date, date);

        assertEquals("tasks on 01 Sep 2026", lines(result).get(0));
    }

    @Test
    public void formatTasksInRange_span_headsTheListingWithBothEnds() {
        TaskList tasks = new TaskList(List.of(new Deadline("submit ip", "2026-09-01")));
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 4);

        String result = ui.formatTasksInRange(tasks.getEntriesWithin(start, end), start, end);

        assertEquals("tasks between 01 Sep 2026 and 04 Sep 2026", lines(result).get(0));
    }

    @Test
    public void formatTasksInRange_noMatchingTask_saysSo() {
        LocalDate date = LocalDate.of(2026, 9, 1);

        String result = ui.formatTasksInRange(new TaskList().getEntriesWithin(date, date),
                date, date);

        assertEquals("you have nothing on 01 Sep 2026 :D", result);
    }

    @Test
    public void formatTasksInRange_filteredListing_keepsTheNumbersFromTheFullList() {
        // A number read off a filtered listing has to stay usable with mark and
        // delete, so the listing must not renumber from one.
        TaskList tasks = new TaskList(List.of(new Todo("read book"),
                new Deadline("submit ip", "2026-09-01"),
                new Deadline("submit tp", "2026-09-02")));
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 2);

        List<String> result = lines(
                ui.formatTasksInRange(tasks.getEntriesWithin(start, end), start, end));

        assertEquals(" 2. [D][ ] submit ip (by: 01 Sep 2026)", result.get(1));
        assertEquals(" 3. [D][ ] submit tp (by: 02 Sep 2026)", result.get(2));
    }

    @Test
    public void formatMatchingTasks_severalMatches_numbersThemUnderAHeading() {
        TaskList tasks = new TaskList(List.of(new Todo("write essay"),
                new Todo("read book"), new Todo("return book")));

        List<String> result = lines(
                ui.formatMatchingTasks(tasks.getEntriesMatching("book"), "book"));

        assertEquals("matching tasks:", result.get(0));
        assertEquals(" 2. [T][ ] read book", result.get(1));
        assertEquals(" 3. [T][ ] return book", result.get(2));
    }

    @Test
    public void formatMatchingTasks_noMatch_saysSoAndQuotesTheKeyword() {
        assertEquals("no task mentions 'book' :o",
                ui.formatMatchingTasks(new TaskList().getEntriesMatching("book"), "book"));
    }

    @Test
    public void formatLoadReport_onlyUnreadableLines_reportsNothingLoadedAndTheSkippedCount() {
        assertEquals("loaded 0 task(s) from your last visit; skipped 2 unreadable line(s)",
                ui.formatLoadReport(new Storage.LoadResult(List.of(), 2, "", "")));
    }
}
