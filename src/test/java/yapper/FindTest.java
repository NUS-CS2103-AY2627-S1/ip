package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests keyword parsing and read-only searches across task types.
 */
public class FindTest {
    @Test
    public void parseKeyword_paddedPhrase_preservesInnerSpaces() throws YapperException {
        Parser parser = new Parser();
        assertEquals(CommandType.FIND, parser.parseCommandType("find book"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("finder book"));
        assertEquals("read  book", parser.parseKeyword("find   read  book  "));
    }

    @Test
    public void parseKeyword_emptyInput_throwsException() {
        Parser parser = new Parser();
        assertThrows(YapperException.class, () -> parser.parseKeyword("find"));
        assertThrows(YapperException.class, () -> parser.parseKeyword("find   "));
    }

    @Test
    public void indicesMatching_mixedTypes_preservesIndicesAndState() {
        LocalDateTime date = LocalDateTime.of(2024, 2, 29, 18, 0);
        TaskList tasks = new TaskList(List.of(new Todo("laundry"), new Todo("Read BOOK"),
                new Deadline("return book", date), new Event("book club", date, date.plusHours(1))));
        tasks.mark(2);
        List<String> before = tasks.asList().stream().map(Task::toDataString).toList();
        assertEquals(List.of(1, 2, 3), tasks.indicesMatching("bOoK"));
        assertEquals(List.of(3), tasks.indicesMatching("book club"));
        assertEquals(before, tasks.asList().stream().map(Task::toDataString).toList());
    }

    @Test
    public void indicesMatching_noMatchOrEmptyList_returnsEmptyList() {
        assertEquals(List.of(), new TaskList().indicesMatching("book"));
        TaskList tasks = new TaskList(List.of(new Todo("laundry")));
        assertEquals(List.of(), tasks.indicesMatching("book"));
    }

    @Test
    public void containsKeyword_descriptionOnly_ignoresStatusTypeAndDate() {
        Task task = new Deadline("BOOK", LocalDateTime.of(2024, 2, 29, 18, 0));
        task.markAsDone();
        assertTrue(task.containsKeyword("ook"));
        assertFalse(task.containsKeyword("2024"));
        assertFalse(task.containsKeyword("[D]"));
        assertFalse(task.containsKeyword("[X]"));
    }
}
