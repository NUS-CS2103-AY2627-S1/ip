package thomas.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import thomas.task.DeadlineTask;
import thomas.task.EventTask;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.TodoTask;

/**
 * Tests Ui: the wording of every message, and the printing of each block.
 * The text-UI plan drives the whole program and pins down every line the user
 * sees, so for a long time this file held only the headers of the three
 * listings. Those are here because of one defect that suite caught too late: the
 * listings share one private method taking the header as an argument, and a
 * merge once left that argument passed by all three callers and read by none, so
 * every listing opened with the {@code list} header. Nothing failed to compile,
 * and no JUnit case touched it.
 * The rest of the file pins the same things for every other message: the
 * wording each {@code get...Message} method settles, and the furniture the
 * {@code show...} methods add when printing -- the dividers, the indent on every
 * line, and that a blank message prints nothing at all. The console is captured
 * for those, so what is compared is exactly the bytes a terminal would receive.
 * The GUI shows the same words without the furniture, which is why the two are
 * tested apart.
 */
public class UiTest {

    /** Day used throughout, with one either side to make the filtering real */
    private static final LocalDate DEC_02 = LocalDate.of(2019, 12, 2);
    private static final LocalDate DEC_03 = LocalDate.of(2019, 12, 3);

    /** One divider line, as printed above and below every block */
    private static final String DIVIDER = "    ____________________________________________________________\n";

    /**
     * Returns a list holding the given tasks, in the order given.
     *
     * @param tasks Tasks to hold, in list order.
     * @return List holding those tasks.
     */
    private static TaskList listOf(Task... tasks) {
        // Built through the constructor rather than add(), which refuses a duplicate and so would make every test
        // declare the exception; what a duplicate does is tested by the add tests themselves.
        return new TaskList(new ArrayList<>(List.of(tasks)));
    }

    /**
     * Returns a deadline due at 6pm on the given day.
     *
     * @param description Task text.
     * @param day Day the task is due.
     * @return New deadline.
     */
    private static Task deadlineOn(String description, LocalDate day) {
        return new DeadlineTask(description, day.atTime(18, 0));
    }

    /**
     * Returns everything a Ui prints while an action runs on it.
     * System.out is swapped for a buffer around the action and put back
     * afterwards whatever happens, so a failing case cannot leave the rest of
     * the run printing into nowhere.
     *
     * @param action What to do with the Ui, typically one show call.
     * @return The console output, exactly as printed.
     */
    private static String printedBy(Consumer<Ui> action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.accept(new Ui());
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * Returns a Ui reading the given text as its standard input.
     * The Ui is built after the swap, since it opens its Scanner over whatever
     * System.in is at construction; the original stream is put back before
     * returning, because the Scanner keeps the one it was given.
     *
     * @param input What the user types, newlines included.
     * @return Ui reading that text.
     */
    private static Ui uiReading(String input) {
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        try {
            return new Ui();
        } finally {
            System.setIn(originalIn);
        }
    }

    // ---- each listing opens with its own header ----

    @Test
    public void getTaskListMessage_severalTasks_opensWithTheListHeader() {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("buy milk"));

        assertEquals("Here is every wagon on my train:\n1. [T][ ] read book\n2. [T][ ] buy milk",
                new Ui().getTaskListMessage(list));
    }

    @Test
    public void getMatchingTasksMessage_matches_opensWithTheMatchingHeader() {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("buy milk"));

