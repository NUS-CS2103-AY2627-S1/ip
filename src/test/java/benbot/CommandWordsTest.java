package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests shared marker lookup for parsed command words. */
class CommandWordsTest {
    @Test
    void findMarker_markerOccursMoreThanOnce_returnsFirstIndex() {
        String[] words = {"event", "meeting", "/from", "1/1/2029", "/from", "2/1/2029"};

        assertEquals(2, CommandWords.findMarker(words, "/from"));
    }

    @Test
    void findMarker_markerAbsent_returnsWordCount() {
        String[] words = {"todo", "read", "book"};

        assertEquals(words.length, CommandWords.findMarker(words, "/by"));
    }

    @Test
    void findMarker_emptyWords_returnsZero() {
        assertEquals(0, CommandWords.findMarker(new String[0], "/by"));
    }

    @Test
    void countMarker_repeatedMarker_returnsOccurrenceCount() {
        String[] words = {
            "event", "meeting", "/from", "1/1/2029", "/from", "2/1/2029", "/to", "3/1/2029"
        };

        assertEquals(2, CommandWords.countMarker(words, "/from"));
        assertEquals(1, CommandWords.countMarker(words, "/to"));
        assertEquals(0, CommandWords.countMarker(words, "/by"));
    }
}
