package odysseus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests task and learning archive persistence adapters. */
class StorageTest {
    @Test
    void taskStorage_roundTripsEveryTaskTypeAndStatus() throws Exception {
        Path path = Files.createTempDirectory("odysseus-storage-test").resolve("tasks.txt");
        TaskList tasks = new TaskList();
        Todo todo = new Todo("mend sail");
        todo.markAsDone();
        tasks.addTasks(todo, new Deadline("return", LocalDate.of(2026, 12, 25)), new Event("meet", "dawn", "dusk"));

        new Storage(path).save(tasks);
        TaskList loaded = new Storage(path).load();

        assertEquals("[T][X] mend sail", loaded.getTask(1).toString());
        assertEquals("[D][ ] return (by: Dec 25 2026)", loaded.getTask(2).toString());
        assertEquals("[E][ ] meet (from: dawn to: dusk)", loaded.getTask(3).toString());
    }

    @Test
    void taskStorage_invalidSavedLine_reportsError() throws Exception {
        Path path = Files.createTempDirectory("odysseus-storage-test").resolve("tasks.txt");
        Files.writeString(path, "D | 1 | return | bad-date");

        assertThrows(OdysseusException.class, () -> new Storage(path).load());
    }

    @Test
    void learningStorage_roundTripsProgressAndRejectsInvalidArchive() throws Exception {
        Path path = Files.createTempDirectory("odysseus-learning-storage-test").resolve("learning.txt");
        LearningCard card = new LearningCard("nostos", "homecoming", "Terms");
        card.markMastered();
        LearningDeck deck = new LearningDeck();
        deck.addCard(card);

        new LearningStorage(path).save(deck);
        LearningCard loaded = new LearningStorage(path).load().getCard(1);

        assertEquals(1, loaded.getMastery());
        assertEquals(1, loaded.getReviewCount());
        Files.writeString(path, "not an archive");
        assertThrows(OdysseusException.class, () -> new LearningStorage(path).load());
    }
}
