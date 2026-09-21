package goat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import goat.GoatException;

/**
 * Tests that Goat behaves the same whatever language the machine is set to.
 *
 * Turkish is the case worth testing because of the dotless i: there,
 * "TITLE".toLowerCase() is "tıtle", so a case-insensitive search folded with
 * the default locale silently stops matching. The same app would find
 * different things on two machines, and the user would have no way to tell
 * why.
 */
public class LocaleTest {

    private final Locale originalLocale = Locale.getDefault();

    @AfterEach
    public void tearDown() {
        Locale.setDefault(originalLocale);
    }

    private static TaskList listOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new Todo(description));
        }
        return tasks;
    }

    @Test
    public void find_turkishLocale_stillMatchesAcrossCase() {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));

        assertEquals(1, listOf("TITLE deed").find("title").size());
        assertEquals(1, listOf("title deed").find("TITLE").size());
    }

    @Test
    public void find_turkishLocale_agreesWithEnglish() {
        Locale.setDefault(Locale.ENGLISH);
        int inEnglish = listOf("TITLE deed", "Illinois", "irrelevant").find("i").size();

        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        int inTurkish = listOf("TITLE deed", "Illinois", "irrelevant").find("i").size();

        assertEquals(inEnglish, inTurkish);
    }

    @Test
    public void find_lithuanianLocale_stillMatches() {
        // Lithuanian has its own lowercasing rules for accented capitals.
        Locale.setDefault(Locale.forLanguageTag("lt-LT"));

        assertEquals(1, listOf("TITLE deed").find("title").size());
    }

    @Test
    public void sortByName_turkishLocale_sameOrderAsEnglish() throws GoatException {
        List<String> expected = new ArrayList<>();
        Locale.setDefault(Locale.ENGLISH);
        TaskList inEnglish = listOf("India", "iceland", "IRAN");
        inEnglish.sort(SortOrder.NAME);
        for (int i = 1; i <= inEnglish.size(); i++) {
            expected.add(inEnglish.get(i).getDescription());
        }

        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        TaskList inTurkish = listOf("India", "iceland", "IRAN");
        inTurkish.sort(SortOrder.NAME);
        List<String> actual = new ArrayList<>();
        for (int i = 1; i <= inTurkish.size(); i++) {
            actual.add(inTurkish.get(i).getDescription());
        }

        assertEquals(expected, actual);
    }

    @Test
    public void taskFormatting_turkishLocale_monthNamesStayEnglish() {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));

        Deadline deadline = new Deadline("x", java.time.LocalDateTime.of(2019, 12, 2, 18, 0));

        // DateTimes pins Locale.ENGLISH on its formatters, so a save file
        // written on one machine still reads on another.
        assertEquals("[D][ ] x (by: Dec 02 2019, 6:00pm)", deadline.toString());
        assertEquals("D | 0 | x | 2019-12-02 1800", deadline.toFileFormat());
    }
}
