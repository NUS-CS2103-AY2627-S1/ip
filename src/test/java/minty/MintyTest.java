package minty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Minty's response API used by the graphical interface.
 */
public class MintyTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-17T00:00:00Z"), ZoneOffset.UTC);
    private static final String HEADER = "Heads up! Here are your reminders for today and the next 6 days:";
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getChatResponse_whitespaceAndInvalidInput_recoversWithoutAddingBadTasks() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));
        assertEquals(Minty.ResponseType.SUCCESS, minty.getChatResponse("  todo\t read   book  ").type());
        assertTrue(minty.getResponse(" list  ").contains("1. [T][ ] read book"));
        for (String input : new String[] {null, "  ", "todo bad\nlist", "todo bad\u0000task",
            "deadline work /by 2026-09-20 /by 2026-09-21", "mark 1 2"}) {
            assertEquals(Minty.ResponseType.ERROR, minty.getChatResponse(input).type());
        }
        assertEquals(Minty.ResponseType.SUCCESS, minty.getChatResponse("mark\t1").type());
        assertTrue(minty.getResponse("list").contains("1. [T][X] read book"));
        assertEquals(Minty.Expression.WAVING, minty.getChatResponse("  bye  ").expression());
    }

    @Test
    public void getChatResponse_duplicateDetails_rejectsEvenWhenCompletedButAllowsDifferentDates() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));
        minty.getResponse("todo read book");
        minty.getResponse("mark 1");
        assertEquals(Minty.ResponseType.ERROR, minty.getChatResponse("todo read   book").type());
        assertEquals(Minty.ResponseType.SUCCESS,
                minty.getChatResponse("deadline read book /by 2026-09-20").type());
        assertEquals(Minty.ResponseType.ERROR,
                minty.getChatResponse("deadline read book /by 2026-09-20").type());
        assertEquals(Minty.ResponseType.SUCCESS,
                minty.getChatResponse("deadline read book /by 2026-09-21").type());
    }

    @Test
    public void getChatResponse_corruptStartupFile_preservesOriginalAndReportsGuiWarning() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | valid\ninvalid data";
        Files.writeString(file, original);
        Minty minty = new Minty(file);

        assertTrue(minty.getStartupError().contains("Saving is paused"));
        Minty.ChatResponse response = minty.getChatResponse("todo session task");
        assertEquals(Minty.ResponseType.ERROR, response.type());
        assertTrue(response.text().contains("Repair or move the data file"));
        assertTrue(minty.getResponse("list").contains("session task"));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void getChatResponse_parentIsFile_reportsFailureAndKeepsSessionUsable() throws IOException {
        Path parent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parent, "preserve me");
        Minty minty = new Minty(parent.resolve("tasks.txt"));
        assertEquals(Minty.ResponseType.ERROR, minty.getChatResponse("todo read book").type());
        assertTrue(minty.getResponse("list").contains("read book"));
        assertEquals("preserve me", Files.readString(parent));
    }

    @Test
    public void getChatResponse_responseTypes_reflectOutcomesWithoutMatchingTaskText() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));
        assertEquals(Minty.ResponseType.SUCCESS,
                minty.getChatResponse("todo Whoops! Needs attention").type());
        assertEquals(Minty.ResponseType.NORMAL, minty.getChatResponse("list").type());
        assertEquals(Minty.ResponseType.SUCCESS, minty.getChatResponse("mark 1").type());
        assertEquals(Minty.ResponseType.SUCCESS, minty.getChatResponse("unmark 1").type());
        assertEquals(Minty.ResponseType.ERROR, minty.getChatResponse("mark 99").type());
        assertEquals(Minty.ResponseType.ERROR, minty.getChatResponse("unknown").type());
        assertEquals(Minty.ResponseType.REMINDER, minty.getChatResponse("reminders").type());
        assertEquals(Minty.ResponseType.SUCCESS, minty.getChatResponse("delete 1").type());
        assertEquals(Minty.ResponseType.NORMAL, minty.getChatResponse("bye").type());
    }

    @Test
    public void getChatResponse_commandOutcomes_selectExpressionsAndPreserveEarlierResponse() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));
        assertEquals(Minty.Expression.DEFAULT, minty.getChatResponse("todo read book").expression());
        Minty.ChatResponse completed = minty.getChatResponse("mark 1");
        assertEquals(Minty.Expression.CELEBRATING, completed.expression());
        assertTrue(completed.text().contains("[T][X] read book"));
        assertEquals(Minty.Expression.CURIOUS, minty.getChatResponse("mark 99").expression());
        assertEquals(Minty.Expression.CURIOUS, minty.getChatResponse("unknown").expression());
        assertEquals(Minty.Expression.DEFAULT, minty.getChatResponse("unmark 1").expression());
        assertEquals(Minty.Expression.DEFAULT, minty.getChatResponse("list").expression());
        assertEquals(Minty.Expression.WAVING, minty.getChatResponse("bye").expression());
        assertEquals(Minty.Expression.CELEBRATING, completed.expression());
        assertTrue(completed.text().contains("[T][X] read book"));
    }

    @Test
    public void getChatResponse_failedSave_usesCuriousInsteadOfCelebrating() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Minty minty = new Minty(file);
        minty.getChatResponse("todo read book");
        Files.delete(file);
        Files.createDirectory(file);

        Minty.ChatResponse response = minty.getChatResponse("mark 1");

        assertEquals(Minty.Expression.CURIOUS, response.expression());
        assertEquals(Minty.ResponseType.ERROR, response.type());
        assertTrue(response.text().contains("couldn't be saved"));
    }

    @Test
    public void getResponse_emptyListThenDeleteLastTask_showsFreshStart() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));
        String empty = "A fresh start! Your list is empty. Try todo read a book to get going.";

        assertEquals(empty, minty.getResponse("list"));
        minty.getResponse("todo read book");
        assertTrue(minty.getResponse("list").contains("1. [T][ ] read book"));
        minty.getResponse("delete 1");
        assertEquals(empty, minty.getResponse("list"));
    }

    @Test
    public void getResponse_searchWithAndWithoutMatches_preservesTasks() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Minty minty = new Minty(file);
        String noMatches = "No matches this time! Give another keyword a go.";

        assertEquals(noMatches, minty.getResponse("find book"));
        minty.getResponse("todo read book");
        assertEquals(noMatches, minty.getResponse("find magazine"));
        assertEquals("Found some fresh matches! Here's what matches your search:\n  1. [T][ ] read book",
                minty.getResponse("find book"));
        assertTrue(new Minty(file).getResponse("list").contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_saveFailure_explainsSessionOnlyChange() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Minty minty = new Minty(file);
        Files.createDirectory(file);

        String response = minty.getResponse("todo read book");

        assertTrue(response.startsWith("I've hit a snag saving your tasks. Your changes are in this session,"
                + " but couldn't be saved to the file. Details: "));
        assertTrue(minty.getResponse("list").contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_addThenList_returnsStoredTask() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));

        String addResponse = minty.getResponse("todo read book");
        String listResponse = minty.getResponse("list");

        assertTrue(addResponse.contains("Fresh task coming right up! I've added:"));
        assertTrue(listResponse.contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));

        String response = minty.getResponse("not a command");

        assertEquals("Whoops! I don't recognize that command. Try list to see your tasks or todo"
                + " read a book to add one.", response);
    }

    @Test
    public void getResponse_bye_returnsGoodbyeMessage() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));

        String response = minty.getResponse("bye");

        assertEquals("Stay fresh! Catch you next time!", response);
    }

    @Test
    public void reminders_boundariesAndTypes_returnsSortedOriginalNumbers() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String data = String.join("\n",
                "T | 0 | undated",
                "D | 0 | last day | 2026-09-23",
                "E | 0 | starts today | 2026-09-17 | 2026-09-25",
                "D | 0 | today | 2026-09-17",
                "D | 0 | outside | 2026-09-24",
                "D | 0 | overdue | 2026-09-16",
                "E | 0 | ongoing | 2026-09-16 | 2026-09-20",
                "D | 1 | completed | 2026-09-18",
                "E | 1 | completed event | 2026-09-18 | 2026-09-19");
        Files.writeString(file, data);
        Minty minty = new Minty(file, FIXED_CLOCK);
        String expected = HEADER + "\n"
                + "3. [E][ ] starts today (from: Sep 17 2026 to: Sep 25 2026)\n"
                + "4. [D][ ] today (by: Sep 17 2026)\n"
                + "2. [D][ ] last day (by: Sep 23 2026)";

        assertEquals(expected, minty.getStartupReminders());
        assertEquals(expected, minty.getResponse("reminders"));
        assertEquals(expected, minty.getResponse("remind"));
        assertEquals(data, Files.readString(file));
    }

    @Test
    public void reminders_taskLifecycle_updatesNextQuery() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"), FIXED_CLOCK);
        String empty = HEADER + "\nNo incomplete deadlines or events in this period. Stay fresh!";
        assertEquals("", minty.getStartupReminders());
        assertEquals(empty, minty.getResponse("reminders"));
        minty.getResponse("deadline work /by 2026-09-17");
        String populated = HEADER + "\n1. [D][ ] work (by: Sep 17 2026)";
        assertEquals(populated, minty.getResponse("reminders"));
        minty.getResponse("mark 1");
        assertEquals(empty, minty.getResponse("reminders"));
        minty.getResponse("unmark 1");
        assertEquals(populated, minty.getResponse("reminders"));
        minty.getResponse("delete 1");
        assertEquals(empty, minty.getResponse("reminders"));
    }

    @Test
    public void reminders_invalidForms_returnsExistingError() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"), FIXED_CLOCK);
        for (String input : new String[] {"reminders 14", "remind 2", "Reminders", "reminder"}) {
            assertEquals("Whoops! I don't recognize that command. Try list to see your tasks or"
                    + " todo read a book to add one.", minty.getResponse(input));
        }
    }

    @Test
    public void run_savedUpcomingTask_showsStartupReminderBeforeGoodbye() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | work | 2026-09-17");
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new Minty(file, FIXED_CLOCK).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("  " + HEADER + "\n  1. [D][ ] work (by: Sep 17 2026)\n"));
        assertTrue(console.indexOf(HEADER) > console.indexOf("Let's get things moving. What's on your list today?"));
        assertTrue(console.indexOf(HEADER) < console.indexOf("Stay fresh!"));
    }
}
