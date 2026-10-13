package ruby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RubyTest {

    @TempDir
    Path tempDir;

    @Test
    void getResponse_taskWorkflow_persistsAndReportsResults() throws Exception {
        Path dataFile = tempDir.resolve("ruby.txt");
        Ruby ruby = new Ruby(dataFile.toString());

        assertEquals("Added to the collection:\n  [T][ ] read book\nThat's 1 task on your plate.",
                ruby.getResponse("todo read book"));
        assertEquals("Done — consider it polished:\n  [T][X] read book", ruby.getResponse("mark 1"));
        assertEquals("Found them — I never miss:\n1. [T][X] read book", ruby.getResponse("find BOOK"));
        assertEquals("T | 1 | read book", Files.readString(dataFile));

        Ruby restoredRuby = new Ruby(dataFile.toString());
        assertEquals("Here's everything on your plate:\n1. [T][X] read book", restoredRuby.getResponse("list"));
    }

    @Test
    void getResponse_contactWorkflow_persistsAndReportsResults() throws Exception {
        Path dataFile = tempDir.resolve("ruby.txt");
        Ruby ruby = new Ruby(dataFile.toString());

        assertEquals("Saved. I never forget a name:\n  Jane | 91234567\nThat's 1 contact in your circle.",
                ruby.getResponse("contact add Jane /phone 91234567"));
        assertEquals("Your circle, as requested:\n1. Jane | 91234567", ruby.getResponse("contact list"));
        assertEquals("C | Jane | 91234567 |  | ", Files.readString(dataFile));
    }

    @Test
    void getResponse_invalidCommand_returnsUserFacingErrorAndKeepsState() {
        Ruby ruby = new Ruby(tempDir.resolve("ruby.txt").toString());

        assertEquals("Hold on — That's not in my repertoire. Try a command I know.", ruby.getResponse("dance"));
        assertEquals("Here's everything on your plate:", ruby.getResponse("list"));
    }
    @Test
    void getResponse_exitVariants_onlySuccessfulExitRequestsClosure() {
        Ruby ruby = new Ruby(tempDir.resolve("ruby.txt").toString());
        assertFalse(ruby.isExitRequested());

        String[] invalidCommands = {"BYE", "Bye", " bye", "bye ", "\tbye", "bye\t", "bye now"};
        for (String input : invalidCommands) {
            assertEquals("Bye. Your tasks are in precious hands.", ruby.getResponse("bye"));
            assertTrue(ruby.isExitRequested());
            assertTrue(ruby.getResponse(input).startsWith("Hold on — "), input);
            assertFalse(ruby.isExitRequested(), input);
            assertEquals("Here's everything on your plate:", ruby.getResponse("list"));
            assertFalse(ruby.isExitRequested());
        }
    }

    @Test
    void getResponse_startupError_doesNotRequestExit() throws Exception {
        Path dataFile = tempDir.resolve("ruby.txt");
        Files.writeString(dataFile, "invalid saved data");
        Ruby ruby = new Ruby(dataFile.toString());

        assertTrue(ruby.getResponse("bye").startsWith("Hold on — "));
        assertFalse(ruby.isExitRequested());
    }

}
