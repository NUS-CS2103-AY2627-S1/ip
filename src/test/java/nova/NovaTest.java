package nova;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests that drive the chatbot the way a front end does, through
 * getResponse, with a real save file in a temporary folder.
 */
public class NovaTest {

    @Test
    public void getGreeting_always_welcomesAndIsNotAnError() {
        Reply greeting = novaIn(Path.of("unused")).getGreeting();

        assertTrue(greeting.text().startsWith("Nova ready."));
        assertFalse(greeting.isError());
    }

    @Test
    public void loadTasks_noSaveFile_emptyReplyThatIsNotAnError(@TempDir Path dir) {
        Reply loaded = novaIn(dir).loadTasks();

        assertTrue(loaded.isEmpty());
        assertFalse(loaded.isError());
    }

    @Test
    public void loadTasks_damagedSaveFile_reportsSkippedLinesAsAnError(@TempDir Path dir)
            throws IOException {
        Files.write(dir.resolve("nova.txt"), List.of("T | 0 | read book", "nonsense"));
        Nova nova = novaIn(dir);

        Reply loaded = nova.loadTasks();

        assertTrue(loaded.isError());
        assertTrue(loaded.text().contains("Skipped 1"));
        assertTrue(nova.getResponse("list").text().contains("read book"));
    }

    @Test
    public void getResponse_addThenList_showsTheTask(@TempDir Path dir) {
        Nova nova = started(dir);

        assertFalse(nova.getResponse("todo read book").isError());
        assertTrue(nova.getResponse("list").text().contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_unknownCommand_isAnErrorAndChangesNothing(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("todo read book");

        Reply reply = nova.getResponse("blah");

        assertTrue(reply.isError());
        assertTrue(nova.getResponse("list").text().contains("1 task in your list."));
    }

    @Test
    public void getResponse_duplicateTask_isRejected(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("todo read book");

        Reply reply = nova.getResponse("todo READ BOOK");

        assertTrue(reply.isError());
        assertTrue(reply.text().contains("already task 1"));
    }

    @Test
    public void getResponse_markThenUnmark_updatesTheStatusIcon(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("todo read book");

        assertTrue(nova.getResponse("mark 1").text().contains("[T][X] read book"));
        assertTrue(nova.getResponse("unmark 1").text().contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_delete_removesTheTask(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("todo read book");

        assertTrue(nova.getResponse("delete 1").text().contains("0 tasks left."));
        assertTrue(nova.getResponse("list").text().contains("Your list is empty."));
    }

    @Test
    public void getResponse_changes_surviveARestart(@TempDir Path dir) {
        Nova first = started(dir);
        first.getResponse("deadline return book /by 2019-12-02 1800");
        first.getResponse("todo read book");
        first.getResponse("mark 2");

        Nova second = started(dir);
        String list = second.getResponse("list").text();

        assertTrue(list.contains("[D][ ] return book (by: Dec 02 2019, 6:00pm)"));
        assertTrue(list.contains("[T][X] read book"));
    }

    @Test
    public void getResponse_sort_reordersAndPersists(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("todo zebra");
        nova.getResponse("deadline due soon /by 2019-01-01");
        nova.getResponse("sort");

        String reloaded = started(dir).getResponse("list").text();
        int deadlineAt = reloaded.indexOf("due soon");
        int todoAt = reloaded.indexOf("zebra");

        assertTrue(deadlineAt < todoAt, "the dated task should come first after sorting");
    }

    @Test
    public void getResponse_find_showsOnlyMatches(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("todo read book");
        nova.getResponse("todo buy milk");

        String found = nova.getResponse("find book").text();

        assertTrue(found.contains("read book"));
        assertFalse(found.contains("buy milk"));
    }

    @Test
    public void getResponse_on_showsTasksFallingOnThatDate(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("deadline return book /by 2019-12-02");
        nova.getResponse("todo no date");

        String onThatDay = nova.getResponse("on 2019-12-02").text();

        assertTrue(onThatDay.contains("return book"));
        assertFalse(onThatDay.contains("no date"));
    }

    @Test
    public void getResponse_help_listsTheCommands(@TempDir Path dir) {
        String help = started(dir).getResponse("help").text();

        assertTrue(help.contains("deadline"));
        assertTrue(help.contains("sort"));
    }

    @Test
    public void getResponse_taskNumberOutOfRange_isAnErrorNamingTheListLength(@TempDir Path dir) {
        Nova nova = started(dir);
        nova.getResponse("todo read book");

        Reply reply = nova.getResponse("mark 5");

        assertTrue(reply.isError());
        assertTrue(reply.text().contains("There is no task 5. Your list has 1."));
    }

    @Test
    public void getResponse_deleteOnEmptyList_isAnError(@TempDir Path dir) {
        Reply reply = started(dir).getResponse("delete 1");

        assertTrue(reply.isError());
    }

    @Test
    public void getResponse_descriptionContainingTheFieldSeparator_survivesARestart(@TempDir Path dir) {
        Nova first = started(dir);
        first.getResponse("todo read a | b");

        assertTrue(started(dir).getResponse("list").text().contains("[T][ ] read a | b"));
    }

    @Test
    public void isExit_onlyAfterBye(@TempDir Path dir) {
        Nova nova = started(dir);

        nova.getResponse("list");
        assertFalse(nova.isExit());

        nova.getResponse("bye");
        assertTrue(nova.isExit());
    }

    @Test
    public void getResponse_saveFileIsWrittenWhereAsked(@TempDir Path dir) {
        Nova nova = started(dir);

        nova.getResponse("todo read book");

        assertTrue(Files.exists(dir.resolve("nova.txt")));
    }

    /** Creates a chatbot saving into the given folder, without loading yet. */
    private static Nova novaIn(Path dir) {
        return new Nova(dir.resolve("nova.txt").toString());
    }

    /** Creates a chatbot and loads whatever is already saved, as a front end would. */
    private static Nova started(Path dir) {
        Nova nova = novaIn(dir);
        nova.loadTasks();
        return nova;
    }
}
