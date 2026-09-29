package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks the three kinds of task: how each one is written for the user, how each one is
 * written to the save file, and when two of them count as the same task.
 */
public class TaskTest {

    private static final LocalDate OCT_14 = LocalDate.of(2019, 10, 14);
    private static final LocalDate OCT_15 = LocalDate.of(2019, 10, 15);
    private static final LocalDate OCT_16 = LocalDate.of(2019, 10, 16);
    private static final LocalDate OCT_17 = LocalDate.of(2019, 10, 17);

    @Test
    public void toString_newTask_showsItsIconAndAnEmptyBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                new Deadline("return book", OCT_15).toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)",
                new Event("meeting", OCT_15, OCT_16).toString());
    }

    @Test
    public void toString_taskThatIsDone_showsATick() {
        Task todo = new Todo("read book");
        todo.setDone(true);
        assertEquals("[T][x] read book", todo.toString());
    }

    @Test
    public void setDone_backToNotDone_showsAnEmptyBoxAgain() {
        Task todo = new Todo("read book");
        todo.setDone(true);
        todo.setDone(false);
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void isDone_newTask_isFalse() {
        assertFalse(new Todo("read book").isDone());
        assertFalse(new Deadline("return book", OCT_15).isDone());
        assertFalse(new Event("meeting", OCT_15, OCT_16).isDone());
    }

    @Test
    public void toString_eventLastingOneDay_showsTheSameDateTwice() {
        assertEquals("[E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)",
                new Event("standup", OCT_15, OCT_15).toString());
    }

    @Test
    public void toJson_todo_holdsTheFieldsEveryTaskNeeds() {
        assertEquals("  {\n"
                + "    \"type\": \"T\",\n"
                + "    \"done\": false,\n"
                + "    \"description\": \"read book\"\n"
                + "  }", new Todo("read book").toJson());
    }

    @Test
    public void toJson_taskThatIsDone_recordsThat() {
        Task todo = new Todo("read book");
        todo.setDone(true);
        assertTrue(todo.toJson().contains("\"done\": true"));
    }

    @Test
    public void toJson_deadline_addsItsDueDate() {
        assertEquals("  {\n"
                + "    \"type\": \"D\",\n"
                + "    \"done\": false,\n"
                + "    \"description\": \"return book\",\n"
                + "    \"by\": \"2019-10-15\"\n"
                + "  }", new Deadline("return book", OCT_15).toJson());
    }

    @Test
    public void toJson_event_addsBothOfItsDates() {
        assertEquals("  {\n"
                + "    \"type\": \"E\",\n"
                + "    \"done\": false,\n"
                + "    \"description\": \"meeting\",\n"
                + "    \"from\": \"2019-10-15\",\n"
                + "    \"to\": \"2019-10-16\"\n"
                + "  }", new Event("meeting", OCT_15, OCT_16).toJson());
    }

    @Test
    public void toJson_descriptionNeedingEscapes_writesThemEscaped() {
        assertTrue(new Todo("say \"hi\"").toJson().contains("\"description\": \"say \\\"hi\\\"\""));
    }

    @Test
    public void hasDescriptionContaining_textInTheDescription_isTrue() {
        Task todo = new Todo("read book");
        assertTrue(todo.hasDescriptionContaining("read"));
        assertTrue(todo.hasDescriptionContaining("ad bo"));
        assertTrue(todo.hasDescriptionContaining("read book"));
        assertTrue(todo.hasDescriptionContaining(""));
    }

    @Test
    public void hasDescriptionContaining_textThatIsNotThere_isFalse() {
        assertFalse(new Todo("read book").hasDescriptionContaining("milk"));
    }

    @Test
    public void hasDescriptionContaining_textInADifferentCase_isFalse() {
        assertFalse(new Todo("read book").hasDescriptionContaining("Read"));
    }

    @Test
    public void hasDescriptionContaining_textFromTheIconOrDates_isFalse() {
        assertFalse(new Deadline("return book", OCT_15).hasDescriptionContaining("Oct"));
        assertFalse(new Deadline("return book", OCT_15).hasDescriptionContaining("[D]"));
    }

    @Test
    public void isDueBy_cutoffAfterOrOnTheDueDate_isTrue() {
        Deadline deadline = new Deadline("return book", OCT_15);
        assertTrue(deadline.isDueBy(OCT_16));
        assertTrue(deadline.isDueBy(OCT_15));
    }

    @Test
    public void isDueBy_cutoffBeforeTheDueDate_isFalse() {
        assertFalse(new Deadline("return book", OCT_15).isDueBy(OCT_14));
    }

    @Test
    public void isOn_theFirstMiddleAndLastDay_isTrue() {
        Event event = new Event("conference", OCT_15, OCT_17);
        assertTrue(event.isOn(OCT_15));
        assertTrue(event.isOn(OCT_16));
        assertTrue(event.isOn(OCT_17));
    }

    @Test
    public void isOn_aDayOutsideTheEvent_isFalse() {
        Event event = new Event("conference", OCT_15, OCT_17);
        assertFalse(event.isOn(OCT_14));
        assertFalse(event.isOn(LocalDate.of(2019, 10, 18)));
    }

    @Test
    public void isOn_theOnlyDayOfAOneDayEvent_isTrue() {
        assertTrue(new Event("standup", OCT_15, OCT_15).isOn(OCT_15));
    }

    @Test
    public void isDuplicateOf_sameTypeAndDescription_isTrue() {
        assertTrue(new Todo("read book").isDuplicateOf(new Todo("read book")));
        assertTrue(new Deadline("return book", OCT_15)
                .isDuplicateOf(new Deadline("return book", OCT_15)));
        assertTrue(new Event("meeting", OCT_15, OCT_16)
                .isDuplicateOf(new Event("meeting", OCT_15, OCT_16)));
    }

    @Test
    public void isDuplicateOf_itself_isTrue() {
        Task todo = new Todo("read book");
        assertTrue(todo.isDuplicateOf(todo));
    }

    @Test
    public void isDuplicateOf_differentDescription_isFalse() {
        assertFalse(new Todo("read book").isDuplicateOf(new Todo("read books")));
    }

    @Test
    public void isDuplicateOf_descriptionInADifferentCase_isFalse() {
        assertFalse(new Todo("read book").isDuplicateOf(new Todo("Read Book")));
    }

    @Test
    public void isDuplicateOf_differentType_isFalse() {
        assertFalse(new Todo("return book").isDuplicateOf(new Deadline("return book", OCT_15)));
        assertFalse(new Deadline("return book", OCT_15).isDuplicateOf(new Todo("return book")));
    }

    /**
     * Each subclass compares dates the other types do not have, so it has to establish
     * that the other task is of its own type before reaching for them. Asking in every
     * direction catches a subclass that casts before it checks.
     */
    @Test
    public void isDuplicateOf_everyPairOfDifferentTypes_isFalseWithoutFailing() {
        Task todo = new Todo("meeting");
        Task deadline = new Deadline("meeting", OCT_15);
        Task event = new Event("meeting", OCT_15, OCT_16);
        for (Task one : List.of(todo, deadline, event)) {
            for (Task other : List.of(todo, deadline, event)) {
                assertEquals(one == other, one.isDuplicateOf(other),
                        one.getClass().getSimpleName() + " vs " + other.getClass().getSimpleName());
            }
        }
    }

    @Test
    public void isDuplicateOf_deadlinesDueOnDifferentDates_isFalse() {
        assertFalse(new Deadline("return book", OCT_15)
                .isDuplicateOf(new Deadline("return book", OCT_16)));
    }

    @Test
    public void isDuplicateOf_eventsDifferingInOneDate_isFalse() {
        Event event = new Event("meeting", OCT_15, OCT_16);
        assertFalse(event.isDuplicateOf(new Event("meeting", OCT_14, OCT_16)));
        assertFalse(event.isDuplicateOf(new Event("meeting", OCT_15, OCT_17)));
    }

    @Test
    public void isDuplicateOf_oneOfThemIsDone_isStillTrue() {
        Task done = new Todo("read book");
        done.setDone(true);
        assertTrue(done.isDuplicateOf(new Todo("read book")));
        assertTrue(new Todo("read book").isDuplicateOf(done));
    }
}
