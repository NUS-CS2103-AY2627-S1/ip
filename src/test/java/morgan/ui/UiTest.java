package morgan.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UiTest {
    @Test
    public void showGoodbye_buffersGoodbyeMessage() {
        Ui ui = new Ui();
        ui.showGoodbye();

        assertEquals("Meow~ Bye bye, human! Don't forget to feed me~", ui.flushResponse());
    }
}
