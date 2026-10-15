package bern.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests command argument extraction with empty keyword lists and missing keyword arguments.
 */
public class ParseFactoryTest {
    @Test
    public void parseData_noKeywords() {
        String[] testTokens = new String[] {"filler", "a", "b", "c", "d"};
        String[] keywordsEmpty = new String[] {};
        String[] parsedData = new String[] {};
        try {
            parsedData = ParseFactory.parseData(testTokens, keywordsEmpty);
        } catch (ParseException e) {
            fail("parseData should be able to parse when keywords array is empty.");
        }

        if (parsedData.length != 1) {
            fail("Parsed data is expected to only contain 1 element, instead got " + parsedData.length);
        }

        assertEquals("a b c d", parsedData[0]);
    }

    @Test
    public void parseData_noKeywordParameters_exceptionThrown() {
        String[] tokens = new String[] {"command", "a", "b", "c", "d"};
        String[][] keywordSets = new String[][] {
                {"a", "b"},
                {"b", "c"},
                {"c", "d"},
                {"b", "d"}
        };
        String[] expected = {"command", "b", "c", "d"};

        for (int i = 0; i < keywordSets.length; i++) {
            String[] keywords = keywordSets[i];
            try {
                ParseFactory.parseData(tokens, keywords);
                fail("parseData should throw an exception if it detects there is no argument supplied after a keyword");
            } catch (ParseException e) {
                assertEquals(expected[i], e.getMessage());
            }
        }
    }
}
