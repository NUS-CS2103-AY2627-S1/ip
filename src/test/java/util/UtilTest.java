package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for utility parsing methods.
 */
public class UtilTest {

    // Tests for toArrayList(String, char)
    @Test
    public void testToArrayListCharDelimiter_multipleValues() {
        String input = "apple,banana,cherry";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(3, result.size());
        assertEquals("apple", result.get(0));
        assertEquals("banana", result.get(1));
        assertEquals("cherry", result.get(2));
    }

    @Test
    public void testToArrayListCharDelimiter_singleValue() {
        String input = "apple";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(1, result.size());
        assertEquals("apple", result.get(0));
    }

    @Test
    public void testToArrayListCharDelimiter_emptyString() {
        String input = "";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(1, result.size());
        assertEquals("", result.get(0));
    }

    @Test
    public void testToArrayListCharDelimiter_valuesWithSpaces() {
        String input = "hello world,foo bar,test";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(3, result.size());
        assertEquals("hello world", result.get(0));
        assertEquals("foo bar", result.get(1));
        assertEquals("test", result.get(2));
    }

    @Test
    public void testToArrayListCharDelimiter_differentDelimiter() {
        String input = "one|two|three|four";
        ArrayList<String> result = Util.toArrayList(input, '|');
        assertEquals(4, result.size());
        assertEquals("one", result.get(0));
        assertEquals("two", result.get(1));
        assertEquals("three", result.get(2));
        assertEquals("four", result.get(3));
    }

    @Test
    public void testToArrayListCharDelimiter_trailingDelimiter() {
        String input = "apple,banana,";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(3, result.size());
        assertEquals("apple", result.get(0));
        assertEquals("banana", result.get(1));
        assertEquals("", result.get(2));
    }

    @Test
    public void testToArrayListCharDelimiter_leadingDelimiter() {
        String input = ",apple,banana";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(3, result.size());
        assertEquals("", result.get(0));
        assertEquals("apple", result.get(1));
        assertEquals("banana", result.get(2));
    }

    @Test
    public void testToArrayListCharDelimiter_consecutiveDelimiters() {
        String input = "a,,b,,c";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(5, result.size());
        assertEquals("a", result.get(0));
        assertEquals("", result.get(1));
        assertEquals("b", result.get(2));
        assertEquals("", result.get(3));
        assertEquals("c", result.get(4));
    }

    @Test
    public void testToArrayListCharDelimiter_onlyDelimiters() {
        String input = ",,,";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(4, result.size());
        assertEquals("", result.get(0));
        assertEquals("", result.get(1));
        assertEquals("", result.get(2));
        assertEquals("", result.get(3));
    }

    @Test
    public void testToArrayListCharDelimiter_manyValues() {
        String input = "a,b,c,d,e,f,g,h";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(8, result.size());
        assertEquals("a", result.get(0));
        assertEquals("h", result.get(7));
    }

    @Test
    public void testToArrayListCharDelimiter_numericValues() {
        String input = "1,2,3,4,5";
        ArrayList<String> result = Util.toArrayList(input, ',');
        assertEquals(5, result.size());
        assertEquals("1", result.get(0));
        assertEquals("5", result.get(4));
    }

    // Tests for toArrayList(String, ArrayList<String>)
    @Test
    public void testToArrayListStringDelimiter_multipleCustomDelimiters() {
        String input = "word1@@@word2%%word3";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("@@@");
        delimiters.add("%%");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(3, result.size());
        assertEquals("word1", result.get(0));
        assertEquals("word2", result.get(1));
        assertEquals("word3", result.get(2));
    }

