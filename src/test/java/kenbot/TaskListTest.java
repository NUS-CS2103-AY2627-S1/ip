package kenbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import kenbot.task.Deadline;
import kenbot.task.Todo;

/**
 * Tests {@link TaskList}, which owns the tasks and checks every task number
 * before using it.
 */
public class TaskListTest {

    /**
     * Builds a list of to-dos named a, b, c... so positions are easy to read.
     * The descriptions are distinct, so the duplicate check never fires here.
     */
    private static TaskList listOf(String... descriptions) throws KenbotException {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new Todo(description));
        }
        return tasks;
    }

    @Test
    public void describe_emptyList_saysSoInsteadOfShowingNothing() throws KenbotException {
        assertEquals("Nothing on the list yet.", new TaskList().describe());
    }

    @Test
    public void describe_threeTasks_numbersThemFromOne() throws KenbotException {
        assertEquals("Here's what you've got:"
                + "\n1.[T][ ] a\n2.[T][ ] b\n3.[T][ ] c", listOf("a", "b", "c").describe());
    }

    /**
     * Removing from the middle has to close the gap, or the numbers the user
     * sees would stop matching the numbers they can type.
     */
    @Test
    public void delete_middleTask_closesTheGapInTheNumbering() throws KenbotException {
        TaskList tasks = listOf("a", "b", "c");
        assertEquals("[T][ ] b", tasks.delete("2").toString());
        assertEquals(2, tasks.size());
        assertEquals("Here's what you've got:\n1.[T][ ] a\n2.[T][ ] c",
                tasks.describe());
    }

    @Test
    public void mark_validNumber_showsTheTaskAsDone() throws KenbotException {
        TaskList tasks = listOf("a");
        assertEquals("[T][X] a", tasks.mark("1").toString());
        assertTrue(tasks.describe().contains("[T][X] a"));
    }

    @Test
    public void unmark_taskThatWasDone_showsItAsNotDone() throws KenbotException {
        TaskList tasks = listOf("a");
        tasks.mark("1");
        assertEquals("[T][ ] a", tasks.unmark("1").toString());
    }

    @Test
    public void delete_numberPastTheEnd_throwsAndSaysHowManyThereAre() throws KenbotException {
        KenbotException thrown = assertThrows(KenbotException.class, () -> listOf("a").delete("5"));
        assertEquals("There's no task 5. You've got 1.", thrown.getMessage());
    }

    @Test
    public void delete_onEmptyList_saysTheListIsEmpty() throws KenbotException {
        KenbotException thrown = assertThrows(KenbotException.class, () -> new TaskList().delete("1"));
        assertEquals("There's no task 1, the list is empty.", thrown.getMessage());
    }

    @Test
    public void mark_notANumber_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").mark("abc"));
    }

    @Test
    public void mark_noNumberGiven_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").mark(""));
    }

    @Test
    public void mark_zero_throwsBecauseNumbersStartAtOne() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").mark("0"));
    }

    @Test
    public void find_keywordInSomeDescriptions_listsOnlyThoseNumberedFromOne()
            throws KenbotException {
        TaskList tasks = listOf("read book", "join club", "return book");
        assertEquals("Found these:"
                + "\n1.[T][ ] read book\n2.[T][ ] return book", tasks.find("book"));
    }

    @Test
    public void find_differentCase_stillMatches() throws KenbotException {
        assertTrue(listOf("join sports club").find("CLUB").contains("join sports club"));
    }

    @Test
    public void find_partOfAWord_matches() throws KenbotException {
        assertTrue(listOf("bookshop").find("book").contains("bookshop"));
    }

    @Test
    public void find_nothingMatches_saysSoInsteadOfAnEmptyHeading() throws KenbotException {
        assertEquals("Nothing matches 'xyz'.", listOf("read book").find("xyz"));
    }

    @Test
    public void find_noKeyword_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("read book").find("  "));
    }

    /**
     * The list handed out for reading must not be a way to change the real one.
     */
    @Test
    public void getTasks_tryingToAddToTheCopy_isRefused() throws KenbotException {
        TaskList tasks = listOf("a");
        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks().add(new Todo("b")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_exactDuplicate_isRefused() throws KenbotException {
        TaskList tasks = listOf("read book");
        KenbotException thrown = assertThrows(KenbotException.class, () ->
                tasks.add(new Todo("read book")));
        assertTrue(thrown.getMessage().contains("already on the list"));
        assertEquals(1, tasks.size(), "the refused task must not be stored");
    }

    @Test
    public void add_sameTextDifferentCase_isAllowed() throws KenbotException {
        // Treated as different on purpose, so a deliberate second task is never
        // silently refused.
        TaskList tasks = listOf("read book");
        tasks.add(new Todo("Read book"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void add_duplicateOfADoneTask_isStillRefused() throws KenbotException {
        // Ticking something off does not make it a different task.
        TaskList tasks = listOf("read book");
        tasks.mark("1");
        assertThrows(KenbotException.class, () -> tasks.add(new Todo("read book")));
    }

    @Test
    public void add_sameDescriptionDifferentType_isAllowed() throws KenbotException {
        TaskList tasks = listOf("read book");
        tasks.add(Deadline.of("read book /by 2019-10-15"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void tag_oneTag_showsItOnTheTask() throws KenbotException {
        assertEquals("[T][ ] a #fun", listOf("a").tag("1 fun").toString());
    }

    @Test
    public void tag_leadingHash_isAccepted() throws KenbotException {
        assertEquals("[T][ ] a #fun", listOf("a").tag("1 #fun").toString());
    }

    @Test
    public void tag_severalTags_addsThemAllInOrder() throws KenbotException {
        assertEquals("[T][ ] a #fun #work", listOf("a").tag("1 fun work").toString());
    }

    @Test
    public void tag_tagTheTaskAlreadyHas_throwsRatherThanDoingNothingQuietly()
            throws KenbotException {
        TaskList tasks = listOf("a");
        tasks.tag("1 fun");
        assertThrows(KenbotException.class, () -> tasks.tag("1 fun"));
    }

    @Test
    public void tag_noTagGiven_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").tag("1"));
    }

    @Test
    public void tag_numberOutOfRange_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").tag("2 fun"));
    }

    @Test
    public void tag_unusableTag_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").tag("1 bad|tag"));
    }

    @Test
    public void tag_oneBadTagAmongGoodOnes_leavesTheTaskUnchanged() throws KenbotException {
        TaskList tasks = listOf("a");
        assertThrows(KenbotException.class, () -> tasks.tag("1 fun bad|tag"));
        assertEquals("Here's what you've got:\n1.[T][ ] a", tasks.describe());
    }

    @Test
    public void untag_tagThatIsThere_removesIt() throws KenbotException {
        TaskList tasks = listOf("a");
        tasks.tag("1 fun work");
        assertEquals("[T][ ] a #work", tasks.untag("1 fun").toString());
    }

    @Test
    public void untag_tagThatIsNotThere_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").untag("1 fun"));
    }

    @Test
    public void find_hashKeyword_searchesTagsNotDescriptions() throws KenbotException {
        TaskList tasks = listOf("read book", "buy milk");
        tasks.tag("2 fun");
        assertEquals("Found these:\n1.[T][ ] buy milk #fun",
                tasks.find("#fun"));
    }

    @Test
    public void find_hashKeywordPartOfATag_stillMatches() throws KenbotException {
        TaskList tasks = listOf("a");
        tasks.tag("1 cs2103");
        assertTrue(tasks.find("#cs").contains("#cs2103"));
    }

    @Test
    public void find_hashKeywordInADifferentCase_stillMatches() throws KenbotException {
        // Two tags differing only in case are different tags, but searching
        // ignores case so that find behaves the same way for tags as for
        // descriptions.
        TaskList tasks = listOf("a");
        tasks.tag("1 fun");
        assertTrue(tasks.find("#FUN").contains("#fun"));
    }

    @Test
    public void find_plainKeywordMatchingATag_doesNotMatch() throws KenbotException {
        TaskList tasks = listOf("read book");
        tasks.tag("1 fun");
        assertEquals("Nothing matches 'fun'.", tasks.find("fun"));
    }

    @Test
    public void find_hashAlone_throws() throws KenbotException {
        assertThrows(KenbotException.class, () -> listOf("a").find("#"));
    }
}
