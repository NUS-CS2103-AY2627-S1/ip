package odysseus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Loads and saves Athena's Archive without changing the existing task log.
 */
public class LearningStorage {
    private static final String HEADER = "ATHENA_ARCHIVE_V1";
    private final Path filePath;

    /**
     * Creates storage for the supplied learning-data path.
     */
    public LearningStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads a learning deck or an empty deck when the archive is absent.
     */
    public LearningDeck load() throws OdysseusException {
        LearningDeck deck = new LearningDeck();
        if (Files.notExists(filePath)) {
            return deck;
        }
        try {
            List<String> lines = Files.readAllLines(filePath);
            if (lines.isEmpty() || !HEADER.equals(lines.getFirst())) {
                throw new OdysseusException("Invalid learning archive");
            }
            for (int index = 1; index < lines.size(); index++) {
                deck.addCard(readCard(lines.get(index)));
            }
            return deck;
        } catch (IOException | IllegalArgumentException exception) {
            throw new OdysseusException("I could not load Athena's Archive.");
        }
    }

    /**
     * Saves every learning card in a delimiter-safe, versioned format.
     */
    public void save(LearningDeck deck) throws OdysseusException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        try {
            for (int number = 1; number <= deck.getCardCount(); number++) {
                lines.add(writeCard(deck.getCard(number)));
            }
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines);
        } catch (IOException exception) {
            throw new OdysseusException("I could not save Athena's Archive.");
        }
    }

    /**
     * Reconstructs one validated card from its saved fields.
     */
    private LearningCard readCard(String line) throws OdysseusException {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 6 || !parts[0].equals("C")) {
            throw new OdysseusException("Invalid learning card");
        }
        try {
            int mastery = Integer.parseInt(parts[1]);
            int reviewCount = Integer.parseInt(parts[2]);
            String topic = decode(parts[3]);
            String prompt = decode(parts[4]);
            String answer = decode(parts[5]);
            if (mastery < 0 || reviewCount < 0 || topic.isBlank() || prompt.isBlank() || answer.isBlank()) {
                throw new OdysseusException("Invalid learning card");
            }
            return new LearningCard(prompt, answer, topic, mastery, reviewCount);
        } catch (IllegalArgumentException exception) {
            throw new OdysseusException("Invalid learning card");
        }
    }

    /**
     * Converts a card into one portable saved line.
     */
    private String writeCard(LearningCard card) {
        return "C|" + card.getMastery() + "|" + card.getReviewCount() + "|" + encode(card.getTopic())
                + "|" + encode(card.getPrompt()) + "|" + encode(card.getAnswer());
    }

    /**
     * Encodes user-provided text so it cannot interfere with the file format.
     */
    private String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a user-provided text field from the file format.
     */
    private String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }
}
