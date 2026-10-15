package jassabot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

import jassabot.parser.Parser;
import jassabot.task.TaskList;
import jassabot.task.Todo;
import jassabot.util.DateTimeFormats;

/**
 * Tests locale-independent commands, English date formatting, and Unicode persistence.
 */
@ResourceLock("java.util.Locale.default")
public class LocaleTest {
    @TempDir
    private Path directory;

    @Test
    public void commands_differentDefaultLocales_preserveDatesSearchAndUnicode() throws Exception {
        Locale original = Locale.getDefault();
        Locale originalDisplay = Locale.getDefault(Locale.Category.DISPLAY);
        Locale originalFormat = Locale.getDefault(Locale.Category.FORMAT);
        try {
            for (Locale locale : List.of(Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE,
                    Locale.forLanguageTag("tr-TR"))) {
                Locale.setDefault(locale);
                LocalDateTime date = LocalDateTime.of(2024, 2, 29, 18, 5);
                assertEquals(date, Parser.parseDateTime("29/2/2024 1805"));
                assertEquals("Feb 29 2024, 6:05 PM", DateTimeFormats.formatForDisplay(date));
                assertEquals("2024-02-29T18:05", DateTimeFormats.formatForStorage(date));
                assertEquals(date, DateTimeFormats.parseStorageDateTime("2024-02-29T18:05"));
                Todo task = new Todo("FINISH report");
                assertEquals(List.of(task), new TaskList(List.of(task)).find("finish"));

                Path dataFile = Path.of("").toAbsolutePath()
                        .relativize(directory.resolve(locale.toLanguageTag() + ".txt"));
                JassaBot bot = new JassaBot(dataFile);
                assertTrue(bot.getResponse("todo \u4e2d\u6587 \ud83c\udf31 | \\ notes")
                        .contains("[T][ ] \u4e2d\u6587 \ud83c\udf31 | \\ notes"));
                assertEquals("Here's what I found in your task garden:\n"
                        + "1.[T][ ] \u4e2d\u6587 \ud83c\udf31 | \\ notes", bot.getResponse("find \u4e2d\u6587"));
                assertEquals(bot.getResponse("list"), new JassaBot(dataFile).getResponse("list"));
            }
        } finally {
            Locale.setDefault(original);
            Locale.setDefault(Locale.Category.DISPLAY, originalDisplay);
            Locale.setDefault(Locale.Category.FORMAT, originalFormat);
        }
    }
}
