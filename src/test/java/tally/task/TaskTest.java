package tally.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/** Tests how each kind of task shows itself to the user and writes itself to the file. */
public class TaskTest {
    @Test
    public void isSameAs_theSameThingRecordedTwice_isRecognized() {
        Task done = new Todo("read book");
        done.markAsDone();
        // Marking a task done does not turn it into a different task, so adding it
        // again would still be adding the same thing twice.
        assertTrue(new Todo("read book").isSameAs(done));
        assertTrue(new Deadline("essay", LocalDate.of(2026, 9, 10))
                .isSameAs(new Deadline("essay", LocalDate.of(2026, 9, 10))));
    }

    @Test
    public void isSameAs_tasksDifferingInAnyPart_areNotTheSame() {
        assertFalse(new Todo("read book").isSameAs(new Todo("buy bread")));
        // Same words, different kind of task.
        assertFalse(new Todo("essay").isSameAs(new Deadline("essay", LocalDate.of(2026, 9, 10))));
        assertFalse(new Deadline("essay", LocalDate.of(2026, 9, 10))
                .isSameAs(new Deadline("essay", LocalDate.of(2026, 9, 11))));
        assertFalse(new Event("party", at("2026-09-08"), at("2026-09-08"))
                .isSameAs(new Event("party", at("2026-09-08"), at("2026-09-09"))));
        // Same days, one of them carrying an hour the other does not.
        assertFalse(new Event("party", at("2026-09-08"), at("2026-09-08"))
                .isSameAs(new Event("party", at("2026-09-08 16:00"), at("2026-09-08"))));
    }

    @Test
    public void isSameAs_oneMomentWrittenTwoWays_isOneEvent() {
        // A start written as a bare day and one written as that day at midnight are two
        // lines naming one moment, so the events over them are one event. Told apart by
        // the saved line alone, the second was recorded alongside the first.
        assertTrue(new Event("party", at("2026-09-08"), at("2026-09-09"))
                .isSameAs(new Event("party", at("2026-09-08 00:00"), at("2026-09-09"))));
        // An end is a different matter: without an hour it is the close of its day,
        // where midnight is the open of it, so these two are not the same event.
        assertFalse(new Event("party", at("2026-09-08"), at("2026-09-09"))
                .isSameAs(new Event("party", at("2026-09-08"), at("2026-09-09 00:00"))));
        // A task of another kind is never the same thing, whatever its line says.
        assertFalse(new Event("party", at("2026-09-08"), at("2026-09-09"))
                .isSameAs(new Todo("party")));
    }

    @Test
    public void constructor_windowEndingBeforeItStarts_isRefused() {
        // The class documents that the end is never before the start. The parser and the
        // storage reader both enforce it, so one arriving here came from neither.
        assertThrows(AssertionError.class, () ->
                new Window("backwards", LocalDate.of(2026, 9, 12), LocalDate.of(2026, 9, 8)));
    }

    @Test
    public void constructor_eventEndingBeforeItStarts_isRefused() {
        // This used to be accepted and quietly read as the same stretch of days, shown
        // to the user back to front. The parser and the storage reader both refuse it
        // now, for the same reason a window that ends before it starts is refused, so
        // one arriving here came from neither.
        assertThrows(AssertionError.class, () ->
                new Event("trip", at("2026-09-10"), at("2026-09-08")));
        // Within one day the hours decide, which a pair of bare dates could not show.
        assertThrows(AssertionError.class, () ->
                new Event("meeting", at("2026-09-10 18:00"), at("2026-09-10 16:00")));
    }

    @Test
    public void constructor_anEndWithNoHour_runsToTheCloseOfItsDay() {
        // "From the 10th at four to the 10th" is a half-day event, not a contradiction:
        // an end given without an hour is the whole of its day.
        assertDoesNotThrow(() -> new Event("trip", at("2026-09-10 16:00"), at("2026-09-10")));
        // A start given without an hour begins when its day does, so this is not
        // backwards either.
        assertDoesNotThrow(() -> new Event("trip", at("2026-09-10"), at("2026-09-10 09:00")));
    }

