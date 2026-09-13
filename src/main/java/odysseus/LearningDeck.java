package odysseus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Owns the learning cards and selects the one most in need of review.
 */
public class LearningDeck {
    private static final int FIRST_CARD_NUMBER = 1;
    private final List<LearningCard> cards = new ArrayList<>();

    /**
     * Adds a newly learned card to the archive.
     */
    public void addCard(LearningCard card) {
        cards.add(card);
    }

    /**
     * Returns the card that should be reviewed next for an optional topic.
     */
    public LearningCard nextCard(String topic) throws OdysseusException {
        return cards.stream()
                .filter(card -> topic == null || card.getTopic().equalsIgnoreCase(topic))
                .min(Comparator.comparingInt(LearningCard::getMastery)
                        .thenComparingInt(LearningCard::getReviewCount))
                .orElseThrow(() -> noCardsFor(topic));
    }

    /**
     * Returns the requested card using its one-based archive number.
     */
    public LearningCard getCard(int cardNumber) throws OdysseusException {
        if (cardNumber < FIRST_CARD_NUMBER || cardNumber > cards.size()) {
            throw new OdysseusException("There is no learning card " + cardNumber
                    + ". Use cards to inspect Athena's Archive.");
        }
        return cards.get(cardNumber - FIRST_CARD_NUMBER);
    }

    /**
     * Removes and returns the requested card using its one-based archive number.
     */
    public LearningCard deleteCard(int cardNumber) throws OdysseusException {
        LearningCard card = getCard(cardNumber);
        cards.remove(cardNumber - FIRST_CARD_NUMBER);
        return card;
    }

    /**
     * Returns the one-based numbers of cards in an optional topic.
     */
    public List<Integer> findCardNumbers(String topic) {
        return IntStream.range(0, cards.size())
                .filter(index -> topic == null || cards.get(index).getTopic().equalsIgnoreCase(topic))
                .map(index -> index + FIRST_CARD_NUMBER)
                .boxed()
                .toList();
    }

    /**
     * Returns the number of cards in the archive.
     */
    public int getCardCount() {
        return cards.size();
    }

    /**
     * Creates the error appropriate for an empty archive or unknown topic.
     */
    private OdysseusException noCardsFor(String topic) {
        if (topic == null) {
            return new OdysseusException("Athena's Archive is empty. Try: learn nostos /answer homecoming.");
        }
        return new OdysseusException("Athena has no cards on " + topic + ". Use cards to inspect the archive.");
    }
}
