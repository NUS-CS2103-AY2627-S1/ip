package elsa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Dates}, the class that reads and writes the dates deadlines and
 * events carry.
 *
 * <p>{@code Dates} is tested here rather than through the text UI because its
 * methods take text and return a value, with no file, console or task list
 * involved. That makes it possible to state one input and one expected result
 * per test, which is awkward to do in a console transcript.
 *
 * <p>Test methods are named featureUnderTest_testScenario_expectedBehavior, so
 * that a failure report says what was being done and what should have happened
 * without anyone having to open this file.
 *
 * <p>All four of the class's methods are covered, along with the two promises it
 * makes in prose: that the forms named in {@link Dates#ACCEPTED_FORMS} really are
 * accepted, and that a date shown to the user can be typed straight back in.
 */
public class DatesTest {

    /** The one day every test below is written about, in whichever form. */
    private static final LocalDate OCT_15_2019 = LocalDate.of(2019, 10, 15);

    // ------------------------------------------------------------------
    // The accepted forms
    // ------------------------------------------------------------------

    @Test
    public void parse_isoForm_returnsDate() throws ElsaException {
        assertEquals(OCT_15_2019, Dates.parse("2019-10-15"));
    }

    @Test
    public void parse_dayFirstSlashForm_returnsDate() throws ElsaException {
        assertEquals(OCT_15_2019, Dates.parse("15/10/2019"));
    }

    @Test
    public void parse_shortMonthNameForm_returnsDate() throws ElsaException {
        assertEquals(OCT_15_2019, Dates.parse("Oct 15 2019"));
    }

    @Test
    public void parse_fullMonthNameForm_returnsDate() throws ElsaException {
        assertEquals(OCT_15_2019, Dates.parse("October 15 2019"));
    }

    /**
     * The class promises that the forms it accepts are different ways of writing
     * the same day, so that a task typed one way and a task typed another way are
     * the same task once read. This checks that promise directly.
     */
    @Test
    public void parse_everyAcceptedForm_givesTheSameDate() throws ElsaException {
        assertEquals(Dates.parse("2019-10-15"), Dates.parse("15/10/2019"));
        assertEquals(Dates.parse("2019-10-15"), Dates.parse("Oct 15 2019"));
        assertEquals(Dates.parse("2019-10-15"), Dates.parse("October 15 2019"));
    }

    // ------------------------------------------------------------------
    // Leniency that is meant to be there
    // ------------------------------------------------------------------

    @Test
    public void parse_singleDigitDayAndMonth_returnsDate() throws ElsaException {
        assertEquals(LocalDate.of(2019, 1, 5), Dates.parse("5/1/2019"));
    }

    @Test
    public void parse_paddedDayAndMonth_returnsDate() throws ElsaException {
        assertEquals(LocalDate.of(2019, 1, 5), Dates.parse("05/01/2019"));
    }

    @Test
    public void parse_monthNameInLowerCase_returnsDate() throws ElsaException {
        assertEquals(OCT_15_2019, Dates.parse("oct 15 2019"));
    }

    @Test
    public void parse_monthNameInUpperCase_returnsDate() throws ElsaException {
        assertEquals(OCT_15_2019, Dates.parse("OCT 15 2019"));
    }

    @Test
    public void parse_surroundingWhitespace_returnsDate() throws ElsaException {
        assertEquals(OCT_15_2019, Dates.parse("   2019-10-15   "));
    }

    // ------------------------------------------------------------------
    // Which number is the day
    // ------------------------------------------------------------------

    /**
     * The slash form is day before month, so this is 2 December and not
     * 12 February. The two readings are both possible dates, so nothing but a
     * test records which one was chosen.
     */
    @Test
    public void parse_ambiguousSlashForm_readsDayBeforeMonth() throws ElsaException {
        assertEquals(LocalDate.of(2019, 12, 2), Dates.parse("2/12/2019"));
    }

    /**
     * The other side of the same decision: written month first, 10/15/2019 asks
     * for month 15, which does not exist, so it is refused rather than quietly
     * read as 15 October.
     */
    @Test
    public void parse_monthFirstSlashForm_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("10/15/2019"));
    }

    // ------------------------------------------------------------------
    // Dates the calendar does not have
    //
    // These are the tests that earn their keep. Without ResolverStyle.STRICT
    // java.time does not refuse these, it silently moves them to the last day of
    // the month, so 31/2/2019 would be accepted and stored as 28 February. That
    // is a wrong answer rather than an error, and the user would never be told.
    // ------------------------------------------------------------------

    @Test
    public void parse_dayThatMonthDoesNotHave_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("31/2/2019"));
    }

    @Test
    public void parse_thirtiethOfFebruary_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("2019-02-30"));
    }

    @Test
    public void parse_thirtyFirstOfApril_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("2019-04-31"));
    }

    @Test
    public void parse_twentyNinthOfFebruaryInCommonYear_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("2019-02-29"));
    }

    /** The same day in a leap year is a real date, so it must still be accepted. */
    @Test
    public void parse_twentyNinthOfFebruaryInLeapYear_returnsDate() throws ElsaException {
        assertEquals(LocalDate.of(2020, 2, 29), Dates.parse("2020-02-29"));
    }

    @Test
    public void parse_monthBeyondTwelve_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("2019-13-01"));
    }

    @Test
    public void parse_dayZero_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("2019-10-00"));
    }

    // ------------------------------------------------------------------
    // Text that is not a date at all
    // ------------------------------------------------------------------

    @Test
    public void parse_word_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("tomorrow"));
    }

    @Test
    public void parse_emptyText_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse(""));
    }

    @Test
    public void parse_whitespaceOnly_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("   "));
    }

    @Test
    public void parse_twoDigitYear_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("15/10/19"));
    }

    @Test
    public void parse_dateWithTrailingText_throwsException() {
        assertThrows(ElsaException.class, () -> Dates.parse("2019-10-15 please"));
    }

    /**
     * The message is shown to the user, so it has to say which text was refused
     * and how a date should be written. Only those two things are checked, so
     * that rewording the sentence around them does not fail this test.
     */
    @Test
    public void parse_notADate_messageNamesTheTextAndTheAcceptedForms() {
        ElsaException thrown = assertThrows(ElsaException.class, () -> Dates.parse("tomorrow"));
        String message = thrown.getMessage();
        assertEquals(true, message.contains("tomorrow"));
        assertEquals(true, message.contains(Dates.ACCEPTED_FORMS));
    }

    // ------------------------------------------------------------------
    // Writing a date back out
    // ------------------------------------------------------------------

    @Test
    public void format_date_returnsDisplayForm() {
        assertEquals("Oct 15 2019", Dates.format(OCT_15_2019));
    }

    /** The display form pads the day to two digits, so it is Jan 05 and not Jan 5. */
    @Test
    public void format_singleDigitDay_padsDayToTwoDigits() {
        assertEquals("Jan 05 2019", Dates.format(LocalDate.of(2019, 1, 5)));
    }

    @Test
    public void toSaveFormat_date_returnsIsoForm() {
        assertEquals("2019-10-15", Dates.toSaveFormat(OCT_15_2019));
    }

    @Test
    public void toSaveFormat_singleDigitMonthAndDay_padsBothToTwoDigits() {
        assertEquals("2019-01-05", Dates.toSaveFormat(LocalDate.of(2019, 1, 5)));
    }

    /**
     * The saved form has to be one that {@link Dates#parse} can read, or a task
     * would be saved and then refused the next time the chatbot starts. Every
     * accepted input form is taken through the round trip, because the stored
     * form must not depend on how the date was typed.
     */
    @Test
    public void toSaveFormatThenParse_everyAcceptedForm_returnsTheSameDate() throws ElsaException {
        String[] forms = {"2019-10-15", "15/10/2019", "Oct 15 2019", "October 15 2019"};
        for (String form : forms) {
            LocalDate typed = Dates.parse(form);
            LocalDate reloaded = Dates.parse(Dates.toSaveFormat(typed));
            assertEquals(typed, reloaded, "round trip failed for " + form);
        }
    }

    /**
     * A date the user has been shown should be one they can type straight back
     * in. The display form is "Oct 15 2019", which is one of the accepted input
     * forms, and this checks the two have not drifted apart. Several dates are
     * used because the display form pads the day, and a padded day has to be
     * readable as well as a bare one.
     */
    @Test
    public void formatThenParse_anyDate_returnsTheSameDate() throws ElsaException {
        LocalDate[] dates = {OCT_15_2019, LocalDate.of(2019, 1, 5),
            LocalDate.of(2020, 2, 29), LocalDate.of(2999, 12, 31)};
        for (LocalDate date : dates) {
            assertEquals(date, Dates.parse(Dates.format(date)),
                    "a shown date could not be typed back in: " + Dates.format(date));
        }
    }

    // ------------------------------------------------------------------
    // What the class says about itself
    // ------------------------------------------------------------------

    /**
     * ACCEPTED_FORMS is what the user is told to write when a date is refused, so
     * it is wrong for it to name a form the chatbot does not actually accept. The
     * examples are listed again here rather than pulled out of the sentence,
     * because a test that took them apart with a regular expression would be
     * harder to trust than the thing it is testing.
     */
    @Test
    public void acceptedForms_everyExampleItNames_parsesToTheSameDate() throws ElsaException {
        String[] examples = {"2019-10-15", "15/10/2019", "Oct 15 2019"};
        for (String example : examples) {
            assertEquals(true, Dates.ACCEPTED_FORMS.contains(example),
                    "ACCEPTED_FORMS no longer names " + example);
            assertEquals(OCT_15_2019, Dates.parse(example),
                    "ACCEPTED_FORMS names a form that is not accepted: " + example);
        }
    }

    // ------------------------------------------------------------------
    // Today
    // ------------------------------------------------------------------

    /**
     * today() reads the computer's clock, so a test cannot state the answer in
     * advance. What it can do is bracket the call: the date returned must not be
     * before the date just before the call, nor after the date just after it.
     * That still catches a fixed date, a date shifted by a day, or a clock read
     * in the wrong time zone, and unlike comparing against a single
     * LocalDate.now() it does not fail for anyone running the tests as the date
     * changes.
     */
    @Test
    public void today_returnsTheDateOnTheComputersClock() {
        LocalDate before = LocalDate.now();
        LocalDate actual = Dates.today();
        LocalDate after = LocalDate.now();
        assertEquals(false, actual.isBefore(before), "today() is behind the clock");
        assertEquals(false, actual.isAfter(after), "today() is ahead of the clock");
    }
}
