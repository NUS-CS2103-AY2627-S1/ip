package odysseus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests learning-card normalization and deterministic deck behavior. */
class LearningModelTest {
    @Test
    void answerMatches_ignoresCasePunctuationAndWhitespace() {
        LearningCard card = new LearningCard("Prompt", "Home-coming", "Terms");

        assertTrue(card.answerMatches("  homecoming "));
    }

    @Test
    void deck_selectsLowestMasteryThenFewestReviews() throws Exception {
        LearningDeck deck = new LearningDeck();
        LearningCard first = new LearningCard("first", "one", "Greek");
        LearningCard second = new LearningCard("second", "two", "Greek");
        deck.addCard(first);
        deck.addCard(second);
        first.markMastered();
        second.markForReview();

        assertEquals("second", deck.nextCard("greek").getPrompt());
        assertEquals(List.of(1, 2), deck.findCardNumbers(null));
        assertThrows(OdysseusException.class, () -> deck.getCard(3));
        assertThrows(OdysseusException.class, () -> deck.nextCard("unknown"));
    }
}
