package suki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FindTest {

    private TaskList sampleTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return Book to library"));
        tasks.add(new Todo("buy milk"));
        return tasks;
    }

    @Test
    public void find_matchingKeyword_returnsOnlyMatches() throws SukiException {
        TaskList matches = sampleTasks().find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[T][ ] return Book to library", matches.get(1).toString());
    }

    @Test
    public void find_differentCase_stillMatches() {
        assertEquals(2, sampleTasks().find("BOOK").size());
        assertEquals(2, sampleTasks().find("BoOk").size());
    }

    @Test
    public void find_partialWord_matches() {
        // "libr" appears inside "library".
        assertEquals(1, sampleTasks().find("libr").size());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        assertTrue(sampleTasks().find("spaceship").isEmpty());
    }

    @Test
    public void find_doesNotModifyOriginalList() {
        TaskList tasks = sampleTasks();
        tasks.find("book");
        assertEquals(3, tasks.size(), "searching should not remove anything");
    }

    @Test
    public void find_searchesDescriptionNotDates() throws SukiException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", DateTime.parse("2019-10-15")));

        assertEquals(1, tasks.find("report").size());
        assertTrue(tasks.find("Oct").isEmpty(), "dates should not be searched");
    }

    @Test
    public void parseFindKeyword_emptyKeyword_throws() {
        assertThrows(SukiException.class, () -> Parser.parseFindKeyword(""));
    }

    @Test
    public void parse_findCommand_isRecognised() {
        assertEquals(CommandType.FIND, Parser.parse("find book").commandType());
        assertEquals("book", Parser.parse("find book").arguments());
    }
}
