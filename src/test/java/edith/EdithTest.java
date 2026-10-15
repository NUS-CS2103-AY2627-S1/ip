package edith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests command-response behavior exposed to the graphical interface. */
public class EdithTest {
    @Test
    public void getResponse_unknownCommand_returnsCorrectiveMessage() {
        Edith edith = Edith.createForGui();

        String response = edith.getResponse("unknown");

        assertEquals("OOPS!!! I don't know what that means. Type 'help' to list all commands.", response);
        assertFalse(edith.isExit());
    }

    @Test
    public void getResponse_bye_returnsGoodbyeAndEndsSession() {
        Edith edith = Edith.createForGui();

        String response = edith.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(edith.isExit());
    }

    @Test
    public void getResponse_help_listsAllCommandsWithoutExiting() {
        Edith edith = Edith.createForGui();

        String response = edith.getResponse("help");

        assertEquals(String.join(System.lineSeparator(),
                "Here are the commands you can use:",
                "1. help",
                "2. todo DESCRIPTION [/tags #TAG ...]",
                "3. deadline DESCRIPTION /by yyyy-MM-dd [HHmm] [/tags #TAG ...]",
                "4. event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm] [/tags #TAG ...]",
                "5. list",
                "6. find KEYWORD",
                "7. mark NUMBER",
                "8. unmark NUMBER",
                "9. delete NUMBER",
                "10. tag NUMBER #TAG [#TAG ...]",
                "11. untag NUMBER #TAG [#TAG ...]",
                "12. bye",
                "",
                "Notes:",
                "Replace UPPER_CASE words with your own text.",
                "Square brackets mark optional input; omit the brackets.",
                "NUMBER is the task's position in the full list, starting at 1."), response);
        assertFalse(edith.isExit());
    }
}
