package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks the filter command, which has more ways to be typed wrongly than any other:
 * it takes a type, and an option that only some types accept.
 */
public class ParserFilterTest {

    @TempDir
    Path tempDir;

    /** A list holding one task of each type, so a filter's results can be seen. */
    private TaskList threeTasks() {
        return new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 10, 15)),
                new Event("meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16))));
    }

    private String run(String input, TaskList tasks) throws EmmaException {
        return Parser.parse(input).execute(tasks, new Storage(tempDir.resolve("emma.json").toString()));
    }

    /** Returns the complaint made about a line Emma cannot read. */
    private String complaintAbout(String input) {
        return assertThrows(EmmaException.class, () -> Parser.parse(input)).getMessage();
    }

    @Test
    public void parse_filterByEachType_keepsOnlyThatType() throws EmmaException {
        TaskList tasks = threeTasks();
        assertEquals("Here's what matches:\n1. [T][ ] read book",
                run("filter /type todo", tasks));
        assertEquals("Here's what matches:\n2. [D][ ] return book (by: Oct 15 2019)",
                run("filter /type deadline", tasks));
        assertEquals("Here's what matches:\n"
                + "3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)",
                run("filter /type event", tasks));
    }

    @Test
    public void parse_deadlineFilterWithACutoff_keepsOnlyDeadlinesDueByThen() throws EmmaException {
        TaskList tasks = threeTasks();
        assertEquals("Here's what matches:\n2. [D][ ] return book (by: Oct 15 2019)",
                run("filter /type deadline /due-by 2019-10-15", tasks));
        assertEquals("Nothing matches that filter.",
                run("filter /type deadline /due-by 2019-10-14", tasks));
    }

    @Test
    public void parse_eventFilterWithADate_keepsOnlyEventsRunningThen() throws EmmaException {
        TaskList tasks = threeTasks();
        assertEquals("Here's what matches:\n"
                + "3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)",
                run("filter /type event /at 2019-10-16", tasks));
        assertEquals("Nothing matches that filter.",
                run("filter /type event /at 2019-10-17", tasks));
    }

    @Test
    public void parse_filterWithNoType_explainsTheUsage() {
        String usage = "A filter needs a type, like \"filter /type deadline\".";
        assertEquals(usage, complaintAbout("filter"));
        assertEquals(usage, complaintAbout("filter todo"));
        assertEquals(usage, complaintAbout("filter /type"));
    }

    @Test
    public void parse_filterByATypeEmmaDoesNotTrack_isRejected() {
        assertEquals("I can only filter by todo, deadline or event.",
                complaintAbout("filter /type homework"));
        assertEquals("I can only filter by todo, deadline or event.",
                complaintAbout("filter /type Todo"));
    }

    @Test
    public void parse_dueByOnATypeThatIsNotADeadline_saysWhoseOptionItIs() {
        assertEquals("Only a deadline filter takes \"/due-by\".",
                complaintAbout("filter /type todo /due-by 2019-10-15"));
        assertEquals("Only a deadline filter takes \"/due-by\".",
                complaintAbout("filter /type event /due-by 2019-10-15"));
    }

    @Test
    public void parse_atOnATypeThatIsNotAnEvent_saysWhoseOptionItIs() {
        assertEquals("Only an event filter takes \"/at\".",
                complaintAbout("filter /type todo /at 2019-10-15"));
        assertEquals("Only an event filter takes \"/at\".",
                complaintAbout("filter /type deadline /at 2019-10-15"));
    }

    @Test
    public void parse_optionEmmaDoesNotKnow_isRejected() {
        assertEquals("I don't know what \"/nonsense\" means in a filter.",
                complaintAbout("filter /type todo /nonsense"));
        assertEquals("I don't know what \"/before\" means in a filter.",
                complaintAbout("filter /type deadline /before 2019-10-15"));
    }

    @Test
    public void parse_optionWithNoDateAfterIt_saysWhatItExpected() {
        assertEquals("I need a cutoff date as a date like 2019-10-15, but I got \"\".",
                complaintAbout("filter /type deadline /due-by"));
        assertEquals("I need an event date as a date like 2019-10-15, but I got \"\".",
                complaintAbout("filter /type event /at"));
    }

    @Test
    public void parse_optionFollowedBySomethingThatIsNotADate_isRejected() {
        assertEquals("I need a cutoff date as a date like 2019-10-15, but I got \"Sunday\".",
                complaintAbout("filter /type deadline /due-by Sunday"));
        assertEquals("I need an event date as a date like 2019-10-15, but I got \"2019-02-30\".",
                complaintAbout("filter /type event /at 2019-02-30"));
    }

    @Test
    public void parse_filterMatchingNothing_saysSoRatherThanShowingAnEmptyList() throws EmmaException {
        assertEquals("Nothing matches that filter.",
                run("filter /type todo", new TaskList(List.of())));
    }
}
