package nyonbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import nyonbot.Logic.Result;
import nyonbot.Parser;

class SimpleCommandTest {
    @Test
    void echoCommand_message_returnsMessageOnly() {
        Result result = new EchoCommand(arguments("echo hello world")).execute();

        assertEquals("hello world", result.out());
        assertFalse(result.shouldExit());
    }

    @Test
    void echoCommand_withoutMessage_returnsKeyword() {
        assertEquals("echo", new EchoCommand(arguments("echo")).execute().out());
    }

    @Test
    void exitCommand_execute_requestsExit() {
        Result result = new ExitCommand(arguments("bye")).execute();

        assertEquals("", result.out());
        assertTrue(result.shouldExit());
    }

    @Test
    void noCommand_execute_returnsFallbackMessage() {
        assertEquals("what...?", new NoCommand().execute().out());
    }

    private static HashMap<String, String> arguments(String input) {
        return Parser.getInstance().parseArguments(input);
    }
}
