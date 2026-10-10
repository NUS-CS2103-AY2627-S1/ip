package cassava.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void testParseUserInput_noArgs() {
        HashMap<String, String> expected = new HashMap<String, String>() {{
                put("command", "list");
                put("list", "");
            }};
        HashMap<String, String> args = Parser.parseUserInput("list");
        assertEquals(expected, args);
    }

    @Test
    public void testParseUserInput_commandArgs() {
        HashMap<String, String> expected = new HashMap<String, String>() {{
                put("command", "mark");
                put("mark", "2");
            }};
        HashMap<String, String> args = Parser.parseUserInput("mark 2");
        assertEquals(expected, args);

    }

    @Test
    public void testParseUserInput_commandMultiWordArgs() {
        HashMap<String, String> expected = new HashMap<String, String>() {{
                put("command", "todo");
                put("todo", "water the plants");
            }};
        HashMap<String, String> args = Parser.parseUserInput("todo water the plants");
        assertEquals(expected, args);
    }

    @Test
    public void testParseUserInput_commandArgsAndParams() {
        HashMap<String, String> expected = new HashMap<String, String>() {{
                put("command", "deadline");
                put("deadline", "finish assignment");
                put("by", "2026-08-28");
            }};
        HashMap<String, String> args = Parser.parseUserInput("deadline finish assignment \\by 2026-08-28");
        assertEquals(expected, args);
    }

    @Test
    public void testParseUserInput_paramNoArgs_emptyStringValue() {
        HashMap<String, String> expected = new HashMap<String, String>() {{
                put("command", "event");
                put("event", "project meeting");
                put("from", "2026-08-28");
                put("to", "");
            }};
        HashMap<String, String> args = Parser.parseUserInput("event project meeting \\from 2026-08-28 \\to");
        assertEquals(expected, args);
    }

    @Test
    public void testParseUserInput_multipleParams() {
        HashMap<String, String> expected = new HashMap<String, String>() {{
                put("command", "event");
                put("event", "project meeting");
                put("from", "2026-08-28");
                put("to", "2026-08-29");
            }};
        HashMap<String, String> args = Parser.parseUserInput(
                "event project meeting \\from 2026-08-28 \\to 2026-08-29");
        assertEquals(expected, args);
    }

    @Test
    public void testParseUserInput_paramWithMultiWordArgs() {
        HashMap<String, String> expected = new HashMap<String, String>() {{
                put("command", "deadline");
                put("deadline", "finish assignment");
                put("by", "next monday afternoon");
            }};
        HashMap<String, String> args = Parser.parseUserInput(
                "deadline finish assignment \\by next monday afternoon");
        assertEquals(expected, args);
    }

}
