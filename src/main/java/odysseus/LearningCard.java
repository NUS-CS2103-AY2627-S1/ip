package odysseus;

import java.util.Locale;

/**
 * A prompt-and-answer item the traveler studies in Athena's Archive.
 */
public class LearningCard {
    private final String prompt;
    private final String answer;
    private final String topic;
    private int mastery;
    private int reviewCount;

    /**
     * Creates a new card that has not yet been reviewed.
     */
    public LearningCard(String prompt, String answer, String topic) {
        this(prompt, answer, topic, 0, 0);
    }

    /**
     * Creates a card with its saved study progress.
     */
    public LearningCard(String prompt, String answer, String topic, int mastery, int reviewCount) {
        this.prompt = prompt;
        this.answer = answer;
        this.topic = topic;
        this.mastery = mastery;
        this.reviewCount = reviewCount;
    }

    /**
     * Returns this card's study prompt.
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Returns this card's expected answer.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Returns this card's topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns how many successful recalls this card has accumulated.
     */
    public int getMastery() {
        return mastery;
    }

    /**
     * Returns how many times this card has been reviewed.
     */
    public int getReviewCount() {
        return reviewCount;
    }

    /**
     * Returns whether a normalized response matches this card's answer.
     */
    public boolean answerMatches(String response) {
        return normalize(answer).equals(normalize(response));
    }

    /**
     * Records a successful recall.
     */
    public void markMastered() {
        mastery++;
        reviewCount++;
    }

    /**
     * Records that the traveler needs to revisit this card.
     */
    public void markForReview() {
        mastery = 0;
        reviewCount++;
    }

    /**
     * Normalizes ordinary answer variation while retaining the words themselves.
     */
    private String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[\\p{Punct}]", "")
                .trim()
                .replaceAll("\\s+", " ");
    }
}
