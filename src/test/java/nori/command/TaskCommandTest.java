package nori.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nori.testutil.IsolatedSessionTest;

/**
 * Tests each command that adds, changes, removes or queries a task.
 *
 * Every test runs a whole session, so a command is checked through the same
 * path a user reaches it by: parsed from text, run against a real task list,
 * saved to a real file, and answered in the words the user actually sees.
 */
public class TaskCommandTest extends IsolatedSessionTest {

    @Test
    public void todo_withDescription_addsTheTask() {
        assertEquals("Noot noot! Task tucked safely under my wing:\n"
                + "  [T][ ] read book\n"
                + "The iceberg now holds 1 task(s).",
                runCommands("todo read book"));
    }

    @Test
    public void todo_withoutDescription_isRefused() {
        assertEquals("NOOT?! A todo needs a description."
                + " Try \"todo borrow book\"; my flippers cannot read minds.",
                runCommands("todo"));
        assertTrue(isLastCommandRefused("todo"));
    }

    @Test
    public void todo_withOnlySpacesForADescription_isRefused() {
        assertTrue(isLastCommandRefused("todo    "));
    }

    @Test
    public void todo_withExtraSpacesAroundTheDescription_keepsTheDescriptionIntact() {
        assertEquals("Noot noot! Task tucked safely under my wing:\n"
                + "  [T][ ] read   book\n"
                + "The iceberg now holds 1 task(s).",
                runCommands("todo    read   book   "));
    }

    @Test
    public void list_emptyList_saysSo() {
        assertEquals("The iceberg is empty. Try \"todo borrow book\". Noot noot!",
                runCommands("list"));
    }

