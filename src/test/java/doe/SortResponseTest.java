package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the GUI sort response and persistence through a fresh chatbot instance. */
class SortResponseTest {
    @TempDir
    Path directory;

    @Test
    void sort_mixedTasks_displaysSectionsAndPersistsNewTaskNumbers() {
        String file = directory.resolve("tasks.txt").toString();
        new Storage(file).save(List.of(new Todo("read"),
                new Deadline("submit", "11-10-2026 1800"),
                new Event("meet", "10-10-2026 0900")));
        Doe chatbot = new Doe(file);
        assertTrue(chatbot.getResponse("todo").contains("4. sort\n5. mark\n6. unmark\n7. find\n8. exit"));
        assertEquals("here are the tasks in your list:\n"
                + "1. [e][ ] meet (at: 10 Oct 2026, 09:00)\n"
                + "2. [d][ ] submit (by: 11 Oct 2026, 18:00)\n\n"
                + "no deadlines:\n3. [t][ ] read\n\n" + chatbot.getWelcomeMessage(),
                chatbot.getResponse("4"));

        Doe reopened = new Doe(file);
        reopened.getResponse("todo");
        reopened.getResponse("5");
        assertTrue(reopened.getResponse("1").contains("[x] meet"));
        List<Task> saved = new Storage(file).load();
        assertEquals(List.of("meet", "submit", "read"),
                saved.stream().map(Task::getDescription).toList());
        assertEquals("x", saved.get(0).getStatusIcon());
    }
}
