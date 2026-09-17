package bro;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies the response API shared by Bro's console and graphical interfaces. */
class BroTest {
    @TempDir
    private Path temporaryDirectory;

    /** The GUI greeting is concise and does not include the console's ASCII banner. */
    @Test
    void getWelcomeMessage_newSession_returnsCompactGreeting() {
        Bro bro = new Bro(temporaryDirectory.resolve("tasks.txt").toString());

        String welcomeMessage = bro.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Yo, I'm Bro —"));
        assertFalse(welcomeMessage.contains("____"));
    }

    /** A GUI-style session can add, persist, and list a task through text responses. */
    @Test
    void getResponse_addThenList_returnsPersistedTask() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Bro bro = new Bro(filePath.toString());

        String addResponse = bro.getResponse("todo read JavaFX tutorial");
        String listResponse = bro.getResponse("list");

        assertTrue(addResponse.contains("[T] [ ] read JavaFX tutorial"));
        assertTrue(listResponse.contains("1. [T] [ ] read JavaFX tutorial"));
        assertTrue(Files.exists(filePath));
    }

    /** Invalid and exit commands are converted to responses instead of escaping as exceptions. */
    @Test
    void getResponse_invalidThenBye_returnsHelpfulMessages() {
        Bro bro = new Bro(temporaryDirectory.resolve("tasks.txt").toString());

        String invalidResponse = bro.getResponse("dance");
        String goodbyeResponse = bro.getResponse("  BYE  ");

        assertTrue(invalidResponse.contains("I don't recognize that command"));
        assertTrue(goodbyeResponse.contains("Catch you later, bro."));
        assertTrue(bro.isExitCommand("  BYE  "));
    }

    /** All task commands can be dispatched through the same response API and persist their changes. */
    @Test
    void getResponse_taskWorkflow_handlesTypedTasksAndStateChanges() {
        Path filePath = temporaryDirectory.resolve("workflow.txt");
        Bro bro = new Bro(filePath.toString());

        String deadlineResponse = bro.getResponse("deadline submit report /by 2019-10-15");
        String eventResponse = bro.getResponse("event project meeting /from 2pm /to 4pm");
        String findResponse = bro.getResponse("find project");
        String markResponse = bro.getResponse("mark 1");
        String unmarkResponse = bro.getResponse("unmark 1");
        String deleteResponse = bro.getResponse("delete 2");
        String listResponse = bro.getResponse("list");

        assertTrue(deadlineResponse.contains("[D] [ ] submit report(by: Oct 15 2019)"));
        assertTrue(eventResponse.contains("[E] [ ] project meeting(from: 2pm to: 4pm)"));
        assertTrue(findResponse.contains("1. [E] [ ] project meeting(from: 2pm to: 4pm)"));
        assertTrue(markResponse.contains("Let's go, bro — this task is complete!"));
        assertTrue(unmarkResponse.contains("No stress, bro — this task is marked incomplete."));
        assertTrue(deleteResponse.contains("No worries, bro — I've cleared:"));
        assertTrue(listResponse.contains("1. [D] [ ] submit report(by: Oct 15 2019)"));
        assertFalse(listResponse.contains("project meeting"));
        assertTrue(Files.exists(filePath));
    }

    /** Commands that need arguments return focused usage guidance instead of mutating state. */
    @Test
    void getResponse_missingArguments_returnsUsageMessages() {
        Bro bro = new Bro(temporaryDirectory.resolve("missing-arguments.txt").toString());

        assertTrue(bro.getResponse("todo").contains("I need a todo description."));
        assertTrue(bro.getResponse("deadline").contains("My format: deadline"));
        assertTrue(bro.getResponse("event").contains("My format: event"));
        assertTrue(bro.getResponse("find").contains("Use: find <keyword>."));
        assertTrue(bro.getResponse("list extra").contains("The list command flies solo, bro."));
        assertTrue(bro.getResponse("mark").contains("Use: mark <task number>."));
        assertTrue(bro.getResponse("delete").contains("Use: delete <task number>."));
    }

    /** A storage failure is included with the compact greeting used by graphical clients. */
    @Test
    void getWelcomeMessage_storageFailure_includesRecoverableError() {
        Bro bro = new Bro(temporaryDirectory.toString());

        String welcomeMessage = bro.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Yo, I'm Bro —"));
        assertTrue(welcomeMessage.contains("I couldn't load the saved task list."));
        assertTrue(welcomeMessage.contains("Bro says:"));
    }

    /** Null and non-exact exit words are kept as ordinary non-exit input. */
    @Test
    void isExitCommand_nonExitInputs_returnsFalse() {
        Bro bro = new Bro(temporaryDirectory.resolve("exit-check.txt").toString());

        assertFalse(bro.isExitCommand(null));
        assertFalse(bro.isExitCommand("bye now"));
        assertFalse(bro.isExitCommand("exit"));
    }

    /** End-of-file still closes a console session with the banner, greeting, and sign-off. */
    @Test
    void run_endOfInput_showsWelcomeAndGoodbye() {
        String renderedOutput = captureRunOutput(temporaryDirectory.resolve("eof.txt"), "");

        assertTrue(renderedOutput.contains("B R O // TASK HQ"));
        assertTrue(renderedOutput.contains("Yo, I'm Bro — your laid-back productivity wingman."));
        assertTrue(renderedOutput.contains("Catch you later, bro. Keep crushing that to-do list!"));
    }

    /** An explicit bye command ends the loop without printing the sign-off twice. */
    @Test
    void run_byeCommand_stopsAfterOneGoodbye() {
        String renderedOutput = captureRunOutput(temporaryDirectory.resolve("bye.txt"), "bye\n");
        String goodbye = "Catch you later, bro. Keep crushing that to-do list!\n";

        assertTrue(renderedOutput.contains(goodbye));
        assertTrue(renderedOutput.indexOf(goodbye) == renderedOutput.lastIndexOf(goodbye));
    }

    /** Runs the console with isolated process streams and restores the caller's streams afterward. */
    private String captureRunOutput(Path filePath, String input) {
        java.io.InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            new Bro(filePath.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
            capturedOutput.close();
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
