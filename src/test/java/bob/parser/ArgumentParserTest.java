package bob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bob.BobException;
import bob.command.CommandWord;

/**
 * Tests {@link ArgumentParser}, which reads the single task number, day, count or
 * keyword that follows a command word.
 */
public class ArgumentParserTest {

    @Test
    public void parseTaskNumber_noNumber_exceptionThrown() {
        assertThrows(BobException.class, () -> ArgumentParser.parseTaskNumber("", CommandWord.MARK));
        assertThrows(BobException.class, () -> ArgumentParser.parseTaskNumber("", CommandWord.UNMARK));
        assertThrows(BobException.class, () -> ArgumentParser.parseTaskNumber("", CommandWord.DELETE));
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        BobException exception = assertThrows(BobException.class, () ->
                ArgumentParser.parseTaskNumber("seven", CommandWord.MARK));

        assertTrue(exception.getMessage().contains("\"seven\""));
    }

    @Test
    public void parseTaskNumber_numberTooBigForInt_exceptionSaysTooBig() {
        // It is a number, just not one any list could reach, so "isn't a task
        // number" would be the wrong complaint.
        BobException exception = assertThrows(BobException.class, () ->
                ArgumentParser.parseTaskNumber("99999999999", CommandWord.MARK));

        assertEquals("99999999999 is too big to be a task number."
                + "\nI need the number shown next to the task in list, for example: mark 2",
                exception.getMessage());
    }

    @Test
    public void parseTaskNumber_hugeNegativeNumber_exceptionSaysNotATaskNumber() {
        // Only digits count as a number too big, so a sign makes it no task number.
        BobException exception = assertThrows(BobException.class, () ->
                ArgumentParser.parseTaskNumber("-99999999999", CommandWord.DELETE));

        assertTrue(exception.getMessage().startsWith("\"-99999999999\" isn't a task number."));
    }

    @Test
    public void parseTaskNumber_severalNumbers_exceptionSaysOneAtATime() {
        BobException deleteException = assertThrows(BobException.class, () ->
                ArgumentParser.parseTaskNumber("1 3", CommandWord.DELETE));
        BobException markException = assertThrows(BobException.class, () ->
                ArgumentParser.parseTaskNumber("2\t5  7", CommandWord.MARK));

        // The example uses the first number typed, so it shows the user their own
        // command, done one task at a time.
        assertEquals("I can only delete one task at a time.\nLike dis: delete 1",
                deleteException.getMessage());
        assertEquals("I can only mark one task at a time.\nLike dis: mark 2",
                markException.getMessage());
    }

    @Test
    public void parseTaskNumber_numbersMixedWithWords_exceptionSaysNotATaskNumber() {
        BobException exception = assertThrows(BobException.class, () ->
                ArgumentParser.parseTaskNumber("1 and 3", CommandWord.DELETE));

        assertTrue(exception.getMessage().startsWith("\"1 and 3\" isn't a task number."));
    }

    @Test
    public void parseTaskNumber_numberOutsideTheList_accepted() throws BobException {
        // Whether a number names a task the user actually has is a fact about the
        // list, not about the text, so it is not this class's complaint to make.
        assertEquals(0, ArgumentParser.parseTaskNumber("0", CommandWord.MARK));
        assertEquals(99, ArgumentParser.parseTaskNumber("99", CommandWord.MARK));
        assertEquals(-1, ArgumentParser.parseTaskNumber("-1", CommandWord.DELETE));
        assertEquals(Integer.MAX_VALUE, ArgumentParser.parseTaskNumber("2147483647", CommandWord.MARK));
    }

    @Test
    public void parseDay_noDay_exceptionThrown() {
        assertThrows(BobException.class, () -> ArgumentParser.parseDay("", CommandWord.ON));
        assertThrows(BobException.class, () -> ArgumentParser.parseDay("", CommandWord.BEFORE));
        assertThrows(BobException.class, () -> ArgumentParser.parseDay("", CommandWord.AFTER));
    }

    @Test
    public void parseDay_dayThatIsNotADay_exceptionThrown() {
        assertThrows(BobException.class, () -> ArgumentParser.parseDay("Sunday", CommandWord.ON));
        assertThrows(BobException.class, () -> ArgumentParser.parseDay("02/12/2026", CommandWord.BEFORE));
    }

    @Test
    public void parseDay_timeAfterTheDay_exceptionThrown() {
        // Refused rather than quietly read as the whole day, since the chatbot
        // does not answer questions about part of a day.
        assertThrows(BobException.class, () -> ArgumentParser.parseDay("2026-12-02 1800", CommandWord.ON));
    }

    @Test
    public void parseDay_day_dayReturned() throws BobException {
        assertEquals(LocalDate.of(2026, 12, 2), ArgumentParser.parseDay("2026-12-02", CommandWord.ON));
    }

    @Test
    public void parseCount_noCount_exceptionThrown() {
        assertThrows(BobException.class, () -> ArgumentParser.parseCount(""));
    }

    @Test
    public void parseCount_notANumber_exceptionThrown() {
        assertThrows(BobException.class, () -> ArgumentParser.parseCount("a few"));
    }

    @Test
    public void parseCount_lessThanOne_exceptionThrown() {
        // Showing nothing would leave a user who typed this none the wiser.
        assertThrows(BobException.class, () -> ArgumentParser.parseCount("0"));
        assertThrows(BobException.class, () -> ArgumentParser.parseCount("-3"));
        assertThrows(BobException.class, () -> ArgumentParser.parseCount("-99999999999"));
    }

    @Test
    public void parseCount_negativeNumberTooBigForInt_exceptionNamesTheNumber() {
        // It is a number, however long, so it is refused for being below one
        // rather than for not being a number, as -3 already was.
        BobException exception = assertThrows(BobException.class,
                () -> ArgumentParser.parseCount("-99999999999"));

        assertTrue(exception.getMessage().contains("but not -99999999999."));
    }

    @Test
    public void parseCount_numberWrittenWithAPlus_readAsPositive() throws BobException {
        assertEquals(5, ArgumentParser.parseCount("+5"));
        assertEquals(Integer.MAX_VALUE, ArgumentParser.parseCount("+99999999999"));
    }

    @Test
    public void parseCount_oneOrMore_accepted() throws BobException {
        assertEquals(1, ArgumentParser.parseCount("1"));
        assertEquals(100, ArgumentParser.parseCount("100"));
    }

    @Test
    public void parseCount_numberTooBigForInt_largestCountReturned() throws BobException {
        // More tasks than any list holds asks for all of them, as "next 99" does
        // on a short list, so it is answered rather than refused.
        assertEquals(Integer.MAX_VALUE, ArgumentParser.parseCount("99999999999"));
    }

    @Test
    public void parseKeyword_noKeyword_exceptionThrown() {
        // Matching every task would be a listing the user already has in "list".
        BobException exception = assertThrows(BobException.class, () -> ArgumentParser.parseKeyword(""));

        assertTrue(exception.getMessage().contains("What should I look for?"));
    }

    @Test
    public void parseKeyword_severalWords_acceptedAsOnePhrase() throws BobException {
        // Everything after the command word is the keyword, so a phrase is a
        // search the user may make.
        assertEquals("sports club", ArgumentParser.parseKeyword("sports club"));
    }

    @Test
    public void parseKeyword_textThatMatchesNothing_accepted() throws BobException {
        // Whether anything matches is a fact about the list, not about the text,
        // so a search that finds nothing is answered rather than refused here.
        assertEquals("zzz", ArgumentParser.parseKeyword("zzz"));
    }
}
