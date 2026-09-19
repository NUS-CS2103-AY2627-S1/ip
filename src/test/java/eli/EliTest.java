package eli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests command responses that operate on an existing task list. */
public class EliTest {
    @Test
    public void nullInput_returnsCommandPrompt() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.getResponse(null).contains("Please enter a command."));
    }

    @Test
    public void blankInput_returnsCommandPrompt() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.getResponse("   ").contains("Please enter a command."));
    }

    @Test
    public void taskNumberCommands_missingNumber_returnSpecificPrompt() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.getResponse("mark").contains("task number after mark"));
        assertTrue(eli.getResponse("unmark").contains("task number after unmark"));
        assertTrue(eli.getResponse("delete").contains("task number after delete"));
    }

    @Test
    public void deadline_repeatedByParameter_returnsError() {
        Eli eli = new Eli(new TaskList());

        String response = eli.getResponse(
                "deadline submit report /by 2026-09-18 /by 2026-09-19");

        assertTrue(response.contains("Use /by only once"));
    }

    @Test
    public void deadline_impossibleCalendarDate_returnsError() {
        Eli eli = new Eli(new TaskList());

        String response = eli.getResponse("deadline submit report /by 2026-02-30");

        assertTrue(response.contains("deadline date is invalid"));
    }

    @Test
    public void event_repeatedTimeParameter_returnsError() {
        Eli eli = new Eli(new TaskList());

        String response = eli.getResponse(
                "event meeting /from 2026-09-15 1400 /to 2026-09-15 1500"
                        + " /to 2026-09-15 1600");

        assertTrue(response.contains("Use /from and /to only once"));
    }

    @Test
    public void event_impossibleTime_returnsError() {
        Eli eli = new Eli(new TaskList());

        String response = eli.getResponse(
                "event meeting /from 2026-09-15 2500 /to 2026-09-15 2600");

        assertTrue(response.contains("event date is invalid"));
    }

    @Test
    public void event_endNotAfterStart_returnsError() {
        Eli eli = new Eli(new TaskList());

        String sameTimeResponse = eli.getResponse(
                "event meeting /from 2026-09-15 1400 /to 2026-09-15 1400");
        String earlierTimeResponse = eli.getResponse(
                "event meeting /from 2026-09-15 1400 /to 2026-09-15 1300");

        assertTrue(sameTimeResponse.contains("must end after it starts"));
        assertTrue(earlierTimeResponse.contains("must end after it starts"));
    }

    @Test
    public void addExactDuplicate_rejectsSecondTask() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        Eli eli = new Eli(tasks);

        String response = eli.getResponse("todo READ NOTES");

        assertTrue(response.contains("exact task is already"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void addSimilarDeadlineWithDifferentDate_acceptsBothTasks() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline("submit report", "2026-09-18"));
        Eli eli = new Eli(tasks);

        String response = eli.getResponse("deadline submit report /by 2026-09-19");

        assertFalse(response.contains("exact task is already"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void exitCommand_recognizesEnglishAndChineseOnly() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.isExitCommand("  bye  "));
        assertTrue(eli.isExitCommand("再见"));
        assertFalse(eli.isExitCommand("goodbye"));
    }

    @Test
    public void todo_validCommand_addsTaskAndHandlesExtraSpaces() {
        TaskList tasks = new TaskList();
        Eli eli = new Eli(tasks);

        String response = eli.getResponse("  todo    read notes  ");

        assertTrue(response.contains("Task captured!"));
        assertEquals(1, tasks.size());
        assertEquals("read notes", tasks.get(0).getDescription());
    }

    @Test
    public void deadline_validDateAndNaturalLanguage_addTasks() {
        TaskList tasks = new TaskList();
        Eli eli = new Eli(tasks);

        String datedResponse = eli.getResponse(
                "deadline submit report /by 2026-09-18 2359");
        String naturalResponse = eli.getResponse(
                "deadline call home /by next Friday");

        assertTrue(datedResponse.contains("Sep 18 2026, 11:59PM"));
        assertTrue(naturalResponse.contains("next Friday"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void deadline_missingDescriptionOrDate_returnsError() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.getResponse("deadline").contains("needs a description"));
        assertTrue(eli.getResponse("deadline /by 2026-09-18")
                .contains("needs a description"));
        assertTrue(eli.getResponse("deadline submit report")
                .contains("needs a /by value"));
        assertTrue(eli.getResponse("deadline submit report /by ")
                .contains("needs a /by value"));
    }

    @Test
    public void event_validDateTimes_addsTask() {
        TaskList tasks = new TaskList();
        Eli eli = new Eli(tasks);

        String response = eli.getResponse(
                "event project meeting /from 2026-09-15 1400 /to 2026-09-15 1600");

        assertTrue(response.contains("Sep 15 2026, 2:00PM"));
        assertTrue(response.contains("Sep 15 2026, 4:00PM"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void event_missingDescriptionOrTime_returnsError() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.getResponse("event").contains("needs a description"));
        assertTrue(eli.getResponse(
                "event /from 2026-09-15 1400 /to 2026-09-15 1600")
                .contains("needs a description"));
        assertTrue(eli.getResponse("event project meeting")
                .contains("needs /from and /to values"));
        assertTrue(eli.getResponse(
                "event project meeting /to 2026-09-15 1600 /from 2026-09-15 1400")
                .contains("needs /from and /to values"));
    }

    @Test
    public void markAndUnmark_validTaskNumber_updateStatus() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        Eli eli = new Eli(tasks);

        String markResponse = eli.getResponse("mark 1");
        assertTrue(markResponse.contains("Nice work!"));
        assertEquals("X", tasks.get(0).getStatusIcon());

        String unmarkResponse = eli.getResponse("unmark 1");
        assertTrue(unmarkResponse.contains("No worries!"));
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void taskNumberCommand_invalidNumber_returnsError() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        Eli eli = new Eli(tasks);

        assertTrue(eli.getResponse("mark abc").contains("no task with that number"));
        assertTrue(eli.getResponse("unmark 0").contains("no task with that number"));
        assertTrue(eli.getResponse("delete 2").contains("no task with that number"));
    }

    @Test
    public void delete_validTaskNumber_removesTask() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        tasks.addTask(new Todo("submit quiz"));
        Eli eli = new Eli(tasks);

        String response = eli.getResponse("delete 1");

        assertTrue(response.contains("Task cleared!"));
        assertEquals(1, tasks.size());
        assertEquals("submit quiz", tasks.get(0).getDescription());
    }

    @Test
    public void unknownCommand_returnsHelpfulError() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.getResponse("dance").contains("don't recognize that command"));
    }

    @Test
    public void find_missingKeyword_returnsSpecificError() {
        Eli eli = new Eli(new TaskList());

        assertTrue(eli.getResponse("find").contains("enter a keyword after find"));
    }

    @Test
    public void list_multipleTasks_returnsNumberedTaskLines() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        tasks.addTask(new Todo("submit quiz"));
        Eli eli = new Eli(tasks);

        String expected = "Let's see what's on your list!"
                + System.lineSeparator() + System.lineSeparator()
                + "来看看你的任务清单吧："
                + System.lineSeparator() + "1.[T][ ] read notes"
                + System.lineSeparator() + "2.[T][ ] submit quiz";

        assertEquals(expected, eli.getResponse("list"));
    }

    @Test
    public void find_matchingTasks_preservesOriginalTaskNumbers() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        tasks.addTask(new Todo("submit quiz"));
        tasks.addTask(new Todo("review notes"));
        Eli eli = new Eli(tasks);

        String expected = "I found these tasks!"
                + System.lineSeparator() + System.lineSeparator()
                + "找到这些任务啦："
                + System.lineSeparator() + "1.[T][ ] read notes"
                + System.lineSeparator() + "3.[T][ ] review notes";

        assertEquals(expected, eli.getResponse("find notes"));
    }

    @Test
    public void find_noMatchingTask_returnsNoMatchesMessage() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read notes"));
        Eli eli = new Eli(tasks);

        String expected = "No matching tasks found."
                + System.lineSeparator() + System.lineSeparator()
                + "没有找到相关任务。";

        assertEquals(expected, eli.getResponse("find quiz"));
    }

    @Test
    public void sort_mixedTasks_returnsSortedSectionsAndReordersTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("write report"));
        tasks.addTask(new Event("lecture", "2026-09-09 1400", "2026-09-09 1600"));
        tasks.addTask(new Deadline("submit quiz", "2026-09-10"));
        Eli eli = new Eli(tasks);

        String expected = "All sorted and ready!"
                + System.lineSeparator() + System.lineSeparator()
                + "任务已经排好啦："
                + System.lineSeparator() + "Deadlines:"
                + System.lineSeparator() + "1.[D][ ] submit quiz (by: Sep 10 2026)"
                + System.lineSeparator() + "Events:"
                + System.lineSeparator() + "2.[E][ ] lecture (from: Sep 9 2026, 2:00PM"
                + " to: Sep 9 2026, 4:00PM)"
                + System.lineSeparator() + "Todos:"
                + System.lineSeparator() + "3.[T][ ] write report";

        assertEquals(expected, eli.getResponse("sort"));
        assertEquals("submit quiz", tasks.get(0).getDescription());
    }

    @Test
    public void sort_emptyTaskList_returnsEmptyMessage() {
        Eli eli = new Eli(new TaskList());

        String expected = "There are no tasks to sort yet."
                + System.lineSeparator() + System.lineSeparator()
                + "暂时没有任务可以排序。";

        assertEquals(expected, eli.getResponse("sort"));
    }

    @Test
    public void bye_returnsBilingualFarewell() {
        Eli eli = new Eli(new TaskList());

        String expected = "Bye for now!"
                + System.lineSeparator() + System.lineSeparator()
                + "再见，记得回来找我！";

        assertEquals(expected, eli.getResponse("bye"));
    }
}