    @Test
    public void occupies_event_coversTheDaysBetweenItsEnds() {
        Event trip = new Event("trip", at("2026-09-08"), at("2026-09-10"));
        assertFalse(trip.occupies(LocalDate.of(2026, 9, 7)));
        assertTrue(trip.occupies(LocalDate.of(2026, 9, 8)));
        assertTrue(trip.occupies(LocalDate.of(2026, 9, 10)));
        assertFalse(trip.occupies(LocalDate.of(2026, 9, 11)));
    }

    @Test
    public void occupies_anEndAtMidnight_leavesThatDayFree() {
        // There is no 24:00 to write, so 00:00 on the next day is the only way to say
        // "until midnight". Counting that day as taken would cost a free day for no
        // minutes at all.
        Event redEye = new Event("red-eye", at("2026-09-09 22:00"), at("2026-09-10 00:00"));
        assertTrue(redEye.occupies(LocalDate.of(2026, 9, 9)));
        assertFalse(redEye.occupies(LocalDate.of(2026, 9, 10)));
        // A minute past midnight is a moment of the day, so the day is taken.
        Event justOver = new Event("just over", at("2026-09-09 22:00"), at("2026-09-10 00:01"));
        assertTrue(justOver.occupies(LocalDate.of(2026, 9, 10)));
        // An event with nowhere else to fall keeps the day it names.
        Event stroke = new Event("stroke", at("2026-09-09 00:00"), at("2026-09-09 00:00"));
        assertTrue(stroke.occupies(LocalDate.of(2026, 9, 9)));
    }

    @Test
    public void constructor_anHourFinerThanAMinute_isKeptToTheMinute() {
        // Nothing typed or saved carries seconds. One that survived would be written to
        // the file as "16:04:33", which the reader refuses, so the task would come back
        // as damage on the next start.
        Moment fine = new Moment(LocalDate.of(2026, 9, 12),
                Optional.of(LocalTime.of(16, 4, 33)));
        assertEquals("2026-09-12 16:04", fine.toSaveFormat());
        assertEquals(Optional.of(fine), Moment.read(fine.toSaveFormat()));
    }