    @Test
    public void list_severalTasks_numbersThemFromOne() {
        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][ ] write essay",
                runCommands("todo read book", "todo write essay", "list"));
    }

    @Test
    public void mark_validNumber_completesTheTask() {
        assertEquals("Noot noot! This task is now ice-cold complete:\n"
                + "  [T][X] read book",
                runCommands("todo read book", "mark 1"));
    }

    @Test
    public void mark_taskAlreadyDone_saysSoAndChangesNothing() {
        assertEquals("Noot noot! That task is already frozen solid (done).",
                runCommands("todo read book", "mark 1", "mark 1"));
        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n1.[T][X] read book",
                runCommands("todo read book", "mark 1", "mark 1", "list"));
    }

    @Test
    public void unmark_completedTask_reopensIt() {
        assertEquals("Brrr... thawing this task back out:\n"
                + "  [T][ ] read book",
                runCommands("todo read book", "mark 1", "unmark 1"));
    }

    @Test
    public void unmark_taskAlreadyOpen_saysSoAndChangesNothing() {
        assertEquals("Noot noot! That task is already thawed (not done).",
                runCommands("todo read book", "unmark 1"));
    }

    @Test
    public void unmark_completedTask_survivesAReload() {
        runCommands("todo read book", "mark 1", "unmark 1");

        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n1.[T][ ] read book",
                runCommands("list"));
    }

    @Test
    public void delete_validNumber_removesTheTaskAndReportsTheCount() {
        assertEquals("Splash! I kicked this task off the iceberg:\n"
                + "  [T][ ] read book\n"
                + "The iceberg now holds 1 task(s).",
                runCommands("todo read book", "todo write essay", "delete 1"));
    }

    @Test
    public void delete_firstTask_renumbersTheRest() {
        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n"
                + "1.[T][ ] write essay",
                runCommands("todo read book", "todo write essay", "delete 1", "list"));
    }

    @Test
    public void delete_lastRemainingTask_emptiesTheList() {
        assertEquals("The iceberg is empty. Try \"todo borrow book\". Noot noot!",
                runCommands("todo read book", "delete 1", "list"));
    }

    @Test
    public void taskNumber_missing_namesTheCommandThatNeedsOne() {
        assertEquals("NOOT?! \"mark\" needs a task number. Try \"mark 1\".",
                runCommands("todo read book", "mark"));
        assertEquals("NOOT?! \"unmark\" needs a task number. Try \"unmark 1\".",
                runCommands("todo read book", "unmark"));
        assertEquals("NOOT?! \"delete\" needs a task number. Try \"delete 1\".",
                runCommands("todo read book", "delete"));
    }

    @Test
    public void taskNumber_zeroOrNegative_saysNumbersStartAtOne() {
        assertEquals("NOOT?! Task numbers start from 1, not 0. Penguins can count!",
                runCommands("todo read book", "mark 0"));
        assertEquals("NOOT?! Task numbers start from 1, not -3. Penguins can count!",
                runCommands("todo read book", "delete -3"));
    }

    @Test
    public void taskNumber_pastTheEndOfTheList_saysHowManyThereAre() {
        assertEquals("NOOT?! The iceberg has only 1 task(s), so \"delete 2\""
                + " points straight into the sea.",
                runCommands("todo read book", "delete 2"));
    }

    @Test
    public void taskNumber_onAnEmptyList_saysThereIsNothingToDo() {
        assertEquals("NOOT?! The iceberg is empty, so there is nothing to delete.",
                runCommands("delete 1"));
    }

    @Test
    public void taskNumber_notANumber_saysToUseDigits() {
        assertEquals("NOOT?! \"one\" is not a task number."
                + " Use \"mark 1\"; penguins count with digits.",
                runCommands("todo read book", "mark one"));
    }

    @Test
    public void taskNumber_tooLargeForAWholeNumber_saysSoWithoutCrashing() {
        assertEquals("GIANT NOOT! \"99999999999999999999\" is far too large for a task number."
                + " Use a whole number from 1 to 1.",
                runCommands("todo read book", "delete 99999999999999999999"));
    }

    @Test
    public void find_matchingKeyword_showsTheFullListNumbers() {
        assertEquals("Noot noot! I found these fish:\n"
                + "2.[T][ ] read book",
                runCommands("todo write essay", "todo read book", "find read"));
    }

    @Test
    public void find_keywordInAnotherCase_stillMatches() {
        assertEquals("Noot noot! I found these fish:\n"
                + "1.[T][ ] Read Book",
                runCommands("todo Read Book", "find read"));
    }

    @Test
    public void find_keywordMatchingNothing_saysSo() {
        assertEquals("No matching fish in this sea. Try another keyword!",
                runCommands("todo read book", "find penguin"));
    }

    @Test
    public void find_withoutAKeyword_isRefused() {
        assertEquals("NOOT?! \"find\" needs a keyword."
                + " Try \"find book\"; even penguins need a clue.",
                runCommands("find"));
        assertTrue(isLastCommandRefused("find"));
    }

    @Test
    public void find_keywordMatchingOnlyADate_doesNotMatch() {
        assertEquals("No matching fish in this sea. Try another keyword!",
                runCommands("deadline report /by 2019-06-06", "find 2019"));
    }

    @Test
    public void on_dateWithTasks_showsTheDeadlinesAndEvents() {
        assertEquals("Noot noot! Things hatching on 2019-06-06:\n"
                + "1.[D][ ] report (by: Jun 06 2019)",
                runCommands("deadline report /by 2019-06-06", "todo read book", "on 2019-06-06"));
    }

    @Test
    public void on_quietDate_saysSo() {
        assertEquals("Nothing is hatching on 2019-06-07. The ice is quiet.",
                runCommands("deadline report /by 2019-06-06", "on 2019-06-07"));
    }

    @Test
    public void on_withoutADate_isRefused() {
        assertEquals("NOOT?! \"on\" needs a date. Try \"on 2019-10-15\".", runCommands("on"));
        assertTrue(isLastCommandRefused("on"));
    }

    @Test
    public void list_dateRange_showsOnlyTheTasksInside() {
        assertEquals("Noot noot! Things hatching from 2019-01-01 to 2019-12-31:\n"
                + "1.[D][ ] report (by: Jun 06 2019)",
                runCommands("deadline report /by 2019-06-06", "deadline later /by 2021-06-06",
                        "list /from 2019-01-01 /to 2019-12-31"));
    }

    @Test
    public void list_dateRangeWithNothingInIt_saysSo() {
        assertEquals("Nothing is hatching from 2020-01-01 to 2020-12-31. The ice is quiet.",
                runCommands("deadline report /by 2019-06-06",
                        "list /from 2020-01-01 /to 2020-12-31"));
    }

    @Test
    public void list_reversedDateRange_isRefused() {
        assertEquals("NOOT?! The \"/to\" date cannot be before the \"/from\" date."
                + " Time only waddles forward.",
                runCommands("list /from 2021-01-02 /to 2021-01-01"));
    }

    @Test
    public void list_singleDayRange_includesThatDay() {
        assertEquals("Noot noot! Things hatching from 2019-06-06 to 2019-06-06:\n"
                + "1.[D][ ] report (by: Jun 06 2019)",
                runCommands("deadline report /by 2019-06-06",
                        "list /from 2019-06-06 /to 2019-06-06"));
    }

    @Test
    public void help_always_listsEveryCommand() {
        String response = runCommands("help");

        assertTrue(response.startsWith("Noot noot! Here is what my flippers understand:"));
        for (String keyword : new String[] {"todo", "deadline", "event", "on", "schedule",
            "list", "find", "mark", "unmark", "delete", "help", "bye"}) {
            assertTrue(response.contains(keyword), "help should mention " + keyword);
        }
    }

    @Test
    public void help_withTrailingText_stillHelps() {
        assertEquals(runCommands("help"), runCommands("help me"));
    }

    @Test
    public void bye_always_saysGoodbye() {
        assertEquals("Noot noot! Time to waddle off. Stay frosty!", runCommands("bye"));
        assertFalse(isLastCommandRefused("bye"));
    }

    @Test
    public void addedTasks_afterEachCommand_areOnDiskForTheNextSession() {
        runCommands("todo read book", "deadline report /by 2019-06-06",
                "event fair /from 1pm /to 3pm", "mark 2");

        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][X] report (by: Jun 06 2019)\n"
                + "3.[E][ ] fair (from: 1pm to: 3pm)",
                runCommands("list"));
    }
}
