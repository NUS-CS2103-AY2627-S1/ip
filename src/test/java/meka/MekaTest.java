package meka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MekaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_invalidEvent_preservesTasksAndRecovers() throws Exception {
        Path file = temporaryDirectory.resolve("events.txt");
        Meka meka = new Meka(file.toString());
        meka.getResponse("todo existing task");
        String saved = Files.readString(file);

        assertEquals("The event end must be after its start.",
                meka.getResponse("event meeting /from 2/12/2019 0900 /to 2/12/2019 0800"));
        assertTrue(meka.isLastResponseError());
        assertEquals(saved, Files.readString(file));
        assertEquals("1. [T][ ] existing task", meka.getResponse("list"));
        assertFalse(meka.isLastResponseError());
    }

    @Test
    void getResponse_validTodo_updatesSharedTaskList() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = meka.getResponse("todo read book");
        String listResponse = meka.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("1. [T][ ] read book", listResponse);
        assertFalse(meka.isLastResponseError());
    }

    @Test
    void getResponse_invalidCommand_returnsFriendlyError() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String response = meka.getResponse("dance");

        assertEquals("I do not understand this command. Please input a valid command.", response);
        assertFalse(meka.isExitRequested());
        assertTrue(meka.isLastResponseError());
    }

    @Test
    void getResponse_bye_requestsExit() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String response = meka.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(meka.isExitRequested());
    }

    @Test
    void getResponse_errorThenValidCommand_resetsErrorAndExitState() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        meka.getResponse("dance");
        assertTrue(meka.isLastResponseError());
        meka.getResponse("bye");
        assertTrue(meka.isExitRequested());

        assertEquals("", meka.getResponse("list"));
        assertFalse(meka.isLastResponseError());
        assertFalse(meka.isExitRequested());
    }

    @Test
    void getResponse_existingData_loadsTasksAndTrimsInput() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 1 | saved task\n");
        Meka meka = new Meka(file.toString());

        String response = meka.getResponse("  list  ");

        assertEquals("1. [T][X] saved task", response);
        assertFalse(meka.isLastResponseError());
    }

    @Test
    void getResponse_unavailableStorage_reportsErrorAndKeepsInMemoryChange() {
        Meka meka = new Meka(temporaryDirectory.toString());

        String response = meka.getResponse("todo session task");

        assertEquals("I could not save the task list. Your changes are available only for this session.",
                response);
        assertTrue(meka.isLastResponseError());
        assertEquals("1. [T][ ] session task", meka.getResponse("list"));
        assertFalse(meka.isLastResponseError());
    }

    @Test
    void run_commandsThroughBye_stopsBeforeLaterInput() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("list\nbye\ndance\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());
            meka.run();

            String session = output.toString(StandardCharsets.UTF_8);
            assertTrue(session.contains("Hello! I'm MEKA."));
            assertTrue(session.contains("Bye. Hope to see you again soon!"));
            assertFalse(session.contains("I do not understand this command."));
            assertTrue(meka.isExitRequested());
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    @Test
    void getResponse_archiveAll_movesTasksAndReportsEmptyList() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Meka meka = new Meka(activeFile.toString());
        meka.getResponse("todo first task");
        meka.getResponse("todo second task");

        String archiveResponse = meka.getResponse("archive all");

        assertEquals("Noted. I've archived all 2 tasks." + System.lineSeparator()
                + " Now you have 0 tasks in the list.", archiveResponse);
        assertEquals("", meka.getResponse("list"));
        assertEquals("", Files.readString(activeFile));
        assertEquals("T | 0 | first task" + System.lineSeparator()
                        + "T | 0 | second task" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("tasks-archive.txt")));
    }

    @Test
    void getResponse_archiveAllWithEmptyList_reportsNoTasks() {
        Meka meka = new Meka(temporaryDirectory.resolve("tasks.txt").toString());

        String response = meka.getResponse("archive all");

        assertEquals("There are no tasks to archive.", response);
    }
}
