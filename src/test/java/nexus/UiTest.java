package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests presentation formatting shared by Nexus responses. */
class UiTest {
    @Test
    void formatLines_varyingNumberOfLines_joinsWithLineBreaks() {
        assertEquals("first", Ui.formatLines("first"));
        assertEquals("first\nsecond\nthird", Ui.formatLines("first", "second", "third"));
    }
}
