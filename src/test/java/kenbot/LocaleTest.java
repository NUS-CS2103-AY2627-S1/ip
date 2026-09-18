package kenbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import kenbot.task.Deadline;
import kenbot.task.Todo;

/**
 * Checks that the program behaves the same whatever language the computer is
 * set to.
 *
 * <p>Turkish is used because it is the case that breaks naive code:
 * {@code "LIST".toLowerCase()} there produces {@code "lıst"} with a dotless i,
 * which no longer matches the word the user typed. Before these tests existed,
 * {@code list}, {@code find} and {@code deadline} were all rejected as unknown
 * commands on a Turkish machine, while every command without an {@code I} in it
 * carried on working. Nothing in the program looked wrong, and no existing test
 * would have caught it.</p>
 *
 * <p>The default is put back after each test, since leaving it changed would
 * affect every test that runs afterwards.</p>
 */
public class LocaleTest {

    private final Locale realDefault = Locale.getDefault();

    @AfterEach
    public void restoreLocale() {
        Locale.setDefault(realDefault);
    }

    /** Switches the whole JVM to Turkish for the duration of one test. */
    private static void useTurkish() {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
    }

    @Test
    public void from_everyCommandUnderTurkish_isStillRecognised() throws KenbotException {
        useTurkish();
        for (CommandType command : CommandType.values()) {
            String typed = command.name().toLowerCase(Locale.ROOT);
            assertEquals(command, CommandType.from(typed),
                    typed + " must still be recognised on a Turkish machine");
        }
    }

    @Test
    public void from_commandsContainingI_areTheOnesThatUsedToBreak() throws KenbotException {
        useTurkish();
        assertEquals(CommandType.LIST, CommandType.from("list"));
        assertEquals(CommandType.FIND, CommandType.from("find"));
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
    }

    @Test
    public void find_underTurkish_stillMatchesRegardlessOfCase() throws KenbotException {
        useTurkish();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("READING list"));
        assertTrue(tasks.find("reading").contains("READING list"));
    }

    @Test
    public void find_tagUnderTurkish_stillMatches() throws KenbotException {
        useTurkish();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.tag("1 LIBRARY");
        assertTrue(tasks.find("#library").contains("#LIBRARY"));
    }

    @Test
    public void toString_dateUnderTurkish_readsTheSameAsAnywhereElse() throws KenbotException {
        useTurkish();
        assertEquals("[D][ ] x (by: Oct 15 2019)",
                Deadline.of("x /by 2019-10-15").toString());
    }

    @Test
    public void toString_dateUnderFrench_readsTheSameAsAnywhereElse() throws KenbotException {
        // A different trap from Turkish: month names are translated, so an
        // unpinned formatter would print "oct." here and "Oct" at home.
        Locale.setDefault(Locale.FRANCE);
        assertEquals("[D][ ] x (by: Oct 15 2019)",
                Deadline.of("x /by 2019-10-15").toString());
    }

    @Test
    public void toStorable_dateUnderTurkish_isUnchanged() throws KenbotException {
        // The save file must not depend on the machine that wrote it, or a file
        // would stop loading after a language change.
        useTurkish();
        assertEquals("D | 0 | x | 2019-10-15", Deadline.of("x /by 2019-10-15").toStorable());
    }

    @Test
    public void of_eventTimesUnderTurkish_areStillComparable() throws KenbotException {
        useTurkish();
        TaskList tasks = new TaskList();
        tasks.add(kenbot.task.Event.of("talk /from 2019-10-15 1400 /to 2019-10-15 1600"));
        assertEquals(1, tasks.size());
    }
}
