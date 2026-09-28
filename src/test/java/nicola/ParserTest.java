package nicola;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void getCommandWord_returnsFirstWord() {
        Parser parser = new Parser();

        assertEquals("todo", parser.getCommandWord("todo buy milk"));
    }

    @Test
    void getDetails_returnsTextAfterCommand() {
        Parser parser = new Parser();

        assertEquals("buy milk", parser.getDetails("todo buy milk"));
    }


}
