package floppy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FloppyTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_invalidCommand_returnsErrorMessage() {
        Floppy floppy = new Floppy();

        String response = floppy.getResponse("dance");

        assertTrue(response.contains("I don't recognise that command."));
    }

    @Test
    void getResponse_byeCommand_returnsGoodbyeMessage() {
        Floppy floppy = new Floppy();

        String response = floppy.getResponse("bye");

        assertTrue(response.contains("Bye. Hope to see you again soon!"));
        assertTrue(response.contains("carrot"));
    }

    @Test
    void getWelcomeMessage_containsCarrotLovingPersonality() {
        Floppy floppy = new Floppy();

        assertTrue(floppy.getWelcomeMessage().contains("carrots"));
    }

    @Test
    void getResponse_taskLifecycle_updatesAndPersistsTask() {
        Path dataFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Floppy floppy = new Floppy(dataFile.toString());

        assertTrue(floppy.getResponse("todo read book").contains("[T][ ] read book"));
        String markResponse = floppy.getResponse("mark 1");
        assertTrue(markResponse.contains("[T][X] read book"));
        assertTrue(markResponse.contains("celebratory carrot"));
        assertTrue(floppy.getResponse("find BOOK").contains("[T][X] read book"));

        Floppy reloadedFloppy = new Floppy(dataFile.toString());
        assertTrue(reloadedFloppy.getResponse("list").contains("[T][X] read book"));
        assertTrue(reloadedFloppy.getResponse("delete 1").contains("[T][X] read book"));
    }
}
