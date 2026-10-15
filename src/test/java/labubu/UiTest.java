package labubu;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests user-facing static messages. */
public class UiTest {
    @Test
    public void includesIntroCommands() {
        String intro = new Ui().getIntro();

        assertTrue(intro.contains("todo [task-title]"));
        assertTrue(intro.contains("deadline [task-title] /by [date-time]"));
        assertTrue(intro.contains("Toggle help menu with 'help'"));
    }

    @Test
    public void includesHelpGuideCommandsAndExitMessage() {
        Ui ui = new Ui();

        assertTrue(ui.getHelp().contains("find [keyword]"));
        assertTrue(ui.getHelp().contains("help"));
        assertTrue(ui.getExit().contains("Bye! Hope to see you again soon!"));
    }
}
