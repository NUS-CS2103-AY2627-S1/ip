package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests {@link SortKey}, which decides what order {@code sort} puts the tasks
 * into.
 *
 * <p>These comparators are worth testing closely because every one of their
 * rules is invisible in the reply: a list that comes back in the wrong order
 * still looks like a perfectly good list, and the user has no way of telling
 * that the bot meant something else by "sorted". Three of the rules in
 * particular would survive being broken without anything else noticing:</p>
 *
 * <ul>
 *   <li>a dateless task sorts last in <em>both</em> directions, which a
 *       comparator that simply reversed everything would get wrong only for
 *       {@code desc};</li>
 *   <li>an event is ordered by when it starts, not when it ends, which shows
 *       only for an event that spans another task's date;</li>
 *   <li>tasks that tie keep the order they were already in, which shows only
 *       when two tasks really do tie.</li>
 * </ul>
 */
public class SortKeyTest {

    /**
     * Returns the descriptions of the given tasks in the order that sorting them
     * by the given key leaves them in, so a case can say what it expects to see
     * rather than how to look for it.
     *
     * @param key       What to order by.
     * @param direction Whether that order runs forwards or backwards.
     * @param tasks     The tasks to sort, in the order they start out in.
     * @return Each task's description, in the sorted order.
     */
    private static List<String> sortDescriptions(SortKey key, SortDirection direction,
            Task... tasks) {
        List<Task> orderedTasks = new ArrayList<>(List.of(tasks));
        orderedTasks.sort(key.getComparator(direction));
        return orderedTasks.stream().map(Task::getDescription).toList();
    }

    // ---------- Sorting by date ----------

    @Test
    public void getComparator_dateAscending_ordersDatedTasksEarliestFirst()
            throws PiplupBotException {
        assertEquals(List.of("meeting", "return book"),
                sortDescriptions(SortKey.DATE, SortDirection.ASC,
                        new Deadline("return book", "2019-10-15 1800"),
                        new Event("meeting", "2019-10-02 1400", "2019-10-02 1600")));
    }

    /**
     * A todo has no date, and is therefore put after every task that has one --
     * not treated as a date at one end of time, which is what an
     * {@code Optional} of {@code LocalDateTime.MIN} would amount to.
     */
    @Test
    public void getComparator_dateAscending_putsTodosAfterEveryDatedTask()
            throws PiplupBotException {
        assertEquals(List.of("meeting", "return book", "read book", "write notes"),
                sortDescriptions(SortKey.DATE, SortDirection.ASC,
                        new Todo("read book"),
                        new Deadline("return book", "2019-10-15 1800"),
                        new Todo("write notes"),
                        new Event("meeting", "2019-10-02 1400", "2019-10-02 1600")));
    }

    /**
     * The decision this case guards: {@code desc} reverses the dates but not the
     * grouping, so the todos stay at the bottom. Reversing the whole comparator
     * -- the obvious way to write it -- would float them to the top instead,
     * burying the very tasks the user asked to see.
     */
    @Test
    public void getComparator_dateDescending_reversesTheDatesButStillPutsTodosLast()
            throws PiplupBotException {
        assertEquals(List.of("return book", "meeting", "read book", "write notes"),
                sortDescriptions(SortKey.DATE, SortDirection.DESC,
                        new Todo("read book"),
                        new Deadline("return book", "2019-10-15 1800"),
                        new Todo("write notes"),
                        new Event("meeting", "2019-10-02 1400", "2019-10-02 1600")));
    }

    /**
     * An event is ordered by when it starts. The event below begins before the
     * deadline and ends after it, so sorting by the end time instead would put
     * the two the other way round -- the one arrangement of dates that tells the
     * two rules apart.
     */
    @Test
    public void getComparator_dateKey_ordersAnEventByItsStartNotItsEnd()
            throws PiplupBotException {
        assertEquals(List.of("conference", "return book"),
                sortDescriptions(SortKey.DATE, SortDirection.ASC,
                        new Deadline("return book", "2019-10-15 1800"),
                        new Event("conference", "2019-10-01 0900", "2019-10-30 1700")));
    }

    /**
     * Two tasks due at the same moment keep the order they were already in.
     * Nothing in {@link SortKey} arranges that -- it is {@code List.sort} being
     * stable -- so this case is what would notice a tie-breaking rule being
     * added, or a sort that is not stable being used instead.
     */
    @Test
    public void getComparator_datesThatTie_keepsTheOrderTheyWereAlreadyIn()
            throws PiplupBotException {
        assertEquals(List.of("standup", "report"),
                sortDescriptions(SortKey.DATE, SortDirection.ASC,
                        new Event("standup", "2019-10-02 1400", "2019-10-02 1600"),
                        new Deadline("report", "2019-10-02 1400")));
    }

    // ---------- Sorting by the other three keys ----------

