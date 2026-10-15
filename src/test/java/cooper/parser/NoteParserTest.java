package cooper.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cooper.exception.CooperException;
import cooper.note.Note;
import cooper.parser.NoteCommand.Operation;

/** Exercises command grammar, literal text, and validation precedence. */
public class NoteParserTest {
    @Test
    public void parse_validCommands_preservesTextAndNormalizesWords() {
        assertEquals(Action.NOTE, Parser.parseAction(" NoTe add text"));
        assertEquals(new NoteCommand(Operation.ADD, 0, "Watch  Dune\tsoon"),
                NoteParser.parse("  NoTe\t AdD   Watch  Dune\tsoon  "));
        assertEquals(new NoteCommand(Operation.EDIT, 1, "new text"), NoteParser.parse("note edit 01 new text"));
        assertEquals(new NoteCommand(Operation.FIND, 0, "waist size"), NoteParser.parse("note find waist size"));
        assertEquals(new NoteCommand(Operation.DELETE, 2, ""), NoteParser.parse("note delete 2"));
        assertEquals(new NoteCommand(Operation.LIST, 0, ""), NoteParser.parse("note list  "));
        String literal = "路径 😀 | C:\\films \\n \"Arrival\" /by tomorrow";
        assertEquals(literal, NoteParser.parse("note add " + literal).text());
    }

    @Test
    public void parse_wrongStructure_returnsUsage() {
        for (String input : new String[] {"note", "note archive 1", "note list extra", "note delete",
            "note delete 1 extra", "note edit"}) {
            assertError(input, NoteParser.USAGE);
        }
    }

    @Test
    public void parse_missingValues_returnsSpecificError() {
        for (String input : new String[] {"note add", "note add \t", "note edit 1", "note edit 1 \t"}) {
            assertError(input, Note.EMPTY_TEXT);
        }
        assertError("note find \t", NoteParser.EMPTY_KEYWORD);
        assertError("note edit abc", Note.EMPTY_TEXT);
    }

    @Test
    public void parse_invalidNumbers_rejectsSignsOverflowAndNonAsciiDigits() {
        for (String number : new String[] {"abc", "-1", "+1", "0", "1.0", "2147483648", "١"}) {
            assertError("note delete " + number, NoteParser.INVALID_NUMBER);
            assertError("note edit " + number + " text", NoteParser.INVALID_NUMBER);
        }
    }

    @Test
    public void parse_lineBreaks_rejectsBeforeOtherValidation() {
        for (String separator : new String[] {"\n", "\r", "\r\n", "\u0085", "\u2028", "\u2029"}) {
            assertError("note add first" + separator + "second", Note.LINE_BREAK);
        }
    }

    private void assertError(String input, String expected) {
        assertEquals(expected, assertThrows(CooperException.class, () -> NoteParser.parse(input)).getMessage());
    }
}
