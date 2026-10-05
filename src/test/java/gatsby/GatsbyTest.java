package gatsby;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gatsby.testutil.TestSupport;

/** Tests the public application entry point through a short command session. */
class GatsbyTest {
    private Path dataBackup;
    private final java.io.InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @BeforeEach
    void isolateDataDirectory() throws Exception {
        dataBackup = TestSupport.isolateDataDirectory();
    }

    @AfterEach
    void restoreEnvironment() throws Exception {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        TestSupport.restoreDataDirectory(dataBackup);
    }

    /** Verifies that the main loop accepts a command, executes it, and exits cleanly. */
    @Test
    void main_shortSession_processesTodoAndBye() {
        System.setIn(new ByteArrayInputStream("todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Gatsby.main(new String[0]);

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("Got it. I've added this task:"));
        assertTrue(printed.contains("[T][ ] read book"));
        assertTrue(printed.contains("Bye. Hope to see you again soon!"));
    }

    /** Verifies that the GUI-facing response path uses the same command behavior. */
    @Test
    void getResponse_todoThenList_returnsUserFacingMessages() {
        Gatsby gatsby = new Gatsby();

        String addResponse = gatsby.getResponse("todo read book");
        String listResponse = gatsby.getResponse("list");

        assertTrue(addResponse.contains("Got it. I've added this task:"));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1. [T][ ] read book"));
    }

    /** Verifies that the public response path supports both help aliases. */
    @Test
    void getResponse_helpAlias_returnsCommandReference() {
        Gatsby gatsby = new Gatsby();

        String helpResponse = gatsby.getResponse("?");

        assertTrue(helpResponse.contains("Here are the commands I know:"));
        assertTrue(helpResponse.contains("help or ? - show this help"));
    }

    /** Verifies that the GUI-facing response path identifies rejected commands. */
    @Test
    void getResponseDetails_unknownCommand_marksResponseAsError() {
        Gatsby gatsby = new Gatsby();

        Gatsby.Response response = gatsby.getResponseDetails("not-a-command");

        assertTrue(response.isError());
        assertTrue(response.getText().contains("I don't recognise that yet"));
    }

    /** Verifies that invalid dates and duplicate tasks are reported without ending the session. */
    @Test
    void getResponseDetails_invalidDateAndDuplicateTask_returnsFriendlyErrors() {
        Gatsby gatsby = new Gatsby();

        Gatsby.Response invalidDate = gatsby.getResponseDetails("deadline submit report /by 2019-02-30 1800");
        Gatsby.Response firstTask = gatsby.getResponseDetails("todo read book");
        Gatsby.Response duplicateTask = gatsby.getResponseDetails("todo read book");

        assertTrue(invalidDate.isError());
        assertTrue(invalidDate.getText().contains("Please enter a valid deadline date and time"));
        assertTrue(!firstTask.isError());
        assertTrue(duplicateTask.isError());
        assertTrue(duplicateTask.getText().contains("same details"));
    }
}
