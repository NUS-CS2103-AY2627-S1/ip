package baby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void getTodoDescription_validInput_returnDescription() {
        String result = Parser.getTodoDescription("todo read book");

        assertEquals("read book", result);
    }

    @Test
    public void getTodoDescription_extraSpaces_returnsTrimmedDescription() {
        String result = Parser.getTodoDescription("todo    read book   ");

        assertEquals("read book", result);
    }

    @Test
    public void normalizeInput_repeatedWhitespace_returnsSingleSpaces() {
        assertEquals("deadline return book /by 2026-08-30 1200",
                Parser.normalizeInput("  deadline   return book   /by   2026-08-30 1200  "));
    }

    @Test
    public void createDeadline_duplicatedBy_returnsNullWithError() {
        Ui ui = new Ui(false);

        Deadline result = Parser.createDeadline(
                "deadline return book /by 2026-08-30 1200 /by 2026-09-01 1200", ui);

        assertNull(result);
        assertTrue(ui.getLastResponse().contains("only have one /by"));
    }

    @Test
    public void createDeadline_impossibleDate_returnsNullWithError() {
        Ui ui = new Ui(false);

        Deadline result = Parser.createDeadline("deadline return book /by 2026-02-30 1200", ui);

        assertNull(result);
        assertTrue(ui.getLastResponse().contains("yyyy-MM-dd HHmm"));
    }

    @Test
    public void createEvent_duplicatedFrom_returnsNullWithError() {
        Ui ui = new Ui(false);

        Event result = Parser.createEvent("event meeting /from 2026-08-30 1200 "
                + "/from 2026-08-30 1300 /to 2026-08-30 1400", ui);

        assertNull(result);
        assertTrue(ui.getLastResponse().contains("only have one /from"));
    }

    @Test
    public void createEvent_duplicatedTo_returnsNullWithError() {
        Ui ui = new Ui(false);

        Event result = Parser.createEvent("event meeting /from 2026-08-30 1000 "
                + "/to 2026-08-30 1100 /to 2026-08-30 1200", ui);

        assertNull(result);
        assertTrue(ui.getLastResponse().contains("only have one /to"));
    }

    @Test
    public void createEvent_endBeforeStart_returnsNullWithError() {
        Ui ui = new Ui(false);

        Event result = Parser.createEvent(
                "event meeting /from 2026-08-30 1400 /to 2026-08-30 1200", ui);

        assertNull(result);
        assertTrue(ui.getLastResponse().contains("end time must be after"));
    }

    @Test
    public void getTaskIndex_outOfBounds_returnsInvalidIndexWithError() {
        Ui ui = new Ui(false);

        int result = Parser.getTaskIndex("mark 3", "mark", 2, ui);

        assertEquals(-1, result);
        assertTrue(ui.getLastResponse().contains("not in your list"));
    }
}
