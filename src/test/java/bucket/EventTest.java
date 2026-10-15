package bucket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class EventTest {

    private final Locale originalLocale = Locale.getDefault();

    @AfterEach
    public void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    private Event makeEvent() {
        return new Event("project meeting",
                LocalDate.of(2019, 10, 15),
                LocalDate.of(2019, 10, 16));
    }

    @Test
    public void getTypeIcon_always_isE() {
        assertEquals("E", makeEvent().getTypeIcon());
    }

    // On screen the dates use the friendly MMM dd yyyy form
    @Test
    public void toString_notDone_showsEmptyBoxAndFormattedDates() {
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                makeEvent().toString());
    }

    @Test
    public void toString_done_showsCross() {
        Event event = makeEvent();
        event.setDone(true);
        assertEquals("[E][X] project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                event.toString());
    }

    @Test
    public void toString_markedThenUnmarked_showsEmptyBoxAgain() {
        Event event = makeEvent();
        event.setDone(true);
        event.setDone(false);
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                event.toString());
    }

    // In the file the dates stay as yyyy-mm-dd, so LocalDate.parse can read them back
    @Test
    public void toSaveString_notDone_usesZeroAndIsoDates() {
        assertEquals("E | 0 | project meeting | 2019-10-15 | 2019-10-16",
                makeEvent().toSaveString());
    }

    @Test
    public void toSaveString_done_usesOne() {
        Event event = makeEvent();
        event.setDone(true);
        assertEquals("E | 1 | project meeting | 2019-10-15 | 2019-10-16",
                event.toSaveString());
    }

    // An event is ordered by when it starts, since that is when the user has to turn up
    @Test
    public void getSortDate_always_isTheStartDate() {
        assertEquals(LocalDate.of(2019, 10, 15), makeEvent().getSortDate().orElseThrow());
    }

    // A one-day event shows the same date twice rather than collapsing it
    @Test
    public void toString_startAndEndSameDay_showsBothDates() {
        assertEquals("[E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)",
                new Event("standup",
                        LocalDate.of(2019, 10, 15),
                        LocalDate.of(2019, 10, 15)).toString());
    }

    /*
     * The month name must not follow the operating system's language; see the note
     * in DeadlineTest for why switching the locale here can only prove so much.
     */
    @Test
    public void toString_chineseDefaultLocale_stillShowsEnglishMonths() {
        Locale.setDefault(Locale.CHINA);
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                makeEvent().toString());
    }

    @Test
    public void toSaveString_germanDefaultLocale_stillUsesIsoDates() {
        Locale.setDefault(Locale.GERMANY);
        assertEquals("E | 0 | project meeting | 2019-10-15 | 2019-10-16",
                makeEvent().toSaveString());
    }

    @Test
    public void toString_spanningYearEnd_showsBothYears() {
        assertEquals("[E][ ] holiday (from: Dec 30 2019 to: Jan 02 2020)",
                new Event("holiday",
                        LocalDate.of(2019, 12, 30),
                        LocalDate.of(2020, 1, 2)).toString());
    }
}
