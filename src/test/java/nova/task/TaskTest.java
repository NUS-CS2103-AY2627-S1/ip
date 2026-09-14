package nova.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {

    private static final LocalDateTime DEC_2_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);
    private static final LocalDateTime AUG_6_2PM = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime AUG_8_4PM = LocalDateTime.of(2019, 8, 8, 16, 0);

    @Test
    public void toString_newTodo_showsTypeAndEmptyStatusBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_doneTodo_showsCross() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void markAsNotDone_previouslyDone_statusCleared() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_deadline_showsFormattedDueDate() {
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)",
                new Deadline("return book", DEC_2_6PM).toString());
    }

    @Test
    public void toString_event_showsFormattedRange() {
        assertEquals("[E][ ] trip (from: Aug 06 2019, 2:00pm to: Aug 08 2019, 4:00pm)",
                new Event("trip", AUG_6_2PM, AUG_8_4PM).toString());
    }

    @Test
    public void toFileString_todo_hasTypeFlagAndDescription() {
        assertEquals("T | 0 | read book", new Todo("read book").toFileString());
    }

    @Test
    public void toFileString_doneTodo_flagIsOne() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toFileString());
    }

    @Test
    public void toFileString_deadline_dateStoredAsIsoText() {
        assertEquals("D | 0 | return book | 2019-12-02T18:00:00",
                new Deadline("return book", DEC_2_6PM).toFileString());
    }

    @Test
    public void toFileString_event_bothDatesStoredAsIsoText() {
        assertEquals("E | 0 | trip | 2019-08-06T14:00:00 | 2019-08-08T16:00:00",
                new Event("trip", AUG_6_2PM, AUG_8_4PM).toFileString());
    }

    @Test
    public void hasKeyword_keywordInDescription_true() {
        Todo todo = new Todo("read book");

        assertTrue(todo.hasKeyword("book"));
        assertTrue(todo.hasKeyword("BOOK"));
        assertTrue(todo.hasKeyword("read book"));
    }

    @Test
    public void hasKeyword_keywordAbsent_false() {
        assertFalse(new Todo("read book").hasKeyword("bicycle"));
    }

    @Test
    public void hasKeyword_matchesDescriptionNotTheDate_false() {
        assertFalse(new Deadline("return book", DEC_2_6PM).hasKeyword("Dec"));
    }

    @Test
    public void getScheduledTime_todo_empty() {
        assertTrue(new Todo("read book").getScheduledTime().isEmpty());
    }

    @Test
    public void getScheduledTime_deadline_isTheDueMoment() {
        assertEquals(DEC_2_6PM, new Deadline("return book", DEC_2_6PM).getScheduledTime().orElseThrow());
    }

    @Test
    public void getScheduledTime_event_isTheStartMoment() {
        assertEquals(AUG_6_2PM, new Event("trip", AUG_6_2PM, AUG_8_4PM).getScheduledTime().orElseThrow());
    }

    @Test
    public void describesSameTaskAs_sameDescriptionDifferentCase_true() {
        assertTrue(new Todo("read book").describesSameTaskAs(new Todo("Read Book")));
    }

    @Test
    public void describesSameTaskAs_doneStateIgnored() {
        Todo done = new Todo("read book");
        done.markAsDone();

        assertTrue(done.describesSameTaskAs(new Todo("read book")));
    }

    @Test
    public void describesSameTaskAs_differentTaskTypes_false() {
        assertFalse(new Todo("return book").describesSameTaskAs(new Deadline("return book", DEC_2_6PM)));
    }

    @Test
    public void describesSameTaskAs_sameDescriptionDifferentDates_false() {
        Deadline early = new Deadline("return book", AUG_6_2PM);
        Deadline late = new Deadline("return book", DEC_2_6PM);

        assertFalse(early.describesSameTaskAs(late));
    }

    @Test
    public void describesSameTaskAs_eventsDifferingOnlyInEnd_false() {
        Event shorter = new Event("trip", AUG_6_2PM, AUG_6_2PM);
        Event longer = new Event("trip", AUG_6_2PM, AUG_8_4PM);

        assertFalse(shorter.describesSameTaskAs(longer));
    }

    @Test
    public void isOn_todo_alwaysFalse() {
        assertFalse(new Todo("read book").isOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void isOn_deadlineOnThatDay_true() {
        assertTrue(new Deadline("return book", DEC_2_6PM).isOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void isOn_deadlineOnAnotherDay_false() {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);

        assertFalse(deadline.isOn(LocalDate.of(2019, 12, 1)));
        assertFalse(deadline.isOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void isOn_dateInsideEventSpan_true() {
        Event event = new Event("trip", AUG_6_2PM, AUG_8_4PM);

        assertTrue(event.isOn(LocalDate.of(2019, 8, 6)));
        assertTrue(event.isOn(LocalDate.of(2019, 8, 7)));
        assertTrue(event.isOn(LocalDate.of(2019, 8, 8)));
    }

    @Test
    public void isOn_dateOutsideEventSpan_false() {
        Event event = new Event("trip", AUG_6_2PM, AUG_8_4PM);

        assertFalse(event.isOn(LocalDate.of(2019, 8, 5)));
        assertFalse(event.isOn(LocalDate.of(2019, 8, 9)));
    }
}
