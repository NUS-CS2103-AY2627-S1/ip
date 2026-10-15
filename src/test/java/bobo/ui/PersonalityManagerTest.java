package bobo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PersonalityManagerTest {

    @Test
    public void testEmptyList() {
        assertEquals(0, PersonalityManager.getStartIndex(0));
        List<String> phrases = PersonalityManager.getPhrasesForCount(0);
        assertEquals(4, phrases.size());
        assertTrue(phrases.get(0).contains("squeak!"));
        assertTrue(phrases.get(3).contains("dust speck"));
        assertEquals(phrases.get(0), PersonalityManager.getGreetingPhrase(0));
        assertEquals(phrases.get(1), PersonalityManager.getTaskAddedPhrase(0));
        assertEquals(phrases.get(2), PersonalityManager.getTaskRemovedPhrase(0));
        assertEquals(phrases.get(3), PersonalityManager.getEmptyListPhrase(0));
    }

    @Test
    public void testEarlyStage() {
        // At 6 items (Stage 2: 5–8 items) -> Start index = 4
        assertEquals(4, PersonalityManager.getStartIndex(6));
        List<String> phrases = PersonalityManager.getPhrasesForCount(6);
        assertEquals(4, phrases.size());
        assertTrue(phrases.get(0).contains("growing a tiny bit bigger"));
        assertTrue(phrases.get(1).contains("feet are firmly planted"));
        assertTrue(phrases.get(2).contains("growing confidence"));
        assertTrue(phrases.get(3).contains("standing tall"));
    }

    @Test
    public void testMiddleStage() {
        // At 10 items (Stage 3: 9–12 items) -> Start index = 8
        assertEquals(8, PersonalityManager.getStartIndex(10));
        List<String> phrases = PersonalityManager.getPhrasesForCount(10);
        assertEquals(4, phrases.size());
        assertTrue(phrases.get(0).contains("bursting with energy"));
        assertTrue(phrases.get(1).contains("enthusiastic vigor"));
        assertTrue(phrases.get(3).contains("energetic jumping jacks"));
    }

    @Test
    public void testLargeStage() {
        // At 15 items (Stage 4: 13–16 items) -> Start index = 12
        assertEquals(12, PersonalityManager.getStartIndex(15));
        List<String> phrases = PersonalityManager.getPhrasesForCount(15);
        assertEquals(4, phrases.size());
        assertTrue(phrases.get(0).contains("GREETINGS MORTAL"));
        assertTrue(phrases.get(1).contains("TASK ACKNOWLEDGED"));
    }

    @Test
    public void testBoundary200() {
        // At 200 items (Stage 5: 17+ items) -> Start index = 16
        assertEquals(16, PersonalityManager.getStartIndex(200));
        List<String> phrases = PersonalityManager.getPhrasesForCount(200);
        assertEquals(4, phrases.size());
        assertTrue(phrases.get(0).contains("MIGHTY TITAN BOBO"));
        assertTrue(phrases.get(3).contains("COLOSSAL TITAN BOBO"));
    }

    @Test
    public void testBeyond200Items() {
        // At 250 items and 1000 items: Capped permanently at Stage 5 (Start index = 16)
        assertEquals(16, PersonalityManager.getStartIndex(250));
        assertEquals(16, PersonalityManager.getStartIndex(1000));

        List<String> phrases200 = PersonalityManager.getPhrasesForCount(200);
        List<String> phrases250 = PersonalityManager.getPhrasesForCount(250);
        List<String> phrases1000 = PersonalityManager.getPhrasesForCount(1000);

        assertEquals(phrases200, phrases250);
        assertEquals(phrases200, phrases1000);
    }

    @Test
    public void testDynamicUpdateOnAddAndDelete() {
        int initialCount = 0;
        assertEquals(0, PersonalityManager.getStartIndex(initialCount));

        int addedCount = 10;
        assertEquals(8, PersonalityManager.getStartIndex(addedCount));

        int deletedCount = 6;
        assertEquals(4, PersonalityManager.getStartIndex(deletedCount));
    }
}
