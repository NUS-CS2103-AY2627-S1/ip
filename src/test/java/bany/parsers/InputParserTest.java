package bany.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** Tests the command parser's handling of tag names and values. */
class InputParserTest {

    @Test
    void parseAndGetTagNames_reservedTags_rejectRegardlessOfCaseOrValue() {
        for (String input : List.of("todo example /command BYE",
                "todo example /CoMmAnD TODO", "delete 1 /description 2",
                "todo example /DESCRIPTION replacement", "todo example /command",
                "todo example /description", "todo example /subCOMMAND value",
                "todo example /command_suffix", "todo example /preDescriptionPost value",
                "todo example /DESCRIPTION_suffix", "todo example /predescription")) {
            assertThrows(IllegalArgumentException.class, () -> InputParser.parse(input), input);
            assertThrows(IllegalArgumentException.class, () -> InputParser.getTagNames(input), input);
        }
    }

    @Test
    void parse_reservedWordsInDescriptionsAndTagValues_areAllowed() {
        String input = "todo review command description /note command description /urgent";

        Map<String, String> values = InputParser.parse(input);

        assertEquals("TODO", values.get("command"));
        assertEquals("review command description", values.get("description"));
        assertEquals("command description", values.get("note"));
        assertEquals("", values.get("urgent"));
        assertEquals(List.of("note", "urgent"), InputParser.getTagNames(input));
    }

    @Test
    void parseAndGetTagNames_repeatedTags_shareParsingRules() {
        String input = "event meeting /from 21-02-2026 21:03 "
                + "/to 22-02-2026 21:03 /to 23-02-2026 21:03";

        Map<String, String> values = InputParser.parse(input);

        assertEquals("23-02-2026 21:03", values.get("to"));
        assertEquals(List.of("from", "to", "to"), InputParser.getTagNames(input));
    }
}
