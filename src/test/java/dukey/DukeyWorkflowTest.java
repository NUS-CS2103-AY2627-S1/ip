package dukey;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests end-to-end command workflows through the Dukey application logic.
 */
public class DukeyWorkflowTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-14T10:15:30Z"), ZoneId.of("Asia/Singapore"));

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addListAndFindWorkflow_updatesTaskListAndStorage() throws IOException {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Dukey dukey = new Dukey(dataFile.toString(), false, FIXED_CLOCK);

        String todoResponse = dukey.getResponse("todo read book");
        String deadlineResponse = dukey.getResponse("deadline return book /by 2099-12-06 1800");
        String eventResponse = dukey.getResponse("event project meeting /from 2099-12-06 1400 /to 2099-12-06 1600");
        String listResponse = dukey.getResponse("list");
        String findResponse = dukey.getResponse("find book");

        assertEquals("Filed neatly. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", todoResponse);
        assertEquals("Filed neatly. I've added this task:\n"
                + "  [D][ ] return book (by: Dec 06 2099, 6:00pm)\n"
                + "Now you have 2 tasks in the list.", deadlineResponse);
        assertEquals("Filed neatly. I've added this task:\n"
                + "  [E][ ] project meeting (from: Dec 06 2099, 2:00pm to: Dec 06 2099, 4:00pm)\n"
                + "Now you have 3 tasks in the list.", eventResponse);
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Dec 06 2099, 6:00pm)\n"
                + "3.[E][ ] project meeting (from: Dec 06 2099, 2:00pm to: Dec 06 2099, 4:00pm)",
                listResponse);
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Dec 06 2099, 6:00pm)", findResponse);
        assertEquals(String.join(System.lineSeparator(),
                "T | 0 | read book |",
                "D | 0 | return book | 2099-12-06 1800 |",
                "E | 0 | project meeting | 2099-12-06 1400 | 2099-12-06 1600 |",
                ""), Files.readString(dataFile));
    }

    @Test
    public void getResponse_markUnmarkDeleteWorkflow_updatesTaskStatusesAndStorage() throws IOException {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "T | 0 | borrow book",
                ""));
        Dukey dukey = new Dukey(dataFile.toString(), false, FIXED_CLOCK);

        String markResponse = dukey.getResponse("mark 1");
        String unmarkResponse = dukey.getResponse("unmark 1");
        String deleteResponse = dukey.getResponse("delete 2");

        assertEquals("Stamped and settled. I've marked this task as done:\n"
                + "  [T][X] read book", markResponse);
        assertEquals("Back on the shelf. I've marked this task as not done yet:\n"
                + "  [T][ ] read book", unmarkResponse);
        assertEquals("Removed from the shelf. I've removed this task:\n"
                + "  [T][ ] borrow book\n"
                + "Now you have 1 tasks in the list.", deleteResponse);
        assertEquals("T | 0 | read book |" + System.lineSeparator(), Files.readString(dataFile));
    }

    @Test
    public void getResponse_onCommandWithMixedTasks_returnsDeadlinesAndEventsOnly() throws IOException {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "D | 0 | return book | 2099-12-06 1800",
                "E | 0 | project meeting | 2099-12-06 1400 | 2099-12-07 1600",
                ""));
        Dukey dukey = new Dukey(dataFile.toString(), false, FIXED_CLOCK);

        String response = dukey.getResponse("on 2099-12-07");

        assertEquals("Here are the deadlines and events on that date:\n"
                + "3.[E][ ] project meeting (from: Dec 06 2099, 2:00pm to: Dec 07 2099, 4:00pm)",
                response);
    }
}