        // Not the list header: a search that said "here are the tasks in your
        // list" would read as though nothing had been filtered at all.
        assertEquals("I searched the yard and found these wagons:\n1. [T][ ] read book",
                new Ui().getMatchingTasksMessage(list, "read"));
    }

    @Test
    public void getTasksOnDayMessage_matches_opensWithTheDayInTheHeader() {
        TaskList list = listOf(deadlineOn("return book", DEC_02));

        assertEquals("Here is my timetable for Dec 02 2019:\n"
                        + "1. [D][ ] return book (by: Dec 02 2019, 6:00 PM)",
                new Ui().getTasksOnDayMessage(list, DEC_02));
    }

    @Test
    public void getTasksOnDayMessage_anotherDay_namesThatDayInstead() {
        TaskList list = listOf(deadlineOn("return book", DEC_03));

        // The day is part of the header, so two days must not word alike.
        assertEquals("Here is my timetable for Dec 03 2019:\n"
                        + "1. [D][ ] return book (by: Dec 03 2019, 6:00 PM)",
                new Ui().getTasksOnDayMessage(list, DEC_03));
    }

    // ---- a header is shown even when nothing matches ----

    @Test
    public void getTaskListMessage_emptyList_isTheHeaderAlone() {
        assertEquals("Here is every wagon on my train:", new Ui().getTaskListMessage(listOf()));
    }

    @Test
    public void getMatchingTasksMessage_noMatches_isTheHeaderAlone() {
        TaskList list = listOf(new TodoTask("read book"));

        // Saying so beats saying nothing: an empty block would read as a fault.
        assertEquals("I searched the yard and found these wagons:",
                new Ui().getMatchingTasksMessage(list, "swim"));
    }

    @Test
    public void getTasksOnDayMessage_noTasksThatDay_isTheHeaderAlone() {
        TaskList list = listOf(deadlineOn("return book", DEC_03));

        assertEquals("Here is my timetable for Dec 02 2019:",
                new Ui().getTasksOnDayMessage(list, DEC_02));
    }

    // ---- the numbers shown are positions in the whole list ----

    @Test
    public void getMatchingTasksMessage_matchesWithGaps_numbersByListPosition() {
        TaskList list = listOf(new TodoTask("borrow book"), new TodoTask("swim"),
                new TodoTask("read book"));

        // 1 and 3, not 1 and 2: the number on screen is the number mark and
        // delete take, so the task in between has to leave a gap.
        assertEquals("I searched the yard and found these wagons:\n"
                        + "1. [T][ ] borrow book\n3. [T][ ] read book",
                new Ui().getMatchingTasksMessage(list, "book"));
    }

    @Test
    public void getTasksOnDayMessage_matchesWithGaps_numbersByListPosition() {
        TaskList list = listOf(new TodoTask("borrow book"), deadlineOn("return book", DEC_03),
                deadlineOn("pay fine", DEC_02));

        // Only the third task falls on the day, and it keeps the number 3.
        assertEquals("Here is my timetable for Dec 02 2019:\n"
                        + "3. [D][ ] pay fine (by: Dec 02 2019, 6:00 PM)",
                new Ui().getTasksOnDayMessage(list, DEC_02));
    }

    // ---- the wording of each message ----

    @Test
    public void getWelcomeMessage_always_isTheTwoSpokenLines() {
        // The banner is not here: it is console furniture, added by showWelcome.
        assertEquals("Peep peep! Thomas the Tank Engine, reporting for duty!\nWhat shall we haul today?",
                new Ui().getWelcomeMessage());
    }

    @Test
    public void getGoodbyeMessage_always_isTheFarewell() {
        assertEquals("Off to the sheds! Peep peep, see you down the line!", new Ui().getGoodbyeMessage());
    }

    @Test
    public void getLoadingErrorMessage_anyReason_quotesItThenSaysTheListIsEmpty() {
        assertEquals("Cinders and ashes! I couldn't read your saved tasks: disk on fire\n"
                + "Setting off with an empty train.",
                new Ui().getLoadingErrorMessage("disk on fire"));
    }

    @Test
    public void getSkippedLineMessage_anyComplaint_quotesItAfterTheWarning() {
        assertEquals("Cinders and ashes! I left a saved line in the yard, I couldn't read it: too few fields: X",
                new Ui().getSkippedLineMessage("too few fields: X"));
    }

    @Test
    public void getSavingErrorMessage_anyReason_quotesItAfterTheWarning() {
        assertEquals("Cinders and ashes! I couldn't save your tasks: disk full",
                new Ui().getSavingErrorMessage("disk full"));
    }

    @Test
    public void getAddedMessage_todo_showsTaskIndentedThenCount() {
        assertEquals("Coupled up! This wagon is on the train now:\n"
                + "   [T][ ] read book\n"
                + "That's 1 wagon(s) behind me now.",
                new Ui().getAddedMessage(new TodoTask("read book"), 1));
    }

    @Test
    public void getAddedMessage_event_showsTheEventForm() throws Exception {
        // Polymorphism picks the subclass's toString; the message never asks the type.
        LocalDateTime start = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime end = LocalDateTime.of(2019, 12, 2, 16, 0);
        assertEquals("Coupled up! This wagon is on the train now:\n"
                + "   [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)\n"
                + "That's 3 wagon(s) behind me now.",
                new Ui().getAddedMessage(new EventTask("meeting", start, end), 3));
    }

    @Test
    public void getRemovedMessage_anyTask_showsTaskIndentedThenCountLeft() {
        assertEquals("Uncoupled! I've left this wagon in the siding:\n"
                + "   [T][ ] read book\n"
                + "That's 0 wagon(s) behind me now.",
                new Ui().getRemovedMessage(new TodoTask("read book"), 0));
    }

    @Test
    public void getMarkedMessage_doneTask_showsTheTick() {
        Task task = new TodoTask("read book");
        task.markAsDone();

        // The message shows the task as it is now, so the caller must mark first.
        assertEquals("Delivered, right on time! This wagon is done:\n   [T][X] read book",
                new Ui().getMarkedMessage(task));
    }

    @Test
    public void getUnmarkedMessage_notDoneTask_showsTheEmptyBox() {
        assertEquals("Back on the train it goes! This wagon is not done yet:\n   [T][ ] read book",
                new Ui().getUnmarkedMessage(new TodoTask("read book")));
    }

    @Test
    public void getUndoneMessage_anyLine_quotesItExactlyAndReportsTheCount() {
        // Quoted as typed, odd spacing and all, so the user can tell which
        // change each of several undos reached.
        assertEquals("Reversing! I've backed out of 'delete   2'.\nThat's 2 wagon(s) behind me now.",
                new Ui().getUndoneMessage("delete   2", 2));
    }

    // ---- printing: dividers and indentation ----

    @Test
    public void showBlock_oneLine_wrapsItInDividersWithIndent() {
        assertEquals(DIVIDER + "     hello\n" + DIVIDER, printedBy(ui -> ui.showBlock("hello")));
    }

    @Test
    public void showBlock_severalLines_indentsEachLine() {
        assertEquals(DIVIDER + "     one\n     two\n" + DIVIDER, printedBy(ui -> ui.showBlock("one", "two")));
    }

    @Test
    public void showBlock_noLines_printsTheTwoDividersAlone() {
        assertEquals(DIVIDER + DIVIDER, printedBy(ui -> ui.showBlock()));
    }

    @Test
    public void showMessage_multiLineMessage_indentsEveryLine() {
        // Split before printing: handed to showBlock whole, the second line
        // would arrive without the indent every other line has.
        assertEquals(DIVIDER + "     one\n     two\n" + DIVIDER, printedBy(ui -> ui.showMessage("one\ntwo")));
    }

    @Test
    public void showMessage_blankMessage_printsNothing() {
        // Dividers around nothing would look like a fault.
        assertEquals("", printedBy(ui -> ui.showMessage("")));
        assertEquals("", printedBy(ui -> ui.showMessage("   ")));
    }

    @Test
    public void showMessage_messageEndingInNewline_printsAnIndentedEmptyLastLine() {
        // Split with limit -1, so a trailing newline keeps its empty line rather
        // than being dropped. No message today ends in one; this pins what would
        // happen if one did, and changing the limit to 0 would drop the line.
        assertEquals(DIVIDER + "     one\n     \n" + DIVIDER, printedBy(ui -> ui.showMessage("one\n")));
    }

    @Test
    public void showError_multiLineMessage_indentsEveryLine() {
        // The duplicate refusal quotes the existing task on its own line.
        assertEquals(DIVIDER + "     first\n        second\n" + DIVIDER,
                printedBy(ui -> ui.showError("first\n   second")));
    }

    @Test
    public void showError_emptyMessage_stillPrintsABlock() {
        // Unlike showMessage: an error with no words is a fault worth seeing.
        assertEquals(DIVIDER + "     \n" + DIVIDER, printedBy(ui -> ui.showError("")));
    }

    @Test
    public void showGoodbye_always_printsTheFarewellAsABlock() {
        assertEquals(DIVIDER + "     Off to the sheds! Peep peep, see you down the line!\n" + DIVIDER,
                printedBy(Ui::showGoodbye));
    }

    @Test
    public void showWelcome_always_printsBannerThenGreetingInOneBlock() {
        String banner = "       ________                              \n"
                + "      /_  __/ /_  ____  ____ ___  ____ ______\n"
                + "       / / / __ \\/ __ \\/ __ `__ \\/ __ `/ ___/\n"
                + "      / / / / / / /_/ / / / / / / /_/ (__  ) \n"
                + "     /_/ /_/ /_/\\____/_/ /_/ /_/\\__,_/____/  \n";
        assertEquals(DIVIDER + banner
                + "     Peep peep! Thomas the Tank Engine, reporting for duty!\n"
                + "     What shall we haul today?\n" + DIVIDER,
                printedBy(Ui::showWelcome));
    }

    @Test
    public void showLoadingError_anyReason_printsBothLinesAsOneBlock() {
        assertEquals(DIVIDER + "     Cinders and ashes! I couldn't read your saved tasks: disk on fire\n"
                + "     Setting off with an empty train.\n" + DIVIDER,
                printedBy(ui -> ui.showLoadingError("disk on fire")));
    }

    @Test
    public void showSkippedLine_anyComplaint_printsTheWarningAsABlock() {
        assertEquals(DIVIDER + "     Cinders and ashes! I left a saved line in the yard, I couldn't read it: bad\n"
                + DIVIDER,
                printedBy(ui -> ui.showSkippedLine("bad")));
    }

    // ---- reading commands ----

    @Test
    public void readCommand_oneLine_returnsItExactlyAsTyped() {
        // nextLine, not next: the whole line, spaces and all, is one command.
        assertEquals("todo  read book ", uiReading("todo  read book \n").readCommand());
    }

    @Test
    public void readCommand_severalLines_returnsThemInOrder() {
        Ui ui = uiReading("list\nbye\n");

        assertEquals("list", ui.readCommand());
        assertEquals("bye", ui.readCommand());
    }

    @Test
    public void hasNextCommand_lineWaiting_isTrue() {
        assertTrue(uiReading("list\n").hasNextCommand());
    }

    @Test
    public void hasNextCommand_inputEmpty_isFalse() {
        // End of input is how a piped session ends without a bye.
        assertFalse(uiReading("").hasNextCommand());
    }

    @Test
    public void hasNextCommand_afterLastLineRead_isFalse() {
        Ui ui = uiReading("list\n");
        ui.readCommand();

        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void readCommand_emptyLine_returnsEmptyString() {
        // A bare enter reaches the parser as "", which is what earns the
        // blank-line message rather than the unknown-command one.
        assertEquals("", uiReading("\n").readCommand());
    }
}
