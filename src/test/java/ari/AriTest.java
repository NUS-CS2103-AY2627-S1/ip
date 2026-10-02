package ari;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AriTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_sortAndSave_reloadsOrderAndUsesUpdatedIds() {
        String path = this.temporaryDirectory.resolve("ari.txt").toString();
        Ari ari = new Ari(path);
        ari.start();
        ari.getResponse("todo zebra");
        ari.getResponse("deadline Apple /by Sunday");
        assertEquals("Here are your tasks sorted alphabetically (IDs updated):\n"
                + "1. [D][ ] Apple (by: Sunday)\n2. [T][ ] zebra", ari.getResponse("  SoRt  "));
        assertEquals("Good job! I've marked this task as done:\n [D][X] Apple (by: Sunday)",
                ari.getResponse("mark 1"));
        ari.getResponse("bye");

        Ari reloaded = new Ari(path);
        reloaded.start();
        assertEquals("Here are the tasks on your list:\n"
                + "1. [D][X] Apple (by: Sunday)\n2. [T][ ] zebra", reloaded.getResponse("list"));
        reloaded.getResponse("todo aardvark");
        assertEquals("Good job! I've marked this task as done:\n [T][X] aardvark",
                reloaded.getResponse("mark 3"));
    }

    @Test
    public void getResponse_sortWithArguments_rejectsWithoutReordering() {
        Ari ari = new Ari(this.temporaryDirectory.resolve("ari.txt").toString());
        ari.start();
        ari.getResponse("todo zebra");
        ari.getResponse("todo apple");
        assertEquals("Use 'sort' without any arguments.", ari.getResponse("sort descending"));
        assertEquals("Here are the tasks on your list:\n1. [T][ ] zebra\n2. [T][ ] apple",
                ari.getResponse("list"));
    }

    @Test
    public void getResponse_addThenList_returnsUpdatedTaskList() {
        Ari ari = new Ari(this.temporaryDirectory.resolve("ari.txt").toString());
        ari.start();

        String addResponse = ari.getResponse("todo read JavaFX tutorial");
        String listResponse = ari.getResponse("list");

        assertEquals(
                "Gotcha. I've added this task for you:\n"
                        + " [T][ ] read JavaFX tutorial\n"
                        + "You currently have 1 task in the list! Better get working...",
                addResponse
        );
        assertEquals(
                "Here are the tasks on your list:\n1. [T][ ] read JavaFX tutorial",
                listResponse
        );
    }

    @Test
    public void getResponse_invalidTaskId_returnsHelpfulError() {
        Ari ari = new Ari(this.temporaryDirectory.resolve("ari.txt").toString());
        ari.start();

        String response = ari.getResponse("mark one");

        assertEquals("Oops! You can only enter integer IDs. Try again!", response);
    }
}
