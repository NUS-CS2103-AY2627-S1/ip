package bucket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DeadlineTest {

    private final Locale originalLocale = Locale.getDefault();

    @AfterEach
    public void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    private Deadline makeDeadline() {
        return new Deadline("return book", LocalDate.of(2019, 10, 15));
    }

    @Test
    public void getTypeIcon_always_isD() {
        assertEquals("D", makeDeadline().getTypeIcon());
    }

    // On screen the date uses the friendly MMM dd yyyy form
    @Test
    public void toString_notDone_showsEmptyBoxAndFormattedDate() {
        assertEquals("[D][ ] return book (by: Oct 15 2019)", makeDeadline().toString());
    }

    @Test
    public void toString_done_showsCross() {
        Deadline deadline = makeDeadline();
        deadline.setDone(true);
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    // In the file the date stays yyyy-mm-dd, so LocalDate.parse can read it back
    @Test
    public void toSaveString_notDone_usesZeroAndIsoDate() {
        assertEquals("D | 0 | return book | 2019-10-15", makeDeadline().toSaveString());
    }

    @Test
    public void toSaveString_done_usesOne() {
        Deadline deadline = makeDeadline();
        deadline.setDone(true);
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toSaveString());
    }

    // A deadline sorts on its single date
    @Test
    public void getSortDate_always_isTheDueDate() {
        assertEquals(LocalDate.of(2019, 10, 15), makeDeadline().getSortDate().orElseThrow());
    }

    /*
     * The month name must not follow the operating system's language. Left to the
     * default locale it reads "10月 15 2019" on a Chinese install and "Okt. 15 2019"
     * on a German one, which changes what every user sees and breaks these tests
     * on those machines.
     *
     * Note the limit of this check: DISPLAY is a static field, so it is fixed the
     * first time the class loads. Switching the locale here only proves the
     * formatter ignores it from then on; the real guarantee comes from the Locale
     * passed to ofPattern in Deadline.
     */
    @Test
    public void toString_chineseDefaultLocale_stillShowsEnglishMonth() {
        Locale.setDefault(Locale.CHINA);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", makeDeadline().toString());
    }

    @Test
    public void toString_germanDefaultLocale_stillShowsEnglishMonth() {
        Locale.setDefault(Locale.GERMANY);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", makeDeadline().toString());
    }

    // The save format must not drift with the locale either, or old files stop loading
    @Test
    public void toSaveString_chineseDefaultLocale_stillUsesIsoDate() {
        Locale.setDefault(Locale.CHINA);
        assertEquals("D | 0 | return book | 2019-10-15", makeDeadline().toSaveString());
    }

    // Single-digit days are padded, so dates line up in a listed column
    @Test
    public void toString_singleDigitDay_isPadded() {
        assertEquals("[D][ ] pay rent (by: Jan 02 2026)",
                new Deadline("pay rent", LocalDate.of(2026, 1, 2)).toString());
    }

    @Test
    public void toString_leapDay_shownCorrectly() {
        assertEquals("[D][ ] leap (by: Feb 29 2024)",
                new Deadline("leap", LocalDate.of(2024, 2, 29)).toString());
    }
}
