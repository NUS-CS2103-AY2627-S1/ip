package org.cs2103t.marquee.cli.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.NoSuchElementException;

import org.cs2103t.marquee.core.DuplicateKeyException;
import org.junit.jupiter.api.Test;

class CommandFormatterTest {
    @Test
    void parseCommand() {
        Code testCode = Code.createOrGet("test", "a", "bb", "c3", "00-de");
        CommandFormatter formatter = new CommandFormatter("/", "/");
        assertEquals(
                new Command(testCode, Map.of(
                    "a", "",
                    "00-de", "flag with number",
                    "c3", "// (!*#Y*()"
                )),
                formatter.parseCommand("test /a /00-de flag with number  /c3    // (!*#Y*()")
        );
        assertThrows(
                NoSuchElementException.class,
                () -> formatter.parseCommand("test illegal argument")
        );
        assertThrows(
                NoSuchElementException.class,
                () -> formatter.parseCommand("test /fake-flag")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> formatter.parseCommand("imaginary-command /imaginary flaggggg")
        );
        assertThrows(
                DuplicateKeyException.class,
                () -> formatter.parseCommand("test /bb   /bb again")
        );
    }

    @Test
    void formatCommand() {
        Code testCode = Code.createOrGet("test", "a", "bb", "c3", "00-de");
        CommandFormatter formatter = new CommandFormatter("-", "\\");
        assertEquals(
                "test -a -00\\-de with\\-escapes",
                formatter.formatCommand(new Command(
                        testCode,
                        Map.of(
                                "a", "",
                                "00-de", "with-escapes"
                        )
                ))
        );
    }
}