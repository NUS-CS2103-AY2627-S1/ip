package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Checks locale-sensitive behavior while preserving all of the original JVM locale defaults. */
@ResourceLock("DEFAULT_LOCALE")
class LocaleBehaviorTest {
    /** Date display, storage, and search must remain predictable in different JVM languages.
     *
     * @param languageTag the locale to use temporarily.
     */
    @ParameterizedTest
    @ValueSource(strings = {"en-US", "zh-CN", "tr-TR"})
    void datesAndSearchDoNotDependOnDefaultLocale(String languageTag) throws SevenSixException {
        Locale previous = Locale.getDefault();
        Locale previousDisplay = Locale.getDefault(Locale.Category.DISPLAY);
        Locale previousFormat = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.forLanguageTag(languageTag));
            DateTimeParser.ParsedDateTime parsed = DateTimeParser.parse("29/2/2024 1805");
            assertEquals(LocalDate.of(2024, 2, 29), parsed.getDate());
            assertEquals(LocalTime.of(18, 5), parsed.getTime());
            assertEquals("Feb 29 2024 6:05 PM", DateTimeParser.formatForDisplay(parsed.getDate(), parsed.getTime()));
            assertEquals("2024-02-29T18:05", DateTimeParser.formatForStorage(parsed.getDate(), parsed.getTime()));
            TaskList tasks = new TaskList(List.of(new Todo("FILE report"), new Todo("读书")));
            assertEquals("FILE report", tasks.find("file").get(0).getDescription());
            assertEquals("读书", tasks.find("书").get(0).getDescription());
        } finally {
            Locale.setDefault(previous);
            Locale.setDefault(Locale.Category.DISPLAY, previousDisplay);
            Locale.setDefault(Locale.Category.FORMAT, previousFormat);
        }
    }
}
