package stewie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import stewie.parser.Command;
import stewie.parser.Parser;

/**
 * Verifies command boundaries, optional fields, and locale-independent normalization.
 */
public class ParserEdgeCaseTest {
    /**
     * Verifies every command is recognized regardless of case and surrounding whitespace.
     */
    @Test
    public void getCommand_recognizesAllCommands() {
        for (Command command : Command.values()) {
            assertEquals(command, Parser.getCommand("  " + command.name() + "  "));
        }
        for (String input : new String[] {null, "", "  ", "unknown", "todoist", "list extra", "bye extra"}) {
            assertEquals(Command.ERROR, Parser.getCommand(input), input);
        }
        assertEquals(Command.TODO, Parser.getCommand("ToDo buy milk"));
    }

    /**
     * Verifies Unicode whitespace and Turkish casing cannot change command recognition.
     */
    @Test
    public void normalize_isIndependentOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("find milk", Parser.normalize("  FIND\t\u2003MILK\n"));
            assertEquals(Command.LIST, Parser.getCommand("LIST"));
            assertEquals(Command.FIND, Parser.getCommand("FIND milk"));
            assertEquals("", Parser.normalize(null));
        } finally {
            Locale.setDefault(original);
        }
    }

    /**
     * Verifies number boundaries and required update arguments without integer overflow.
     */
    @Test
    public void parseIndex_checksBoundaries() {
        assertEquals(0, Parser.getTaskIndex("mark 0001"));
        assertEquals(Integer.MAX_VALUE - 1, Parser.getTaskIndex("mark 2147483647"));
        assertEquals(Integer.MAX_VALUE - 1, Parser.getUpdateTaskIndex("update 2147483647 revised"));
        for (String input : new String[] {null, "", "mark", "mark 1.0", "mark ၁", "mark -1"}) {
            assertEquals(-1, Parser.getTaskIndex(input), input);
        }
        for (String input : new String[] {null, "update", "update 1", "update 0 x", "update +1 x",
            "update 2147483648 x"}) {
            assertEquals(-1, Parser.getUpdateTaskIndex(input), input);
        }
    }

    /**
     * Verifies parser helpers preserve descriptions and split search terms on Unicode whitespace.
     */
    @Test
    public void parseText_preservesDescriptionsAndKeywords() {
        assertEquals("Read Book", Parser.parseTodo("  todo\tRead Book  "));
        assertArrayEquals(new String[] {"Milk", "စာအုပ်", "bread"},
                Parser.parseFindKeywords("find Milk\tစာအုပ်\u2003bread"));
        for (String input : new String[] {null, "", "todo", "todo   "}) {
            assertEquals("Use: todo <description>.",
                    assertThrows(IllegalArgumentException.class, () -> Parser.parseTodo(input)).getMessage());
        }
        for (String input : new String[] {null, "", "find", "find   "}) {
            assertEquals("Use: find <keyword> [more keywords].",
                    assertThrows(IllegalArgumentException.class, () -> Parser.parseFindKeywords(input)).getMessage());
        }
    }

    /**
     * Verifies creation rejects missing, unknown, reordered, repeated, and empty fields.
     */
    @Test
    public void parseCreation_rejectsMalformedFields() {
        for (String input : new String[] {null, "deadline", "deadline report", "deadline /by 2026-01-01",
            "deadline report /from 2026-01-01", "deadline report /by 2026-01-01 /by 2026-01-02"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseDeadline(input), input);
        }
        for (String input : new String[] {null, "event", "event trip /from 2026-01-01",
            "event /from 2026-01-01 /to 2026-01-02", "event trip /from /to 2026-01-02",
            "event trip /from 2026-01-01 /to", "event trip /to 2026-01-02 /from 2026-01-01",
            "event trip /from 2026-01-01 /until 2026-01-02"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseEvent(input), input);
        }
        assertArrayEquals(new String[] {"read notes/book", "1/1/2026"},
                Parser.parseDeadline("deadline read notes/book /by 1/1/2026"));
    }

    /**
     * Verifies omitted fields remain null and aliases and field order are handled correctly.
     */
    @Test
    public void parseUpdate_preservesOmittedFields() {
        assertArrayEquals(new String[] {"Read Book", null, null, null}, Parser.parseUpdate("update 1 Read Book"));
        assertArrayEquals(new String[] {null, "1/1/2026", null, null}, Parser.parseUpdate("update 1 BY/1/1/2026"));
        assertArrayEquals(new String[] {"Trip", null, "1/1/2026", "2/1/2026"},
                Parser.parseUpdate("update 1 Trip TO/2/1/2026 FROM/1/1/2026"));
        assertArrayEquals(new String[4], Parser.parseUpdate(null));
        assertEquals("", Parser.parseUpdateDescription("update 1 d/1/1/2026"));
    }

    /**
     * Verifies empty, duplicate, and unsupported update fields are never silently discarded.
     */
    @Test
    public void parseUpdate_rejectsMalformedFields() {
        for (String input : new String[] {"update 1 from/", "update 1 from/ to/2/1/2026",
            "update 1 to/2/1/2026 to/3/1/2026", "update 1 from/1/1/2026 from/2/1/2026",
            "update 1 by/1/1/2026 d/2/1/2026", "update 1 bogus/value", "update 1 /by 1/1/2026"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseUpdate(input), input);
        }
    }
}
