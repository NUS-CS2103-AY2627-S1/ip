package jaku.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jaku.JakuException;

/**
 * Verifies parsing of Jaku commands and their task-number arguments.
 */
class ParserTest {
    /** Verifies that command recognition is case-insensitive and ignores arguments. */
    @Test
    void parseCommand_recognizesCommandsCaseInsensitively() {
        assertEquals(Command.TODO, Parser.parseCommand("ToDo read book"));
        assertEquals(Command.DELETE, Parser.parseCommand("DELETE 3"));
        assertEquals(Command.FIND, Parser.parseCommand("find book"));
        assertEquals(Command.REPEAT, Parser.parseCommand("repeat todo read book /from 2026-09-10 /every daily"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    /** Verifies that unsupported input is represented by the unknown command. */
    @Test
    void parseCommand_returnsUnknownForUnsupportedCommand() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("remind buy milk"));
    }

    /** Verifies that command arguments exclude the keyword and surrounding whitespace. */
    @Test
    void getArguments_returnsTrimmedArguments() {
        assertEquals("finish report", Parser.getArguments("todo   finish report  ", Command.TODO));
        assertEquals("", Parser.getArguments("list", Command.LIST));
    }

    /** Verifies valid, missing, and nonnumeric task-number arguments. */
    @Test
    void parseTaskNumber_handlesValidAndInvalidNumbers() throws JakuException {
        assertEquals(12, Parser.parseTaskNumber("mark 12", Command.MARK));

        JakuException missingNumber = assertThrows(JakuException.class,
                () -> Parser.parseTaskNumber("delete", Command.DELETE));
        assertEquals("Use: delete <task number>.", missingNumber.getMessage());

        JakuException nonnumericNumber = assertThrows(JakuException.class,
                () -> Parser.parseTaskNumber("unmark second", Command.UNMARK));
        assertEquals("Use: unmark <task number>.", nonnumericNumber.getMessage());
    }

    /** Verifies signed and overflowing task numbers are handled without parser crashes. */
    @Test
    void parseTaskNumber_handlesSignedAndOverflowingNumbers() throws JakuException {
        assertEquals(-1, Parser.parseTaskNumber("delete -1", Command.DELETE));

        JakuException overflowingNumber = assertThrows(JakuException.class,
                () -> Parser.parseTaskNumber("mark 999999999999999999999", Command.MARK));
        assertEquals("Use: mark <task number>.", overflowingNumber.getMessage());
    }
}
