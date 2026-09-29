package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks that text is quoted and escaped the way the save file needs, since a single
 * missed escape would produce a file Emma cannot read back.
 */
public class JsonTest {

    @Test
    public void quote_plainText_isWrappedInQuotes() {
        assertEquals("\"read book\"", Json.quote("read book"));
    }

    @Test
    public void quote_emptyText_isJustAPairOfQuotes() {
        assertEquals("\"\"", Json.quote(""));
    }

    @Test
    public void quote_textContainingAQuote_escapesIt() {
        assertEquals("\"say \\\"hi\\\"\"", Json.quote("say \"hi\""));
    }

    @Test
    public void quote_textContainingABackslash_escapesIt() {
        assertEquals("\"a\\\\b\"", Json.quote("a\\b"));
    }

    @Test
    public void quote_textContainingANewline_escapesIt() {
        assertEquals("\"line1\\nline2\"", Json.quote("line1\nline2"));
    }

    @Test
    public void quote_textContainingATab_escapesIt() {
        assertEquals("\"a\\tb\"", Json.quote("a\tb"));
    }

    @Test
    public void quote_textNeedingSeveralEscapes_escapesEveryOne() {
        assertEquals("\"\\\\\\\"\\n\\t\"", Json.quote("\\\"\n\t"));
    }

    @Test
    public void quote_textWithCharactersNeedingNoEscape_leavesThemAlone() {
        assertEquals("\"a/b:c-d_e 1\"", Json.quote("a/b:c-d_e 1"));
    }

    @Test
    public void buildField_textValue_quotesBothSides() {
        assertEquals("\"description\": \"read book\"",
                Json.buildField("description", "read book"));
    }

    @Test
    public void buildField_textValueNeedingEscapes_escapesTheValue() {
        assertEquals("\"description\": \"say \\\"hi\\\"\"",
                Json.buildField("description", "say \"hi\""));
    }

    @Test
    public void buildField_booleanValue_isNotQuoted() {
        assertEquals("\"done\": true", Json.buildField("done", true));
        assertEquals("\"done\": false", Json.buildField("done", false));
    }
}
