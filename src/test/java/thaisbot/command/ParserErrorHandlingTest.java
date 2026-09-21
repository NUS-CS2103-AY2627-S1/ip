package thaisbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thaisbot.Storage;
import thaisbot.ThaisBotException;

/**
 * Tests how invalid user input and corrupted data files are handled.
 */
public class ParserErrorHandlingTest {
    @TempDir
    Path tempDir;

    private final Parser parser = new Parser();

    @Test
    public void parse_todoWithoutDescription_throwsUsageError() {
        ThaisBotException e = assertThrows(ThaisBotException.class, () -> parser.parse("todo"));
        assertEquals("Use: todo <description> [#tag ...]", e.getMessage());
    }

    @Test
    public void parse_descriptionWithSeparator_throwsException() {
        assertThrows(ThaisBotException.class, () -> parser.parse("todo a | b"));
        assertThrows(ThaisBotException.class, () -> parser.parse("deadline a | b /by 2026-09-18"));
        assertThrows(ThaisBotException.class, () ->
                parser.parse("event a | b /from 2026-09-18 /to 2026-09-19"));
    }

    @Test
    public void parse_eventWithToBeforeFrom_throwsUsageError() {
        assertThrows(ThaisBotException.class, () ->
                parser.parse("event weird /to 2026-09-18 /from 2026-09-19"));
        assertThrows(ThaisBotException.class, () -> parser.parse("event no start /to 2026-09-18"));
    }

    @Test
    public void parse_nonExistentDate_throwsException() {
        assertThrows(ThaisBotException.class, () -> parser.parse("deadline a /by 2026-02-30"));
        assertThrows(ThaisBotException.class, () -> parser.parseDate("2026-13-01"));
    }

    @Test
    public void load_corruptedFile_backsUpDataAndThrows() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        List<String> corruptedLines = List.of("T | 0 | read book", "X | ? | broken");
        Files.write(dataFile, corruptedLines);
        Storage storage = new Storage(dataFile.toString(), parser);

        ThaisBotException e = assertThrows(ThaisBotException.class, storage::load);

        assertTrue(e.getMessage().contains("line 2"));
        assertEquals(corruptedLines, Files.readAllLines(tempDir.resolve("tasks.txt.bak")));
    }
}