    @Test
    public void testToArrayListStringDelimiter_noDelimiters_throws() {
        // The method always looks up the first delimiter up front, so an empty
        // delimiter list is not a supported input and throws instead of returning
        // the whole string as a single token.
        String input = "onlyword";
        ArrayList<String> delimiters = new ArrayList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> Util.toArrayList(input, delimiters));
    }

    @Test
    public void testToArrayListStringDelimiter_emptyStringNoDelimiters_throws() {
        String input = "";
        ArrayList<String> delimiters = new ArrayList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> Util.toArrayList(input, delimiters));
    }

    @Test
    public void testToArrayListStringDelimiter_differentLengthDelimiters() {
        String input = "a#b##c###d";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("#");
        delimiters.add("##");
        delimiters.add("###");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(4, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
        assertEquals("d", result.get(3));
    }

    @Test
    public void testToArrayListStringDelimiter_originalTestCase() {
        ArrayList<String> result = Util.toArrayList(
                "a%%%b@@c&d",
                new ArrayList<>(List.of("%%%", "@@", "&"))
        );
        assertEquals(new ArrayList<>(List.of("a", "b", "c", "d")), result);
    }

    @Test
    public void testToArrayListStringDelimiter_leadingDelimiter() {
        String input = "@@@word1##word2";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("@@@");
        delimiters.add("##");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(3, result.size());
        assertEquals("", result.get(0));
        assertEquals("word1", result.get(1));
        assertEquals("word2", result.get(2));
    }

    @Test
    public void testToArrayListStringDelimiter_trailingDelimiterMulti() {
        // Two @@@ delimiters in input requires two entries in delimiter array
        String input = "first@@@second@@@";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("@@@");
        delimiters.add("@@@");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(3, result.size());
        assertEquals("first", result.get(0));
        assertEquals("second", result.get(1));
        assertEquals("", result.get(2));
    }

    @Test
    public void testToArrayListStringDelimiter_singleDelimiterMultipleOccurrences() {
        // Three ## delimiters in input requires three entries in delimiter array
        String input = "a##b##c##d";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("##");
        delimiters.add("##");
        delimiters.add("##");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(4, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
        assertEquals("d", result.get(3));
    }

    @Test
    public void testToArrayListStringDelimiter_emptyValues() {
        // Five delimiters (@@, ##, @@, ##, @@) in input requires matching array
        String input = "@@##@@##@@";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("@@");
        delimiters.add("##");
        delimiters.add("@@");
        delimiters.add("##");
        delimiters.add("@@");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(6, result.size());
        assertEquals("", result.get(0));
        assertEquals("", result.get(1));
        assertEquals("", result.get(2));
        assertEquals("", result.get(3));
        assertEquals("", result.get(4));
        assertEquals("", result.get(5));
    }

    @Test
    public void testToArrayListStringDelimiter_longDelimiters() {
        String input = "startXXXmiddleYYYend";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("XXX");
        delimiters.add("YYY");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(3, result.size());
        assertEquals("start", result.get(0));
        assertEquals("middle", result.get(1));
        assertEquals("end", result.get(2));
    }

    @Test
    public void testToArrayListStringDelimiter_manyDelimiters() {
        String input = "a###b$$c%%d@@e";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("###");
        delimiters.add("$$");
        delimiters.add("%%");
        delimiters.add("@@");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(5, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
        assertEquals("d", result.get(3));
        assertEquals("e", result.get(4));
    }

    @Test
    public void testToArrayListStringDelimiter_singleCharDelimiters() {
        String input = "x|y&z";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("|");
        delimiters.add("&");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(3, result.size());
        assertEquals("x", result.get(0));
        assertEquals("y", result.get(1));
        assertEquals("z", result.get(2));
    }

    @Test
    public void testToArrayListStringDelimiter_repeatingPattern() {
        // Four : delimiters in input requires four entries in delimiter array
        String input = "1:2:3:4:5";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add(":");
        delimiters.add(":");
        delimiters.add(":");
        delimiters.add(":");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(5, result.size());
        assertEquals("1", result.get(0));
        assertEquals("2", result.get(1));
        assertEquals("3", result.get(2));
        assertEquals("4", result.get(3));
        assertEquals("5", result.get(4));
    }

    @Test
    public void testToArrayListStringDelimiter_complexMixedDelimiters() {
        // "|" and "##" are adjacent in the input, so the token between them is empty.
        String input = "p1|##p2@@p3&p4";
        ArrayList<String> delimiters = new ArrayList<>();
        delimiters.add("|");
        delimiters.add("##");
        delimiters.add("@@");
        delimiters.add("&");
        ArrayList<String> result = Util.toArrayList(input, delimiters);
        assertEquals(5, result.size());
        assertEquals("p1", result.get(0));
        assertEquals("", result.get(1));
        assertEquals("p2", result.get(2));
        assertEquals("p3", result.get(3));
        assertEquals("p4", result.get(4));
    }

    // Tests for trimAndExtractInteger(String, int, int)
    @Test
    public void testTrimAndExtractInteger_withRange_basic() {
        String text = "  42  ";
        int result = Util.trimAndExtractInteger(text, 0, 6);
        assertEquals(42, result);
    }

    @Test
    public void testTrimAndExtractInteger_withRange_negativeNumber() {
        String text = "  -5  ";
        int result = Util.trimAndExtractInteger(text, 0, 6);
        assertEquals(-5, result);
    }

    @Test
    public void testTrimAndExtractInteger_withRange_singleDigit() {
        String text = "7";
        int result = Util.trimAndExtractInteger(text, 0, 1);
        assertEquals(7, result);
    }

    @Test
    public void testTrimAndExtractInteger_withRange_largeNumber() {
        String text = "99999";
        int result = Util.trimAndExtractInteger(text, 0, 5);
        assertEquals(99999, result);
    }

    @Test
    public void testTrimAndExtractInteger_withRange_zero() {
        String text = "0";
        int result = Util.trimAndExtractInteger(text, 0, 1);
        assertEquals(0, result);
    }

    // Tests for trimAndExtractInteger(String, int)
    @Test
    public void testTrimAndExtractInteger_fromIndex_basic() {
        String text = "  456  ";
        int result = Util.trimAndExtractInteger(text, 0);
        assertEquals(456, result);
    }

    @Test
    public void testTrimAndExtractInteger_fromIndex_withTrailingSpace() {
        String text = "  789  ";
        int result = Util.trimAndExtractInteger(text, 2);
        assertEquals(789, result);
    }

    @Test
    public void testTrimAndExtractInteger_fromIndex_negativeNumber() {
        String text = "  -25  ";
        int result = Util.trimAndExtractInteger(text, 0);
        assertEquals(-25, result);
    }

    @Test
    public void testTrimAndExtractInteger_fromIndex_noLeadingSpace() {
        String text = "123";
        int result = Util.trimAndExtractInteger(text, 0);
        assertEquals(123, result);
    }

    @Test
    public void testTrimAndExtractInteger_fromIndex_zero() {
        String text = "0";
        int result = Util.trimAndExtractInteger(text, 0);
        assertEquals(0, result);
    }
}
