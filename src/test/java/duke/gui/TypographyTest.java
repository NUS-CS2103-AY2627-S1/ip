package duke.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Verifies cross-platform font choices without requiring any particular installed fonts. */
class TypographyTest {
    @Test
    void selectFontFamily_macFonts_avoidsIncorrectAvenirWeightMapping() {
        assertEquals("Arial", Typography.selectFontFamily(List.of("Arial", "Avenir Next")));
    }

    @Test
    void selectFontFamily_windowsFonts_usesSegoeUi() {
        assertEquals("Segoe UI", Typography.selectFontFamily(List.of("Arial", "Segoe UI")));
    }

    @Test
    void selectFontFamily_linuxFonts_usesAvailableSansSerif() {
        assertEquals("Noto Sans", Typography.selectFontFamily(List.of("DejaVu Sans", "Noto Sans")));
        assertEquals("DejaVu Sans", Typography.selectFontFamily(List.of("DejaVu Sans")));
        assertEquals("Liberation Sans", Typography.selectFontFamily(List.of("Liberation Sans")));
    }

    @Test
    void selectFontFamily_missingPreferredFonts_fallsBackSafely() {
        assertEquals("Arial", Typography.selectFontFamily(List.of("Arial")));
        assertEquals("System", Typography.selectFontFamily(List.of("Unknown Font")));
        assertEquals("System", Typography.selectFontFamily(List.of()));
    }
}
