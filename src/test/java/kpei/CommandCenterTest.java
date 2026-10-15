package kpei;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kpei.datatypes.Task;
import kpei.datatypes.TaskList;
import kpei.storage.Storage;
import kpei.ui.Cli;

class CommandCenterTest {

    @TempDir
    Path tempDir;

    /*
     * Runs all possible cli commands that a user would normally use, expecting no errors.
     */
    @Test
    void run_allCliCommands_noErrors() {
        String testDataFilePath = tempDir.resolve("task_list.txt").toString();
        Storage storage = new Storage(testDataFilePath);
        TaskList taskList = new TaskList(storage.getFileName());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Cli cli = new Cli(new ByteArrayInputStream(new byte[0]), outputStream);
        CommandCenter commandCenter = new CommandCenter(storage, taskList, cli);
        String taskType = new Task("Clean my room").getType();

        {
            outputStream.reset();
            String input = "todo 'Clean my room'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Added " + taskType));
            assertTrue(output.contains("[" + taskType + "][ ] Clean my room"));
        }

        {
            outputStream.reset();
            String input = "deadline --description 'Math homework' --by-date '2026-08-29' --by-time '16:00'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Added deadline"));
            assertTrue(output.contains("[deadline][ ] Math homework (by: 29 Aug 2026 4:00 pm)"), output);
        }

        {
            outputStream.reset();
            String input = "event --description 'nerd con' --from-date '2027-05-31' --toDate '2027-06-10'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Added event"), output);
            assertTrue(output.contains("[event][ ] nerd con (from: 31 May 2027 12:00 am, "
                    + "to: 10 Jun 2027 12:00 am)"), output);
        }

        {
            outputStream.reset();
            String input = "list";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Displaying list of size 3."));
        }

        {
            outputStream.reset();
            String input = "mark 1";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Marked " + taskType));
            assertTrue(output.contains("1.[" + taskType + "][X] Clean my room"));
        }

        {
            outputStream.reset();
            String input = "unmark 1";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Unmarked " + taskType));
            assertTrue(output.contains("1.[" + taskType + "][ ] Clean my room"));
        }

        {
            outputStream.reset();
            String input = "delete 1";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Removed " + taskType));
        }

        {
            outputStream.reset();
            String input = "list";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Displaying list of size 2."));
            assertTrue(output.contains("1.[deadline][ ] Math homework (by: 29 Aug 2026 4:00 pm)"), output);
            assertTrue(output.contains("2.[event][ ] nerd con (from: 31 May 2027 12:00 am, "
                    + "to: 10 Jun 2027 12:00 am)"), output);
        }
    }

    @Test
    void findCommand_matchingTasks_displayedSuccessfully() {
        String testDataFilePath = tempDir.resolve("task_list_find.txt").toString();
        Storage storage = new Storage(testDataFilePath);
        TaskList taskList = new TaskList(storage.getFileName());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Cli cli = new Cli(new ByteArrayInputStream(new byte[0]), outputStream);
        CommandCenter commandCenter = new CommandCenter(storage, taskList, cli);
        String taskType = new Task("Clean my room").getType();

        {
            outputStream.reset();
            String input = "todo 'Clean my room'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Added " + taskType));
        }

        {
            outputStream.reset();
            String input = "deadline --description 'Math homework' "
                    + "--by-date '2026-08-29' --by-time '16:00'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Added deadline"));
        }

        {
            outputStream.reset();
            String input = "event --description 'nerd con' --from-date '2027-05-31' --toDate '2027-06-10'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Added event"));
        }

        {
            outputStream.reset();
            String input = "find room";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Found 1 matching tasks."));
            assertTrue(output.contains("1.[" + taskType + "][ ] Clean my room"));
        }

        {
            outputStream.reset();
            String input = "find 'Math homework'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Found 1 matching tasks."));
            assertTrue(output.contains("1.[deadline][ ] Math homework (by: 29 Aug 2026 4:00 pm)"), output);
        }

        {
            outputStream.reset();
            String input = "find MATH";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Found 1 matching tasks."));
            assertTrue(output.contains("1.[deadline][ ] Math homework (by: 29 Aug 2026 4:00 pm)"), output);
        }
    }

    @Test
    void findCommand_noMatchingTasks_countDisplayed() {
        String testDataFilePath = tempDir.resolve("task_list_find_empty.txt").toString();
        Storage storage = new Storage(testDataFilePath);
        TaskList taskList = new TaskList(storage.getFileName());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Cli cli = new Cli(new ByteArrayInputStream(new byte[0]), outputStream);
        CommandCenter commandCenter = new CommandCenter(storage, taskList, cli);

        {
            outputStream.reset();
            String input = "todo 'Clean my room'";
            commandCenter.executeCommand(input);
        }

        {
            outputStream.reset();
            String input = "find 'non-existent keyword'";
            commandCenter.executeCommand(input);
            String output = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(output.contains("Found 0 matching tasks."));
        }
    }
}
