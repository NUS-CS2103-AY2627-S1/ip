package luckynoslacky;

import java.nio.file.Path;

import luckynoslacky.luckyparser.DateTimeParser;
import luckynoslacky.luckystorage.CsvSaver;

/** Creates production chatbots backed by isolated test data files. */
public final class TestChatbotFactory {
    private TestChatbotFactory() {
        // Prevent instantiation of this test utility class.
    }

    /**
     * Creates a chatbot that persists tasks to the supplied file.
     *
     * @param dataFile isolated CSV file used by the chatbot
     * @return chatbot backed by the supplied file
     * @throws IllegalArgumentException if {@code dataFile} is null
     */
    public static LuckyNoSlacky createWithDataFile(Path dataFile) {
        if (dataFile == null) {
            throw new IllegalArgumentException("Data file cannot be null.");
        }
        return new LuckyNoSlacky(new DateTimeParser(), new CsvSaver(dataFile));
    }
}