    @Test
    public void occupies_endsThousandsOfYearsApart_costsNothingToAsk() {
        // Both ends are ordinary dates, so the date form does not refuse them: listing
        // the 3.65 million days between them is what used to exhaust the heap. Asking
        // about one day has to stay cheap, which only holds if nothing is built.
        Event doom = new Event("doom", at("0001-01-01"), at("9999-12-31"));

        // Asking a thousand times is the point: each answer has to cost nothing. Building
        // the three and a half million days between these ends even once takes a third of
        // a second, so a thousand answers could not be given inside this bound.
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            for (int time = 0; time < 1000; time++) {
                assertTrue(doom.occupies(LocalDate.of(2026, 9, 8)));
                assertFalse(doom.occupies(LocalDate.of(10000, 1, 1)));
            }
        });
    }

    @Test
    public void toString_window_showsBothEndsInTheDisplayFormat() {
        Window window = new Window("submit form",
                LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 12));
        assertEquals("[W][ ] submit form (window: Sep 08 2026 to Sep 12 2026)",
                window.toString());
    }

    @Test
    public void toSaveFormat_window_writesBothDatesInTheFormatItReadsBack() {
        Window window = new Window("submit form",
                LocalDate.of(2026, 9, 8), LocalDate.of(2026, 9, 12));
        assertEquals("W | 0 | submit form | 2026-09-08 | 2026-09-12", window.toSaveFormat());
    }

    @Test
    public void toString_todo_tagsTypeAndCheckbox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_afterMarking_showsTheCross() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_deadline_showsDateInTheDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        // Level-8 asks that the date be shown in a different format from the one typed.
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_event_showsBothEndsInTheDisplayFormat() {
        Event event = new Event("project meeting", at("2019-08-06"), at("2019-08-07"));
        // Level-8 asks that a date be shown in a different format from the one typed.
        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)",
                event.toString());
    }

    @Test
    public void toString_eventCarryingHours_showsThemOnAClockFace() {
        // 16:00 is typed and 4:00 pm is shown, for the same reason the date is.
        Event lecture = new Event("lecture", at("2026-09-12 16:00"), at("2026-09-12 18:00"));
        assertEquals("[E][ ] lecture (from: Sep 12 2026 4:00 pm to: Sep 12 2026 6:00 pm)",
                lecture.toString());
        // Midday and midnight are the two a twelve-hour clock most often gets wrong.
        Event vigil = new Event("vigil", at("2026-09-12 00:00"), at("2026-09-12 12:00"));
        assertEquals("[E][ ] vigil (from: Sep 12 2026 12:00 am to: Sep 12 2026 12:00 pm)",
                vigil.toString());
    }

    @Test
    public void toString_eventWithAnHourAtOneEndOnly_showsTheOtherAsADay() {
        Event trip = new Event("trip", at("2026-09-12 09:30"), at("2026-09-14"));
        assertEquals("[E][ ] trip (from: Sep 12 2026 9:30 am to: Sep 14 2026)",
                trip.toString());
    }

    @Test
    public void toSaveFormat_todo_writesTypeAndDoneFlag() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toSaveFormat());
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_deadline_writesDateInTheFormatItIsReadBackFrom() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        // The file must keep the yyyy-mm-dd form, not the displayed one, or the
        // deadline cannot be read back on the next run.
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_event_writesBothEndsInTheFormTheyAreReadBackFrom() {
        assertEquals("E | 0 | project meeting | 2019-08-06 | 2019-08-07",
                new Event("project meeting", at("2019-08-06"), at("2019-08-07")).toSaveFormat());
        // The file keeps the 24-hour form that was typed, not the clock face shown, or
        // the event cannot be read back on the next run.
        assertEquals("E | 0 | lecture | 2026-09-12 16:00 | 2026-09-12 18:00",
                new Event("lecture", at("2026-09-12 16:00"), at("2026-09-12 18:00"))
                        .toSaveFormat());
    }

    @Test
    public void getStatusIcon_doneAndNotDone_showsCrossOrSpace() {
        Todo todo = new Todo("read book");
        assertEquals(" ", todo.getStatusIcon());
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void occupies_aTaskNamingNoDays_takesUpNone() {
        // A todo is owed whenever; it does not stand between the user and a free day.
        Task todo = new Todo("read book");
        assertFalse(todo.occupies(LocalDate.of(2026, 9, 9)));
    }

    @Test
    public void readDate_paddedByAHandEditedFile_readsTheDayItNames() {
        // The data file is meant to be corrected by hand, and a space either side of a
        // date is the most ordinary thing such an edit leaves behind. Reading the text
        // as it came left that date unreadable, which is how a damaged file is reported.
        assertEquals(LocalDate.of(2026, 9, 8), Task.readDate("  2026-09-08 ").orElseThrow());
    }

    @Test
    public void readDate_theShapeAloneIsNotEnough_needsTheDayToExist() {
        // A date read here has already been shown to the user as a day, so a month with
        // thirty days cannot be allowed to have a thirty-first.
        assertTrue(Task.readDate("2026-02-30").isEmpty());
        assertTrue(Task.readDate("2026-13-45").isEmpty());
        assertTrue(Task.readDate("2026-9-1").isEmpty());
        assertTrue(Task.readDate("Mon 2pm").isEmpty());
    }

    /**
     * Returns the moment some text names, failing the test if it names none.
     *
     * @param text a date, optionally followed by a time, as a user would write it.
     * @return the moment it names.
     */
    private static Moment at(String text) {
        return Moment.read(text).orElseThrow(() ->
                new AssertionError("the test wrote a moment Tally cannot read: " + text));
    }
}
