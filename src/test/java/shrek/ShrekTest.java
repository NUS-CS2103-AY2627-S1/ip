package shrek;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ShrekTest {

    @TempDir
    Path tempDir;

    @Test
    public void tagAndUntag_multipleTagsUpdateTaskAtomically() {
        Shrek shrek = new Shrek(tempDir.resolve("shrek.txt").toString());
        shrek.getResponse("todo play game");

        assertEquals("Got it. I've added #fun and #school to this task:\n"
                + "  [T][ ] play game #fun #school",
                shrek.getResponse("tag 1 #fun #school"));
        assertEquals("OK, I've removed #fun and #school from this task:\n"
                + "  [T][ ] play game",
                shrek.getResponse("untag 1 #fun #school"));
    }

    @Test
    public void tag_invalidBatchDoesNotMutateTask() {
        Shrek shrek = new Shrek(tempDir.resolve("shrek.txt").toString());
        shrek.getResponse("todo play game #fun");

        assertEquals("OOPS!!! This task already has the tag #fun.",
                shrek.getResponse("tag 1 #school #fun"));
        assertEquals("Here are the tasks in your list:\n  1. [T][ ] play game #fun",
                shrek.getResponse("list"));
    }

    @Test
    public void find_matchesTags() {
        Shrek shrek = new Shrek(tempDir.resolve("shrek.txt").toString());
        shrek.getResponse("todo play game #weekend");

        assertEquals("Here are the matching tasks in your list:\n  1. [T][ ] play game #weekend",
                shrek.getResponse("find week"));
    }

    @Test
    public void listAndBye_rejectArguments() {
        Shrek shrek = new Shrek(tempDir.resolve("shrek.txt").toString());

        assertEquals("OOPS!!! The 'list' command does not accept arguments.",
                shrek.getResponse("  list   extra  "));
        assertEquals("OOPS!!! The 'bye' command does not accept arguments.",
                shrek.getResponse(" bye now "));
    }

    @Test
    public void addDuplicateTask_rejectsCanonicalDuplicate() {
        Shrek shrek = new Shrek(tempDir.resolve("shrek.txt").toString());
        assertEquals("Got it. I've added this task:\n  [T][ ] Read Book #a #b\n"
                + "Now you have 1 tasks in the list.",
                shrek.getResponse("todo  Read   Book #B #A"));

        assertEquals("OOPS!!! This task already exists in your list.",
                shrek.getResponse("todo read book #a #b"));
    }
}
