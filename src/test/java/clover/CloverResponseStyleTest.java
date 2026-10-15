package clover;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import clover.command.CommandResponseStyle;

/** Tests visual response styles selected after Clover processes a chat command. */
class CloverResponseStyleTest {
    @Test
    void getResponse_invalidCommand_responseUsesErrorStyle() {
        Clover clover = new Clover();

        clover.getResponse("not-a-command");

        assertEquals(CommandResponseStyle.ERROR, clover.getResponseStyle());
    }
}
