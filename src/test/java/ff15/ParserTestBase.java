package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;

import ff15.command.Command;
import ff15.contact.ContactList;
import ff15.task.Task;
import ff15.task.TaskList;

/**
 * The harness the parser tests share. The commands the parser builds keep their
 * contents to themselves, so a successfully parsed line is checked by running it
 * against a real {@link TaskList} or {@link ContactList} and looking at what ended
 * up there. That also covers the substring arithmetic used to pull a description
 * and a date out of one line, which is where an off-by-one would hide.
 */
abstract class ParserTestBase {

    @TempDir
    Path tempDir;

    /** Returns a Storage writing both save files into the test's temporary folder. */
    protected Storage storage() {
        return new Storage(tempDir.resolve("tasks.txt").toString(),
                tempDir.resolve("contacts.txt").toString());
    }

    /** Parses and runs {@code input} against {@code tasks} and an empty contact list. */
    protected void run(TaskList tasks, String input) throws Exception {
        run(tasks, new ContactList(), input);
    }

    /**
     * Parses {@code input} and runs it against both lists, saving into the test's
     * temporary folder and throwing away whatever the Ui prints.
     */
    protected void run(TaskList tasks, ContactList contacts, String input) throws Exception {
        PrintStream realOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, contacts, new Ui(), storage());
        } finally {
            System.setOut(realOut);
        }
    }

    /** Parses and runs {@code input} against {@code tasks}, returning what the Ui printed. */
    protected String runCapturing(TaskList tasks, String input) throws Exception {
        return runCapturing(tasks, new ContactList(), input);
    }

    /**
     * Parses and runs {@code input} against both lists, returning everything the
     * Ui printed while doing so.
     */
    protected String runCapturing(TaskList tasks, ContactList contacts, String input) throws Exception {
        PrintStream realOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, contacts, new Ui(), storage());
        } finally {
            System.setOut(realOut);
        }
        return captured.toString(StandardCharsets.UTF_8);
    }

    /** Parses and runs {@code input} against an empty list, returning the single task it added. */
    protected Task addedBy(String input) throws Exception {
        TaskList tasks = new TaskList();
        run(tasks, input);
        assertEquals(1, tasks.size(), "the command should have added exactly one task");
        return tasks.get(1);
    }
}
