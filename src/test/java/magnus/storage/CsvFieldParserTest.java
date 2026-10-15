package magnus.storage;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests decoding of the comma-separated fields used by task storage.
 */
public class CsvFieldParserTest {
    @Test
    public void parseFields_unquotedFields_returnsAllFields() {
        List<String> fields = CsvFieldParser.parseFields("T,0,read book");

        assertIterableEquals(List.of("T", "0", "read book"), fields);
    }

    @Test
    public void parseFields_emptyAndConsecutiveFields_preservesEmptyFields() {
        assertIterableEquals(List.of(""), CsvFieldParser.parseFields(""));
        assertIterableEquals(List.of("T", "", "task", ""),
                CsvFieldParser.parseFields("T,,task,"));
        assertIterableEquals(List.of(""), CsvFieldParser.parseFields("\"\""));
    }

    @Test
    public void parseFields_quotedComma_returnsDecodedField() {
        List<String> fields = CsvFieldParser.parseFields("T,0,\"buy milk, eggs\"");

        assertIterableEquals(List.of("T", "0", "buy milk, eggs"), fields);
    }

    @Test
    public void parseFields_quotedFieldBeforeDelimiter_resumesParsingNextField() {
        List<String> fields = CsvFieldParser.parseFields("\"task, one\",second");

        assertIterableEquals(List.of("task, one", "second"), fields);
    }

    @Test
    public void parseFields_escapedQuotationMark_returnsDecodedField() {
        List<String> fields = CsvFieldParser.parseFields("T,0,\"read \"\"Dune\"\"\"");

        assertIterableEquals(List.of("T", "0", "read \"Dune\""), fields);
    }

    @Test
    public void parseFields_unclosedQuotedField_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> CsvFieldParser.parseFields("T,0,\"read book"));
    }

    @Test
    public void parseFields_quotationMarkInsideUnquotedField_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> CsvFieldParser.parseFields("T,0,re\"ad book"));
    }

    @Test
    public void parseFields_textAfterQuotedField_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> CsvFieldParser.parseFields("T,0,\"read book\"later"));
    }

    @Test
    public void parseFields_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CsvFieldParser.parseFields(null));
    }
}
