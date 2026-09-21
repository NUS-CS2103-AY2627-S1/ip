package sel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sel.exception.SelException;
import sel.parser.Parser;
import sel.task.Deadline;
import sel.task.Event;
import sel.task.TaskList;
import sel.task.ToDo;

/**
 * Checks that Sel behaves the same whatever language the machine is set to.
 *
 * <p>Turkish and Azerbaijani are the interesting cases: in those languages
 * {@code "I".toLowerCase()} is the dotless {@code "ı"}, which silently
 * breaks case-insensitive matching written with the default locale. Chinese
 * and German are included as everyday non-English settings.
 *
 * <p>These tests change the default locale after the classes under test have
 * been loaded, so they cover decisions made at call time. Anything decided
 * when a static field is first initialised - such as the date formatters in
 * {@link Parser} - keeps the locale the JVM started with, so checking those
 * means launching the whole suite in that language:
 *
 * <pre>{@code
 * ./gradlew test -Dorg.gradle.jvmargs="-Duser.language=tr -Duser.country=TR"
 * }</pre>
 */
public class LocaleIndependenceTest {

    private static final Locale TURKISH = Locale.forLanguageTag("tr-TR");
    private static final Locale CHINESE = Locale.forLanguageTag("zh-CN");

    @TempDir
    Path tempDir;

    private final Locale originalLocale = Locale.getDefault();

    @AfterEach
    public void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    // ---------- searching ----------

    @Test
    public void find_inTurkish_stillMatchesWordsContainingAnI() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("BIKE ride"));
        tasks.add(new ToDo("swim"));

        Locale.setDefault(TURKISH);

        assertEquals(1, tasks.find("bike").size());
        assertEquals(1, tasks.find("BIKE").size());
        assertEquals(1, tasks.find("ride").size());
    }

    @Test
    public void find_inTurkish_matchesTheSameTasksAsInEnglish() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("FIX bicycle"));
        tasks.add(new ToDo("Interview prep"));
        tasks.add(new ToDo("walk dog"));

        Locale.setDefault(Locale.UK);
        int inEnglish = tasks.find("i").size();

        Locale.setDefault(TURKISH);
        int inTurkish = tasks.find("i").size();

        assertEquals(inEnglish, inTurkish);
        assertEquals(2, inTurkish);
    }

    @Test
    public void find_inChinese_behavesNormally() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        Locale.setDefault(CHINESE);

        assertEquals(1, tasks.find("BOOK").size());
    }

    @Test
    public void find_matchesNonLatinDescriptions() {
        // Descriptions are stored and searched as plain text, so a task
        // written in Chinese is searchable in any locale.
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("读书"));
        tasks.add(new ToDo("read book"));

        Locale.setDefault(CHINESE);

        assertEquals(1, tasks.find("读书").size());
    }

    // ---------- duplicate detection ----------

    @Test
    public void hasSameDetailsAs_inTurkish_stillSpotsCaseOnlyDifferences() {
        Locale.setDefault(TURKISH);

        // equalsIgnoreCase does not consult the locale, so this holds; the
        // test guards against someone "simplifying" it to toLowerCase().
        assertTrue(new ToDo("BIKE ride").hasSameDetailsAs(new ToDo("bike ride")));
        assertFalse(new ToDo("BIKE ride").hasSameDetailsAs(new ToDo("bike rides")));
    }

    // ---------- dates ----------

    @Test
    public void parseDateTime_isTheSameInEveryLocale() throws SelException {
        for (Locale locale : new Locale[] {Locale.UK, CHINESE, TURKISH, Locale.GERMANY}) {
            Locale.setDefault(locale);

            assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                Parser.parseDateTime("2019-12-02 1800"), "locale " + locale);
        }
    }

    @Test
    public void parseDateTime_stillRejectsImpossibleDatesInEveryLocale() {
        for (Locale locale : new Locale[] {Locale.UK, CHINESE, TURKISH, Locale.GERMANY}) {
            Locale.setDefault(locale);

            assertTrue(new Sel(tempDir.resolve("sel.txt").toString())
                    .getResponse("deadline x /by 2019-02-30 1800").isError(),
                "locale " + locale);
        }
    }

    @Test
    public void taskDisplayText_staysEnglishInEveryLocale() throws SelException {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("meeting",
            LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0));

        for (Locale locale : new Locale[] {Locale.UK, CHINESE, TURKISH, Locale.GERMANY}) {
            Locale.setDefault(locale);

            assertEquals("[D][ ] return book(by:Dec 2 2019, 6:00PM)",
                deadline.toString(), "locale " + locale);
            assertEquals("[E][ ] meeting(from:Dec 2 2019, 2:00PM to:Dec 2 2019, 4:00PM)",
                event.toString(), "locale " + locale);
        }
    }

    // ---------- the save file ----------

    @Test
    public void savedTasks_surviveARestartUnderADifferentLocale() {
        Path saveFile = tempDir.resolve("sel.txt");

        Locale.setDefault(TURKISH);
        Sel turkish = new Sel(saveFile.toString());
        assertFalse(turkish.getResponse("todo BIKE ride").isError());
        assertFalse(turkish.getResponse(
            "deadline return book /by 2019-12-02 1800").isError());

        Locale.setDefault(CHINESE);
        Sel chinese = new Sel(saveFile.toString());

        assertTrue(chinese.getStartupWarning().isEmpty());
        String list = chinese.getResponse("list").text();
        assertTrue(list.contains("BIKE ride"), list);
        assertTrue(list.contains("Dec 2 2019, 6:00PM"), list);
    }

    @Test
    public void nonLatinDescriptions_surviveARestart() {
        Path saveFile = tempDir.resolve("sel.txt");

        Locale.setDefault(CHINESE);
        assertFalse(new Sel(saveFile.toString()).getResponse("todo 读书").isError());

        assertTrue(new Sel(saveFile.toString()).getResponse("list").text().contains("读书"));
    }
}
