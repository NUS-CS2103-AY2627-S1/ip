package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Bob's command-level responses and error handling. */
public class BobTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void responses_coverLifecycleAndPersistence() {
        Bob bob = new Bob(temporaryDirectory.resolve("tasks.txt").toString());

        assertTrue(bob.getResponse("todo Learn testing").contains("added this task"));
        assertTrue(bob.getResponse("mark 1").contains("marked this task"));
        assertTrue(bob.getResponse("unmark 1").contains("not done"));
        assertTrue(bob.getResponse("list").contains("Learn testing"));
        assertTrue(bob.getResponse("delete 1").contains("removed this task"));
        assertEquals("Bye. Hope to see you again soon!", bob.getResponse("bye"));
    }

    @Test
    public void malformedCommands_returnUserFacingErrors() {
        Bob bob = new Bob(temporaryDirectory.resolve("tasks.txt").toString());

        assertTrue(bob.getResponse("").startsWith("OOPS!!!"));
        assertTrue(bob.getResponse("unknown").startsWith("OOPS!!!"));
        assertTrue(bob.getResponse("delete nope").startsWith("OOPS!!!"));
    }
}
