package com.fireinyu.themyth.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.exceptions.ArgumentFormatException;
import com.fireinyu.themyth.tasks.TaskOrder;
import com.fireinyu.themyth.util.MythDateTime;

/**
 * Unit tests for {@link InputFieldParser} implementations.
 */
public class InputFieldParserTest {

    /**
     * Tests {@link InputFieldParser#STRING}.
     */
    @Test
    public void stringParser_returnsInputString() {
        assertEquals("hello world", InputFieldParser.STRING.parse("hello world"));
        assertEquals("", InputFieldParser.STRING.parse(""));
    }

    /**
     * Tests {@link InputFieldParser#INT} with valid integers.
     */
    @Test
    public void intParser_validInteger_success() {
        assertEquals(42, InputFieldParser.INT.parse("42"));
        assertEquals(-7, InputFieldParser.INT.parse("-7"));
        assertEquals(0, InputFieldParser.INT.parse("0"));
    }

    /**
     * Tests {@link InputFieldParser#INT} with non-integer inputs.
     */
    @Test
    public void intParser_invalidInteger_throwsException() {
        assertThrows(ArgumentFormatException.class, () -> InputFieldParser.INT.parse("abc"));
        assertThrows(ArgumentFormatException.class, () -> InputFieldParser.INT.parse("12.34"));
    }

    /**
     * Tests {@link InputFieldParser#DATETIME} with valid date-time strings.
     */
    @Test
    public void datetimeParser_validDateTime_success() {
        MythDateTime dt = InputFieldParser.DATETIME.parse("2025-10-15-18-00-00");
        assertNotNull(dt);
        assertEquals("2025-10-15-18-00-00", dt.dump());
    }

    /**
     * Tests {@link InputFieldParser#DATETIME} with invalid date-time strings.
     */
    @Test
    public void datetimeParser_invalidDateTime_throwsException() {
        assertThrows(ArgumentFormatException.class, () -> InputFieldParser.DATETIME.parse("not-a-valid-date"));
    }

    /**
     * Tests {@link InputFieldParser#ORDER} with valid case-insensitive ordering strings.
     */
    @Test
    public void orderParser_validOrder_success() {
        assertEquals(TaskOrder.CREATED, InputFieldParser.ORDER.parse("created"));
        assertEquals(TaskOrder.CREATED, InputFieldParser.ORDER.parse("CREATED"));
        assertEquals(TaskOrder.MODIFIED, InputFieldParser.ORDER.parse("modified"));
        assertEquals(TaskOrder.MODIFIED, InputFieldParser.ORDER.parse("MODIFIED"));
    }

    /**
     * Tests {@link InputFieldParser#ORDER} with invalid ordering strings.
     */
    @Test
    public void orderParser_invalidOrder_throwsException() {
        assertThrows(ArgumentFormatException.class, () -> InputFieldParser.ORDER.parse("alphabetical"));
    }
}
