package nico;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ExitCommandTest {
    @Test
    void isExit_exitCommand_trueReturned() {
        assertTrue(new ExitCommand().isExit());
    }
}