    /**
     * Alphabetical order ignores capitals, as {@code find} does, so a
     * capitalized description sorts among the others rather than ahead of all of
     * them -- which is what plain {@code String} comparison would do, since every
     * capital letter has a lower character code than every small one.
     */
    @Test
    public void getComparator_nameKey_ignoresCapitals() {
        assertEquals(List.of("apples", "Bananas", "cherries"),
                sortDescriptions(SortKey.NAME, SortDirection.ASC,
                        new Todo("cherries"), new Todo("Bananas"), new Todo("apples")));
    }

    @Test
    public void getComparator_nameDescending_reversesTheAlphabet() {
        assertEquals(List.of("cherries", "Bananas", "apples"),
                sortDescriptions(SortKey.NAME, SortDirection.DESC,
                        new Todo("cherries"), new Todo("Bananas"), new Todo("apples")));
    }

    /**
     * Capitals are ignored the same way on every machine. With Turkish as the
     * default language, lower-casing in that language would turn "Ibis" into a
     * word whose dotless first letter sorts after every plain one, so "ice"
     * would come first.
     *
     * <p>The default language is shared by the whole program, so it is put back
     * in a {@code finally} block, as {@link DateTimesTest} does.</p>
     */
    @Test
    public void getComparator_nameKeyWithTurkishDefaultLocale_stillIgnoresCapitals() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(List.of("Ibis", "ice"),
                    sortDescriptions(SortKey.NAME, SortDirection.ASC, new Todo("ice"), new Todo("Ibis")));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void getComparator_typeKey_ordersTodosThenDeadlinesThenEvents()
            throws PiplupBotException {
        assertEquals(List.of("read book", "return book", "meeting"),
                sortDescriptions(SortKey.TYPE, SortDirection.ASC,
                        new Event("meeting", "2019-10-02 1400", "2019-10-02 1600"),
                        new Deadline("return book", "2019-10-15 1800"),
                        new Todo("read book")));
    }

    @Test
    public void getComparator_statusKey_putsUnfinishedWorkFirst() {
        Todo finished = new Todo("read book");
        finished.markAsDone();

        assertEquals(List.of("write notes", "read book"),
                sortDescriptions(SortKey.STATUS, SortDirection.ASC,
                        finished, new Todo("write notes")));
    }

    @Test
    public void getComparator_statusDescending_putsFinishedWorkFirst() {
        Todo finished = new Todo("read book");
        finished.markAsDone();

        assertEquals(List.of("read book", "write notes"),
                sortDescriptions(SortKey.STATUS, SortDirection.DESC,
                        finished, new Todo("write notes")));
    }

    /**
     * Every task ties on a key they all share, and they all keep their places.
     * This is the same stability the date case relies on, checked where a tie is
     * the rule rather than the exception: it is what lets one sort refine
     * another instead of undoing it.
     */
    @Test
    public void getComparator_tasksThatAllTie_leavesTheOrderAlone() {
        assertEquals(List.of("c", "a", "b"),
                sortDescriptions(SortKey.TYPE, SortDirection.ASC,
                        new Todo("c"), new Todo("a"), new Todo("b")));
    }

    // ---------- Reading the keyword ----------

    @Test
    public void fromKeyword_everyKeyword_returnsThatKey() throws PiplupBotException {
        for (SortKey key : SortKey.values()) {
            assertEquals(key, SortKey.fromKeyword(key.getKeyword()));
        }
    }

    /**
     * The match is exact, so a capitalized key is refused rather than guessed
     * at -- the same rule the command words themselves follow.
     */
    @Test
    public void fromKeyword_capitalisedOrUnknownWord_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> SortKey.fromKeyword("Date"));
        assertThrows(PiplupBotException.class, () -> SortKey.fromKeyword("chronological"));
        assertThrows(PiplupBotException.class, () -> SortKey.fromKeyword(""));
    }

    /**
     * {@code done} was the old name of {@link SortKey#STATUS}. It is refused
     * rather than kept as a second spelling, because it read as "done tasks
     * first" -- the opposite of the order it gave. A user who types it is shown
     * the hint, which names {@code status} instead.
     */
    @Test
    public void fromKeyword_oldDoneKeyword_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> SortKey.fromKeyword("done"));
    }

    /**
     * The refusal has to leave the user able to try again, so it quotes what
     * they typed and lists every key. Checking the list in full also checks the
     * grammar that builds it -- the commas, and the "or" before the last one.
     */
    @Test
    public void fromKeyword_unknownWord_messageQuotesItAndListsEveryKey() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> SortKey.fromKeyword("xyz"));

        assertArrayEquals(new String[] {
            "Pip... I don't know how to sort by \"xyz\".",
            "Try: date, name, type, or status.",
        }, exception.getMessageLines());
    }

    @Test
    public void getKeywordHint_namesEveryKeyInDeclarationOrder() {
        assertEquals("Try: date, name, type, or status.", SortKey.getKeywordHint());
    }
}
