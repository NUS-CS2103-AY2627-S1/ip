package nutrisoy.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Verifies release native-library selection without starting JavaFX.
 */
class NativeLibrariesTest {
    @Test
    void platformFor_supportedPlatforms_selectsMatchingLibraries() {
        assertEquals("win", NativeLibraries.platformFor("Windows 11", "amd64"));
        assertEquals("linux", NativeLibraries.platformFor("Linux", "x86_64"));
        assertEquals("mac", NativeLibraries.platformFor("Mac OS X", "x86_64"));
        assertEquals("mac-aarch64", NativeLibraries.platformFor("Mac OS X", "aarch64"));
        assertEquals("mac-aarch64", NativeLibraries.platformFor("Mac OS X", "arm64"));
    }

    @Test
    void platformFor_unsupportedPlatforms_reportsClearError() {
        assertThrows(IllegalArgumentException.class, () -> NativeLibraries.platformFor("Linux", "aarch64"));
        assertThrows(IllegalArgumentException.class, () -> NativeLibraries.platformFor("Windows 11", "x86"));
        assertThrows(IllegalArgumentException.class, () -> NativeLibraries.platformFor("Darwin", "aarch64"));
    }
}
