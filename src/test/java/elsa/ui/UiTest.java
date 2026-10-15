package elsa.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import elsa.Dates;
import elsa.task.Deadline;
import elsa.task.Reminder;
import elsa.task.TaskList;
import elsa.task.Todo;

/**
 * Tests {@link Ui}, which words everything the chatbot says.
 *
 * <p>Wording is not decoration here: it is what the user reads, so it is part of
 * what these methods promise. The expected text is therefore written out rather
 * than built from the same pieces the method uses, because a test that asked the
 * method how it words things could never disagree with it.
 *
 * <p>What is checked hardest is the deciding these methods do rather than the
 * joining: one task or several, no tasks at all, one keyword or three, a
 * deadline overdue or due tomorrow, one damaged line in the data file or four.
 * Those branches are where a mistake hides, and several of them are reached by
 * nothing else in the suite.
 */
public class UiTest {

    /** The prefix put in front of anything the chatbot could not do. */
    private static final String ERROR_PREFIX = "OLAF!!! ";

    /** Two dates far enough apart in time to stay past and future for good. */
    private static final LocalDate PAST = LocalDate.of(2019, 10, 15);
    private static final LocalDate FUTURE = LocalDate.of(2999, 1, 1);

    /** A list holding a todo, then a deadline due in the future. */
    private static TaskList todoThenDeadline() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", FUTURE));
        return tasks;
    }

    // ------------------------------------------------------------------
    // Opening and closing a session
    // ------------------------------------------------------------------

    @Test
    public void getWelcomeMessage_always_putsTheBannerBeforeTheGreeting() {
        String welcome = new Ui().getWelcomeMessage();

        assertTrue(welcome.startsWith(" _____"), "the banner is not first");
        assertTrue(welcome.contains("Hello! I'm Elsa."), "the greeting is missing");
        assertTrue(welcome.contains("Do you want to build a snowman?"));
    }

    /**
     * The banner is drawn out of punctuation and lines up only in a font whose
     * characters are all one width. A window's font is not, so the window's
     * greeting leaves it out and would arrive crooked if it did not.
     */
    @Test
    public void getGreetingMessage_always_leavesTheBannerOutAndPointsAtHelp() {
        String greeting = new Ui().getGreetingMessage();

        assertFalse(greeting.contains("_____"), "the banner belongs to the terminal alone");
        assertTrue(greeting.startsWith("Hello! I'm Elsa."));
        assertTrue(greeting.contains("help"), "a first-time user is sent nowhere");
    }

    @Test
    public void getFarewellMessage_always_saysGoodbye() {
        assertEquals("The cold never bother me anyways!", new Ui().getFarewellMessage());
    }

    @Test
    public void getErrorMessage_anyComplaint_marksItAsOne() {
        assertEquals(ERROR_PREFIX + "something went wrong",
                new Ui().getErrorMessage("something went wrong"));
    }

    // ------------------------------------------------------------------
    // Help
    // ------------------------------------------------------------------

    /**
     * The groups are what makes a list of twelve commands readable, so the
     * headings have to survive into the text, each with its commands beneath it
     * and a blank line between one group and the next.
     */
    @Test
    public void getHelpMessage_severalGroups_showsEachHeadingAboveItsCommands() {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        groups.put("Adding a task", List.of("todo <description>"));
        groups.put("Anything else", List.of("help", "bye"));

        String help = new Ui().getHelpMessage(groups);

        assertTrue(help.startsWith("Here is what you can ask me:\n\n"));
        assertTrue(help.contains("Adding a task\n  todo <description>"),
                "the first group is not laid out under its heading");
        assertTrue(help.contains("\n\nAnything else\n  help\n  bye"),
                "the second group does not follow a blank line");
    }

    /**
     * Four of the usage lines end in a date without saying what one looks like,
     * so help is the only place a window user is told. The forms come from the
     * same constant that refuses a date, so the two cannot disagree.
     */
    @Test
    public void getHelpMessage_always_saysHowToWriteADate() {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        groups.put("Adding a task", List.of("todo <description>"));

        assertTrue(new Ui().getHelpMessage(groups).contains(Dates.ACCEPTED_FORMS));
    }

    // ------------------------------------------------------------------
    // Confirmations that count the list
    // ------------------------------------------------------------------

    @Test
    public void getAddedMessage_theFirstTask_countsItInTheSingular() {
        assertEquals("Frozen in place. I've added this task:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 1 task in the list.",
                new Ui().getAddedMessage(new Todo("read book"), 1));
    }

    /**
     * One task is "1 task" and any other number is "tasks". Getting this wrong
     * produces "1 tasks", which is the sort of thing a user notices and a test
     * that only checked the count would not.
     */
    @Test
    public void getAddedMessage_severalTasks_countsThemInThePlural() {
        String message = new Ui().getAddedMessage(new Todo("read book"), 2);

        assertTrue(message.endsWith("Now you have 2 tasks in the list."), message);
    }

    @Test
    public void getRemovedMessage_downToOneTask_countsWhatIsLeftInTheSingular() {
        assertEquals("Melted away. I've removed this task:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 1 task in the list.",
                new Ui().getRemovedMessage(new Todo("read book"), 1));
    }

    /** Removing the last task leaves none, which is a plural like any other. */
    @Test
    public void getRemovedMessage_theLastTask_countsNoneInThePlural() {
        String message = new Ui().getRemovedMessage(new Todo("read book"), 0);

        assertTrue(message.endsWith("Now you have 0 tasks in the list."), message);
    }

    @Test
    public void getMarkedMessage_aTask_showsItAsDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("Let it go! I've marked this task as done:\n  [T][X] read book",
                new Ui().getMarkedMessage(todo));
    }

    @Test
    public void getUnmarkedMessage_aTask_showsItAsNotDone() {
        assertEquals("Back into the cold. I've marked this task as not done yet:\n"
                        + "  [T][ ] read book",
                new Ui().getUnmarkedMessage(new Todo("read book")));
    }

    // ------------------------------------------------------------------
    // Listing tasks
    // ------------------------------------------------------------------

    @Test
    public void getTasksMessage_severalTasks_numbersThemFromOne() {
        assertEquals("Here are the tasks in your list:\n"
                        + "1.[T][ ] read book\n"
                        + "2.[D][ ] return book (by: Jan 01 2999)",
                new Ui().getTasksMessage(todoThenDeadline()));
    }

    /**
     * An empty list is answered with a line of its own rather than a heading
     * followed by nothing, which would read as though the answer had been cut
     * off.
     */
    @Test
    public void getTasksMessage_noTasks_saysSoInsteadOfShowingAHeading() {
        assertEquals("Into the Unknown.", new Ui().getTasksMessage(new TaskList()));
    }

    /**
     * A filtered listing keeps each task's number from the full list rather than
     * counting the shown ones from 1. The number is what the user types next, so
     * renumbering would point "mark" and "delete" at the wrong task.
     */
    @Test
    public void getTasksOnMessage_oneTaskMatching_keepsItsNumberFromTheFullList() {
        assertEquals("Here are the tasks on Jan 01 2999:\n"
                        + "2.[D][ ] return book (by: Jan 01 2999)",
                new Ui().getTasksOnMessage(todoThenDeadline(), FUTURE));
    }

    @Test
    public void getTasksOnMessage_nothingOnThatDate_namesTheDateAskedAbout() {
        assertEquals("Nothing on Oct 15 2019.",
                new Ui().getTasksOnMessage(todoThenDeadline(), PAST));
    }

    @Test
    public void getMatchingTasksMessage_oneMatch_keepsItsNumberFromTheFullList() {
        assertEquals("Here are the matching tasks in your list:\n"
                        + "1.[T][ ] read book",
                new Ui().getMatchingTasksMessage(todoThenDeadline(), "read"));
    }

    /** One keyword that matches nothing is named on its own, in quotation marks. */
    @Test
    public void getMatchingTasksMessage_oneKeywordMatchingNothing_quotesIt() {
        assertEquals("Nothing matching \"socks\".",
                new Ui().getMatchingTasksMessage(todoThenDeadline(), "socks"));
    }

    /**
     * Two keywords are joined by "or" so the complaint reads as a sentence
     * rather than as a list. This is reached by nothing else in the suite.
     */
    @Test
    public void getMatchingTasksMessage_twoKeywordsMatchingNothing_joinsThemWithOr() {
        assertEquals("Nothing matching \"socks\" or \"hats\".",
                new Ui().getMatchingTasksMessage(todoThenDeadline(), "socks", "hats"));
    }

    /**
     * Three or more are separated by commas until the last, which keeps "or".
     * Writing every gap as "or", or every gap as a comma, would each pass a test
     * that only tried two keywords.
     */
    @Test
    public void getMatchingTasksMessage_threeKeywordsMatchingNothing_commasThenOr() {
        assertEquals("Nothing matching \"socks\", \"hats\" or \"gloves\".",
                new Ui().getMatchingTasksMessage(todoThenDeadline(), "socks", "hats", "gloves"));
    }

    // ------------------------------------------------------------------
    // Reminders
    // ------------------------------------------------------------------

    @Test
    public void getRemindersMessage_nothingToShow_namesHowFarAheadItLooked() {
        assertEquals("Nothing is overdue, and nothing is due in the next 7 days.",
                new Ui().getRemindersMessage(List.of(), 7));
    }

    @Test
    public void getRemindersMessage_dueToday_saysSo() {
        Reminder reminder = new Reminder(0, new Deadline("email tutor", FUTURE), 0);

        assertEquals("Here is what needs your attention:\n"
                        + "  1.[D][ ] email tutor (by: Jan 01 2999) -- due today",
                new Ui().getRemindersMessage(List.of(reminder), 7));
    }

    @Test
    public void getRemindersMessage_dueTomorrow_saysSoRatherThanInOneDay() {
        Reminder reminder = new Reminder(0, new Deadline("buy gift", FUTURE), 1);

        assertTrue(new Ui().getRemindersMessage(List.of(reminder), 7).endsWith(" -- due tomorrow"));
    }

    @Test
    public void getRemindersMessage_dueLater_countsTheDays() {
        Reminder reminder = new Reminder(0, new Deadline("submit report", FUTURE), 5);

        assertTrue(new Ui().getRemindersMessage(List.of(reminder), 7).endsWith(" -- due in 5 days"));
    }

    /**
     * An overdue deadline already ends its own text with " -- overdue", so no
     * note is added after it. Two notes would read as two separate facts about
     * the same deadline.
     */
    @Test
    public void getRemindersMessage_overdue_addsNoSecondNote() {
        Reminder reminder = new Reminder(0, new Deadline("return book", PAST), -3);

        assertEquals("Here is what needs your attention:\n"
                        + "  1.[D][ ] return book (by: Oct 15 2019) -- overdue",
                new Ui().getRemindersMessage(List.of(reminder), 7));
    }

    /** A reminded deadline keeps its number from the full list, as any listing does. */
    @Test
    public void getRemindersMessage_severalReminders_keepsTheirNumbersAndOrder() {
        List<Reminder> reminders = List.of(
                new Reminder(4, new Deadline("email tutor", FUTURE), 0),
                new Reminder(1, new Deadline("buy gift", FUTURE), 1));

        String message = new Ui().getRemindersMessage(reminders, 7);

        assertEquals("Here is what needs your attention:\n"
                        + "  5.[D][ ] email tutor (by: Jan 01 2999) -- due today\n"
                        + "  2.[D][ ] buy gift (by: Jan 01 2999) -- due tomorrow", message);
    }

    // ------------------------------------------------------------------
    // Warning about a damaged data file
    // ------------------------------------------------------------------

    /**
     * One damaged line is "1 line ... left it out"; more than one is "lines ...
     * left them out". Both halves change together, so a message that got one
     * right and the other wrong would read as broken English either way.
     */
    @Test
    public void getSkippedLinesMessage_oneLine_usesTheSingularThroughout() {
        ArrayList<String> problems = new ArrayList<>(List.of("line 2 is not a task"));

        String message = new Ui().getSkippedLinesMessage(problems, "elsa.txt");

        assertTrue(message.startsWith(ERROR_PREFIX + "I could not understand 1 line of elsa.txt,"
                + " so I have left it out:"), message);
        assertTrue(message.contains("\n  line 2 is not a task"), "the damaged line is not named");
        assertTrue(message.contains("without the line above"), message);
        assertTrue(message.endsWith("if you want to keep it."), message);
    }

    @Test
    public void getSkippedLinesMessage_severalLines_usesThePluralThroughout() {
        ArrayList<String> problems = new ArrayList<>(List.of("line 2 is bad", "line 5 is bad"));

        String message = new Ui().getSkippedLinesMessage(problems, "elsa.txt");

        assertTrue(message.contains("I could not understand 2 lines of elsa.txt,"
                + " so I have left them out:"), message);
        assertTrue(message.contains("\n  line 2 is bad\n  line 5 is bad"));
        assertTrue(message.contains("without the lines above"), message);
        assertTrue(message.endsWith("if you want to keep them."), message);
    }

    // ------------------------------------------------------------------
    // Reading from the keyboard and writing to the screen
    // ------------------------------------------------------------------

    /**
     * Shows a message between two borders with every line indented inside them.
     * The terminal is the only place this happens; the window draws its own
     * border round each dialog box and is handed the same text undecorated.
     */
    @Test
    public void show_messageOfSeveralLines_indentsEachOneBetweenTwoBorders() {
        String printed = capturePrinted(ui -> ui.show("first line\nsecond line"));

        String border = "   " + " *".repeat(30);
        assertEquals(border + "\n"
                + "     first line\n"
                + "     second line\n"
                + border + "\n"
                + "\n", printed);
    }

    @Test
    public void hasNextCommand_inputWithLinesLeft_returnsTrue() {
        withInput("list\n", ui -> assertTrue(ui.hasNextCommand()));
    }

    /**
     * Input can end without a "bye", which is what happens when the chatbot is
     * fed a file of commands rather than typed at. The session has to notice
     * rather than wait for a line that will never come.
     */
    @Test
    public void hasNextCommand_inputThatHasRunOut_returnsFalse() {
        withInput("", ui -> assertFalse(ui.hasNextCommand()));
    }

    @Test
    public void readCommand_lineWithSpacesAroundIt_returnsItTrimmed() {
        withInput("   todo read book   \n", ui -> assertEquals("todo read book", ui.readCommand()));
    }

    @Test
    public void readCommand_severalLines_returnsThemInTurn() {
        withInput("list\nbye\n", ui -> {
            assertEquals("list", ui.readCommand());
            assertEquals("bye", ui.readCommand());
        });
    }

    /**
     * Runs an action on a user interface reading the text given, then puts the
     * real keyboard back however the action ends.
     *
     * @param input what the user is to have typed
     * @param action what to do with the user interface reading it
     */
    private static void withInput(String input, Consumer<Ui> action) {
        InputStream keyboard = System.in;
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            action.accept(new Ui());
        } finally {
            System.setIn(keyboard);
        }
    }

    /**
     * Returns what an action printed, with the line endings of whichever machine
     * the tests are running on turned into plain newlines.
     *
     * @param action what to do with the user interface being watched
     * @return everything that reached the screen while it ran
     */
    private static String capturePrinted(Consumer<Ui> action) {
        PrintStream screen = System.out;
        ByteArrayOutputStream caught = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(caught, true, StandardCharsets.UTF_8));
            action.accept(new Ui());
        } finally {
            System.setOut(screen);
        }
        return caught.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
