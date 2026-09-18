package toothless;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests platform-specific console symbol selection.
 */
public class DisplaySymbolsTest {
    @Test
    public void getDoneMark_windowsName_returnsX() {
        assertEquals("X", DisplaySymbols.getDoneMark("Windows 11"));
    }

    @Test
    public void getDoneMark_macosName_returnsStar() {
        assertEquals("★", DisplaySymbols.getDoneMark("Mac OS X"));
    }

    @Test
    public void getDecorativeMark_windowsName_returnsAsterisk() {
        assertEquals("*", DisplaySymbols.getDecorativeMark("Windows 11"));
    }

    @Test
    public void getDecorativeMark_macosName_returnsStar() {
        assertEquals("★", DisplaySymbols.getDecorativeMark("Mac OS X"));
    }
}
