package kiki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kiki.storage.Storage;
import kiki.ai.AiService;
import kiki.task.Deadlines;
import kiki.task.Events;
import kiki.task.Task;
import kiki.task.ToDos;

public class KikiTest {

    @TempDir
    private Path tempDirectory;

    private static final LocalDate RANGE_START = LocalDate.of(2035, Month.AUGUST, 20);
    private static final LocalDate RANGE_END = LocalDate.of(2035, Month.AUGUST, 26);

    @Test
    public void taskOverlapsRange_deadlineInsideRange_returnsTrue() {
        Task deadline = new Deadlines("submit report",
                LocalDateTime.of(2035, Month.AUGUST, 21, 17, 0));

        assertTrue(Kiki.taskOverlapsRange(deadline, RANGE_START, RANGE_END));
    }

    @Test
    public void taskOverlapsRange_deadlineOutsideRange_returnsFalse() {
        Task deadline = new Deadlines("unrelated task",
                LocalDateTime.of(2035, Month.SEPTEMBER, 1, 9, 0));

        assertFalse(Kiki.taskOverlapsRange(deadline, RANGE_START, RANGE_END));
    }

    @Test
    public void taskOverlapsRange_eventSpanningRangeBoundary_returnsTrue() {
        Task event = new Events("workshop",
                LocalDateTime.of(2035, Month.AUGUST, 19, 9, 0),
                LocalDateTime.of(2035, Month.AUGUST, 23, 18, 0));

        assertTrue(Kiki.taskOverlapsRange(event, RANGE_START, RANGE_END));
    }

    @Test
    public void taskOverlapsRange_eventEntirelyBeforeRange_returnsFalse() {
        Task event = new Events("old workshop",
                LocalDateTime.of(2035, Month.AUGUST, 1, 9, 0),
                LocalDateTime.of(2035, Month.AUGUST, 2, 18, 0));

        assertFalse(Kiki.taskOverlapsRange(event, RANGE_START, RANGE_END));
    }

    @Test
    public void taskOverlapsRange_todo_returnsFalse() {
        Task todo = new ToDos("read book");

        assertFalse(Kiki.taskOverlapsRange(todo, RANGE_START, RANGE_END));
    }

    @Test
    public void getSortKey_deadline_returnsByDateTime() {
        LocalDateTime by = LocalDateTime.of(2035, Month.AUGUST, 21, 17, 0);
        Task deadline = new Deadlines("submit report", by);

        assertEquals(by, Kiki.getSortKey(deadline));
    }

    @Test
    public void getSortKey_event_returnsFromDateTime() {
        LocalDateTime from = LocalDateTime.of(2035, Month.AUGUST, 19, 9, 0);
        Task event = new Events("workshop", from, LocalDateTime.of(2035, Month.AUGUST, 23, 18, 0));

        assertEquals(from, Kiki.getSortKey(event));
    }

    @Test
    public void getSortKey_todo_returnsMaxDateTime() {
        Task todo = new ToDos("read book");

        assertEquals(LocalDateTime.MAX, Kiki.getSortKey(todo));
    }

    @Test
    public void descriptionContainsKeyword_substringMatch_returnsTrue() {
        Task todo = new ToDos("read book");

        assertTrue(Kiki.descriptionContainsKeyword(todo, "book"));
    }

    @Test
    public void descriptionContainsKeyword_differentCase_returnsTrue() {
        Task todo = new ToDos("read book");

        assertTrue(Kiki.descriptionContainsKeyword(todo, "BOOK"));
    }

    @Test
    public void descriptionContainsKeyword_noMatch_returnsFalse() {
        Task todo = new ToDos("read book");

        assertFalse(Kiki.descriptionContainsKeyword(todo, "movie"));
    }

    @Test
    public void getResponse_todoPersistsForNextSession() {
        Path saveFile = tempDirectory.resolve("kiki.txt");
        Kiki firstSession = new Kiki(new Storage(saveFile));

        String addResponse = firstSession.getResponse("todo read book");
        String listResponse = new Kiki(new Storage(saveFile)).getResponse("list");

        assertTrue(addResponse.contains("Added to your list:"));
        assertTrue(listResponse.contains("read book"));
    }

    @Test
    public void getResponse_emptyTodo_showsErrorWithoutCreatingSaveFile() {
        Path saveFile = tempDirectory.resolve("kiki.txt");
        Kiki kiki = new Kiki(new Storage(saveFile));

        String response = kiki.getResponse("todo");

        assertTrue(response.contains("OOPS!!!"));
        assertTrue(response.contains("description of a todo cannot be empty"));
        assertFalse(Files.exists(saveFile));
    }

    @Test
    public void getResponse_aiQuestion_usesReadOnlyAiService() {
        AiService aiService = question -> "Use deadline DESCRIPTION /by TIME.";
        Kiki kiki = new Kiki(new Storage(tempDirectory.resolve("kiki.txt")), aiService);

        String response = kiki.getResponse("@ai how do I add a deadline?");

        assertTrue(response.contains("Use deadline DESCRIPTION /by TIME."));
        assertFalse(Files.exists(tempDirectory.resolve("kiki.txt")));
    }

    @Test
    public void commands_markUnmarkAndDelete_updateTaskList() {
        Kiki kiki = new Kiki(new Storage(tempDirectory.resolve("kiki.txt")));
        kiki.getResponse("todo read book");

        String markResponse = kiki.getResponse("mark 1");
        String unmarkResponse = kiki.getResponse("unmark 1");
        String deleteResponse = kiki.getResponse("delete 1");

        assertTrue(markResponse.contains("Marked as complete:"));
        assertTrue(unmarkResponse.contains("Reopened this task:"));
        assertTrue(deleteResponse.contains("Removed from your list:"));
        assertFalse(kiki.getResponse("list").contains("read book"));
    }

    @Test
    public void commands_findAndCheckDay_filterScheduledTasks() {
        Kiki kiki = new Kiki(new Storage(tempDirectory.resolve("kiki.txt")));
        kiki.getResponse("todo read book");
        kiki.getResponse("deadline submit report /by 2035-03-02 1800");

        String findResponse = kiki.getResponse("find report");
        String dayResponse = kiki.getResponse("check day 2 March 2035");

        assertTrue(findResponse.contains("submit report"));
        assertFalse(findResponse.contains("read book"));
        assertTrue(dayResponse.contains("submit report"));
        assertFalse(dayResponse.contains("read book"));
    }

    @Test
    public void sort_datedTasksComeBeforeTodos() {
        Kiki kiki = new Kiki(new Storage(tempDirectory.resolve("kiki.txt")));
        kiki.getResponse("todo unscheduled task");
        kiki.getResponse("event team meeting /from 2035-03-03 1000 /to 2035-03-03 1100");
        kiki.getResponse("deadline submit report /by 2035-03-02 1800");

        String sortResponse = kiki.getResponse("sort");

        assertTrue(sortResponse.indexOf("submit report") < sortResponse.indexOf("team meeting"));
        assertTrue(sortResponse.indexOf("team meeting") < sortResponse.indexOf("unscheduled task"));
    }

    @Test
    public void priorityCommand_updatesTaskAndPersistsIt() {
        Path saveFile = tempDirectory.resolve("kiki.txt");
        Kiki kiki = new Kiki(new Storage(saveFile));
        kiki.getResponse("todo prepare presentation");

        String response = kiki.getResponse("priority 1 high");

        assertTrue(response.contains("[H] prepare presentation"));
        assertTrue(new Kiki(new Storage(saveFile)).getResponse("list").contains("[H] prepare presentation"));
    }
}
