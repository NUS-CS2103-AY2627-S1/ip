package atlas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests the priority words used by the priority command and by storage. */
public class PriorityTest {

    @Test
    void exposesTheWordUsersTypeAndTheTagShownInListings() {
        assertEquals("high", Priority.HIGH.getWord());
        assertEquals("HIGH", Priority.HIGH.getTag());
        assertEquals("medium", Priority.MEDIUM.getWord());
        assertEquals("MEDIUM", Priority.MEDIUM.getTag());
        assertEquals("low", Priority.LOW.getWord());
        assertEquals("LOW", Priority.LOW.getTag());
    }

    @Test
    void mapsEachWordToItsLevelAndRejectsOtherWords() {
        assertEquals(Priority.HIGH, Priority.fromWord("high"));
        assertEquals(Priority.MEDIUM, Priority.fromWord("medium"));
        assertEquals(Priority.LOW, Priority.fromWord("low"));

        assertNull(Priority.fromWord("High"));
        assertNull(Priority.fromWord("urgent"));
        assertNull(Priority.fromWord(""));
        assertNull(Priority.fromWord("none"));
    }

    @Test
    void hasExactlyThreeLevels() {
        assertEquals(3, Priority.values().length);
    }

    @Test
    void theTagDoesNotFollowTheDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            Todo ranked = new Todo("buy milk");
            ranked.setPriority(Priority.HIGH);

            assertEquals("HIGH", Priority.HIGH.getTag());
            assertEquals("[T][ ][HIGH] buy milk", ranked.toString());
        } finally {
            Locale.setDefault(original);
        }
    }
}
