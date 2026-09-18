package com.fireinyu.themyth.chatmodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fireinyu.themyth.requests.AtRequest;
import com.fireinyu.themyth.requests.DeadlineRequest;
import com.fireinyu.themyth.requests.DeleteRequest;
import com.fireinyu.themyth.requests.DueRequest;
import com.fireinyu.themyth.requests.EventRequest;
import com.fireinyu.themyth.requests.FindRequest;
import com.fireinyu.themyth.requests.ListRequest;
import com.fireinyu.themyth.requests.MarkRequest;
import com.fireinyu.themyth.requests.TodoRequest;
import com.fireinyu.themyth.requests.UnmarkRequest;
import com.fireinyu.themyth.requests.events.CloseRequest;
import com.fireinyu.themyth.requests.events.InitRequest;
import com.fireinyu.themyth.responses.Response;

/**
 * Unit tests for {@link TaskMode}.
 */
public class TaskModeTest {

    @TempDir
    Path tempDir;

    /**
     * Tests default constructor instantiation.
     */
    @Test
    public void defaultConstructor_instantiatesSuccessfully() {
        TaskMode mode = new TaskMode();
        assertNotNull(mode.getTaskList());
    }

    /**
     * Tests full lifecycle of tasks management: adding, listing, filtering, marking, unmarking, and deleting.
     */
    @Test
    public void taskMode_lifecycleAndCommands() {
        Path taskFile = tempDir.resolve("tasks.csv");
        TaskMode mode = new TaskMode(taskFile);

        Response initResponse = mode.respondTo(new InitRequest());
        assertEquals("The archives are unlocked and looking drop-dead gorgeous! All tasks loaded, honey! 💅✨",
                initResponse.getBody());

        // 1. Add Todo
        Response todoRes = mode.respondTo(new TodoRequest(List.of("todo", "read textbook"), Map.of()));
        assertTrue(todoRes.getBody().contains("read textbook"));
        assertTrue(todoRes.getBody().contains("1 tasks, superstar!"));

        // 2. Add Deadline
        Response deadlineRes = mode.respondTo(new DeadlineRequest(
                List.of("deadline", "submit homework"),
                Map.of("by", "2025-10-15-18-00-00")
        ));
        assertTrue(deadlineRes.getBody().contains("submit homework"));
        assertTrue(deadlineRes.getBody().contains("2 tasks, superstar!"));

        // 3. Add Event
        Response eventRes = mode.respondTo(new EventRequest(
                List.of("event", "orientation camp"),
                Map.of("from", "2025-06-01-09-00-00", "to", "2025-06-01-17-00-00")
        ));
        assertTrue(eventRes.getBody().contains("orientation camp"));
        assertTrue(eventRes.getBody().contains("3 tasks, superstar!"));

        // 4. List tasks
        Response listRes = mode.respondTo(new ListRequest(List.of("list"), Map.of("sort", "modified")));
        assertTrue(listRes.getBody().contains("Feast your eyes, darling! Here is your glamorous itinerary:"));
        assertTrue(listRes.getBody().contains("1. [T][ ] read textbook"));
        assertTrue(listRes.getBody().contains("2. [D][ ] submit homework"));
        assertTrue(listRes.getBody().contains("3. [E][ ] orientation camp"));

        // 5. Find tasks
        Response findRes = mode.respondTo(new FindRequest(List.of("find", "textbook"), Map.of()));
        assertTrue(findRes.getBody().contains(
                "Found them! These matching gems were practically begging for my spotlight:"));
        assertTrue(findRes.getBody().contains("1. [T][ ] read textbook"));

        // 6. At filter (for event)
        Response atRes = mode.respondTo(new AtRequest(
                List.of("at", "2025-06-01-12-00-00"),
                Map.of()
        ));
        assertTrue(atRes.getBody().contains("Clear the runway! Here is the fabulous drama scheduled on"));
        assertTrue(atRes.getBody().contains("orientation camp"));

        // 7. Due filter (for deadline)
        Response dueRes = mode.respondTo(new DueRequest(
                List.of("due", "2025-10-16-00-00-00"),
                Map.of()
        ));
        assertTrue(dueRes.getBody().contains("Tick-tock, gorgeous! Here are the deadlines looming over you by"));
        assertTrue(dueRes.getBody().contains("submit homework"));

        // 8. Mark task 1
        Response markRes = mode.respondTo(new MarkRequest(List.of("mark", "1"), Map.of()));
        assertTrue(markRes.getBody().contains(
                "Slay, honey! Slay! That task is officially conquered and looking iconic:"));
        assertTrue(markRes.getBody().contains("[T][X] read textbook"));
        assertEquals(Response.Mood.HAPPY, markRes.getMood());

        // 9. Unmark task 1
        Response unmarkRes = mode.respondTo(new UnmarkRequest(List.of("unmark", "1"), Map.of()));
        assertTrue(unmarkRes.getBody().contains("Ugh, backpedaling? Fine, darling. Marked it as not done yet:"));
        assertTrue(unmarkRes.getBody().contains("[T][ ] read textbook"));
        assertEquals(Response.Mood.NORMAL, unmarkRes.getMood());

        // 10. Delete task 1
        Response deleteRes = mode.respondTo(new DeleteRequest(List.of("delete", "1"), Map.of()));
        assertTrue(deleteRes.getBody().contains(
                "Trash it, burn it, make room for fabulous! I kicked this task to the curb:"));
        assertTrue(deleteRes.getBody().contains("2 tasks, babe. Keep it chic! 💅"));

        // 11. Close mode
        Response closeResponse = mode.respondTo(new CloseRequest());
        assertEquals("Curtain down, beauties! But someone forgot my grand finale encore! 💅",
                closeResponse.getBody());
    }
}
