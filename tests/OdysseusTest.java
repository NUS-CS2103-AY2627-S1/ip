package odysseus;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Deterministic behavior tests for valid and invalid Odysseus commands.
 */
public class OdysseusTest {
    public static void main(String[] args) throws Exception {
        printsIntroArtAndAcceptsDateTimeDeadlines();
        rejectsInvalidCommandsWithoutChangingTasks();
        deletesTasksAndRenumbersTheList();
        growsBeyondTheOriginalArrayLimit();
        savesAndLoadsTasksAcrossConversations();
        startsWithAnEmptyLogWhenNoSaveFileExists();
        respondsToGuiCommandsAndKeepsGoingAfterAnError();
        learnsReviewsAndReloadsCardsAcrossConversations();
    }

    private static void printsIntroArtAndAcceptsDateTimeDeadlines() throws Exception {
        String output = run("""
                deadline return book /by 2019-11-02 1800
                bye
                """);

        assertContains(output, "___    ____  __   __  ____   ____  _____  _   _  ____");
        assertContains(output, "[D][ ] return book (by: Nov 02 2019)");
        assertContains(output, "Our voyage now holds 1 task.");
        assertNotContains(output, "Deadline dates use yyyy-MM-dd");
    }

    private static void rejectsInvalidCommandsWithoutChangingTasks() throws Exception {
        String output = run("""
                todo
                todo read book
                mark two
                mark 2
                deadline return book /by
                deadline return book /by 15-10-2019
                deadline return book /by 2019-10-15
                event project meeting /from /to 4pm
                event project meeting /from Mon 2pm /to 4pm
                unmark 4
                mark 1
                list
                bye
                """);

        assertContains(output, "A to-do needs a task to steer by.");
        assertContains(output, "Use a task number after mark");
        assertContains(output, "There is no task 2.");
        assertContains(output, "A deadline needs /by <date or time>.");
        assertContains(output, "Deadline dates use yyyy-MM-dd");
        assertContains(output, "Give both an event start after /from and an end after /to.");
        assertContains(output, "There is no task 4.");
        assertContains(output, "1. [T][X] read book");
        assertContains(output, "2. [D][ ] return book (by: Oct 15 2019)");
        assertContains(output, "3. [E][ ] project meeting (from: Mon 2pm to: 4pm)");
        assertNotContains(output, "4. [");
    }

    private static void deletesTasksAndRenumbersTheList() throws Exception {
        String output = run("""
                todo read book
                deadline return book /by 2019-10-15
                event project meeting /from Mon 2pm /to 4pm
                mark 3
                delete 2
                delete 9
                list
                bye
                """);

        assertContains(output, "The waves have carried this task from our log:");
        assertContains(output, "[D][ ] return book (by: Oct 15 2019)");
        assertContains(output, "There is no task 9.");
        assertContains(output, "1. [T][ ] read book");
        assertContains(output, "2. [E][X] project meeting (from: Mon 2pm to: 4pm)");
        assertNotContains(output, "3. [");
    }

    private static void growsBeyondTheOriginalArrayLimit() throws Exception {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 101; i++) {
            tasks.addTask(new Todo("task " + i));
        }
        if (tasks.getTaskCount() != 101) {
            throw new AssertionError("Expected the task list to grow beyond 100 tasks");
        }
    }

    private static void savesAndLoadsTasksAcrossConversations() throws Exception {
        Path storagePath = Files.createTempDirectory("odysseus-test").resolve("data").resolve("tasks.txt");
        run("""
                todo read book
                deadline return book /by 2019-10-15
                mark 1
                bye
                """, storagePath);

        String output = run("""
                list
                bye
                """, storagePath);

        assertContains(Files.readString(storagePath), "T | 1 | read book");
        assertContains(Files.readString(storagePath), "D | 0 | return book | 2019-10-15");
        assertContains(output, "1. [T][X] read book");
        assertContains(output, "2. [D][ ] return book (by: Oct 15 2019)");
    }

    private static void startsWithAnEmptyLogWhenNoSaveFileExists() throws Exception {
        Path storagePath = Files.createTempDirectory("odysseus-test").resolve("missing").resolve("tasks.txt");
        String output = run("""
                list
                bye
                """, storagePath);

        assertContains(output, "My ship's log is clear, traveler.");
    }

    private static void respondsToGuiCommandsAndKeepsGoingAfterAnError() throws Exception {
        Path storagePath = Files.createTempDirectory("odysseus-test").resolve("tasks.txt");
        Odysseus odysseus = new Odysseus(storagePath);

        String addedTask = odysseus.getResponse("todo mend the sail");
        String rejectedTask = odysseus.getResponse("mark six");
        String listedTasks = odysseus.getResponse("list");

        assertContains(addedTask, "Well charted. I've added this task:");
        assertContains(addedTask, "[T][ ] mend the sail");
        assertContains(rejectedTask, "Use a task number after mark");
        assertContains(listedTasks, "1. [T][ ] mend the sail");
        assertContains(Files.readString(storagePath), "T | 0 | mend the sail");
    }

    private static void learnsReviewsAndReloadsCardsAcrossConversations() throws Exception {
        Path storagePath = Files.createTempDirectory("odysseus-test").resolve("tasks.txt");
        Odysseus odysseus = new Odysseus(storagePath);

        String learned = odysseus.getResponse("learn nostos /answer homecoming /topic Greek terms");
        String reviewed = odysseus.getResponse("review Greek terms");
        String answered = odysseus.getResponse("answer homecoming");
        String reloadedCards = new Odysseus(storagePath).getResponse("cards Greek terms");

        assertContains(learned, "Athena has added this card");
        assertContains(reviewed, "Athena offers a card from Greek terms");
        assertContains(answered, "Well recalled");
        assertContains(reloadedCards, "mastery: 1");
        assertContains(Files.readString(storagePath.resolveSibling("odysseus-learning.txt")),
                "ATHENA_ARCHIVE_V1");
    }

    private static String run(String commands) throws Exception {
        Path storagePath = Files.createTempDirectory("odysseus-test").resolve("tasks.txt");
        return run(commands, storagePath);
    }

    private static String run(String commands, Path storagePath) {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner(commands)) {
            Odysseus.run(scanner, output, storagePath);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }

    private static void assertContains(String text, String expected) {
        if (!text.contains(expected)) {
            throw new AssertionError("Expected output to contain: " + expected + "\nActual output:\n" + text);
        }
    }

    private static void assertNotContains(String text, String unexpected) {
        if (text.contains(unexpected)) {
            throw new AssertionError("Expected output not to contain: " + unexpected + "\nActual output:\n" + text);
        }
    }
}
