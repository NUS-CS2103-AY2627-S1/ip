package tally.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * Tests the one line a task is written as, read back.
 *
 * <p>Asked of TaskLine directly rather than through Storage, since none of this needs
 * a file: these are questions about the format, and a line that does not describe a
 * task is answered with an empty Optional however it reached us.
 */
public class TaskLineTest {
    @Test
    public void read_aLineOfEachKind_readsThemBackAsThemselves() {
        assertEquals("[T][ ] read book", TaskLine.read("T | 0 | read book").orElseThrow().toString());
        assertEquals("[D][X] return book (by: Oct 15 2019)",
                TaskLine.read("D | 1 | return book | 2019-10-15").orElseThrow().toString());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)",
                TaskLine.read("E | 0 | project meeting | 2019-08-06 | 2019-08-07")
                        .orElseThrow().toString());
        assertEquals("[W][ ] submit form (window: Sep 08 2026 to Sep 12 2026)",
                TaskLine.read("W | 0 | submit form | 2026-09-08 | 2026-09-12").orElseThrow().toString());
    }

    @Test
    public void read_theDoneFlag_decidesWhetherTheTaskIsDone() {
        assertFalse(TaskLine.read("T | 0 | read book").orElseThrow().isDone());
        assertTrue(TaskLine.read("T | 1 | read book").orElseThrow().isDone());
        // Anything else in that field is not a flag at all.
        assertTrue(TaskLine.read("T | 2 | read book").isEmpty());
        assertTrue(TaskLine.read("T | yes | read book").isEmpty());
    }

    @Test
    public void read_aTypeLetterNobodyWrites_isNoTask() {
        assertTrue(TaskLine.read("Q | 0 | whatever this is").isEmpty());
        assertTrue(TaskLine.read("| 0 | no letter at all").isEmpty());
        assertTrue(TaskLine.read("not a task line").isEmpty());
        assertTrue(TaskLine.read("").isEmpty());
    }

    @Test
    public void read_theWrongNumberOfFields_isNoTask() {
        assertTrue(TaskLine.read("T | 0 | read book | extra").isEmpty());
        assertTrue(TaskLine.read("D | 0 | return book").isEmpty());
        assertTrue(TaskLine.read("E | 0 | meeting | 2026-09-08").isEmpty());
        assertTrue(TaskLine.read("W | 0 | form | 2026-09-08").isEmpty());
    }

    @Test
    public void read_aBlankFieldWhereOneIsRequired_isNoTask() {
        assertTrue(TaskLine.read("T |   | read book").isEmpty());
        assertTrue(TaskLine.read("T | 0 |   ").isEmpty());
        assertTrue(TaskLine.read("D | 0 |   | 2019-10-15").isEmpty());
    }

    @Test
    public void read_datesThatCannotBeRead_isNoTask() {
        assertTrue(TaskLine.read("D | 0 | return book | last Tuesday").isEmpty());
        assertTrue(TaskLine.read("W | 0 | submit form | last Tuesday | 2026-09-12").isEmpty());
        assertTrue(TaskLine.read("W | 0 | submit form | 2026-09-08 | whenever").isEmpty());
        // Nobody types February 30th, and LocalDate will not read it either.
        assertTrue(TaskLine.read("D | 0 | return book | 2026-02-30").isEmpty());
    }

    @Test
    public void read_endsThatRunBackwards_isNoTask() {
        // The parser refuses both of these, so a file holding one was edited by hand.
        assertTrue(TaskLine.read("W | 0 | submit form | 2026-09-12 | 2026-09-08").isEmpty());
        assertTrue(TaskLine.read("E | 0 | trip | 2026-09-12 | 2026-09-08").isEmpty());
        // Padded the same way, since the ends are tidied before they are read.
        assertTrue(TaskLine.read("E | 0 | trip |  2026-09-12 | 2026-09-08").isEmpty());
    }

    @Test
    public void read_aTaskItRead_writesBackTheSameLine() {
        for (String line : new String[] {
            "T | 1 | read book",
            "D | 0 | return book | 2019-10-15",
            "E | 1 | project meeting | 2019-08-06 | 2019-08-07",
            "W | 0 | submit form | 2026-09-08 | 2026-09-12",
        }) {
            assertEquals(line, TaskLine.read(line).orElseThrow().toSaveFormat(), line);
        }
    }

    @Test
    public void read_anEventEndCarryingATime_readsItBack() {
        assertEquals("[E][ ] lecture (from: Sep 12 2026 4:00 pm to: Sep 12 2026 6:00 pm)",
                TaskLine.read("E | 0 | lecture | 2026-09-12 16:00 | 2026-09-12 18:00")
                        .orElseThrow().toString());
        // An event saved before an end could carry a time is still read.
        assertEquals("[E][ ] trip (from: Sep 12 2026 to: Sep 14 2026)",
                TaskLine.read("E | 0 | trip | 2026-09-12 | 2026-09-14")
                        .orElseThrow().toString());
    }

    @Test
    public void read_anEventWhoseTimesRunBackwardsWithinADay_isNoTask() {
        // Refused at the keyboard, so a file holding one was edited by hand.
        assertTrue(TaskLine.read("E | 0 | meeting | 2026-09-12 18:00 | 2026-09-12 16:00")
                .isEmpty());
        assertTrue(TaskLine.read("E | 0 | meeting | 2026-09-12 | 2026-09-12 24:00").isEmpty());
    }

    @Test
    public void read_anEventEndThatIsNotADate_isNoTask() {
        // Never accepted at the keyboard, so a file holding one was edited by hand, or
        // written by a version of Tally that took an event's ends as free text. Either
        // way the line is reported and set aside rather than loaded.
        assertTrue(TaskLine.read("E | 0 | trip | 2026-02-30 | 2026-03-05").isEmpty());
        assertTrue(TaskLine.read("E | 0 | trip | 2026-09-08 | 2026-13-45").isEmpty());
        assertTrue(TaskLine.read("E | 0 | standup | Mon 2pm | 4pm").isEmpty());
    }
}
