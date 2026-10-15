package kpei.commands.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the tokenization behavior of {@link CommandParser}.
 */
class CommandParserTest {

    private final CommandParser commandParser = new CommandParser();

    @Test
    void tokenize_singleQuotedArgumentWithEscapedApostrophe_escapedApostropheIncluded() {
        assertEquals(List.of("todo", "I'm sorry"), commandParser.tokenize("todo 'I\\'m sorry'"));
    }

    @Test
    void tokenize_doubleQuotedArgumentWithEscapedQuote_escapedQuoteIncluded() {
        assertEquals(List.of("todo", "She said \"hello\""),
                commandParser.tokenize("todo \"She said \\\"hello\\\"\""));
    }
}
