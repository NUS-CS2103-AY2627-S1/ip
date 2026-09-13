package odysseus;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * Tests Athena's Archive through Odysseus's public conversation interface.
 */
class LearningCommandTest {
    @Test
    void learningCard_correctAnswer_persistsMasteryAcrossConversations() throws Exception {
        Path taskPath = Files.createTempDirectory("odysseus-learning-test").resolve("tasks.txt");
        Odysseus odysseus = new Odysseus(taskPath);

        String learned = odysseus.getResponse("learn nostos /answer homecoming /topic Greek terms");
        String reviewed = odysseus.getResponse("review greek terms");
        String answered = odysseus.getResponse("answer Homecoming!");
        String cards = new Odysseus(taskPath).getResponse("cards Greek terms");

        assertTrue(learned.contains("Athena has added this card"));
        assertTrue(reviewed.contains("Athena offers a card from Greek terms"));
        assertTrue(reviewed.contains("nostos"));
        assertTrue(answered.contains("Well recalled"));
        assertTrue(cards.contains("mastery: 1"));
        assertTrue(Files.readString(taskPath.resolveSibling("odysseus-learning.txt"))
                .startsWith("ATHENA_ARCHIVE_V1"));
    }

    @Test
    void studyCommands_withoutActiveCard_reportErrorWithoutChangingArchive() throws Exception {
        Path taskPath = Files.createTempDirectory("odysseus-learning-test").resolve("tasks.txt");
        Odysseus odysseus = new Odysseus(taskPath);

        String noCard = odysseus.getResponse("answer homecoming");
        odysseus.getResponse("learn Who blinds Polyphemus? /answer Odysseus");
        String unknownTopic = odysseus.getResponse("review Book 9");
        String cards = odysseus.getResponse("cards");

        assertTrue(noCard.contains("Choose a card with review"));
        assertTrue(unknownTopic.contains("Athena has no cards on Book 9"));
        assertTrue(cards.contains("Who blinds Polyphemus?"));
        assertTrue(cards.contains("mastery: 0"));
    }

    @Test
    void reveal_thenAgain_resetsMasteryAndEndsStudyTurn() throws Exception {
        Path taskPath = Files.createTempDirectory("odysseus-learning-test").resolve("tasks.txt");
        Odysseus odysseus = new Odysseus(taskPath);
        odysseus.getResponse("learn Athena's bird /answer owl /topic Symbols");

        odysseus.getResponse("review Symbols");
        String revealed = odysseus.getResponse("reveal");
        String repeated = odysseus.getResponse("again");
        String noLongerActive = odysseus.getResponse("mastered");

        assertTrue(revealed.contains("Athena's answer: owl"));
        assertTrue(repeated.contains("remains close at hand"));
        assertTrue(noLongerActive.contains("Choose a card with review"));
    }
}
