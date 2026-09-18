package student.project.bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Bob's command-loop entry point in isolated working directories.
 */
public class BobTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that the main entry point starts and handles the goodbye command.
     */
    @Test
    public void main_byeCommand_displaysWelcomeAndGoodbye() throws Exception {
        createDataFile("");

        String output = runBob("bye\n");

        assertTrue(output.contains("Hello! I'm Bob."), output);
        assertTrue(output.contains("Bye. Hope to see you again soon!"), output);
    }

    /**
     * Verifies that task creation, marking, unmarking, deletion, and listing are dispatched correctly.
     */
    @Test
    public void main_taskLifecycleCommands_updatesAndDisplaysTasks() throws Exception {
        createDataFile("");

        String output = runBob("todo write report\nmark 1\nunmark 1\ndelete 1\nlist\nbye\n");

        assertTrue(output.contains("Got it. I've added this task:"), output);
        assertTrue(output.contains("Nice! I've marked this task as done:"), output);
        assertTrue(output.contains("OK, I've marked this task as not done yet:"), output);
        assertTrue(output.contains("Noted. I've removed this task:"), output);
        assertTrue(output.contains("Here are the tasks in your list:"), output);
    }

    /**
     * Verifies that undo restores task creation, completion status, and deletion position.
     */
    @Test
    public void main_undoCommand_revertsMostRecentStateChange() throws Exception {
        createDataFile("");

        String output = runBob("todo first\n"
                + "todo second\n"
                + "delete 1\n"
                + "undo\n"
                + "mark 1\n"
                + "undo\n"
                + "list\n"
                + "undo\n"
                + "bye\n");

        assertTrue(output.contains("Undid the last command:\n    [T][ ] first"), output);
        assertTrue(output.contains("Undid the last command:\n  [ ] first"), output);
        assertTrue(output.contains("Oops! There is no command to undo."), output);
        assertTrue(output.contains("1.[T][ ] first"), output);
        assertTrue(output.contains("2.[T][ ] second"), output);
    }

    /**
     * Verifies that undo rejects arguments without discarding a valid undo action.
     */
    @Test
    public void main_undoCommand_invalidArguments() throws Exception {
        createDataFile("");

        String output = runBob("todo first\nundo now\nundo\nlist\nbye\n");

        assertTrue(output.contains("Oops! The undo command does not take any arguments."), output);
        assertTrue(output.contains("Undid the last command:\n    [T][ ] first"), output);
        String emptyListMessage =
                "Here are the tasks in your list:\n\n" + "____________________________________________________________";
        assertTrue(output.contains(emptyListMessage), output);
    }

    /**
     * Verifies that upcoming, on-date, and overdue queries select the correct task types and dates.
     */
    @Test
    public void main_dateQueryCommands_displaysMatchingTasks() throws Exception {
        createDataFile("");

        String output = runBob("deadline future report /by 9999-12-31\n"
                + "event future meeting /from 9999-12-30 /to 9999-12-30\n"
                + "deadline old report /by 2019-10-15\n"
                + "upcoming 3000000\n"
                + "on 9999-12-30\n"
                + "overdue\n"
                + "bye\n");

        assertTrue(output.contains("Here are the upcoming tasks in the next 3000000 days:"), output);
        assertTrue(output.contains("future meeting (from: Thu, Dec 30 9999 to: Thu, Dec 30 9999)"), output);
        assertTrue(output.contains("Here are the tasks on Thu, Dec 30 9999:"), output);
        assertTrue(output.contains("Here are your overdue tasks:"), output);
        assertTrue(output.contains("old report (by: Tue, Oct 15 2019)"), output);
    }

    /**
     * Verifies that find searches task descriptions across task types and ignores letter case.
     */
    @Test
    public void main_findCommand_displaysMatchingTaskDescriptions() throws Exception {
        createDataFile("");

        String output = runBob("todo read book\n"
                + "deadline return book /by 9999-12-31\n"
                + "todo write report\n"
                + "find BOOK\n"
                + "bye\n");

        assertTrue(output.contains("Here are the matching tasks in your list:"), output);
        assertTrue(output.contains("1.[T][ ] read book"), output);
        assertTrue(output.contains("2.[D][ ] return book (by: Fri, Dec 31 9999)"), output);
        assertTrue(
                !output.substring(output.indexOf("Here are the matching tasks in your list:"))
                        .contains("write report"),
                output);
    }

    /**
     * Verifies that invalid commands are reported while Bob continues running.
     */
    @Test
    public void main_invalidCommand_reportsErrorAndContinues() throws Exception {
        createDataFile("");

        String output = runBob("blah\ntodo continue working\nlist\nbye\n");

        assertTrue(output.contains("Oops! I do not recognise that command."), output);
        assertTrue(output.contains("[T][ ] continue working"), output);
        assertTrue(output.contains("Here are the tasks in your list:"), output);
    }

    /**
     * Verifies that malformed saved data and save failures are reported without crashing Bob.
     */
    @Test
    public void main_storageFailures_reportsErrorsAndContinues() throws Exception {
        createDataFile("not a valid task line\n");

        String loadFailureOutput = runBob("list\nbye\n");

        assertTrue(loadFailureOutput.contains("Oops! I couldn't load the saved tasks."), loadFailureOutput);

        createDataDirectory();
        String saveFailureOutput = runBob("todo test save failure\nbye\n");

        assertTrue(saveFailureOutput.contains("Oops! I couldn't save the task list."), saveFailureOutput);
        assertTrue(saveFailureOutput.contains("[T][ ] test save failure"), saveFailureOutput);
    }

    private void createDataFile(String contents) throws IOException {
        Path dataDirectory = temporaryDirectory.resolve("data");
        Files.createDirectories(dataDirectory);
        Files.writeString(dataDirectory.resolve("Bob.txt"), contents, StandardCharsets.UTF_8);
    }

    private void createDataDirectory() throws IOException {
        Path dataDirectory = temporaryDirectory.resolve("data");
        Files.createDirectories(dataDirectory);
        Files.deleteIfExists(dataDirectory.resolve("Bob.txt"));
        Files.createDirectory(dataDirectory.resolve("Bob.txt"));
    }

    private String runBob(String input) throws IOException, InterruptedException {
        ProcessBuilder processBuilder =
                new ProcessBuilder(javaExecutable(), "-cp", absoluteClassPath(), "student.project.bob.Bob");
        processBuilder.directory(temporaryDirectory.toFile());

        Process process = processBuilder.start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();

        assertEquals(0, exitCode, error);
        assertEquals("", error);
        return output.replace("\r\n", "\n").replace('\r', '\n');
    }

    private String javaExecutable() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private String absoluteClassPath() {
        return Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
                .map(path -> Path.of(path).toAbsolutePath().normalize().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }
}
